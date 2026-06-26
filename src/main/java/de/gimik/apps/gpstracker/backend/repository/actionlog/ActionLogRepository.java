package de.gimik.apps.gpstracker.backend.repository.actionlog;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.gimik.apps.gpstracker.backend.model.ActionLog;

/**
 * Created by dang on 29.08.2014.
 */
public interface ActionLogRepository extends JpaRepository<ActionLog, Long>, ActionLogRepositoryCustom {	
    ActionLog findById(Long actionLogID);
}
