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


import static com.yourrents.api.jooq.tenant.tables.Property.PROPERTY;
import static org.jooq.Records.mapping;

import com.yourrents.api.jooq.tenant.tables.records.PropertyRecord;
import com.yourrents.api.tenant.JooqTenantService;
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
import org.jooq.Record8;
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
  private final JooqUtils jooqUtils;
  private final JooqTenantService jst;

  public PropertyRepository(AddressRepository addressRepository, JooqUtils jooqUtils,
      JooqTenantService jst) {
    this.addressRepository = addressRepository;
    this.jooqUtils = jooqUtils;
    this.jst = jst;
  }


  @Transactional(readOnly = false)
  public Property add(Property property) {
    DSLContext dsl = jst.getDslForTenant();
    UUID addressUuid = null;
    if (property.addressUuid() != null) {
      addressRepository.findByExternalId(property.addressUuid())
          .orElseThrow(() -> new DataNotFoundException(
              "cannot find address with uuid: " + property.addressUuid()));
      addressUuid = property.addressUuid();
    }
    PropertyRecord newProperty = dsl.newRecord(PROPERTY);
    newProperty.setType(property.type());
    newProperty.setName(property.name());
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
    DSLContext dsl = jst.getDslForTenant();
    Integer propertyId = dsl.select(PROPERTY.ID)
        .from(PROPERTY)
        .where(PROPERTY.EXTERNAL_ID.eq(uuid))
        .fetchOptional(PROPERTY.ID).orElseThrow(
            () -> new DataNotFoundException("cannot find property with uuid: " + uuid));
    return dsl.deleteFrom(PROPERTY)
        .where(PROPERTY.ID.eq(propertyId))
        .execute() > 0;
  }

  @Transactional(readOnly = false)
  public Property update(UUID uuid, Property property) {
    DSLContext dsl = jst.getDslForTenant();
    PropertyRecord propertyRecord = dsl.selectFrom(PROPERTY)
        .where(PROPERTY.EXTERNAL_ID.eq(uuid))
        .fetchOptional().orElseThrow(
            () -> new DataNotFoundException("Record not found: " + uuid));
    if (property.addressUuid() != null) {
      addressRepository.findByExternalId(property.addressUuid())
          .orElseThrow(() -> new DataNotFoundException(
              "cannot find address with uuid: " + property.addressUuid()));
      propertyRecord.setAddressId(property.addressUuid());
    }

    if (property.type() != null) {
      propertyRecord.setType(property.type());
    }
    if (property.name() != null) {
      propertyRecord.setName(property.name());
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
    DSLContext dsl = jst.getDslForTenant();
    return getSelectPropertySpec(dsl)
        .where(PROPERTY.ID.eq(id))
        .fetchOptional()
        .map(mapping(Property::new));
  }

  public Optional<Property> findByExternalId(UUID externalId) {
    DSLContext dsl = jst.getDslForTenant();
    return getSelectPropertySpec(dsl)
        .where(PROPERTY.EXTERNAL_ID.eq(externalId))
        .fetchOptional()
        .map(mapping(Property::new));
  }

  public Page<Property> find(Searchable filter, Pageable pageable) {
    DSLContext dsl = jst.getDslForTenant();
    Select<?> result = jooqUtils.paginate(
        dsl,
        jooqUtils.getQueryWithConditionsAndSorts(getSelectPropertySpec(dsl),
            filter, this::getSupportedSortField,
            pageable, this::getSupportedSortField),
        pageable.getPageSize(), pageable.getOffset());
    List<Property> properties = result.fetch(r ->
        new Property(
            r.get("uuid", UUID.class),
            r.get("name", String.class),
            r.get("type", PropertyType.class),
            r.get("description", String.class),
            r.get("yearOfBuild", Integer.class),
            r.get("sizeMq", Integer.class),
            r.get("landRegistry", String.class),
            r.get("addressUuid", UUID.class))
    );
    int totalRows = Objects.requireNonNullElse(
        result.fetchAny("total_rows", Integer.class), 0);
    return new PageImpl<>(properties, pageable, totalRows);
  }



  private SelectJoinStep<Record8<UUID, String, PropertyType, String, Integer, Integer, String, UUID>> getSelectPropertySpec(
      DSLContext tenantDsl) {
    return tenantDsl
        .select(
            PROPERTY.EXTERNAL_ID.as("uuid"),
            PROPERTY.NAME.as("name"),
            PROPERTY.TYPE.as("type"),

            PROPERTY.DESCRIPTION.as("description"),
            PROPERTY.YEAR_OF_BUILD.as("yearOfBuild"),
            PROPERTY.SIZE_MQ.as("sizeMq"),
            PROPERTY.LAND_REGISTRY.cast(String.class).as("landRegistry"),
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
