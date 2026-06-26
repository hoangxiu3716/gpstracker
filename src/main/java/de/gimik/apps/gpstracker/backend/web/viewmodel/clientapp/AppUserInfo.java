package de.gimik.apps.gpstracker.backend.web.viewmodel.clientapp;


public class AppUserInfo {
	private String forename;
    private String surname;
    private String email;
    private String password;
    private Integer gender;
    private String areas;
    private String zipCode;
    private String city;
    private String yearOfBith;
    

	public AppUserInfo() {
		super();
	}
	public String getForename() {
		return forename;
	}
	public void setForename(String forename) {
		this.forename = forename;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getGender() {
		return gender;
	}
	public void setGender(Integer gender) {
		this.gender = gender;
	}
	public String getAreas() {
		return areas;
	}
	public void setAreas(String areas) {
		this.areas = areas;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getYearOfBith() {
		return yearOfBith;
	}
	public void setYearOfBith(String yearOfBith) {
		this.yearOfBith = yearOfBith;
	}
    
    
    
    
}
