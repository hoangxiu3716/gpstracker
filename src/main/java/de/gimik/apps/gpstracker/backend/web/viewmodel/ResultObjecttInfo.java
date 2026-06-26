package de.gimik.apps.gpstracker.backend.web.viewmodel;

import de.gimik.apps.gpstracker.backend.util.Constants;

public class ResultObjecttInfo {
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	private Integer resultCode;
	private String resultMessage;
	private Integer id;
	private String token;
	private Object data;
	public ResultObjecttInfo() {
		super();
	}
	public ResultObjecttInfo(String result) {
		super();
		this.resultCode = Constants.OK;
		this.resultMessage = result;
	}
	public ResultObjecttInfo(Integer resultCode, String resultMessage) {
		super();
		this.resultCode = resultCode;
		this.resultMessage = resultMessage;
	}
	public ResultObjecttInfo(Integer resultCode, String resultMessage,String token) {
		super();
		this.resultCode = resultCode;
		this.resultMessage = resultMessage;
		this.token = token;
	}
	public ResultObjecttInfo(Integer resultCode, String resultMessage,String token,Object data) {
		super();
		this.resultCode = resultCode;
		this.resultMessage = resultMessage;
		this.token = token;
		this.data= data;
	}
	public ResultObjecttInfo(Integer resultCode, String resultMessage,Object data) {
		super();
		this.resultCode = resultCode;
		this.resultMessage = resultMessage;
		this.data = data;
	}
	public ResultObjecttInfo(Integer resultCode, String resultMessage,Integer id) {
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

}
