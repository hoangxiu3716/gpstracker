package de.gimik.apps.gpstracker.backend.web.viewmodel;

public class MultipleLanguageInfo {
	private Integer id;
	private String english;
	private String german;
	
	public MultipleLanguageInfo() {
		super();
	}
	
	public MultipleLanguageInfo(String english, String german) {
		super();
		this.english = english;
		this.german = german;
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getEnglish() {
		return english;
	}
	public void setEnglish(String english) {
		this.english = english;
	}
	public String getGerman() {
		return german;
	}
	public void setGerman(String german) {
		this.german = german;
	}

	
	
	
}
