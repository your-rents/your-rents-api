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

import com.yourrents.api.common.GenericJooqRepository;
import java.util.UUID;
import org.jooq.DSLContext;
import org.jooq.Record4;
import org.jooq.RecordMapper;
import org.jooq.SelectJoinStep;
import org.springframework.stereotype.Repository;

@Repository
class PropertyTypeRepository extends
    GenericJooqRepository<PropertyType, Record4<UUID, String, String, String>> {

  public PropertyTypeRepository(DSLContext dsl) {
    super(dsl);
  }

  @Override
  public final SelectJoinStep<Record4<UUID, String, String, String>> selectJoinStep() {
    return dsl
        .select(
            PROPERTY_TYPE.EXTERNAL_ID.as("uuid"),
            PROPERTY_TYPE.NAME.as("name"),
            PROPERTY_TYPE.CODE.as("code"),
            PROPERTY_TYPE.DESCRIPTION.as("description")
        )
        .from(PROPERTY_TYPE);
  }

  @Override
  public final RecordMapper<Record4<UUID, String, String, String>, PropertyType> mapper() {
    return mapping(PropertyType::new);
  }

  @Override
  public final com.yourrents.api.jooq.global.tables.PropertyType getTable() {
    return PROPERTY_TYPE;
  }
}
