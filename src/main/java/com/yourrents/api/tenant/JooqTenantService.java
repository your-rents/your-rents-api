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
