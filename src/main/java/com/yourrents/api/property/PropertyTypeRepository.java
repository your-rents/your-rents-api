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

import static com.yourrents.api.jooq.global.Tables.PROPERTY_TYPE;
import static org.jooq.Records.mapping;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jooq.DSLContext;
import org.jooq.Record4;
import org.jooq.SelectJoinStep;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
class PropertyTypeRepository {

  private final DSLContext dsl;

  PropertyTypeRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  /**
   * Return, if present, the property_type record identified by id
   */
  Optional<PropertyType> findById(Integer id) {
    return selectJoinStep()
        .where(PROPERTY_TYPE.ID.eq(id))
        .fetchOptional()
        .map(mapping(PropertyType::new));
  }

  /**
   * Return, if present, the property_type record identified by externalId
   */
  Optional<PropertyType> findByExternalId(UUID externalId) {
    return selectJoinStep()
        .where(PROPERTY_TYPE.EXTERNAL_ID.eq(externalId))
        .fetchOptional()
        .map(mapping(PropertyType::new));
  }

  /**
   * Return all the property_type records sorted by id asc.
   */
  List<PropertyType> findAll() {
    return selectJoinStep()
        .orderBy(PROPERTY_TYPE.ID.asc()) // Apply sorting here
        .fetch()
        .map(mapping(PropertyType::new));
  }

  private SelectJoinStep<Record4<UUID, String, String, String>> selectJoinStep() {
    return dsl
        .select(
            PROPERTY_TYPE.EXTERNAL_ID.as("uuid"),
            PROPERTY_TYPE.NAME.as("name"),
            PROPERTY_TYPE.CODE.as("code"),
            PROPERTY_TYPE.DESCRIPTION.as("description")
        )
        .from(PROPERTY_TYPE);
  }
}
