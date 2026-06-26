/*

 * Copyright 2014 trung.
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

import java.util.List;

import java.util.Map;







import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

import de.gimik.apps.gpstracker.backend.model.User;
import de.gimik.apps.gpstracker.backend.security.DefaultUserDetails;

/**
 *
 * @author trung
 */
public interface UserService extends UserDetailsService{
	
    User save(RemoteClientInfo clientInfo, User user);
//    Page<User> getAll(int pageIndex, int pageSize, String sortField, String sortDirection, Map<String, String> filter);
    Page<User> getAll(int pageIndex, int pageSize, String sortField, String sortDirection);
    User findByUsername(String username);
    User addNewUser(RemoteClientInfo clientInfo, User user);
    void delete(RemoteClientInfo clientInfo, int userID);
    User getByID(int userID);
	User changePass(RemoteClientInfo clientInfo, User user);
	DefaultUserDetails findByUsernameAndPassword(String username, String password);
	List<User> findByIdIn(List<Integer> ids);
	Page<User> getAll(int pageIndex, int pageSize, String sortField, String sortDirection, Map<String, String> filter);
	User findById(Integer id);

	List<User> getAllEmployee();
    List<User> getAllUser();
}
