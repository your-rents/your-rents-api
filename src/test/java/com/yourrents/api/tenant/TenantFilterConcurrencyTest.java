package com.yourrents.api.tenant;

/*-
 * #%L
 * YourRents API
 * %%
 * Copyright (C) 2023 - 2024 Your Rents Team
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static com.yourrents.api.jooq.global.tables.Tenant.TENANT;
import static com.yourrents.api.jooq.global.tables.TenantUser.TENANT_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.jooq.DSLContext;
import org.jooq.exception.TooManyRowsException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.yourrents.api.TestYourRentsApiApplication;
import com.yourrents.api.security.PrincipalAccessor;

/**
 * Demonstrates the race condition in TenantFilter: when two concurrent requests arrive for
 * the same new user, both detect no tenant exists and both call initTenantForUser(), creating
 * two separate tenant records linked to the same account. The next getTenantId() call then
 * fails with TooManyRowsException because getUserTenant() finds two rows.
 */
@SpringBootTest
@Import(TestYourRentsApiApplication.class)
class TenantFilterConcurrencyTest {

    // A new account not present in any test fixture
    private static final UUID TEST_ACCOUNT_UUID = UUID.fromString("99000000-0000-0000-0000-000000000099");

    @MockitoBean
    private PrincipalAccessor principalAccessor;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private DSLContext dsl;

    @AfterEach
    void cleanup() {
        List<UUID> createdTenantIds = dsl.select(TENANT.EXTERNAL_ID)
            .from(TENANT)
            .join(TENANT_USER).on(TENANT_USER.TENANT_ID.eq(TENANT.ID))
            .where(TENANT_USER.ACCOUNT_ID.eq(TEST_ACCOUNT_UUID))
            .fetch(r -> r.get(TENANT.EXTERNAL_ID));

        for (UUID tenantId : createdTenantIds) {
            tenantRepository.delete(tenantId);
            dsl.execute("DROP SCHEMA IF EXISTS \"" + tenantId + "\" CASCADE");
        }
    }

    @Test
    void concurrentFirstAccessCreatesDuplicateTenantRecords() throws InterruptedException {
        Mockito.when(principalAccessor.isValid()).thenReturn(true);
        Mockito.when(principalAccessor.getSubject()).thenReturn(TEST_ACCOUNT_UUID.toString());
        Mockito.when(principalAccessor.getName()).thenReturn("Concurrent Test User");

        CountDownLatch startGate = new CountDownLatch(1);

        // Both tasks simulate what TenantFilter.doFilterInternal does after catching
        // TenantNotFoundException: they both proceed to call initTenantForUser().
        Runnable task = () -> {
            try {
                startGate.await();
                tenantService.initTenantForUser();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        ExecutorService executor = Executors.newFixedThreadPool(2);
        List<Future<?>> futures = List.of(executor.submit(task), executor.submit(task));
        try {
            startGate.countDown();
        } finally {
            executor.shutdown();
        }
        assertThat(executor.awaitTermination(30, TimeUnit.SECONDS)).isTrue();

        // Both tasks created their own tenant without errors — no DB constraint prevents it
        for (Future<?> f : futures) {
            assertThatCode(f::get).as("initTenantForUser should complete without errors")
                .doesNotThrowAnyException();
        }

        // Two tenant_user rows now exist for the same account_id. The next call to
        // getTenantId() hits getUserTenant() which uses fetchOptional(), and that
        // throws TooManyRowsException when more than one row is returned.
        assertThatThrownBy(() -> tenantService.getTenantId())
            .isInstanceOf(TooManyRowsException.class);
    }
}
