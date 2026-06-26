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
package de.gimik.apps.gpstracker.backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import de.gimik.apps.gpstracker.backend.model.Role;
import de.gimik.apps.gpstracker.backend.model.User;
import de.gimik.apps.gpstracker.backend.repository.role.RoleRepository;
import de.gimik.apps.gpstracker.backend.web.viewmodel.user.UserInputInfo;
import de.gimik.apps.gpstracker.backend.service.CommonService;
import de.gimik.apps.gpstracker.backend.service.RoleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dang
 */
@Service
@Transactional
public class RoleServiceImpl extends CommonService implements RoleService {

    @Autowired
    RoleRepository roleRepository;


    @Override
    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    @Override
    public List<Role> getAllUserRoles() {
        return roleRepository.findAllUserRoles();
    }

    @Override
    public Role getByID(long roleID) {
        return roleRepository.findOne(roleID);
    }
//    @Override
//    public void deleteByUser(User user) {
//         roleRepository.deleteByUser(user);
//    }

    @Override
    public Role getByName(String name) {
        return roleRepository.findByName(name);
    }
    @Override
    public  List<Role> parseRoles( Map<String, Boolean> roleMap) {
        if (roleMap != null && !roleMap.isEmpty()) {
            List<Role> roles = new ArrayList<>();

            for (Map.Entry<String, Boolean> roleMapEntry : roleMap.entrySet()) {
                if (roleMapEntry.getValue()) {
                    String roleName = roleMapEntry.getKey();
                    Role role = getByName(roleName);

                    if (role != null) {
                        roles.add(role);
                    }
                }
            }

            return roles;
        }

        return null;
    }
}
