/*
 * Copyright 2015 trung.
 *
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
 */

package de.gimik.apps.gpstracker.backend.repository.employees;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.gimik.apps.gpstracker.backend.model.Employees;

import java.util.List;
import java.util.Map;

/**
 * @author trung
 */
public interface EmployeesRepositoryCustom {
	Page<Employees> findAll(Pageable pageable, Map<String, String> filter, String type);

	Page<Employees> findAllByChoice(Pageable pageable, Map<String, String> filter);
}
