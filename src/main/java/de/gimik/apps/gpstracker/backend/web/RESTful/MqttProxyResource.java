package de.gimik.apps.gpstracker.backend.web.RESTful;

import de.gimik.apps.gpstracker.backend.service.MqttService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Component
@Path("/mqtt/robot/{robot_id}")
public class MqttProxyResource {

    @Autowired
    private MqttService mqttService;

    private static final int DEFAULT_QOS = 1;

    @POST
    @Path("/cmd/{service}/{path:.*}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response proxyPost(@PathParam("robot_id") String robotId,
                              @PathParam("service") String service,
                              @PathParam("path") String path,
                              String body) {
        return handleRequest("POST", robotId, service, path, body);
    }

    @GET
    @Path("/cmd/{service}/{path:.*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response proxyGet(@PathParam("robot_id") String robotId,
                             @PathParam("service") String service,
                             @PathParam("path") String path) {
        return handleRequest("GET", robotId, service, path, null);
    }

    @PUT
    @Path("/cmd/{service}/{path:.*}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response proxyPut(@PathParam("robot_id") String robotId,
                             @PathParam("service") String service,
                             @PathParam("path") String path,
                             String body) {
        return handleRequest("PUT", robotId, service, path, body);
    }

    @DELETE
    @Path("/cmd/{service}/{path:.*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response proxyDelete(@PathParam("robot_id") String robotId,
                                @PathParam("service") String service,
                                @PathParam("path") String path) {
        return handleRequest("DELETE", robotId, service, path, null);
    }

    private Response handleRequest(String method, String robotId, String service, String path, String body) {
        if (!mqttService.isConnected()) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("{\"error\": \"MQTT service is not connected\"}")
                    .build();
        }

        String topic = String.format("gpstracker/robot/%s/cmd/%s", robotId, service);
        String requestId = UUID.randomUUID().toString();

        // Construct the payload JSON object
        // Using simple string concatenation for simplicity, but a JSON library would be more robust.
        String payload = "{"
                + "\"request_id\": \"" + requestId + "\","
                + "\"method\": \"" + method + "\","
                + "\"path\": \"/" + path + "\"";

        if (body != null && !body.isEmpty()) {
            payload += ",\"body\": " + body;
        }

        payload += "}";

        mqttService.publish(topic, payload, DEFAULT_QOS);

        return Response.status(Response.Status.ACCEPTED)
                .entity("{\"message\": \"Command sent to robot via MQTT\", \"request_id\": \"" + requestId + "\"}")
                .build();
    }
}
