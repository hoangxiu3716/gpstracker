package de.gimik.apps.gpstracker.backend.web.viewmodel.employees;

import java.util.Date;
import java.util.List;

import org.springframework.util.CollectionUtils;

import de.gimik.apps.gpstracker.backend.model.Role;
import de.gimik.apps.gpstracker.backend.model.User;
import org.springframework.util.StringUtils;

public class UserInfo {
	private Integer userId;
	private String username;
	private String password;
	private List<Role> role;
	private String roleName;
	private Date effectiveFrom;
	private Date effectiveTo;
	private String sessionType;
	private Double sessionTime;
	private boolean passwordChanged;
	private String fullName;
	private String avatar;
	private String email;
	private String phone;
	private String note;
	private String name;
	private String firstName;
	public UserInfo() {
		super();
	}
	
	public UserInfo(User user) {
		super();
		this.userId = user.getId();
		this.username = user.getUsername();
		this.role = user.getRoles();
		if(!CollectionUtils.isEmpty(user.getRoles()))
			this.roleName = user.getRoles().get(0).getName();
		this.effectiveFrom = user.getEffectiveFrom();
		this.effectiveTo = user.getEffectiveTo();
		this.sessionTime = user.getSessionTime();
		this.sessionType = user.getSessionType();
		this.passwordChanged = user.isPasswordChanged();
		this.fullName = user.getFullname();
	}
	public UserInfo(User user,String host) {
		super();
		this.userId = user.getId();
		this.username = user.getUsername();
		this.role = user.getRoles();
		if(!CollectionUtils.isEmpty(user.getRoles()))
			this.roleName = user.getRoles().get(0).getName();
		this.sessionTime = user.getSessionTime();
		this.sessionType = user.getSessionType();
		this.passwordChanged = user.isPasswordChanged();
		this.firstName = StringUtils.isEmpty(user.getFirstName()) ? "" : user.getFirstName();
		this.name = StringUtils.isEmpty(user.getName()) ? "" : user.getName();
		this.avatar = StringUtils.isEmpty(user.getAvatar()) ? "" : (host + user.getAvatar()) ;
		this.email = user.getEmail();
		this.phone = user.getPhone();
		this.note = user.getNote();
		this.fullName = (this.firstName + " " + this.name).trim();
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
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

	public List<Role> getRole() {
		return role;
	}

	public void setRole(List<Role> role) {
		this.role = role;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Date getEffectiveFrom() {
		return effectiveFrom;
	}

	public void setEffectiveFrom(Date effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}

	public Date getEffectiveTo() {
		return effectiveTo;
	}

	public void setEffectiveTo(Date effectiveTo) {
		this.effectiveTo = effectiveTo;
	}

	public String getSessionType() {
		return sessionType;
	}

	public void setSessionType(String sessionType) {
		this.sessionType = sessionType;
	}

	public Double getSessionTime() {
		return sessionTime;
	}

	public void setSessionTime(Double sessionTime) {
		this.sessionTime = sessionTime;
	}

	public boolean isPasswordChanged() {
		return passwordChanged;
	}

	public void setPasswordChanged(boolean passwordChanged) {
		this.passwordChanged = passwordChanged;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
}
