package de.gimik.apps.gpstracker.backend.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import de.gimik.apps.gpstracker.backend.model.User;
import de.gimik.apps.gpstracker.backend.web.viewmodel.user.UserInputInfo;

/**
 * Created by dang on 30.08.2014.
 */
public abstract class CommonService {
    /**
     * Returns a new object which specifies the the wanted result page.
     *
     * @param pageIndex The index of the wanted result page
     * @return
     */
    public Pageable constructPageSpecification(int pageIndex, int pageSize, String sortField, String sortDirection) {
        if (pageSize <= 0) {
            pageSize = Constant.NUMBER_OF_ITEMS_PER_PAGE;
        }
        Pageable pageSpecification = new PageRequest(pageIndex, pageSize, constructSortSpecification(sortField, sortDirection));
        return pageSpecification;
    }

    public Sort constructSortSpecification(String sortField, String sortDirection) {
        if (StringUtils.isEmpty(sortField)) {
            return sortByDefaultFieldAsc();
        }
        Sort.Direction d = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort s = new Sort(d, sortField);
        String defaultField = defaultSortField();
        if (!StringUtils.isEmpty(defaultField) && !defaultField.equalsIgnoreCase(sortField)) {
            Sort defaultSort = sortByDefaultFieldAsc();
            if (defaultSort!=null)
                s = s.and(defaultSort);
        }
        return s;
    }

    /**
     * Returns a Sort object which sorts objects in default direction order by using the default name .
     *
     * @return
     */
    public Sort sortByDefaultFieldAsc() {
        String defaultField = defaultSortField();
        if (StringUtils.isEmpty(defaultField))
            return null;
        return new Sort(defaultSortDirection(), defaultField);
    }

    public String defaultSortField(){
        return "id";
    }

    public Sort.Direction defaultSortDirection() {
        return Sort.Direction.ASC;
    }

	public void resetPassword(RemoteClientInfo clientInfo, int id) {
		// TODO Auto-generated method stub
		
	}

	public String retrieveResetPassword(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	public void resetPassword(RemoteClientInfo clientInfo, UserInputInfo user) {
		
		// TODO Auto-generated method stub
		
	}

	
}
