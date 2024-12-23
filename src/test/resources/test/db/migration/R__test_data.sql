---
-- #%L
-- YourRents GeoData Service
-- %%
-- Copyright (C) 2023 Your Rents Team
-- %%
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- 
--      http://www.apache.org/licenses/LICENSE-2.0
-- 
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-- #L%
---

--test data for address
INSERT INTO yrs_geodata.address
(id, address_line_1, address_line_2, postal_code, city_external_id, city, province_external_id,
 province, country_external_id, country, external_id)
VALUES (1000000000, '42 Rose Street', 'Apt 4B', 'W1G', null, 'Paddington', null, 'London',
        'ecda2cd6-5353-4fa0-8456-b92959ff504c', 'United Kingdom',
        '00000000-0000-0000-0000-000000000001'),
       (1000000001, 'Corso Vittorio Emanuele 45', 'Interno 5', '00186', null, 'Fiumicino',
        '9d0723cb-8974-4b16-b30f-448346492da8', 'Roma', '27d46902-f762-4428-9a34-65eccd28ab51',
        'Italy', '00000000-0000-0000-0000-000000000002'),
       (1000000002, 'Piazza Venezia 12', 'scala 44', '00187', null, 'Roma',
        '9d0723cb-8974-4b16-b30f-448346492da8', 'Roma', '27d46902-f762-4428-9a34-65eccd28ab51',
        'Italy', '00000000-0000-0000-0000-000000000003'),
       (1000000003, 'Via delle Magnolie 12', 'Scala A', '20121', null, 'Bresso',
        '16541241-c451-49f0-bae8-937dbf4c5c59', 'Milano', '27d46902-f762-4428-9a34-65eccd28ab51',
        'Italy', '00000000-0000-0000-0000-000000000004');

--test data for global schema

INSERT INTO global.tenant (id, name, external_id)
VALUES (10000000, 'Demo Tenant', '00000000-0000-0000-0000-000000000001');

INSERT INTO global.tenant_user (id, tenant_id, account_id)
VALUES (10000001, 10000000, '00000000-0000-0000-0000-000000000002');


--test data for tenant
CREATE SCHEMA "00000000-0000-0000-0000-000000000001";

SET search_path TO "00000000-0000-0000-0000-000000000001", global, yrs_geodata;

CREATE TABLE property (
    id SERIAL,
    name character varying(256) NOT NULL,
    address_id UUID,
    year_of_build integer,
    type global.property_type,
    description text,
    size_mq integer,
    external_id UUID NOT NULL UNIQUE DEFAULT gen_random_uuid()
);

ALTER TABLE ONLY property
    ADD CONSTRAINT property_pkey PRIMARY KEY (id);

--test data for properties
INSERT INTO property (id, name, description, external_id,
                      address_id, year_of_build, size_mq, type)
              VALUES (1000000, 'my flat', 'residential flat', '00000000-0000-0000-0000-000000000001',
                      null, 1971, 100, 'Apartment'),
                     (1000001, 'my house', null, '00000000-0000-0000-0000-000000000002',
                      null, null, 45, null),
                     (1000002, 'penthouse', null, '00000000-0000-0000-0000-000000000003',
                      '00000000-0000-0000-0000-000000000004', 1980, null, null);