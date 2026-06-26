package de.gimik.apps.gpstracker.backend;

import de.gimik.apps.gpstracker.backend.util.Constants;

public class BackendException extends RuntimeException {

	private static final long serialVersionUID = 2024934252853606808L;
	private Integer resultCode;
	private String resultMessage;
	private Integer id;
	private String token;
	private Object data;
	public BackendException(Integer resultCode){
		super();
		
		this.resultCode = resultCode;
	}
	public BackendException(String resultMessage){
		super();
		this.resultCode = Constants.ERROR;
		this.resultMessage = resultMessage;
		this.id =null;
		this.token ="";
		this.data = null;
	}
	public BackendException(Integer resultCode,String resultMessage){
		super(resultMessage);
		this.resultCode = resultCode;
		this.resultMessage = resultMessage;
		this.id =null;
		this.token ="";
		this.data = null;
	}
	
	public BackendException(Exception innerException, Integer resultCode){
		super(innerException);
		
		this.resultCode = resultCode;
	}
	
	public BackendException(String message, Exception innerException, Integer resultCode){
		super(message, innerException);
		
		this.resultCode = resultCode;
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

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	
}
