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
COPY global.property_type (id, code, name, description) FROM stdin;
1	APT	Apartment	A self-contained housing unit occupying part of a building.
2	HSE	House	A standalone building intended for residential use.
3	CONDO	Condominium	A unit in a building owned individually but with shared common areas.
4	VLL	Villa	A luxurious standalone property, often with a garden or pool.
5	TWNHS	Townhouse	A row or terrace house sharing walls with adjacent units.
6	PTH	Penthouse	An upscale apartment located on the top floor of a building.
7	DPX	Duplex	A property divided into two separate units.
8	STD	Studio	A compact apartment with a single main room.
9	LFT	Loft	An open-plan living space, often converted from an industrial building.
10	FRMHS	Farmhouse	A residential property located on agricultural land.
\.
