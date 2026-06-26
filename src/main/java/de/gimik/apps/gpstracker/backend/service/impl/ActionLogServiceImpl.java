package de.gimik.apps.gpstracker.backend.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import de.gimik.apps.gpstracker.backend.model.ActionLog;
import de.gimik.apps.gpstracker.backend.model.search.ActionLogSearchInfo;
import de.gimik.apps.gpstracker.backend.repository.actionlog.ActionLogRepository;
import de.gimik.apps.gpstracker.backend.util.Constants;
import de.gimik.apps.gpstracker.backend.service.ActionLogService;
import de.gimik.apps.gpstracker.backend.service.CommonService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import java.util.Date;
import java.util.Map;

@Service
public class ActionLogServiceImpl extends CommonService implements ActionLogService {

	@Autowired
    private ActionLogRepository actionLogRepository;

    @Override
	public void log(String object, String action, String content, String ip) {
		ActionLog actionLog = new ActionLog();
		
		actionLog.setActionTime(new Date());
		actionLog.setContent(content);
        try {
            actionLog.setPerformer(getLogUsername());
        }catch (Exception e){}
        try {
            actionLog.setIp(ip);
        }catch (Exception e){}

		actionLog.setObject(object);
		actionLog.setAction(action);

        this.actionLogRepository.save(actionLog);
	}

    public void log(String object, String action, String content, String ip, String user) {
        ActionLog actionLog = new ActionLog();

        actionLog.setActionTime(new Date());
        actionLog.setContent(content);
        actionLog.setPerformer(user);
        actionLog.setObject(object);
        actionLog.setIp(ip);
        actionLog.setAction(action);

        this.actionLogRepository.save(actionLog);
    }
	
	private String getLogUsername(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication == null ? Constants.ANONYMOUS : authentication.getName();
	}

    /*
	@Override
	public SearchResult<ActionLog> searchLogs(ActionLogSearchInfo searchInfo) {
		//return actionLogDao.searchLogs(searchInfo);
        return null;
	}
    */

	@Override
	public ActionLog getByID(long actionLogID) {
        return actionLogRepository.findOne(actionLogID);
	}

    @Override
    public Page<ActionLog> getAll(int pageIndex, int pageSize, String sortField, String sortDirection, ActionLogSearchInfo searchInfo) {
        return actionLogRepository.findAll(constructPageSpecification(pageIndex, pageSize, sortField, sortDirection), searchInfo);
    }

    @Override
    public Page<ActionLog> getAll(int pageIndex, int pageSize, String sortField, String sortDirection) {
        return actionLogRepository.findAll(constructPageSpecification(pageIndex, pageSize, sortField, sortDirection), null);
    }

    @Override
    public String defaultSortField() {
        return ActionLog.ACTION_TIME;
    }

    @Override
    public Sort.Direction defaultSortDirection() {
        return Sort.Direction.DESC;
    }
}
