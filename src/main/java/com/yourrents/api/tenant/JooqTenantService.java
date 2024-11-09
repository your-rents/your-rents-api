package com.yourrents.api.tenant;

import org.jooq.DSLContext;
import org.jooq.conf.MappedSchema;
import org.jooq.conf.RenderMapping;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JooqTenantService {
    
    @Autowired
    private TenantService tenantService;
    @Autowired
    private DSLContext commonDsl;

    public DSLContext getDslForTenant() {
        DSLContext dsl = DSL.using(this.commonDsl.configuration()
                .deriveSettings(settings -> settings.withRenderMapping(
                        new RenderMapping().withSchemata(new MappedSchema()
                            .withInput("tenant")
                            .withOutput(tenantService.getTenantId())))));
        return dsl;
    }

}
