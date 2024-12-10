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

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.Year;
import java.util.UUID;

public record Property(UUID uuid,
                       @Size(min = 3, max = 50, message = "name must be between 3 and 50 characters") String name,
                       PropertyType type,
                       @Size(min = 5, max = 500, message = "Description must be between 5 and 500 characters")
                       String description,
                       @Min(value = 1700, message = "Year of build must not be earlier than 1700")
                       @Max(value = Year.MAX_VALUE, message = "Year of build must not exceed the current year")
                       Integer yearOfBuild,
                       @Positive(message = "Size in square meters must be greater than 0")
                       Integer sizeMq,
                       UUID addressUuid) {

}
