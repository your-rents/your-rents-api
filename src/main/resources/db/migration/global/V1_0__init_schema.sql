---
-- #%L
-- YourRents API
-- %%
-- Copyright (C) 2023 - 2024 Your Rents Team
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

CREATE TABLE global.tenant (
    id SERIAL,
    name character varying(256) NOT NULL,
    external_id UUID NOT NULL UNIQUE DEFAULT gen_random_uuid()
);

CREATE TABLE global.tenant_user (
    id SERIAL,
    tenant_id integer NOT NULL,
    account_id UUID NOT NULL
);

CREATE TYPE property_type AS ENUM (
  'Apartment', 'House', 'Condominium', 'Villa', 'Townhouse',
  'Penthouse', 'Duplex', 'Studio', 'Loft', 'Farmhouse');

