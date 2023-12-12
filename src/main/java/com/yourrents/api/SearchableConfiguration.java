package com.yourrents.api;

import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.yourrents.services.common.searchable.config.SearchableArgumentResolverConfigurer;
import com.yourrents.services.common.searchable.springdoc.SearchableOpenAPIConverter;
import com.yourrents.services.common.searchable.springdoc.customizer.SearchableOperationCustomizer;

@Configuration
public class SearchableConfiguration {

    @Bean
    public SearchableArgumentResolverConfigurer searchableArgumentResolverConfigurer() {
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
