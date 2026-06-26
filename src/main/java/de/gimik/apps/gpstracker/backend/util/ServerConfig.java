package de.gimik.apps.gpstracker.backend.util;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class ServerConfig {
    private String hostFileUpload;
    private String directoryFileUpload;
    private Map<String, String> staticImages;
    private String sqliteTemplate;
    private String pushUrl;
    private String host;
    
    public String getSqliteTemplate() {
		return sqliteTemplate;
	}

	public void setSqliteTemplate(String sqliteTemplate) {
		this.sqliteTemplate = sqliteTemplate;
	}

	public String getHostFileUpload() {
        return hostFileUpload;
    }

    public void setHostFileUpload(String hostFileUpload) {
        this.hostFileUpload = hostFileUpload;
    }

    public String getDirectoryFileUpload() {
        return directoryFileUpload;
    }

    public void setDirectoryFileUpload(String directoryFileUpload) {
        this.directoryFileUpload = directoryFileUpload;
    }

    public Map<String, String> getStaticImages() {
        return staticImages;
    }

    public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	@PostConstruct
    public void init() {
        staticImages = new LinkedHashMap<>();
        URL url = this.getClass().getClassLoader().getResource("/static-images");
        File file = null;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            file = new File(url.getPath());
        }
        if (file != null) {
            File[] files = file.listFiles();
            if (files.length > 0) {
                try {
                    DocumentBuilderFactory documentFactory = DocumentBuilderFactory
                            .newInstance();
                    DocumentBuilder documentBuilder = documentFactory
                            .newDocumentBuilder();
                    for (File xmlFile : files) {
                        try {
                            Document doc = documentBuilder.parse(xmlFile);
                            String docBase = doc.getDocumentElement().getAttribute("docBase");
                            directoryFileUpload = docBase;
                            String filename = FilenameUtils.getBaseName(xmlFile.getName());
//                            hostFileUpload = filename;
                            staticImages.put(filename,docBase);
                        } catch (Exception e) {

                        }
                    }
                } catch (Exception e) {

                }
            }
        }
    }

	public String getPushUrl() {
		return pushUrl;
	}

	public void setPushUrl(String pushUrl) {
		this.pushUrl = pushUrl;
	}
    
    
    
}
