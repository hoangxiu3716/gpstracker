package de.gimik.apps.gpstracker.backend.repository.actionlog;


import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;

import de.gimik.apps.gpstracker.backend.model.ActionLog;
import de.gimik.apps.gpstracker.backend.model.search.ActionLogSearchInfo;
import de.gimik.apps.gpstracker.backend.repository.CommonRepository;
import de.gimik.apps.gpstracker.backend.util.DateTimeUtility;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

public class ActionLogRepositoryImpl extends CommonRepository implements ActionLogRepositoryCustom {
    @Override
    public Page<ActionLog> findAll(Pageable pageable, ActionLogSearchInfo searchInfo) {
        CriteriaBuilder queryBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ActionLog> criteriaQuery = queryBuilder
                .createQuery(ActionLog.class);
        Root<ActionLog> root = criteriaQuery.from(ActionLog.class);

        Predicate[] predicatesArray = calculatePredicates(searchInfo, queryBuilder, root);
        criteriaQuery.where(predicatesArray);

        Sort sort = pageable.getSort();
        if (sort != null) {
            criteriaQuery.orderBy(toOrders(sort, root, queryBuilder));
        }

        TypedQuery<ActionLog> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<ActionLog> list = query.getResultList();

        if (CollectionUtils.isEmpty(list)&& pageable.getOffset()>0){
            query.setFirstResult(0);
            list = query.getResultList();
        }

        CriteriaQuery<Long> countCriteriaQuery = queryBuilder.createQuery(Long.class);
        countCriteriaQuery.select(queryBuilder.count(countCriteriaQuery.from(ActionLog.class)));
        countCriteriaQuery.where(predicatesArray);
        TypedQuery<Long> countQuery = entityManager.createQuery(countCriteriaQuery);
        Long total = countQuery.getSingleResult();

        Page<ActionLog> page = new PageImpl<>(list, pageable, total);
        return page;
    }

    private Predicate[] calculatePredicates(ActionLogSearchInfo searchInfo, CriteriaBuilder queryBuilder, Root<ActionLog> root) {
        List<Predicate> predicates = new ArrayList<>();

        if (searchInfo != null) {

            if (!StringUtils.isBlank(searchInfo.getIp()))
                predicates.add(queryBuilder.like(root.<String>get("ip"), "%" + searchInfo.getIp() + "%"));

            if (!StringUtils.isBlank(searchInfo.getContent()))
                predicates.add(queryBuilder.like(root.<String>get("content"), "%" + searchInfo.getContent() + "%"));

            if (!StringUtils.isBlank(searchInfo.getPerformer()))
                predicates.add(queryBuilder.like(root.<String>get("performer"), "%" + searchInfo.getPerformer() + "%"));

            Date fromDate = DateTimeUtility.parseFromDate(searchInfo.getFromDate());
            fromDate = DateTimeUtility.getBeginTimeOfDay(fromDate);

            Date toDate = DateTimeUtility.parseToDate(searchInfo.getToDate());
            toDate = DateTimeUtility.getEndTimeOfDay(toDate);

            predicates.add(queryBuilder.between(root.<Date>get("actionTime"), fromDate, toDate));

            List<String> actions = parseSearchActions(searchInfo);
            if (actions != null && actions.size() > 0)
                predicates.add(root.<String>get("action").in(actions));

            List<String> objects = parseSearchObjects(searchInfo);
            if (objects != null && objects.size() > 0)
                predicates.add(root.<String>get("object").in(objects));
        }

        return predicates.toArray(new Predicate[0]);
    }

    private List<String> parseSearchActions(ActionLogSearchInfo searchInfo) {
        if (searchInfo.getActions() != null) {
            List<String> actions = new ArrayList<>();

            for (Map.Entry<String, Boolean> actionMapEntry : searchInfo.getActions().entrySet()) {
                if (actionMapEntry.getValue()) {
                    actions.add(actionMapEntry.getKey());
                }
            }

            return actions;
        }

        return null;
    }

    private List<String> parseSearchObjects(ActionLogSearchInfo searchInfo) {
        if (searchInfo.getObjects() != null) {
            List<String> objects = new ArrayList<>();

            for (Map.Entry<String, Boolean> objectMapEntry : searchInfo.getObjects().entrySet()) {
                if (objectMapEntry.getValue()) {
                    objects.add(objectMapEntry.getKey());
                }
            }

            return objects;
        }

        return null;
    }
}
