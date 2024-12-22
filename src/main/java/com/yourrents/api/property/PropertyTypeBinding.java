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

import java.sql.SQLException;
import java.sql.Types;
import org.jooq.BindingGetResultSetContext;
import org.jooq.BindingSQLContext;
import org.jooq.BindingSetStatementContext;
import org.jooq.Converter;
import org.jooq.impl.AbstractBinding;
import org.jooq.impl.EnumConverter;

/**
 * A custom Binding for PostgreSQL ENUM types.
 */
public class PropertyTypeBinding extends AbstractBinding<Object, PropertyType> {
  private final Converter<Object, PropertyType> converter;

  static final String GLOBAL_PROPERTY_TYPE_SQL = "?::global.property_type";


  public PropertyTypeBinding() {
    this.converter = new EnumConverter<>(Object.class, PropertyType.class){
      @Override
      public Object to(PropertyType propertyType) {
        return propertyType.getTypeName();
      }
    };
  }

  @Override
  public Converter<Object, PropertyType> converter() {
    return this.converter;
  }


  @Override
  public void sql(BindingSQLContext<PropertyType> ctx) {
    // Add the SQL cast for PostgreSQL ENUM type
    ctx.render().sql(GLOBAL_PROPERTY_TYPE_SQL);
  }

  @Override
  public void set(BindingSetStatementContext<PropertyType> ctx) throws SQLException {
    // Use the converter to transform the value to String and set it in the PreparedStatement
    if (ctx.value() == null) {
      ctx.statement().setNull(ctx.index(), Types.VARCHAR); // Set null if the value is null
    } else {
      Object value = converter().to(ctx.value()); // Convert PropertyType to String
      ctx.statement()
          .setString(ctx.index(), (String) value); // Set the String value in the PreparedStatement
    }
  }

  @Override
  public void get(BindingGetResultSetContext<PropertyType> ctx) throws SQLException {
    // Retrieve the value from the ResultSet and convert it to PropertyType
    String value = ctx.resultSet()
        .getString(ctx.index()); // Get the String value from the ResultSet
    ctx.value(converter().from(value)); // Convert the String to PropertyType and set it
  }
}




