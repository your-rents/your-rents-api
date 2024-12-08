package com.yourrents.api.common;

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


import com.yourrents.services.common.util.exception.DataNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;


public abstract class GenericController<T extends java.lang.Record> {

  private final GenericJooqRepository<T, ?> repository;

  public GenericController(GenericJooqRepository<T, ?> repository) {
    this.repository = repository;
  }

  protected ResponseEntity<List<T>> findAll() {
    List<T> propertyTypes = repository.findAll();
    return ResponseEntity.ok(propertyTypes);
  }

  protected ResponseEntity<T> findByUuid(@PathVariable UUID uuid) {
    T record = repository.findByExternalId(uuid)
        .orElseThrow(
            () -> new DataNotFoundException("can't find record having uuid: " + uuid));
    return ResponseEntity.ok(record);
  }

  protected void delete(@PathVariable UUID uuid) {
    repository.delete(uuid);
  }
}
