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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yourrents.api.TestYourRentsApiApplication;
import com.yourrents.services.geodata.repository.AddressRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@Import(TestYourRentsApiApplication.class)
@AutoConfigureMockMvc(addFilters = false)
class PropertyControllerTest {

  static final int NUM_PROPERTIES = 3;

  final static String PROPERTY_URL = "/properties";
  @Autowired
  MockMvc mvc;
  @Autowired
  PropertyRepository propertyRepository;
  @Autowired
  AddressRepository addressRepository;
  @Value("${yrs-api.api.basepath}")
  String basePath;

  @Test
  void findAll() throws Exception {
    mvc.perform(get(basePath + PROPERTY_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .param("page", "0")
            .param("size", "2147483647"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content", hasSize(NUM_PROPERTIES)))
        .andExpect(jsonPath("$.content[0].name", is("my flat")))
        .andExpect(jsonPath("$.content[1].name", is("my house")))
        .andExpect(jsonPath("$.content[2].name", is("penthouse")))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$.totalElements", is(NUM_PROPERTIES)))
        .andExpect(jsonPath("$.last", is(true)))
        .andExpect(jsonPath("$.first", is(true)))
        .andExpect(jsonPath("$.size", is(2147483647)))
        .andExpect(jsonPath("$.number", is(0)))
        .andExpect(jsonPath("$.numberOfElements", is(NUM_PROPERTIES)))
        .andExpect(jsonPath("$.empty", is(false)))
        .andExpect(jsonPath("$.sort.sorted", is(true)));
  }


  @Test
  void findByUuid() throws Exception {
    String uUID = "00000000-0000-0000-0000-000000000001";
    mvc.perform(get(basePath + PROPERTY_URL + "/" + uUID).contentType(
            MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name", is("my flat")));
  }

  @Test
  void findByNameDescAndSizeOne() throws Exception {
    mvc.perform(get(basePath + PROPERTY_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .param("page", "0")
            .param("sort", "name,DESC")
            .param("size", "1"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].name", is("penthouse")))
        .andExpect(jsonPath("$.totalPages", is(3)))
        .andExpect(jsonPath("$.totalElements", is(NUM_PROPERTIES)))
        .andExpect(jsonPath("$.last", is(false)))
        .andExpect(jsonPath("$.first", is(true)))
        .andExpect(jsonPath("$.size", is(1)))
        .andExpect(jsonPath("$.number", is(0)))
        .andExpect(jsonPath("$.numberOfElements", is(1)))
        .andExpect(jsonPath("$.empty", is(false)))
        .andExpect(jsonPath("$.sort.sorted", is(true)));
  }

}
