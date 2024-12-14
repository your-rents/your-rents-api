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

import com.yourrents.api.exception.ValidationGroups;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record Property(UUID uuid,

                       @NotNull(message = NAME_NOT_NULL_CONSTRAINT, groups = ValidationGroups.Post.class)
                       @Size(min = 3, max = 50, message = NAME_CONSTRAINT, groups = {
                           ValidationGroups.Post.class, ValidationGroups.Patch.class})
                       String name,

                       PropertyType type,

                       @Size(min = 5, max = 50, message = DESCRIPTION_CONSTRAINT, groups = {
                           ValidationGroups.Post.class, ValidationGroups.Patch.class})
                       String description,

                       @Min(value = 1000, message = YOB_MIN_CONSTRAINT,
                           groups = {ValidationGroups.Post.class, ValidationGroups.Patch.class})
                       @Max(value = 2100, message = YOB_MAX_CONSTRAINT,
                           groups = {ValidationGroups.Post.class, ValidationGroups.Patch.class})
                       Integer yearOfBuild,

                       @Positive(message = SIZE_MQ_CONSTRAINT,
                           groups = {ValidationGroups.Post.class, ValidationGroups.Patch.class})
                       Integer sizeMq,

                       UUID addressUuid) {

  static final String NAME_NOT_NULL_CONSTRAINT = "must not be null";
  static final String NAME_CONSTRAINT = "name must be between 3 and 50 characters";
  static final String DESCRIPTION_CONSTRAINT = "description must be between 5 and 50 characters";
  static final String YOB_MIN_CONSTRAINT = "Year of build must not be earlier than 1000";
  static final String YOB_MAX_CONSTRAINT = "Year of build must not exceed 2100";
  static final String SIZE_MQ_CONSTRAINT = "Size in square meters must be greater than 0";

}
