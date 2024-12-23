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

import com.yourrents.api.common.GenericController;
import com.yourrents.api.common.GenericJooqRepository;
import java.util.List;
import java.util.UUID;
import org.jooq.Record4;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${yrs-api.api.basepath}/propertytypes")
class PropertyTypeController extends
    GenericController<PropertyType> {


  public PropertyTypeController(
      GenericJooqRepository<PropertyType, Record4<UUID, String, String, String>> repository) {
    super(repository);
  }

  @Override
  @GetMapping
  protected ResponseEntity<List<PropertyType>> findAll() {
    return super.findAll();
  }

  @Override
  @GetMapping("/{uuid}")
  protected ResponseEntity<PropertyType> findByUuid(@PathVariable UUID uuid) {
    return super.findByUuid(uuid);
  }

  @Override
  @DeleteMapping("/{uuid}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  protected void delete(@PathVariable UUID uuid) {
    super.delete(uuid);
  }


}
