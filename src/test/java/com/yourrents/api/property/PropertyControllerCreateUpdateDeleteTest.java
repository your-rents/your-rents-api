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
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.yourrents.api.TestYourRentsApiApplication;
import com.yourrents.api.security.PrincipalAccessor;
import com.yourrents.services.geodata.repository.AddressRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(TestYourRentsApiApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class PropertyControllerCreateUpdateDeleteTest {


  static final String ADDRESS_UUID = "00000000-0000-0000-0000-000000000001";

  static final String ACCOUNT_UUID = "00000000-0000-0000-0000-000000000002";

  static final String PROPERTY_URL = "/properties";


  @Autowired
  MockMvc mvc;
  @Autowired
  PropertyRepository propertyRepository;
  @Autowired
  AddressRepository addressRepository;
  @Value("${yrs-api.api.basepath}")
  String basePath;

  @MockitoBean
  private PrincipalAccessor principalAccessor;

  @BeforeEach
  void setUp() {
    Mockito.when(principalAccessor.getSubject()).thenReturn(ACCOUNT_UUID);
  }

  @Test
  void createNewProperty() throws Exception {
    mvc.perform(post(basePath + PROPERTY_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                   "name": "My House in London",
                   "type": "APARTMENT",
                   "description": "vacation real estate",
                   "yearOfBuild": 2015,
                   "sizeMq": 90,
                   "landRegistry": "{\\"country\\": \\"IT\\", \\"foglio\\": \\"AD/61\\" }",
                   "addressUuid": "%s"
                }
                """.formatted(ADDRESS_UUID)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name", is("My House in London")))
        .andExpect(jsonPath("$.uuid").isNotEmpty())
        .andExpect(jsonPath("$.type", is(PropertyType.APARTMENT.name())))
        .andExpect(jsonPath("$.landRegistry").isNotEmpty())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

  }

  @Test
  void createNewPropertyWIthInvalidAddress() throws Exception {
    UUID randomUUID = UUID.randomUUID();
    mvc.perform(post(basePath + PROPERTY_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                   "name": "fake property",
                   "addressUuid": "%s"
                }
                """.formatted(randomUUID)))
        .andExpect(status().is4xxClientError())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void updateAnExistingProperty() throws Exception {
    UUID propertyUuid = propertyRepository.findById(1000000).map(Property::uuid).orElseThrow();
    mvc.perform(patch(basePath + PROPERTY_URL + "/" + propertyUuid)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                   "name": "updated property",
                   "addressUuid": "%s"
                }
                """.formatted(ADDRESS_UUID)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", is("updated property")))
        .andExpect(jsonPath("$.addressUuid", is(ADDRESS_UUID.toString())))
        .andExpect(jsonPath("$.description", is("residential flat")));
  }

  @Test
  void updateAPropertyWIthAnInvalidAddress() throws Exception {
    UUID propertyUuid = propertyRepository.findById(1000000).map(Property::uuid).orElseThrow();
    UUID randomUUID = UUID.randomUUID();
    mvc.perform(patch(basePath + PROPERTY_URL + "/" + propertyUuid)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "addressUuid": "%s"
                }
                """.formatted(randomUUID)))
        .andExpect(status().is4xxClientError())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void deleteAnExistingProperty() throws Exception {
    UUID propertyUuid = propertyRepository.findById(1000000).map(Property::uuid).orElseThrow();
    mvc.perform(delete(basePath + PROPERTY_URL + "/" + propertyUuid).contentType(
            MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
    assertThat(propertyRepository.findById(1000000).isPresent(), is(false));
  }

  @Test
  void deleteANotExistingProperty() throws Exception {
    UUID randomUUID = UUID.randomUUID();
    mvc.perform(
            delete(basePath + PROPERTY_URL + "/" + randomUUID).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

}
