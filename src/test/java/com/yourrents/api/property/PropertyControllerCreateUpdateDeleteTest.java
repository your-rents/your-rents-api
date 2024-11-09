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
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yourrents.api.TestYourRentsApiApplication;
import com.yourrents.api.security.PrincipalAccessor;
import com.yourrents.services.geodata.model.Address;
import com.yourrents.services.geodata.repository.AddressRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(TestYourRentsApiApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class PropertyControllerCreateUpdateDeleteTest {


  final static String PROPERTY_URL = "/properties";

  static final String ACCOUNT_ID = "00000000-0000-0000-0000-000000000002";

  @Autowired
  MockMvc mvc;
  @Autowired
  PropertyRepository propertyRepository;
  @Autowired
  AddressRepository addressRepository;
  @Value("${yrs-api.api.basepath}")
  String basePath;

  @MockBean
  private PrincipalAccessor principalAccessor;

  @BeforeEach
  void setUp() {
    Mockito.when(principalAccessor.getSubject()).thenReturn(ACCOUNT_ID);
  }

  @Test
  void createNewProperty() throws Exception {
    mvc.perform(post(basePath + PROPERTY_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                   "name": "My House in London",
                   "type": "residential flat",
                   "description": "vacation real estate",
                   "yearOfBuild": 2015,
                   "sizeMq": 90,
                   "addressUuid": "00000000-0000-0000-0000-000000000001"
                }
                """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name", is("My House in London")))
        .andExpect(jsonPath("$.uuid").isNotEmpty())
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
    UUID addressUuid = addressRepository.findById(1000000000).map(Address::uuid).orElseThrow();
    Property property = propertyRepository.findById(1000000).orElseThrow();
    assertThat(property.addressUuid(), nullValue());
    mvc.perform(patch(basePath + PROPERTY_URL + "/" + property.uuid())
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                   "name": "updated property",
                   "addressUuid": "%s"
                }
                """.formatted(addressUuid)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", is("updated property")))
        .andExpect(jsonPath("$.addressUuid", is(addressUuid.toString())))
        .andExpect(jsonPath("$.description", is("residential flat")));
  }

  @Test
  void updateAPropertyWIthAnInvalidAddress() throws Exception {
    UUID randomUUID = UUID.randomUUID();
    mvc.perform(patch(basePath + PROPERTY_URL + "/" + randomUUID)
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
    Property property = propertyRepository.findById(1000000).orElseThrow();
    mvc.perform(delete(basePath + PROPERTY_URL + "/" + property.uuid()).contentType(
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


