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

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
class PropertyControllerCreateUpdateValidationTest {


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
  void createNewPropertyWithNullName() throws Exception {
    mvc.perform(post(basePath + PROPERTY_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "type": "%s"
                }
                """.formatted(PropertyType.APARTMENT.name())
            ))
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.name", is(Property.NAME_NOT_NULL_CONSTRAINT)))
        .andDo(print())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }


  @Test
  void createNewPropertyWithInvalidNameAndInvalidDescription() throws Exception {
    mvc.perform(post(basePath + PROPERTY_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                "name" : "my",
                "description" : "ds"
                }
                 """
            ))
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.name", is(Property.NAME_CONSTRAINT)))
        .andExpect(jsonPath("$.description", is(Property.DESCRIPTION_CONSTRAINT)))
        //.andDo(print())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void updateAnExistingPropertyWithAnInvalidYearOfBuild() throws Exception {
    UUID propertyUuid = propertyRepository.findById(1000000).map(Property::uuid).orElseThrow();
    mvc.perform(patch(basePath + PROPERTY_URL + "/" + propertyUuid)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                   "yearOfBuild": 999
                }
                """))
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.yearOfBuild", is(Property.YOB_MIN_CONSTRAINT)))
        //.andDo(print())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void updateAnExistingPropertyWithAnInvalidSizeMq() throws Exception {
    UUID propertyUuid = propertyRepository.findById(1000000).map(Property::uuid).orElseThrow();
    mvc.perform(patch(basePath + PROPERTY_URL + "/" + propertyUuid)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                   "sizeMq": -10
                }
                """))
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.sizeMq", is(Property.SIZE_MQ_CONSTRAINT)))
        //.andDo(print())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }


}


