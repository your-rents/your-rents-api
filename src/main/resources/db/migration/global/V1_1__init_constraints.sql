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


ALTER TABLE ONLY global.tenant
    ADD CONSTRAINT tenant_pkey PRIMARY KEY (id);

ALTER TABLE ONLY global.tenant_user
    ADD CONSTRAINT tenant_user_pkey PRIMARY KEY (id);

ALTER TABLE ONLY global.property_type
  ADD CONSTRAINT property_type_pkey PRIMARY KEY (id);

-- Add foreign key constraints

ALTER TABLE ONLY global.tenant_user
    ADD CONSTRAINT tenant_user_manager_id_fkey FOREIGN KEY (tenant_id) REFERENCES global.tenant(id);
