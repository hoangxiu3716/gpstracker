package de.gimik.apps.gpstracker.backend.model.search;


import java.io.Serializable;
import java.util.Map;

public class ActionLogSearchInfo implements Serializable{

	private static final long serialVersionUID = 6002539869190861676L;

	private String performer;
	private String ip;
	private String content;
	private String fromDate;
	private String toDate;
    private Map<String, Boolean> actions;
    private Map<String, Boolean> objects;
	
	public ActionLogSearchInfo(){
	}

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public Map<String, Boolean> getActions() {
        return actions;
    }

    public void setActions(Map<String, Boolean> actions) {
        this.actions = actions;
    }

    public Map<String, Boolean> getObjects() {
        return objects;
    }

    public void setObjects(Map<String, Boolean> objects) {
        this.objects = objects;
    }
}