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


import com.yourrents.services.common.searchable.Searchable;
import com.yourrents.services.common.util.exception.DataNotFoundException;
import java.util.UUID;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${yrs-api.api.basepath}/properties")
class PropertyController {

  private final PropertyRepository propertyRepository;

  PropertyController(PropertyRepository propertyRepository) {
    this.propertyRepository = propertyRepository;
  }

  @GetMapping
  ResponseEntity<Page<Property>> find(
      @ParameterObject Searchable filter,
      @ParameterObject @SortDefault(sort = "name", direction = Direction.ASC) Pageable pagination) {
    Page<Property> page = propertyRepository.find(filter, pagination);
    return ResponseEntity.ok(page);
  }

  @GetMapping("/{uuid}")
  ResponseEntity<Property> findByUuid(@PathVariable UUID uuid) {
    Property property = propertyRepository.findByExternalId(uuid)
        .orElseThrow(
            () -> new DataNotFoundException("can't find property having uuid: " + uuid));
    return ResponseEntity.ok(property);
  }

  @PostMapping
  ResponseEntity<Property> add(@RequestBody Property property) {
    Property savedProperty = propertyRepository.add(property);
    return new ResponseEntity<>(savedProperty, HttpStatus.CREATED);
  }

  @PatchMapping("/{uuid}")
  ResponseEntity<Property> update(@PathVariable UUID uuid, @RequestBody Property propertyToUpdate) {
    Property updatedProperty = propertyRepository.update(uuid, propertyToUpdate);
    return ResponseEntity.ok(updatedProperty);
  }

  @DeleteMapping("/{uuid}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void delete(@PathVariable UUID uuid) {
    propertyRepository.delete(uuid);
  }
}

