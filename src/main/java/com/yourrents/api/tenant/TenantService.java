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

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yourrents.api.security.PrincipalAccessor;

@Service
public class TenantService {

    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private PrincipalAccessor principalAccessor;

    public String getTenantId() {
        String accountId = principalAccessor.getSubject();
        return tenantRepository.getUserTenant(UUID.fromString(accountId)).map(Tenant::uuid)
                .map(UUID::toString).orElseThrow(() -> new TenantNotFoundException("Tenant not found for account " + accountId));
    }

    @Transactional
    public void initTenantForUser() {
        String userName = principalAccessor.getName();
        Tenant tenant = tenantRepository.add(new Tenant(null, userName + " Tenant"));
        String accountId = principalAccessor.getSubject();
        tenantRepository.linkTenantToUser(tenant.uuid(), UUID.fromString(accountId));
        tenantRepository.initTenantSchema(tenant.uuid());
    }

    @Transactional
    public void initTenantDemoData() {
        String tenantUUID = getTenantId();
        tenantRepository.initTenantDemoData(UUID.fromString(tenantUUID));
    }
}
