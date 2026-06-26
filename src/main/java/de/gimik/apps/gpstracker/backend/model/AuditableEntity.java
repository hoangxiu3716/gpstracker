package de.gimik.apps.gpstracker.backend.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import de.gimik.apps.gpstracker.backend.service.AuditableEntityListener;
import de.gimik.apps.gpstracker.backend.util.DateTimeSerializer;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Dang on 17.09.2015.
 */
@EntityListeners(AuditableEntityListener.class)
@MappedSuperclass
public class AuditableEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "created_by_user")
    @CreatedBy
    private String createdByUser;

    @Column(name = "creation_time")
    @CreatedDate
    @JsonSerialize(using = DateTimeSerializer.class)
    private Date creationTime;

    @Column(name = "modified_by_user")
    @LastModifiedBy
    private String modifiedByUser;

    @Column(name = "modification_time")
    @LastModifiedDate
    @JsonSerialize(using = DateTimeSerializer.class)
    private Date modificationTime;

    @Column(nullable = false, columnDefinition = "TINYINT(1)  DEFAULT 1")
    private boolean active = true;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isNew() {
        return id == null;
    }

    public String getCreatedBy() {
        return createdByUser;
    }

    public void setCreatedBy(String createdBy) {
        this.createdByUser = createdBy;
    }

    public Date getCreatedDate() {
        return creationTime;
    }

    public void setCreatedDate(Date creationDate) {
        this.creationTime = creationDate;
    }

    public String getLastModifiedBy() {
        return modifiedByUser;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.modifiedByUser = lastModifiedBy;
    }

    public Date getLastModifiedDate() {
        return modificationTime;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.modificationTime = lastModifiedDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("ID=").append(this.getId()).append("|");

        sb.append("modifiedByUser=").append(modifiedByUser == null ? "" : modifiedByUser).append("|");
        sb.append("modificationTime=").append(modificationTime == null ? "" : modificationTime).append("|");

        return sb.toString();
    }
}
