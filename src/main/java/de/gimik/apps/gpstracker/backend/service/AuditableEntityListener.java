package de.gimik.apps.gpstracker.backend.service;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import de.gimik.apps.gpstracker.backend.model.AuditableEntity;
import de.gimik.apps.gpstracker.backend.util.Constants;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import java.lang.annotation.Annotation;

/**
 * Created by Dang on 15.10.2015.
 */
@Component
public class AuditableEntityListener extends AuditingEntityListener {

    private ObjectFactory<AuditingHandler> handler;

    @Override
    public void setAuditingHandler(ObjectFactory<AuditingHandler> auditingHandler) {
        super.setAuditingHandler(auditingHandler);
        this.handler = auditingHandler;
    }

    static String getTableName(final AuditableEntity entity) {
        Annotation annotation = entity.getClass().getAnnotation(Table.class);
        return annotation != null ? entity.getClass().getAnnotation(Table.class).name() : entity.getClass().getSimpleName();
    }
}
