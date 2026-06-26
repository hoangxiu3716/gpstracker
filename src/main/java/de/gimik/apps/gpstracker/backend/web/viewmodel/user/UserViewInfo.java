package de.gimik.apps.gpstracker.backend.web.viewmodel.user;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.gimik.apps.gpstracker.backend.model.Employees;
import de.gimik.apps.gpstracker.backend.model.Role;
import de.gimik.apps.gpstracker.backend.model.User;
import de.gimik.apps.gpstracker.backend.util.DateTimeUtility;
import de.gimik.apps.gpstracker.backend.web.viewmodel.employees.EmployeesInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserViewInfo
{
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;
    private String username;

    private String fullname;
	private Map<String, Boolean> roles;
	private EmployeesInfo employeesInfo;
	private String roleName;
	private String avatar;
	private String email;
	private String firstName;
	private String name;
	private String phone;
	private String note;

    public UserViewInfo() {}

	public UserViewInfo(String username, String fullname, Map<String, Boolean> roles,List<Role> roleList)
	{
		this.username = username;
        this.fullname = fullname;
		this.roles = roles;
		if(!CollectionUtils.isEmpty(roleList))
			this.roleName = roleList.get(0).getName();
	}
	
	public UserViewInfo(User user) {
		super();
		this.id = user.getId();
		this.username = user.getUsername();
		this.fullname = user.getFullname();
	}

	public UserViewInfo(User user, Map<String, Boolean> roles,List<Role> roleList)
	{
		this.id= user.getId();
		this.username = user.getUsername();
        this.fullname = user.getFullname();
		this.roles = roles;
		if(!CollectionUtils.isEmpty(roleList))
			this.roleName = roleList.get(0).getName();
	}
	public UserViewInfo(String username, String fullname, Map<String, Boolean> roles,Employees employees)
	{
		this.username = username;
        this.fullname = fullname;
		this.roles = roles;
		this.employeesInfo = employees == null ? null : new EmployeesInfo(employees,false);
	}
	public UserViewInfo(User user, Map<String, Boolean> roles)
	{
		this.id = user.getId();
		this.username = user.getUsername();
		this.fullname = user.getFullname();
		this.roles = roles;
		this.name = user.getName();
		this.avatar = user.getAvatar();
		this.email = user.getEmail();
		this.phone = user.getPhone();
		this.note = user.getNote();
		this.firstName = user.getFirstName();
	}
    public String getUsername() {
        return username;
    }

    public String getFullname() {
        return fullname;
    }

	public Map<String, Boolean> getRoles()
	{
		return this.roles;
	}

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setRoles(Map<String, Boolean> roles) {
        this.roles = roles;
    }

	public EmployeesInfo getEmployeesInfo() {
		return employeesInfo;
	}

	public void setEmployeesInfo(EmployeesInfo employeesInfo) {
		this.employeesInfo = employeesInfo;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
//	public String getDisplayRoles() {
//        return StringUtils.join(roles.keySet(), ", ")
//                          .replace("[", "")
//                          .replace("]", "");
//    }
}