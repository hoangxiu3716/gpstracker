package de.gimik.apps.gpstracker.backend.web.viewmodel;

import java.util.List;

import de.gimik.apps.gpstracker.backend.util.Constants;

public class ResultInfo {
	private Integer resultCode;
	private String resultMessage;
	private Integer id;
	private String token;
	private List<?> data;
	public ResultInfo() {
		super();
	}
	public ResultInfo(String result) {
		super();
		this.resultCode = Constants.OK;
		this.resultMessage = result;
	}
	public ResultInfo(Integer resultCode, String resultMessage) {
		super();
		this.resultCode = resultCode;
		this.resultMessage = resultMessage;
	}
	public ResultInfo(Integer resultCode, String resultMessage,String token) {
		super();
		this.resultCode = resultCode;
		this.resultMessage = resultMessage;
		this.token = token;
	}
	public ResultInfo(Integer resultCode, String resultMessage,List<?> data) {
		super();
		this.resultCode = resultCode;
		this.resultMessage = resultMessage;
		this.data = data;
	}
	public ResultInfo(Integer resultCode, String resultMessage,Integer id) {
		super();
		this.id = id;
		this.resultCode = resultCode;
		this.resultMessage = resultMessage;
	}

	public Integer getResultCode() {
		return resultCode;
	}
	public void setResultCode(Integer resultCode) {
		this.resultCode = resultCode;
	}
	public String getResultMessage() {
		return resultMessage;
	}
	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public List<?> getData() {
		return data;
	}
	public void setData(List<?> data) {
		this.data = data;
	}

}
