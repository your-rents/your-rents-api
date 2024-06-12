package com.yourrents.api.flyway;

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

import java.util.Arrays;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.stereotype.Component;

@Component
public class MultiModuleFlywayMigrationStrategy implements FlywayMigrationStrategy {

    @Value("${spring.flyway.schemas}")
    private String[] schemas;

    @Value("${spring.flyway.locations}")
    private String[] locations;

    @Override
    public void migrate(Flyway flyway) {
        var dataSource = flyway.getConfiguration().getDataSource();

        for (int i = 0; i < schemas.length - 1; i++) {
            Flyway currentModule = Flyway.configure()
                .schemas(schemas[i])
                .locations(locations[i])
                .dataSource(dataSource).load();
            currentModule.migrate();
        }

        Flyway appModule = Flyway.configure()
            .schemas(schemas[schemas.length - 1])
            .locations(Arrays.copyOfRange(locations, schemas.length - 1, locations.length))
            .dataSource(dataSource).load();
        appModule.migrate();
    }

}
