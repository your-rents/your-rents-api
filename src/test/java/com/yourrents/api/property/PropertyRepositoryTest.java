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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.Matchers.notNullValue;

import com.yourrents.api.TestYourRentsApiApplication;
import com.yourrents.api.security.PrincipalAccessor;
import com.yourrents.services.common.searchable.FilterCondition;
import com.yourrents.services.common.searchable.FilterCriteria;
import com.yourrents.services.geodata.repository.AddressRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(TestYourRentsApiApplication.class)
@Transactional
class PropertyRepositoryTest {

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
  void findAll() {
    Page<Property> result = propertyRepository.find(FilterCriteria.of(),
        PageRequest.ofSize(Integer.MAX_VALUE));
    assertThat(result, iterableWithSize(3));
  }

  @Test
  void findById() {
    Property property = propertyRepository.findById(1000000).orElseThrow();
    assertThat(property, notNullValue());
    assertThat(property.uuid(), equalTo(UUID.fromString("00000000-0000-0000-0000-000000000001")));
  }

  @Test
  void findByExternalId() {
    Property property = propertyRepository.findByExternalId(
        UUID.fromString("00000000-0000-0000-0000-000000000001")).orElseThrow();
    assertThat(property, notNullValue());
    assertThat(property.uuid(), equalTo(UUID.fromString("00000000-0000-0000-0000-000000000001")));
  }

  @Test
  void findFilteredByNameContainsIgnoreCase() {
    Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Order.asc("name")));
    FilterCriteria filter = FilterCriteria.of(
        FilterCondition.of("name", "containsIgnoreCase", "flat"));
    Page<Property> page = propertyRepository.find(filter, pageable);
    assertThat(page, iterableWithSize(1));
    Property property = page.getContent().get(0);
    assertThat(property, notNullValue());
    assertThat(property.name(), equalTo("my flat"));
  }
}
