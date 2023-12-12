package com.yourrents.api;

/*-
 * #%L
 * YourRents API
 * %%
 * Copyright (C) 2023 Your Rents Team
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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.yourrents.services.common.util.jooq.JooqUtils;

@Configuration
@ComponentScan(basePackages = { "com.yourrents.services.geodata.controller",
                "com.yourrents.services.geodata.repository", "com.yourrents.services.geodata.mapper" })
public class YourRentsServiceGeodataConfiguration {

        @Bean
        public JooqUtils jooqUtils() {
                return new JooqUtils();
        }

}
