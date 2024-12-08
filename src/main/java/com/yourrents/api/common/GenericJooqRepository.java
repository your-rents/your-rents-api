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
import java.util.Optional;
import java.util.UUID;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.RecordMapper;
import org.jooq.SelectJoinStep;
import org.springframework.transaction.annotation.Transactional;


@Transactional(readOnly = true)
public abstract class GenericJooqRepository<T extends java.lang.Record, R extends org.jooq.Record> {

  public static final String COLUMN_ID = "id";

  public static final String COLUMN_UUID = "external_id";

  protected final DSLContext dsl;

  protected GenericJooqRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public Optional<T> findById(Integer id) {
    return selectJoinStep()
        .where(getIdField().eq(id))
        .fetchOptional()
        .map(mapper());
  }

  public Optional<T> findByExternalId(UUID externalId) {
    return selectJoinStep()
        .where(getExternalIdField().eq(externalId))
        .fetchOptional()
        .map(mapper());
  }

  public List<T> findAll() {
    return selectJoinStep()
        .orderBy(getIdField().asc()) // Apply sorting here
        .fetch()
        .map(mapper());
  }

  @Transactional(readOnly = false)
  public boolean delete(UUID uuid) {
    Integer id = dsl.select(getIdField())
        .from(getTable())
        .where(getExternalIdField().eq(uuid))
        .fetchOptional(getIdField()).orElseThrow(
            () -> new DataNotFoundException("cannot find record with uuid: " + uuid));
    return dsl.deleteFrom(getTable())
        .where(getIdField().eq(id))
        .execute() > 0;
  }

  @SuppressWarnings("unchecked")
  protected Field<Integer> getIdField() {
    return (Field<Integer>) getTable().field(COLUMN_ID);
  }

  @SuppressWarnings("unchecked")
  protected Field<UUID> getExternalIdField() {
    return (Field<UUID>) getTable().field(COLUMN_UUID);
  }

  protected abstract SelectJoinStep<R> selectJoinStep();

  protected abstract RecordMapper<R, T> mapper();

  protected abstract org.jooq.Table<? extends org.jooq.Record> getTable();

}
