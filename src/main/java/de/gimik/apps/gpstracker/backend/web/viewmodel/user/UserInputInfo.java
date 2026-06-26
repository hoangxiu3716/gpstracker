package de.gimik.apps.gpstracker.backend.web.viewmodel.user;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import de.gimik.apps.gpstracker.backend.model.Role;

/**
 * Created by GIMIK10 on 9/1/2014.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInputInfo extends UserViewInfo {

    public UserInputInfo() { }
    private String roleName;
	
    public UserInputInfo(String username, String password, String fullname, Map<String, Boolean> roles,List<Role> roleList) {
        super(username, fullname, roles,roleList);

        this.password = password;
    }

    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}


}
