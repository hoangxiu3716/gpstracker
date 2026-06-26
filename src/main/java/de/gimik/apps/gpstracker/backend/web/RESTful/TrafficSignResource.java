package de.gimik.apps.gpstracker.backend.web.RESTful;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import de.gimik.apps.gpstracker.backend.model.TrafficSign;
import de.gimik.apps.gpstracker.backend.repository.trafficsign.TrafficSignRepository;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

@Component
@Path("/signs")
public class TrafficSignResource {

    @Autowired
    private TrafficSignRepository trafficSignRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSigns() {
        try {
            List<TrafficSign> signs = trafficSignRepository.findAll();
            return Response.ok(signs).build();
        } catch (Exception e) {
            return Response.serverError().entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSign(TrafficSign trafficSign) {
        try {
            // Set default created time if not provided from client
            if (trafficSign.getCreatedAt() == null) {
                trafficSign.setCreatedAt(new Date());
            }
            
            // Save to database
            TrafficSign savedSign = trafficSignRepository.save(trafficSign);
            
            return Response.ok(savedSign).build();
        } catch (Exception e) {
            return Response.serverError().entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }
}
