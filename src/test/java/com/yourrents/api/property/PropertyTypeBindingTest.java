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

import static com.yourrents.api.property.PropertyTypeBinding.GLOBAL_PROPERTY_TYPE_SQL;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.jooq.BindingGetResultSetContext;
import org.jooq.BindingSQLContext;
import org.jooq.BindingSetStatementContext;
import org.jooq.RenderContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PropertyTypeBindingTest {
  PropertyTypeBinding instance;

  @BeforeEach
  void setUp() {
    this.instance = new PropertyTypeBinding();
  }

  @Test
  void sql() {
    //given
    @SuppressWarnings("unchecked")
    BindingSQLContext<PropertyType> mockBindingSQLContext = mock(BindingSQLContext.class);
    RenderContext renderContext =  mock(RenderContext.class);
    when(mockBindingSQLContext.render()).thenReturn(renderContext);
    //when
    instance.sql(mockBindingSQLContext);
    // then
    verify(renderContext).sql(GLOBAL_PROPERTY_TYPE_SQL);
  }

  @Test
  void set() throws SQLException {
    //given
    @SuppressWarnings("unchecked")
    BindingSetStatementContext<PropertyType> ctx = mock(BindingSetStatementContext.class);
    PreparedStatement statement = mock(PreparedStatement.class);
    PropertyType propertyType = PropertyType.VILLA;
    when(ctx.statement()).thenReturn(statement);
    when(ctx.index()).thenReturn(1);
    when(ctx.value()).thenReturn(propertyType);
    //when
    instance.set(ctx);
    //then
    verify(statement).setString(1, propertyType.getTypeName());
  }

  @Test
  void get() throws SQLException {
    //given
    @SuppressWarnings("unchecked")
    BindingGetResultSetContext<PropertyType> ctx = mock(BindingGetResultSetContext.class);
    ResultSet resultSet = mock(ResultSet.class);
    when(ctx.resultSet()).thenReturn(resultSet);
    when(ctx.index()).thenReturn(1);
    when(resultSet.getString(1)).thenReturn(PropertyType.APARTMENT.getTypeName());
    //when
    instance.get(ctx);
    //then
    verify(ctx).value(PropertyType.APARTMENT);
  }
}
