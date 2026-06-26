package de.gimik.apps.gpstracker.backend.web.viewmodel.user;

import de.gimik.apps.gpstracker.backend.model.User;

public class SortViewUserInfo {
	private Integer id;
	private String username;
	private String fullName;
	
	public SortViewUserInfo() {
		super();
	}
	public SortViewUserInfo(User user) {
		super();
		this.id = user.getId();
		this.username = user.getUsername();
		this.fullName = user.getFullname();
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
}
