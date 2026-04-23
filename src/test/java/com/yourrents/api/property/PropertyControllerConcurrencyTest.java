package com.yourrents.api.property;

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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.jooq.DSLContext;
import org.jooq.exception.TooManyRowsException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.yourrents.api.TestYourRentsApiApplication;
import com.yourrents.api.security.PrincipalAccessor;
import com.yourrents.api.tenant.TenantRepository;

/**
 * Demonstrates the TenantFilter race condition via real concurrent HTTP requests to
 * PropertyController. Unlike PropertyControllerTest, addFilters is NOT suppressed so
 * TenantFilter runs as part of the Spring Security chain.
 *
 * Two concurrent requests for the same brand-new user both detect no tenant and both call
 * initTenantForUser(), producing two tenant records for the same account. A third request
 * then throws TooManyRowsException because TenantFilter.getTenantId() finds two rows and
 * that exception is not caught by the filter's catch block (which only handles TenantNotFoundException).
 */
@SpringBootTest
@Import(TestYourRentsApiApplication.class)
@AutoConfigureMockMvc
class PropertyControllerConcurrencyTest {

    private static final Logger log = LoggerFactory.getLogger(PropertyControllerConcurrencyTest.class);

    private static final UUID TEST_ACCOUNT_UUID = UUID.fromString("99000000-0000-0000-0000-000000000099");
    private static final String FIRST_PROPERTY_UUID = "00000000-0000-0000-0000-000000000001";

    @Autowired
    MockMvc mvc;

    @Value("${yrs-api.api.basepath}")
    String basePath;

    @MockitoBean
    private PrincipalAccessor principalAccessor;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private DSLContext dsl;

    @BeforeEach
    void setUpPrincipal() {
        Mockito.when(principalAccessor.isValid()).thenReturn(true);
        Mockito.when(principalAccessor.getSubject()).thenReturn(TEST_ACCOUNT_UUID.toString());
        Mockito.when(principalAccessor.getName()).thenReturn("Concurrent Test User");
        Mockito.when(principalAccessor.getUsername()).thenReturn("concurrent.user");
    }

    @AfterEach
    void cleanup() {
        List<UUID> createdTenantIds = dsl.select(TENANT.EXTERNAL_ID)
            .from(TENANT)
            .join(TENANT_USER).on(TENANT_USER.TENANT_ID.eq(TENANT.ID))
            .where(TENANT_USER.ACCOUNT_ID.eq(TEST_ACCOUNT_UUID))
            .fetch(r -> r.get(TENANT.EXTERNAL_ID));

        for (UUID tenantId : createdTenantIds) {
            tenantRepository.delete(tenantId);
            if (tenantRepository.existsTenantSchema(tenantId)) {
                int result = dsl.execute("DROP SCHEMA IF EXISTS \"" + tenantId + "\" CASCADE");
                log.info("Dropped tenant schema {} (execute result: {})", tenantId, result);
            } else {
                log.warn("Tenant schema {} not found during cleanup — skipping drop", tenantId);
            }
        }
    }

    @Test
    void findByUuidWithConcurrentFirstAccessCreatesDuplicateTenants() throws Exception {
        String url = basePath + "/properties/" + FIRST_PROPERTY_UUID;

        CountDownLatch startGate = new CountDownLatch(1);

        // Both threads hit GET /properties/{uuid} for the same brand-new user at the same time.
        // TenantFilter runs for each: getTenantId() → TenantNotFoundException → initTenantForUser().
        Runnable request = () -> {
            try {
                startGate.await();
                mvc.perform(get(url)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();
            } catch (Exception ignored) {}
        };

        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            executor.submit(request);
            executor.submit(request);
            startGate.countDown();
        } finally {
            executor.shutdown();
        }
        assertThat(executor.awaitTermination(30, TimeUnit.SECONDS)).isTrue();

        // Race condition confirmed: two tenants for the same account
        long duplicateCount = dsl.select(TENANT.EXTERNAL_ID)
            .from(TENANT)
            .join(TENANT_USER).on(TENANT_USER.TENANT_ID.eq(TENANT.ID))
            .where(TENANT_USER.ACCOUNT_ID.eq(TEST_ACCOUNT_UUID))
            .fetch()
            .size();
        assertThat(duplicateCount)
            .as("both concurrent findByUuid requests should have each created a tenant record")
            .isEqualTo(2);

        // Third call to the same endpoint: TooManyRowsException from TenantFilter
        assertThatThrownBy(() ->
            mvc.perform(get(url)
                    .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                    .contentType(MediaType.APPLICATION_JSON))
                .andReturn())
            .isInstanceOf(TooManyRowsException.class);
    }
}
