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
class PropertyTypeControllerTest {

  static final int NUM_PROPERTY_TYPES = 10;

  static final String PROPERTY_TYPE_URL = "/propertytypes";

  @Autowired
  MockMvc mvc;
  @Value("${yrs-api.api.basepath}")
  String basePath;


  @Test
  void findAll() throws Exception {
    mvc.perform(get(basePath + PROPERTY_TYPE_URL)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(NUM_PROPERTY_TYPES + 1)))
        .andExpect(jsonPath("$[0].name", is("Apartment")))
        .andExpect(jsonPath("$[0].code", is("APT")))
        .andExpect(jsonPath("$[0].description",
            is("A self-contained housing unit occupying part of a building.")))
        .andExpect(jsonPath("$[0].uuid").isNotEmpty())
        .andExpect(jsonPath("$[9].name", is("Farmhouse")))
        .andExpect(jsonPath("$[9].code", is("FRMHS")))
        .andExpect(jsonPath("$[9].description",
            is("A residential property located on agricultural land.")))
        .andExpect(jsonPath("$[9].uuid").isNotEmpty());
  }
}
