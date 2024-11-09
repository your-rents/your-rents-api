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
import java.util.Optional;
import java.util.UUID;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.yourrents.services.common.util.exception.DataNotFoundException;

@Repository
@Transactional(readOnly = true)
public class TenantRepository {

    @Autowired
    private DSLContext dsl;
    @Autowired
    private DataSource dataSource;

    @Transactional(readOnly = false)
    public Tenant add(Tenant tenant) {
        return dsl.insertInto(TENANT, TENANT.NAME)
            .values(tenant.name())
            .returningResult(TENANT.NAME, TENANT.EXTERNAL_ID)
            .fetchOne(r -> new Tenant(r.get(TENANT.EXTERNAL_ID), r.get(TENANT.NAME)));
    }

    public Tenant get(UUID externalId) {
        return dsl.select(TENANT.NAME)
            .from(TENANT)
            .where(TENANT.EXTERNAL_ID.eq(externalId))
            .fetchOptional(r -> new Tenant(externalId, r.get(TENANT.NAME)))
            .orElseThrow(() -> new DataNotFoundException("Tenant not found"));
    }

    @Transactional(readOnly = false)
    public int delete(UUID externalId) {
        Integer tenantId = getTenantId(externalId);
        dsl.deleteFrom(TENANT_USER)
            .where(TENANT_USER.TENANT_ID.eq(tenantId))
            .execute();
        return dsl.deleteFrom(TENANT)
            .where(TENANT.EXTERNAL_ID.eq(externalId))
            .execute();
    }

    public boolean exists(UUID externalId) {
        return dsl.fetchExists(dsl.selectFrom(TENANT).where(TENANT.EXTERNAL_ID.eq(externalId)));
    }

    public Optional<Tenant> getUserTenant(UUID accountId) {
        return dsl.select(TENANT.EXTERNAL_ID, TENANT.NAME)
            .from(TENANT)
            .join(TENANT_USER).on(TENANT_USER.TENANT_ID.eq(TENANT.ID))
            .where(TENANT_USER.ACCOUNT_ID.eq(accountId))
            .fetchOptional(r -> new Tenant(r.get(TENANT.EXTERNAL_ID), r.get(TENANT.NAME)));            
    }

    @Transactional(readOnly = false)
    public void linkTenantToUser(UUID tenantExternalId, UUID accountId) {
        Integer tenantId = getTenantId(tenantExternalId);
        dsl.insertInto(TENANT_USER, TENANT_USER.TENANT_ID, TENANT_USER.ACCOUNT_ID)
            .values(tenantId, accountId)
            .execute();
    }

    public boolean existsTenantSchema(UUID tenantExternalId) {
        return !dsl.meta().filterSchemas(schema -> schema.getName().equals(tenantExternalId.toString())).getSchemas().isEmpty();
    }

    @Transactional(readOnly = false)
    public void initTenantSchema(UUID tenantExtenalId) {
        if (!existsTenantSchema(tenantExtenalId)) {
            Flyway currentModule = Flyway.configure()
                .schemas(tenantExtenalId.toString())
                .createSchemas(true)
                .locations("db/migration/tenant")
                .dataSource(dataSource).load();
                currentModule.migrate();
        }
    }

    private Integer getTenantId(UUID tenantExternalId) {
        Integer tenantId = dsl.select(TENANT.ID)
            .from(TENANT)
            .where(TENANT.EXTERNAL_ID.eq(tenantExternalId))
            .fetchOptional(r -> r.get(TENANT.ID))
            .orElseThrow(() -> new DataNotFoundException("Tenant not found"));
        return tenantId;
    }
}
