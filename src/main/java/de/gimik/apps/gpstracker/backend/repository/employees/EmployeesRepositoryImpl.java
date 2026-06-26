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

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import de.gimik.apps.gpstracker.backend.model.Employees;
import de.gimik.apps.gpstracker.backend.repository.CommonRepository;

import javax.persistence.criteria.*;

/**
 * @author trung
 */
public class EmployeesRepositoryImpl extends CommonRepository implements EmployeesRepositoryCustom {
    @Override
    public Page<Employees> findAll(Pageable pageable, Map<String, String> filter,String type) {
        CriteriaBuilder queryBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Employees> criteriaQuery = queryBuilder.createQuery(Employees.class);

        Root<Employees> root = criteriaQuery.from(Employees.class);
        criteriaQuery.where(getPredicates(filter, queryBuilder, root,type))
                .distinct(true);

        Sort sort = pageable.getSort();
        if (sort != null) {
            criteriaQuery.orderBy(toOrders(sort, root, queryBuilder));
        }

        TypedQuery<Employees> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Employees> list = query.getResultList();

        CriteriaQuery<Long> countCriteriaQuery = queryBuilder.createQuery(Long.class);
        Root<Employees> countRoot = countCriteriaQuery.from(Employees.class);
        countCriteriaQuery.where(getPredicates(filter, queryBuilder, countRoot,type));
        countCriteriaQuery.select(queryBuilder.countDistinct(countRoot));
        TypedQuery<Long> countQuery = entityManager.createQuery(countCriteriaQuery);
        Long total = countQuery.getSingleResult();

        Page<Employees> page = new PageImpl<>(list, pageable, total);
        return page;
    } 
    @Override
    public Page<Employees> findAllByChoice(Pageable pageable, Map<String, String> filter) {
        CriteriaBuilder queryBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Employees> criteriaQuery = queryBuilder.createQuery(Employees.class);

        Root<Employees> root = criteriaQuery.from(Employees.class);
        criteriaQuery.where(getPredicatesOrParams(filter, queryBuilder, root))
                .distinct(true);

        Sort sort = pageable.getSort();
        if (sort != null) {
            criteriaQuery.orderBy(toOrders(sort, root, queryBuilder));
        }

        TypedQuery<Employees> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Employees> list = query.getResultList();

        CriteriaQuery<Long> countCriteriaQuery = queryBuilder.createQuery(Long.class);
        Root<Employees> countRoot = countCriteriaQuery.from(Employees.class);
        countCriteriaQuery.where(getPredicatesOrParams(filter, queryBuilder, countRoot));
        countCriteriaQuery.select(queryBuilder.countDistinct(countRoot));
        TypedQuery<Long> countQuery = entityManager.createQuery(countCriteriaQuery);
        Long total = countQuery.getSingleResult();

        Page<Employees> page = new PageImpl<>(list, pageable, total);
        return page;
    } 
    
    private Predicate[] getPredicates(Map<String, String> filter, CriteriaBuilder queryBuilder, Root<Employees> root,String type) {
        List<Predicate> predicates = new ArrayList<>();
        if (!CollectionUtils.isEmpty(filter)) {
            for (Map.Entry<String, String> entry : filter.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (StringUtils.isEmpty(value) || StringUtils.isEmpty(key))
                    continue;

                predicates.add(queryBuilder.like(root.<String>get(key), "%" + value + "%"));
            }
        }
        if(!StringUtils.isEmpty(type))
        	predicates.add(queryBuilder.equal(root.<String>get("type"),type));
        return predicates.toArray(new Predicate[0]);
    }
    private Predicate[] getPredicatesOrParams(Map<String, String> filter, CriteriaBuilder queryBuilder, Root<Employees> root) {
        List<Predicate> predicates = new ArrayList<>();
        List<Predicate> strPredicates = new ArrayList<>();
        if (!CollectionUtils.isEmpty(filter)) {
            for (Map.Entry<String, String> entry : filter.entrySet()) {
            	String key = entry.getKey();
            	String value = entry.getValue();
                if (StringUtils.isEmpty(entry.getKey()) || StringUtils.isEmpty(entry.getValue()))
                    continue;
                strPredicates.add(queryBuilder.like(root.<String>get(key), "%" + value + "%"));
            }
        }
        predicates.add(queryBuilder.or(strPredicates.toArray(new Predicate[strPredicates.size()])));
        
        return predicates.toArray(new Predicate[0]);
    }
    
    
}
