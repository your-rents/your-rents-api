package com.yourrents.api.property;

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


import static org.assertj.core.api.Assertions.assertThat;

import com.yourrents.api.TestYourRentsApiApplication;
import com.yourrents.services.geodata.model.Address;
import com.yourrents.services.geodata.repository.AddressRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(TestYourRentsApiApplication.class)
@Transactional
class PropertyRepositoryCreateUpdateDeleteTest {

  static final int ADDRESS_ID = 1000000000;
  static final int PROPERTY_ID = 1000000;

  @Autowired
  PropertyRepository propertyRepository;

  @Autowired
  AddressRepository addressRepository;

  @Test
  void add() {
    Address address = addressRepository.findById(ADDRESS_ID).orElseThrow();
    Property newProperty = new Property(null, "p1", "flat",
        "small flat", 1970, 45, address.uuid());
    Property result = propertyRepository.add(newProperty);
    assertThat(result).isNotNull();
    assertThat(result.uuid()).isNotNull();
    assertThat(result.name()).isEqualTo("p1");
    assertThat(result.type()).isEqualTo("flat");
    assertThat(result.description()).isEqualTo("small flat");
    assertThat(result.yearOfBuild()).isEqualTo(1970);
    assertThat(result.sizeMq()).isEqualTo(45);
    assertThat(result.addressUuid()).isEqualTo(address.uuid());
  }

  @Test
  void delete() {
    Property property = propertyRepository.findById(PROPERTY_ID).orElseThrow();
    boolean isDeleted = propertyRepository.delete(property.uuid());
    assertThat(isDeleted).isTrue();
    Optional<Property> optional = propertyRepository.findById(PROPERTY_ID);
    assertThat(optional.isEmpty()).isTrue();
  }

  @Test
  void update() {
    Property property = propertyRepository.findById(PROPERTY_ID).orElseThrow();
    UUID addressUuid = addressRepository.findById(ADDRESS_ID).map(Address::uuid).orElseThrow();
    Property updateProperty = new Property(null, "house", "house", "small house", null, null,
        addressUuid);
    Property result = propertyRepository.update(property.uuid(), updateProperty);
    assertThat(result).isNotNull();
    assertThat(result.addressUuid()).isEqualTo(addressUuid);
    assertThat(result.yearOfBuild()).isEqualTo(property.yearOfBuild());
    assertThat(result.name()).isEqualTo(updateProperty.name());
  }
}
