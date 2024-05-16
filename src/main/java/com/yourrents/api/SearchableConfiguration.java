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

import com.yourrents.services.common.searchable.config.SearchableArgumentResolverConfigurer;
import com.yourrents.services.common.searchable.springdoc.SearchableOpenAPIConverter;
import com.yourrents.services.common.searchable.springdoc.customizer.SearchableOperationCustomizer;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SearchableConfiguration {

    @Bean
    SearchableArgumentResolverConfigurer searchableArgumentResolverConfigurer() {
        return new SearchableArgumentResolverConfigurer();
    }

    @Bean
    SearchableOpenAPIConverter searchableOpenAPIConverter(ObjectMapperProvider objectMapperProvider) {
        return new SearchableOpenAPIConverter(objectMapperProvider);
    }

    @Bean
    SearchableOperationCustomizer searchableOperationCustomizer() {
        return new SearchableOperationCustomizer();
    }
}
