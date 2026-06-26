package de.gimik.apps.gpstracker.backend.security;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import de.gimik.apps.gpstracker.backend.model.Employees;
import de.gimik.apps.gpstracker.backend.model.Role;
import de.gimik.apps.gpstracker.backend.model.User;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by trung on 30.08.2014.
 */
public class DefaultUserDetails implements UserDetails {
    private User user;
    private Employees employee;
    private String role;
    private Date effectiveFrom;
	private Date effectiveTo;
	private String sessionType;
	private Double sessionTime;
    public DefaultUserDetails(User user) {
        this.user = user;
    }
    
    public DefaultUserDetails(User user, Employees employee) {
		super();
		this.user = user;
		this.employee = employee;
		this.role = CollectionUtils.isEmpty(user.getRoles()) ? "" : user.getRoles().get(0).getName();
		this.effectiveFrom = user.getEffectiveFrom();
		this.effectiveTo = user.getEffectiveTo();
		this.sessionTime = user.getSessionTime();
		this.sessionType = user.getSessionType();
	}

	public List<Role> getRoles() {
        return user == null ? null : user.getRoles();
    }

    public String getFullname() {
        return user == null ? null : user.getFullname();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user == null ? null : user.getRoles();
    }

    @Override
    public String getPassword() {
        return user == null ? null : user.getPassword();
    }

    @Override
    public String getUsername() {
        return user == null ? null : user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

	public Employees getEmployee() {
		return employee;
	}

	public void setEmployee(Employees employee) {
		this.employee = employee;
	}

	public User getUser() {
		return user;
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

	public void setUser(User user) {
		this.user = user;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
