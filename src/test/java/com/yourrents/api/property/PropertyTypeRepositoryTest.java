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
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(TestYourRentsApiApplication.class)
@Transactional
class PropertyTypeRepositoryTest {

  static final UUID PROPERTY_TYPE_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");

  @Autowired
  PropertyTypeRepository propertyTypeRepository;

  @Test
  void findById() {
    PropertyType propertyType = propertyTypeRepository.findById(1)
        .orElseThrow(RuntimeException::new);
    assertThat(propertyType.name()).isEqualTo("Apartment");
    assertThat(propertyType.uuid()).isNotNull();
  }

  @Test
  void findByExternalId() {
    PropertyType propertyType = propertyTypeRepository.findByExternalId(PROPERTY_TYPE_UUID)
        .orElseThrow(RuntimeException::new);
    assertThat(propertyType.name()).isEqualTo("test type");
    assertThat(propertyType.uuid()).isEqualTo(PROPERTY_TYPE_UUID);
  }

  @Test
  void findAll() {
    List<PropertyType> propertyTypeList = propertyTypeRepository.findAll();
    assertThat(propertyTypeList).hasSize(11);
    assertThat(propertyTypeList.getFirst().code()).isEqualTo("APT");
    assertThat(propertyTypeList.getLast().code()).isEqualTo("TTY");
  }

  @Test
  void delete() {
    boolean delete = propertyTypeRepository.delete(PROPERTY_TYPE_UUID);
    assertThat(delete).isTrue();
  }
}
