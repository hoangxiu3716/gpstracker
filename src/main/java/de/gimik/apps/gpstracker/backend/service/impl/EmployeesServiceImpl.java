/*
 * Copyright 2014 trung.
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

import java.util.Map;

import javax.ws.rs.WebApplicationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.gimik.apps.gpstracker.backend.BackendException;
import de.gimik.apps.gpstracker.backend.model.Employees;
import de.gimik.apps.gpstracker.backend.model.User;
import de.gimik.apps.gpstracker.backend.repository.employees.EmployeesRepository;
import de.gimik.apps.gpstracker.backend.repository.user.UserRepository;
import de.gimik.apps.gpstracker.backend.service.ActionLogService;
import de.gimik.apps.gpstracker.backend.service.CommonService;
import de.gimik.apps.gpstracker.backend.service.EmployeesService;
import de.gimik.apps.gpstracker.backend.service.RemoteClientInfo;
import de.gimik.apps.gpstracker.backend.util.Constants;


/**
 *
 * @author trung
 */
@Service
@Transactional
public class EmployeesServiceImpl extends CommonService implements EmployeesService {

	@Autowired
	private EmployeesRepository employeesRepository;
	@Autowired
	private ActionLogService actionLogService;
	@Autowired
	private UserRepository userRepository;
	@Override
	public Employees findById(Integer id){
		return employeesRepository.findOne(id);
	}
	@Override
	public Employees findByIdAndUser(Integer id,User user){
		return employeesRepository.findByIdAndUser(id, user);
	}
	@Override
	public Employees findByUser(User user){
		return employeesRepository.findByUser(user);
	}
	@Override
    public Page<Employees> findAll(int pageIndex, int pageSize, String sortField, String sortDirection, Map<String, String> filter,String type) {
        return employeesRepository.findAll(constructPageSpecification(pageIndex, pageSize, sortField, sortDirection), filter,type);
    }
	@Override
    public Page<Employees> findAllByChoice(int pageIndex, int pageSize, String sortField, String sortDirection, Map<String, String> filter) {
        return employeesRepository.findAllByChoice(constructPageSpecification(pageIndex, pageSize, sortField, sortDirection), filter);
    }

	@Override
	public void create(RemoteClientInfo clientInfo , Employees item){
		if(item ==null)
			throw new WebApplicationException(404);
		employeesRepository.save(item);
		actionLogService.log(Constants.Object.EMPLOYEES, Constants.Action.ADD,item.toString(), clientInfo.getIp());
	}
	@Override
	public void update(RemoteClientInfo clientInfo , Employees item){
		if(item ==null)
			throw new WebApplicationException(404);
		employeesRepository.save(item);
		actionLogService.log(Constants.Object.EMPLOYEES, Constants.Action.UPDATE,item.toString(), clientInfo.getIp());
	}
	@Override
	public void delete(RemoteClientInfo clientInfo , Employees item){
		if(item !=null){
			if(item.getUser() != null)
				userRepository.delete(item.getUser());
			employeesRepository.delete(item);
			actionLogService.log(Constants.Object.EMPLOYEES, Constants.Action.DELETE,item.toString(), clientInfo.getIp());
		}
	}
}
