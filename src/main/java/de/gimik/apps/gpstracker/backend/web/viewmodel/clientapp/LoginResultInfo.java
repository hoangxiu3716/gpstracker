package de.gimik.apps.gpstracker.backend.web.viewmodel.clientapp;

import java.util.List;

import de.gimik.apps.gpstracker.backend.web.viewmodel.TokenInfo;
import de.gimik.apps.gpstracker.backend.web.viewmodel.employees.EmployeesInfo;

public class LoginResultInfo {
private TokenInfo tokenInfo;
private EmployeesInfo employeeInfo;



public LoginResultInfo() {
	super();
}
public LoginResultInfo(TokenInfo tokenInfo, EmployeesInfo employeeInfo) {
	super();
	this.tokenInfo = tokenInfo;
	this.employeeInfo = employeeInfo;
}
public TokenInfo getTokenInfo() {
	return tokenInfo;
}
public void setTokenInfo(TokenInfo tokenInfo) {
	this.tokenInfo = tokenInfo;
}
public EmployeesInfo getEmployeeInfo() {
	return employeeInfo;
}
public void setEmployeeInfo(EmployeesInfo employeeInfo) {
	this.employeeInfo = employeeInfo;
}


}
