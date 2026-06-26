package de.gimik.apps.gpstracker.backend.model;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import de.gimik.apps.gpstracker.backend.util.Constants;

import de.gimik.apps.gpstracker.backend.web.viewmodel.user.UserInputInfo;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"roles"}, ignoreUnknown = true)
public class User extends AuditableEntity  implements Serializable {
    private static final long serialVersionUID = -1325531598075759936L;

    public static final String USER_NAME = "username";

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullname;
	@Column
	private String deviceIdKey;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "roleId"))
    private List<Role> roles = new ArrayList<Role>();

    @Column(nullable = false, columnDefinition = "TINYINT(1)  DEFAULT 0")
    private boolean passwordChanged;
    @Column
	private Date effectiveFrom;
	@Column
	private Date effectiveTo;
	@Column(columnDefinition = "varchar(50) default 'once'")
	private String sessionType;
	@Column
	private Double sessionTime;
    @Column
    private String avatar;
    @Column(length = 255, nullable = true)
    private String email;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String name;
    @Column
    private String phone;
    @Column(columnDefinition="TEXT")
    private String note;
    @Column(nullable = false, columnDefinition = "TINYINT(1)  DEFAULT 0")
    private boolean deleted = false;
    public User() {
    }

    public User(String username, String passwordHash) {
        this.username = username;
        this.password = passwordHash;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role) {
        this.roles.add(role);
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

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

	public String getDeviceIdKey() {
		return deviceIdKey;
	}

	public void setDeviceIdKey(String deviceIdKey) {
		this.deviceIdKey = deviceIdKey;
	}

	public boolean isPasswordChanged() {
		return passwordChanged;
	}

	public void setPasswordChanged(boolean passwordChanged) {
		this.passwordChanged = passwordChanged;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Collection<String> getAuthorities() {
        if (roles == null) {
            return Collections.emptyList();
        }

        return Collections2.transform(roles, new Function<Role, String>() {
            @Override
            public String apply(Role role) {
                return role.getAuthority();
            }
        });
    }
    public static User copyData(User item, UserInputInfo data) {
        item.setName(data.getName());
        item.setFirstName(data.getFirstName());
        item.setFullname(data.getFullname());
        item.setEmail(data.getEmail());
        item.setPhone(data.getPhone());
        item.setNote(data.getNote());
        return item;
    }
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("ID=").append(this.getId()).append("|");
        sb.append("Username=").append(this.getUsername()).append("|");
        if (this.getFullname() != null) {
            sb.append("Fullname=").append(this.getFullname()).append("|");
        }

        String roles = StringUtils.join(this.getAuthorities(), ", ").replace("[", "")
                .replace("]", "");
        sb.append("Roles=").append(roles);


        return sb.toString();
    }
    public boolean isAdmin() {
    	if(!CollectionUtils.isEmpty(roles)) {
	    	for(Role role : roles) {
	    		if(role.getName().equals(Constants.ROLE_ADMIN))
	    			return true;
	    	}
    	}
        return false;
    }
}
