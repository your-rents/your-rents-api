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

public enum PropertyType {
  APARTMENT("Apartment", "A self-contained housing unit occupying part of a building."),
  HOUSE("House", "A standalone building intended for residential use."),
  CONDOMINIUM("Condominium",
      "A unit in a building owned individually but with shared common areas."),
  VILLA("Villa", "A luxurious standalone property, often with a garden or pool."),
  TOWNHOUSE("Townhouse", "A row or terrace house sharing walls with adjacent units."),
  PENTHOUSE("Penthouse", "An upscale apartment located on the top floor of a building."),
  DUPLEX("Duplex", "A property divided into two separate units."),
  STUDIO("Studio", "A compact apartment with a single main room."),
  LOFT("Loft", "An open-plan living space, often converted from an industrial building."),
  FARMHOUSE("Farmhouse", "A residential property located on agricultural land.");

  private final String typeName;
  private final String description;

  PropertyType(String typeName, String description) {
    this.typeName = typeName;
    this.description = description;
  }

  /**
   * Returns the corresponding enum constant for a given database value. If no match is found,
   * throws an IllegalArgumentException.
   */
  public static PropertyType fromDatabaseValue(String dbValue) {
    if (dbValue == null) {
      throw new IllegalArgumentException("Property type value cannot be null.");
    }
    for (PropertyType type : values()) {
      if (type.name().equalsIgnoreCase(dbValue)) {
        return type;
      }
    }
    throw new IllegalArgumentException("No enum found for value: " + dbValue);
  }

  public String getTypeName() {
    return typeName;
  }

  public String getDescription() {
    return description;
  }
}
