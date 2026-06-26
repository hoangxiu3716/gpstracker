package de.gimik.apps.gpstracker.backend.web.viewmodel.employees;

public class ChangePassInfo {
	private Integer employeesId;
	private String username;
	private String password;
	private String newPassword;
	private String repeatPassword;


	public Integer getEmployeesId() {
		return employeesId;
	}
	public void setEmployeesId(Integer employeesId) {
		this.employeesId = employeesId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getRepeatPassword() {
		return repeatPassword;
	}
	public void setRepeatPassword(String repeatPassword) {
		this.repeatPassword = repeatPassword;
	}
}
