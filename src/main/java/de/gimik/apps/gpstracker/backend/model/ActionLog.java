package de.gimik.apps.gpstracker.backend.model;


import org.codehaus.jackson.map.annotate.JsonSerialize;

import de.gimik.apps.gpstracker.backend.util.DateTimeSerializer;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Entity
public class ActionLog implements Serializable {
    public static final String ACTION_TIME = "actionTime";

    private static final long serialVersionUID = 4352967678325832115L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String performer;

    @Column
    @JsonSerialize(using = DateTimeSerializer.class)
    private Date actionTime;

    @Column
    private String ip;

    @Column
    private String object;

    @Column
    private String action;

    @Column(columnDefinition = "text")
    private String content;

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public Date getActionTime() {
        return actionTime;
    }

    public void setActionTime(Date actionTime) {
        this.actionTime = actionTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
