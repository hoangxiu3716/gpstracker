package de.gimik.apps.gpstracker.backend.web.RESTful;

import de.gimik.apps.gpstracker.backend.BackendException;
import de.gimik.apps.gpstracker.backend.model.TrafficSign;
import de.gimik.apps.gpstracker.backend.model.User;
import de.gimik.apps.gpstracker.backend.service.ProfileService;
import de.gimik.apps.gpstracker.backend.repository.trafficsign.TrafficSignRepository;
import de.gimik.apps.gpstracker.backend.util.Constants;
import de.gimik.apps.gpstracker.backend.web.viewmodel.ResultInfo;
import de.gimik.apps.gpstracker.backend.web.viewmodel.ResultObjecttInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

@Component
@Path("/signs") // Đường dẫn gốc: /gpstracker/rest/signs
public class TrafficSignResource {

    @Autowired
    private TrafficSignRepository trafficSignRepository;

    @Autowired
    private ProfileService profileService;

    @GET
    @Path("getAllSigns") // Link thực tế: /gpstracker/rest/signs/getAllSigns
    @Produces(MediaType.APPLICATION_JSON)
    public ResultInfo getAllSigns(@Context HttpServletRequest request) {
        // Giữ cơ chế check bảo mật token giống hệt file Metadata của bạn
        User token = profileService.getProfile(request);
        if (token == null) {
            throw new BackendException(Constants.ErrorCode.BAD_TOKEN, Constants.ERROR_MESSAGE.BAD_TOKEN);
        }

        List<TrafficSign> signs = trafficSignRepository.findAll();
        // Bọc danh sách vào ResultInfo chuẩn cấu trúc hệ thống cũ của bạn
        return new ResultInfo(Constants.OK, Constants.SUCCESS, signs);
    }

    @POST
    @Path("addSign") // Link thực tế: /gpstracker/rest/signs/addSign
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResultObjecttInfo addSign(@Context HttpServletRequest request, TrafficSign trafficSign) {
        User token = profileService.getProfile(request);
        if (token == null) {
            throw new BackendException(Constants.ErrorCode.BAD_TOKEN, Constants.ERROR_MESSAGE.BAD_TOKEN);
        }

        if (trafficSign.getCreatedAt() == null) {
            trafficSign.setCreatedAt(new Date());
        }

        TrafficSign savedSign = trafficSignRepository.save(trafficSign);
        // Bọc đối tượng đơn lẻ vào ResultObjecttInfo chuẩn hệ thống
        return new ResultObjecttInfo(Constants.OK, Constants.SUCCESS, savedSign);
    }
}