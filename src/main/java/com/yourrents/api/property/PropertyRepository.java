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


import static com.yourrents.api.jooq.tables.Property.PROPERTY;
import static org.jooq.Records.mapping;

import com.yourrents.api.jooq.tables.records.PropertyRecord;
import com.yourrents.services.common.searchable.Searchable;
import com.yourrents.services.common.util.exception.DataNotFoundException;
import com.yourrents.services.common.util.jooq.JooqUtils;
import com.yourrents.services.geodata.repository.AddressRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record7;
import org.jooq.Select;
import org.jooq.SelectJoinStep;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional(readOnly = true)
public class PropertyRepository {

  private final AddressRepository addressRepository;
  private final DSLContext dsl;
  private final JooqUtils jooqUtils;

  public PropertyRepository(AddressRepository addressRepository, DSLContext dsl,
      JooqUtils jooqUtils) {
    this.addressRepository = addressRepository;
    this.dsl = dsl;
    this.jooqUtils = jooqUtils;
  }


  @Transactional(readOnly = false)
  public Property add(Property property) {
    UUID addressUuid = null;
    if (property.addressUuid() != null) {
      addressRepository.findByExternalId(property.addressUuid())
          .orElseThrow(() -> new IllegalArgumentException(
              "cannot find address with uuid: " + property.addressUuid()));
      addressUuid = property.addressUuid();
    }
    PropertyRecord newProperty = dsl.newRecord(PROPERTY);
    newProperty.setName(property.name());
    newProperty.setType(property.type());
    newProperty.setDescription(property.description());
    if (property.yearOfBuild() != null && property.yearOfBuild() < 0) {
      throw new IllegalArgumentException("yearOfBuild must be a positive integer");
    }
    newProperty.setYearOfBuild(property.yearOfBuild());
    if (property.sizeMq() != null && property.sizeMq() < 0) {
      throw new IllegalArgumentException("sizeMq must be a positive integer");
    }
    newProperty.setSizeMq(property.sizeMq());
    newProperty.setAddressId(addressUuid);
    newProperty.insert();
    return findById(newProperty.getId()).orElseThrow();
  }

  @Transactional(readOnly = false)
  public boolean delete(UUID uuid) {
    Integer propertyId = dsl.select(PROPERTY.ID)
        .from(PROPERTY)
        .where(PROPERTY.EXTERNAL_ID.eq(uuid))
        .fetchOptional(PROPERTY.ID).orElseThrow(
            () -> new DataNotFoundException("Property not found: " + uuid));
    return dsl.deleteFrom(PROPERTY)
        .where(PROPERTY.ID.eq(propertyId))
        .execute() > 0;
  }

  @Transactional(readOnly = false)
  public Property update(UUID uuid, Property property) {
    PropertyRecord propertyRecord = dsl.selectFrom(PROPERTY)
        .where(PROPERTY.EXTERNAL_ID.eq(uuid))
        .fetchOptional().orElseThrow(
            () -> new DataNotFoundException("Record not found: " + uuid));
    if (property.addressUuid() != null) {
      addressRepository.findByExternalId(property.addressUuid())
          .orElseThrow(() -> new IllegalArgumentException(
              "cannot find address with uuid: " + property.addressUuid()));
      propertyRecord.setAddressId(property.addressUuid());
    }
    if (property.name() != null) {
      propertyRecord.setName(property.name());
    }
    if (property.type() != null) {
      propertyRecord.setType(property.type());
    }
    if (property.description() != null) {
      propertyRecord.setDescription(property.description());
    }
    if (property.yearOfBuild() != null) {
      if (property.yearOfBuild() < 0) {
        throw new IllegalArgumentException("yearOfBuild must be a positive integer");
      }
      propertyRecord.setYearOfBuild(property.yearOfBuild());
    }
    if (property.sizeMq() != null) {
      if (property.sizeMq() < 0) {
        throw new IllegalArgumentException("sizeMq must be a positive integer");
      }
      propertyRecord.setSizeMq(property.sizeMq());
    }
    propertyRecord.update();
    return findById(propertyRecord.getId())
        .orElseThrow(() -> new RuntimeException("failed to update property: " + uuid));
  }

  public Optional<Property> findById(Integer id) {
    return getSelectPropertySpec()
        .where(PROPERTY.ID.eq(id))
        .fetchOptional()
        .map(mapping(Property::new));
  }

  public Optional<Property> findByExternalId(UUID externalId) {
    return getSelectPropertySpec()
        .where(PROPERTY.EXTERNAL_ID.eq(externalId))
        .fetchOptional()
        .map(mapping(Property::new));
  }

  public Page<Property> find(Searchable filter, Pageable pageable) {
    Select<?> result = jooqUtils.paginate(
        dsl,
        jooqUtils.getQueryWithConditionsAndSorts(getSelectPropertySpec(),
            filter, this::getSupportedSortField,
            pageable, this::getSupportedSortField),
        pageable.getPageSize(), pageable.getOffset());
    List<Property> countries = result.fetch(r ->
        new Property(
            r.get("uuid", UUID.class),
            r.get("name", String.class),
            r.get("type", String.class),
            r.get("description", String.class),
            r.get("yearOfBuild", Integer.class),
            r.get("sizeMq", Integer.class),
            r.get("addressUuid", UUID.class))
    );
    int totalRows = Objects.requireNonNullElse(
        result.fetchAny("total_rows", Integer.class), 0);
    return new PageImpl<>(countries, pageable, totalRows);
  }

  private SelectJoinStep<Record7<UUID, String, String, String, Integer, Integer, UUID>> getSelectPropertySpec() {
    return this.dsl.select(
            PROPERTY.EXTERNAL_ID.as("uuid"),
            PROPERTY.NAME.as("name"),
            PROPERTY.TYPE.as("type"),
            PROPERTY.DESCRIPTION.as("description"),
            PROPERTY.YEAR_OF_BUILD.as("yearOfBuild"),
            PROPERTY.SIZE_MQ.as("sizeMq"),
            PROPERTY.ADDRESS_ID.as("addressUuid"))
        .from(PROPERTY);
  }

  private Field<?> getSupportedSortField(String field) {
    return switch (field) {
      case "name" -> PROPERTY.NAME;
      default -> throw new IllegalArgumentException(
          "Unexpected value for filter/sort field: " + field);
    };
  }
}
