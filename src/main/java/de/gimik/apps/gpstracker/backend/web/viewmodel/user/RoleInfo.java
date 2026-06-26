package de.gimik.apps.gpstracker.backend.web.viewmodel.user;

import de.gimik.apps.gpstracker.backend.model.Role;

public class RoleInfo {
	private Integer id;
	private String name;
	private String description;
	
	
	public RoleInfo() {
		super();
	}
	public RoleInfo(Role item) {
		super();
		this.id = item.getId();
		this.name = item.getName();
		this.description = item.getDescription();
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
}
