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
package de.gimik.apps.gpstracker.backend.repository.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import de.gimik.apps.gpstracker.backend.model.Role;
import de.gimik.apps.gpstracker.backend.model.User;
import de.gimik.apps.gpstracker.backend.repository.CommonRepository;
import de.gimik.apps.gpstracker.backend.util.Constants;

/**
 *
 * @author dang
 */
public class UserRepositoryImpl extends CommonRepository implements UserRepositoryCustom {

    @Override
    public Page<User> findAll(Pageable pageable, Map<String, String> filter) {
        CriteriaBuilder queryBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = queryBuilder.createQuery(User.class);

        Root<User> root = criteriaQuery.from(User.class);
        ListJoin<User, Role> roleJoin = root.joinList("roles", JoinType.INNER);
        //Fetch<User, Role> roles = root.fetch("roles", JoinType.LEFT);
//        Predicate[] predicatesArray = getPredicates(filter, queryBuilder, root, roleJoin);
        criteriaQuery.where(getPredicates(filter, queryBuilder, root, roleJoin,null))
                     .distinct(true);

        Sort sort = pageable.getSort();
        if (sort != null) {
            criteriaQuery.orderBy(toOrders(sort, root, queryBuilder));
        }

        TypedQuery<User> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<User> list = query.getResultList();
        if (CollectionUtils.isEmpty(list)&& pageable.getOffset()>0){
            query.setFirstResult(0);
            list = query.getResultList();
        }

        CriteriaQuery<Long> countCriteriaQuery = queryBuilder.createQuery(Long.class);
        Root<User> countRoot = countCriteriaQuery.from(User.class);
        //countRoot.fetch("roles", JoinType.LEFT);
        ListJoin<User, Role> countRoleJoin = countRoot.joinList("roles", JoinType.INNER);

        countCriteriaQuery.where(getPredicates(filter, queryBuilder, countRoot, countRoleJoin,null));
        countCriteriaQuery.select(queryBuilder.countDistinct(countRoot));
        TypedQuery<Long> countQuery = entityManager.createQuery(countCriteriaQuery);
        Long total = countQuery.getSingleResult();
        
        Page<User> page = new PageImpl<>(list, pageable, total);
        return page;
    }

    private Predicate[] getPredicates(Map<String, String> filter, CriteriaBuilder queryBuilder, Root<User> root, ListJoin<User, Role> roleJoin,String role) {
        List<Predicate> predicates = new ArrayList<>();
        if (!CollectionUtils.isEmpty(filter)) {
            for (Map.Entry<String, String> entry : filter.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (StringUtils.isEmpty(value) || StringUtils.isEmpty(key))
                    continue;

                if (key.equalsIgnoreCase("roles"))
                    predicates.add(queryBuilder.like(roleJoin.<String>get("name"), "%"+value+"%"));
                else
                    predicates.add(queryBuilder.like(root.<String>get(key), "%"+value+"%"));
            }
        }
        if(!StringUtils.isEmpty(role))
        	 predicates.add(queryBuilder.equal(roleJoin.<String>get("name"), role));
        return predicates.toArray(new Predicate[0]);
    }
    @Override
    public List<User> getAllEmployee() {
        CriteriaBuilder queryBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = queryBuilder.createQuery(User.class);

        Root<User> root = criteriaQuery.from(User.class);
        ListJoin<User, Role> roleJoin = root.joinList("roles", JoinType.INNER);
        //Fetch<User, Role> roles = root.fetch("roles", JoinType.LEFT);
//        Predicate[] predicatesArray = getPredicates(filter, queryBuilder, root, roleJoin);
        criteriaQuery.where(getPredicates(null, queryBuilder, root, roleJoin,Constants.ROLE_EMPLOYEES))
                     .distinct(true);

 

        TypedQuery<User> query = entityManager.createQuery(criteriaQuery);
 
        List<User> list = query.getResultList();


        return list;
    }
    @Override
    public List<User> findAllAndActiveTrue() {
        CriteriaBuilder queryBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = queryBuilder.createQuery(User.class);

        Root<User> root = criteriaQuery.from(User.class);
        ListJoin<User, Role> roleJoin = root.joinList("roles", JoinType.INNER);
        //Fetch<User, Role> roles = root.fetch("roles", JoinType.LEFT);
//        Predicate[] predicatesArray = getPredicates(filter, queryBuilder, root, roleJoin);
        criteriaQuery.where(queryBuilder.isTrue(root.get("active")))
                .distinct(true);
        TypedQuery<User> query = entityManager.createQuery(criteriaQuery);

        List<User> list = query.getResultList();


        return list;
    }

}
