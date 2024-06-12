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

import java.util.Optional;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "token.converter")
class TokenConverterProperties {
  private String resourceId;
  private String principalAttribute;

  public String getResourceId() {
    return resourceId;
  }

  public Optional<String> getPrincipalAttribute() {
    return Optional.ofNullable(principalAttribute);
  }

  public void setResourceId(String resourceId) {
    this.resourceId = resourceId;
  }

  public void setPrincipalAttribute(String principalAttribute) {
    this.principalAttribute = principalAttribute;
  }
}
