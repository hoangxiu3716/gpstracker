package de.gimik.apps.gpstracker.backend.web.RESTful;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

/**
 * Proxy Resource for Robot API
 * Forwards requests to https://docs.techvisioncloud.pl
 */
@Component
@Path("/robot-api")
public class RobotProxyResource {

    private static final String ROBOT_API_BASE_URL = "https://api.techvisioncloud.pl";

    /**
     * Proxy GET requests
     */
    @GET
    @Path("{path:.*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response proxyGet(@PathParam("path") String path, @Context HttpServletRequest request) {
        return proxyRequest("GET", path, null, request);
    }

    /**
     * Proxy POST requests
     */
    @POST
    @Path("{path:.*}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response proxyPost(@PathParam("path") String path, String body, @Context HttpServletRequest request) {
        return proxyRequest("POST", path, body, request);
    }

    /**
     * Proxy PUT requests
     */
    @PUT
    @Path("{path:.*}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response proxyPut(@PathParam("path") String path, String body, @Context HttpServletRequest request) {
        return proxyRequest("PUT", path, body, request);
    }

    /**
     * Proxy DELETE requests
     */
    @DELETE
    @Path("{path:.*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response proxyDelete(@PathParam("path") String path, @Context HttpServletRequest request) {
        return proxyRequest("DELETE", path, null, request);
    }

    /**
     * Forward request to Robot API
     */
    private Response proxyRequest(String method, String path, String body, HttpServletRequest request) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        
        try {
            // Build target URL
            String queryString = request.getQueryString();
            String targetUrl = ROBOT_API_BASE_URL + "/" + path;
            if (queryString != null && !queryString.isEmpty()) {
                targetUrl += "?" + queryString;
            }

            // Create request based on method
            HttpRequestBase httpRequest;
            switch (method.toUpperCase()) {
                case "POST":
                    HttpPost postRequest = new HttpPost(targetUrl);
                    if (body != null && !body.isEmpty()) {
                        postRequest.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
                    }
                    postRequest.setHeader("Content-Type", "application/json");
                    httpRequest = postRequest;
                    break;
                case "PUT":
                    HttpPut putRequest = new HttpPut(targetUrl);
                    if (body != null && !body.isEmpty()) {
                        putRequest.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
                    }
                    putRequest.setHeader("Content-Type", "application/json");
                    httpRequest = putRequest;
                    break;
                case "DELETE":
                    httpRequest = new HttpDelete(targetUrl);
                    break;
                default:
                    httpRequest = new HttpGet(targetUrl);
                    break;
            }

            // Set common headers
            httpRequest.setHeader("Accept", "application/json");
            
            // Forward authorization headers if present
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null) {
                httpRequest.setHeader("Authorization", authHeader);
            }
            
            // Forward X-Auth-Token header if present
            String authToken = request.getHeader("X-Auth-Token");
            if (authToken != null) {
                httpRequest.setHeader("X-Auth-Token", authToken);
            }

            // Execute request
            HttpResponse response = httpClient.execute(httpRequest);
            
            // Get response
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = "";
            if (response.getEntity() != null) {
                responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }

            // Return response with CORS headers
            return Response.status(statusCode)
                    .entity(responseBody)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Auth-Token")
                    .build();

        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Failed to connect to Robot API\", \"message\": \"" + e.getMessage().replace("\"", "'") + "\"}")
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Proxy error\", \"message\": \"" + e.getMessage().replace("\"", "'") + "\"}")
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handle CORS preflight requests
     */
    @OPTIONS
    @Path("{path:.*}")
    public Response handleCors() {
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Auth-Token")
                .header("Access-Control-Max-Age", "86400")
                .build();
    }
}
