package de.gimik.apps.gpstracker.backend.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;

import de.gimik.apps.gpstracker.backend.web.viewmodel.user.UserInputInfo;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
public class Role implements GrantedAuthority, Serializable {

    private static final long serialVersionUID = 5351318508205267629L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
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

    @Override
    public String getAuthority() {
        return getName();
    }

    @Override
    public String toString() {
        return getName();
    }
    
    
}
