package com.yourrents.api.security;

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

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

class KeycloakJwtTokenConverter implements Converter<Jwt, JwtAuthenticationToken> {

  private static final Logger log = LoggerFactory.getLogger(KeycloakJwtTokenConverter.class);
  private static final String RESOURCE_ACCESS = "resource_access";
  private static final String ROLES = "roles";
  private static final String ROLE_PREFIX = "ROLE_";
  private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;
  private final TokenConverterProperties properties;

  KeycloakJwtTokenConverter(JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter,
      TokenConverterProperties properties) {
    this.jwtGrantedAuthoritiesConverter = jwtGrantedAuthoritiesConverter;
    this.properties = properties;
  }

  @Override
  @SuppressWarnings("unchecked")
  public JwtAuthenticationToken convert(@NonNull Jwt jwt) {
    log.debug(jwt.getTokenValue());
    Stream<SimpleGrantedAuthority> accesses =
        Optional.of(jwt).map(token -> token.getClaimAsMap(RESOURCE_ACCESS))
            .map(claimMap -> (Map<String, Object>) claimMap.get(properties.getResourceId()))
            .map(resourceData -> (Collection<String>) resourceData.get(ROLES)).stream()
            .flatMap(
                roles -> roles.stream().map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role)))
            .distinct();

    Set<GrantedAuthority> authorities =
        Stream.concat(jwtGrantedAuthoritiesConverter.convert(jwt).stream(), accesses)
            .collect(Collectors.toSet());

    String principalClaimName = properties.getPrincipalAttribute().map(jwt::getClaimAsString)
        .orElse(jwt.getClaimAsString(JwtClaimNames.SUB));

    return new JwtAuthenticationToken(jwt, authorities, principalClaimName);
  }
}
