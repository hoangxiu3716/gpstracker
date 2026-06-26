package de.gimik.apps.gpstracker.backend.web.viewmodel;

import java.io.InputStream;



public class FileUploadInfo {
	private String fileUrl;
	private String base64Data;
	private String qrcodeText;
	private InputStream inputStream;
	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	
	public FileUploadInfo() {
		super();
	}

	public FileUploadInfo(String fileUrl) {
		super();
		this.fileUrl = fileUrl;
	}

	public FileUploadInfo(String fileUrl, String base64Data) {
		super();
		this.fileUrl = fileUrl;
		this.base64Data = base64Data;
	}
	public FileUploadInfo(String fileUrl, String base64Data,String qrcodeText) {
		super();
		this.fileUrl = fileUrl;
		this.base64Data = base64Data;
		this.qrcodeText = qrcodeText;
	}
	
	
	public FileUploadInfo(String qrcodeText, InputStream inputStream) {
		super();
		this.qrcodeText = qrcodeText;
		this.inputStream = inputStream;
	}

	public String getBase64Data() {
		return base64Data;
	}

	public void setBase64Data(String base64Data) {
		this.base64Data = base64Data;
	}

	public String getQrcodeText() {
		return qrcodeText;
	}

	public void setQrcodeText(String qrcodeText) {
		this.qrcodeText = qrcodeText;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
}
