package com.yourrents.api.security;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.yourrents.api.tenant.TenantFilter;

@Configuration
@EnableWebSecurity
class SecurityConfiguration {
  private static final Logger log = LoggerFactory.getLogger(SecurityConfiguration.class);

  private final KeycloakJwtTokenConverter keycloakJwtTokenConverter;

  private final String allowedOrigins;

  @Value("${spring.websecurity.debug:false}")
  private boolean webSecurityDebug;  

  SecurityConfiguration(TokenConverterProperties properties,
      @Value("${yrs-api.cors.allowed-origins}") String allowedOrigins) {
    JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter =
        new JwtGrantedAuthoritiesConverter();
    this.keycloakJwtTokenConverter =
        new KeycloakJwtTokenConverter(jwtGrantedAuthoritiesConverter, properties);
    this.allowedOrigins = allowedOrigins;
  }

  @Bean
  WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.debug(webSecurityDebug);
  }

  @Bean
  FilterRegistrationBean<TenantFilter> tenantFilterRegistration(TenantFilter filter) {
    FilterRegistrationBean<TenantFilter> registration = new FilterRegistrationBean<>(filter);
    registration.setEnabled(false);
    return registration;
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http, TenantFilter tenantFilter)
      throws Exception {
    return http.csrf(csrf -> csrf.disable()).cors(Customizer.withDefaults())
        .authorizeHttpRequests(
            authorizeRequests -> authorizeRequests.requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/api/**").hasRole("USER").anyRequest().permitAll())
        .oauth2ResourceServer(
            oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtTokenConverter)))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
        .addFilterBefore(tenantFilter, AuthorizationFilter.class).build();
  }


  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    log.info("Configuring CORS with allowed origins: {}", allowedOrigins);
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
    configuration.setAllowedMethods(Arrays.asList("*"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

}
