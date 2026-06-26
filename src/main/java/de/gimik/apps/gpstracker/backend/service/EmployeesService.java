/*
 * Copyright 2014 dang.
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

package de.gimik.apps.gpstracker.backend.service;

import java.util.Map;













import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

import de.gimik.apps.gpstracker.backend.model.Employees;
import de.gimik.apps.gpstracker.backend.model.User;



/**
 *
 * @author trung
 */
public interface EmployeesService {

	Employees findById(Integer id);

	Employees findByIdAndUser(Integer id, User user);

	Employees findByUser(User user);

	Page<Employees> findAllByChoice(int pageIndex, int pageSize, String sortField,
			String sortDirection, Map<String, String> filter);

	void create(RemoteClientInfo clientInfo, Employees item);

	void update(RemoteClientInfo clientInfo, Employees item);

	void delete(RemoteClientInfo clientInfo, Employees item);

	Page<Employees> findAll(int pageIndex, int pageSize, String sortField, String sortDirection,
			Map<String, String> filter, String type);


}
