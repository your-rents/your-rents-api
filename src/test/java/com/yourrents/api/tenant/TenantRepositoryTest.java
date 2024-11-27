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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import com.yourrents.api.TestYourRentsApiApplication;
import com.yourrents.services.common.util.exception.DataNotFoundException;

@SpringBootTest
@Import(TestYourRentsApiApplication.class)
@Transactional
public class TenantRepositoryTest {


    private static final UUID TENANT_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID ACCOUNT_UUID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID OTHER_ACCOUNT_UUID = UUID.fromString("00000000-0000-0000-0000-000000000003");

    @Autowired
    private TenantRepository tenantRepository;

    @Test
    void add() {
        Tenant newTenant = new Tenant(null, "t1");
        Tenant result = tenantRepository.add(newTenant);
        assertThat(result).isNotNull();
        assertThat(result.uuid()).isNotNull();
        assertThat(result.name()).isEqualTo("t1");
    }

    @Test
    void get() {
        Tenant tenant = tenantRepository.get(TENANT_UUID);
        assertThat(tenant).isNotNull();
        assertThat(tenant.uuid()).isEqualTo(TENANT_UUID);
        assertThat(tenant.name()).isEqualTo("Demo Tenant");
    }

    @Test
    void delete() {
        int deleted = tenantRepository.delete(TENANT_UUID);
        assertThat(deleted).isEqualTo(1);
        assertThatThrownBy(() -> tenantRepository.get(TENANT_UUID))
            .isInstanceOf(DataNotFoundException.class)
            .hasMessage("Tenant not found");
    }

    @Test
    void exists() {
        assertThat(tenantRepository.exists(TENANT_UUID)).isTrue();
        assertThat(tenantRepository.exists(UUID.randomUUID())).isFalse();
    }

    @Test
    void getUserTenant() {
        Tenant tenant = tenantRepository.getUserTenant(ACCOUNT_UUID).orElse(null);
        assertThat(tenant).isNotNull();
        assertThat(tenant.uuid()).isEqualTo(TENANT_UUID);
        assertThat(tenant.name()).isEqualTo("Demo Tenant");
    }

    @Test
    void linkTenantToUser() {
        tenantRepository.linkTenantToUser(TENANT_UUID, OTHER_ACCOUNT_UUID);
        Tenant tenant = tenantRepository.getUserTenant(OTHER_ACCOUNT_UUID).orElse(null);
        assertThat(tenant).isNotNull();
        assertThat(tenant.uuid()).isEqualTo(TENANT_UUID);
        assertThat(tenant.name()).isEqualTo("Demo Tenant");
    }

    @Test
    void existsTenantSchema() {
        assertThat(tenantRepository.existsTenantSchema(TENANT_UUID)).isTrue();
        assertThat(tenantRepository.existsTenantSchema(UUID.randomUUID())).isFalse();
    }
}
