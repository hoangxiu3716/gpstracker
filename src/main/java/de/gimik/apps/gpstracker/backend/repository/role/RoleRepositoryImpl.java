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
package de.gimik.apps.gpstracker.backend.repository.role;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import de.gimik.apps.gpstracker.backend.model.Role;
import de.gimik.apps.gpstracker.backend.repository.CommonRepository;
import de.gimik.apps.gpstracker.backend.util.Constants;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

/**
 *
 * @author dang
 */
public class RoleRepositoryImpl extends CommonRepository implements RoleRepositoryCustom {

    @Override
    public List<Role> findAllUserRoles() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Role> query = cb.createQuery(Role.class);
        Root<Role> role = query.from(Role.class);

        return entityManager.createQuery(query).getResultList();
    }
}
