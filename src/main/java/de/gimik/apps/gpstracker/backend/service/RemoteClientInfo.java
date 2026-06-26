package de.gimik.apps.gpstracker.backend.service;

import com.google.common.base.Strings;

import javax.servlet.http.HttpServletRequest;

public class RemoteClientInfo {
    private String ip;
    //private String webUrl;
    private String webRealPath;
    public RemoteClientInfo() {
    }

    public RemoteClientInfo(HttpServletRequest request) {
        this.ip = request.getRemoteAddr();
        this.webRealPath = request.getSession().getServletContext().getRealPath("/");
        //this.webUrl = parseWebUrl(request);
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

//    public String getWebUrl() {
//		return webUrl;
//	}

    public String getWebRealPath() {
		return webRealPath;
	}

	public void setWebRealPath(String webRealPath) {
		this.webRealPath = webRealPath;
	}

	public static RemoteClientInfo backgroundOne(String backgroundName) {
        RemoteClientInfo clientInfo = new RemoteClientInfo();

        clientInfo.setIp(backgroundName);

        return clientInfo;
    }

    public static String extractBaseUrl(HttpServletRequest request, String contextPath) {
        String scheme = request.getScheme();             // http
        String serverName = request.getServerName();     // hostname.com
        int serverPort = request.getServerPort();        // 80
        if (Strings.isNullOrEmpty(contextPath))
            contextPath = request.getContextPath();   // /mywebapp
////        String servletPath = req.getServletPath();   // /servlet/MyServlet
////        String pathInfo = req.getPathInfo();         // /a/b;c=123
////        String queryString = req.getQueryString();          // d=789
//
//        // Reconstruct original requesting URL
        StringBuffer url = new StringBuffer();
        url.append(scheme).append("://").append(serverName);

        if ((serverPort != 80) && (serverPort != 443)) {
            url.append(":").append(serverPort);
        }

        if (!Strings.isNullOrEmpty(contextPath))
            url.append("/").append(contextPath);
////        url.append(servletPath);
////
////        if (pathInfo != null) {
////            url.append(pathInfo);
////        }
////        if (queryString != null) {
////            url.append("?").append(queryString);
////        }
        return url.toString();
    }
}
