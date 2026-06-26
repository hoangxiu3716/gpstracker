package de.gimik.apps.gpstracker.backend.web.viewmodel;

import javax.xml.bind.annotation.XmlElement;

import com.google.gson.annotations.SerializedName;

public class FireBasePushResult {
	private Integer success;
	private Integer faulure;
//	private String results;
	@XmlElement(name = "multicast_id")
    @SerializedName("multicast_id")
	private String multicastId;
	@XmlElement(name = "canonical_ids")
    @SerializedName("canonical_ids")
	private Integer canonicalIds;
	
	public FireBasePushResult() {
		super();
	}
	public Integer getSuccess() {
		return success;
	}
	public void setSuccess(Integer success) {
		this.success = success;
	}
	public Integer getFaulure() {
		return faulure;
	}
	public void setFaulure(Integer faulure) {
		this.faulure = faulure;
	}
//	public String getResults() {
//		return results;
//	}
//	public void setResults(String results) {
//		this.results = results;
//	}

	public Integer getCanonicalIds() {
		return canonicalIds;
	}
	public void setCanonicalIds(Integer canonicalIds) {
		this.canonicalIds = canonicalIds;
	}
	public String getMulticastId() {
		return multicastId;
	}
	public void setMulticastId(String multicastId) {
		this.multicastId = multicastId;
	}
	
}
