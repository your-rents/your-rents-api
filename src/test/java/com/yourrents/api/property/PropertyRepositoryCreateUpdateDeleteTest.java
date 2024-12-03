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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.yourrents.api.TestYourRentsApiApplication;
import com.yourrents.api.security.PrincipalAccessor;
import com.yourrents.services.common.util.exception.DataNotFoundException;
import com.yourrents.services.geodata.model.Address;
import com.yourrents.services.geodata.repository.AddressRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(TestYourRentsApiApplication.class)
@Transactional
class PropertyRepositoryCreateUpdateDeleteTest {

  static final int ADDRESS_ID = 1000000000;

  static final UUID PROPERTY_TYPE_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");

  static final UUID PROPERTY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");

  static final int PROPERTY_YEAR_OF_BUILD = 1971;

  static final String ACCOUNT_UUID = "00000000-0000-0000-0000-000000000002";

  @Autowired
  PropertyRepository propertyRepository;

  @Autowired
  AddressRepository addressRepository;

  @MockitoBean
  private PrincipalAccessor principalAccessor;

  @BeforeEach
  void setUp() {
    Mockito.when(principalAccessor.getSubject()).thenReturn(ACCOUNT_UUID);
  }

  @Test
  void add() {
    Address address = addressRepository.findById(ADDRESS_ID)
        .orElseThrow(RuntimeException::new);

    Property newProperty = new Property(null, "p1",
        new PropertyType(PROPERTY_TYPE_UUID, null, null, null),
        "small flat", 1970, 45, address.uuid());
    Property result = propertyRepository.add(newProperty);
    assertThat(result).isNotNull();
    assertThat(result.uuid()).isNotNull();
    assertThat(result.name()).isEqualTo("p1");
    assertThat(result.type().uuid()).isEqualTo(PROPERTY_UUID);
    assertThat(result.description()).isEqualTo("small flat");
    assertThat(result.yearOfBuild()).isEqualTo(1970);
    assertThat(result.sizeMq()).isEqualTo(45);
    assertThat(result.type()).isNotNull();
    assertThat(result.addressUuid()).isEqualTo(address.uuid());
    assertThat(result.type().name()).isEqualTo("test type");
  }

  @Test
  void delete() {
    boolean isDeleted = propertyRepository.delete(PROPERTY_UUID);
    assertThat(isDeleted).isTrue();
    Optional<Property> optional = propertyRepository.findByExternalId(PROPERTY_UUID);
    assertThat(optional.isEmpty()).isTrue();
  }

  @Test
  void deleteANotExistingProperty() {
    UUID randomUUID = UUID.randomUUID();
    assertThatExceptionOfType(DataNotFoundException.class)
        .isThrownBy(() -> propertyRepository.delete(randomUUID))
        .withMessageContaining("cannot find property with uuid: " + randomUUID);
  }


  @Test
  void update() {
    UUID addressUuid = addressRepository.findById(ADDRESS_ID).map(Address::uuid).orElseThrow();
    Property updateProperty = new Property(null, "house",
        new PropertyType(PROPERTY_TYPE_UUID, null, null, null),
        "small house", null,
        null,
        addressUuid);
    Property result = propertyRepository.update(PROPERTY_UUID, updateProperty);
    assertThat(result).isNotNull();
    assertThat(result.addressUuid()).isEqualTo(addressUuid);
    assertThat(result.name()).isEqualTo("house");
    assertThat(result.description()).isEqualTo("small house");
    assertThat(result.yearOfBuild()).isEqualTo(PROPERTY_YEAR_OF_BUILD);
    assertThat(result.name()).isEqualTo(updateProperty.name());
    assertThat(result.type()).isNotNull();
    assertThat(result.type().uuid()).isEqualTo(PROPERTY_TYPE_UUID);
    assertThat(result.type().name()).isEqualTo("test type");
  }

  @Test
  void updatePropertyWithAnInvalidAddress() {
    Property property = propertyRepository.findById(1000000).get();
    UUID randomUUID = UUID.randomUUID();
    Property updateProperty = new Property(null, null, null, "desc", null, null, randomUUID);
    assertThatExceptionOfType(DataNotFoundException.class)
        .isThrownBy(() -> propertyRepository.update(property.uuid(), updateProperty))
        .withMessageContaining("cannot find address with uuid: " + randomUUID);
  }

  @Test
  void updatePropertyWithAnInvalidType() {
    Property property = propertyRepository.findById(1000000).get();
    UUID randomUUID = UUID.randomUUID();
    Property updateProperty = new Property(null, null,
        new PropertyType(randomUUID, null, null, null),
        "desc", null, null, null);
    assertThatExceptionOfType(DataNotFoundException.class)
        .isThrownBy(() -> propertyRepository.update(property.uuid(), updateProperty))
        .withMessageContaining("cannot find property_type with uuid: " + randomUUID);
  }
}
