package de.gimik.apps.gpstracker.backend.web.viewmodel.employees;

import javax.persistence.Column;

import de.gimik.apps.gpstracker.backend.model.Employees;

public class EmployeesInfo {
	private Integer id;
	private String name;
	private String email;
	private String street;
	private String postCode;
	private String city;
	private String phone;
	private UserInfo userInfo;
	private String address;
	private String username;
	private String password;
	private String type;
	public EmployeesInfo() {
		super();
	}


	public EmployeesInfo(Employees employees, boolean userInfo) {
		this.id = employees.getId();
		this.name = employees.getName();
		this.email = employees.getEmail();
		this.street = employees.getStreet();
		this.postCode = employees.getPostCode();
		this.city = employees.getCity();
		this.phone = employees.getPhone();
		this.address = this.street + " " + this.postCode + " " + this.city;
		if (userInfo)
			this.userInfo = employees.getUser() == null ? null : new UserInfo(employees.getUser());
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getStreet() {
		return street;
	}


	public void setStreet(String street) {
		this.street = street;
	}


	public String getPostCode() {
		return postCode;
	}


	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}


	public String getCity() {
		return city;
	}


	public void setCity(String city) {
		this.city = city;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public UserInfo getUserInfo() {
		return userInfo;
	}


	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
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


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}



}
