package de.gimik.apps.gpstracker.backend.model;

import javax.persistence.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;

@Entity
@Table(name = "employees")
@EntityListeners(AuditingEntityListener.class)
public class Employees extends AuditableEntity   implements Serializable {

	private static final long serialVersionUID = 3483915904023463446L;
	@Column(nullable = false)
    private String name;
    @Column
    private String email;
    @Column
    private String street;
    @Column
    private String postCode;
    @Column
    private String city;
    @Column
    private String phone;
    @Column
    private String type;
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id", name = "user_id")
    private User user;

    public Employees() {
        super();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	

}
