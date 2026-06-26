package de.gimik.apps.gpstracker.backend.repository.actionlog;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.gimik.apps.gpstracker.backend.model.ActionLog;
import de.gimik.apps.gpstracker.backend.model.search.ActionLogSearchInfo;

public interface ActionLogRepositoryCustom {
    public Page<ActionLog> findAll(Pageable pageable, ActionLogSearchInfo searchInfo);
}
