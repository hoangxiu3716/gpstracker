package de.gimik.apps.gpstracker.backend.web.RESTful;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.gimik.apps.gpstracker.backend.BackendException;
import de.gimik.apps.gpstracker.backend.model.Role;
import de.gimik.apps.gpstracker.backend.model.User;
import de.gimik.apps.gpstracker.backend.security.DefaultUserDetails;
import de.gimik.apps.gpstracker.backend.service.ProfileService;
import de.gimik.apps.gpstracker.backend.service.RemoteClientInfo;
import de.gimik.apps.gpstracker.backend.service.RoleService;
import de.gimik.apps.gpstracker.backend.service.UserService;
import de.gimik.apps.gpstracker.backend.util.Constants;
import de.gimik.apps.gpstracker.backend.web.viewmodel.PageInfo;
import de.gimik.apps.gpstracker.backend.web.viewmodel.TransferHelper;
import de.gimik.apps.gpstracker.backend.web.viewmodel.user.ChangePasswordInput;
import de.gimik.apps.gpstracker.backend.web.viewmodel.user.UserInputInfo;
import de.gimik.apps.gpstracker.backend.web.viewmodel.user.UserViewInfo;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Path("/profile")
public class ProfileResource {

    @Autowired
    private UserService userService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private RoleService roleService;

	private int id;

	private RemoteClientInfo remoteClientInfo;
    @Path("list")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public PageInfo list(
            @DefaultValue("0") @QueryParam("page") int pageIndex,
            @DefaultValue("10") @QueryParam("size") int pageSize,
            @QueryParam("field") String field,
            @QueryParam("direction") String direction,
            @QueryParam("filter") String filters
          
    ) throws JsonGenerationException, JsonMappingException {
        //List<User> list = userService.getAllByCurrentUser(pageIndex, pageSize);
        //System.out.println("filter: " + filters);
        Map<String, String> filter = null;
        if (!StringUtils.isEmpty(filters)) {
            filter = new Gson().fromJson(filters, new TypeToken<HashMap<String, String>>() {
            }.getType());
        }

        Page<User> page = userService.getAll(pageIndex, pageSize, field, direction, filter);

        return TransferHelper.convertToUserInfoPage(page);
    }
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public UserViewInfo updateProfile(@Context HttpServletRequest request, UserInputInfo userInfo) {
        User user = userService.findByUsername(userInfo.getUsername());

        if (user == null)
            throw new BackendException(Constants.ErrorCode.USERNAME_NOT_EXIST);

        user.setFullname(userInfo.getFullname());

        if (profileService.updateProfile(new RemoteClientInfo(request), user) != null)
            return new UserViewInfo(user.getUsername(), user.getFullname(), TransferHelper.createRoleMap(user.getRoles()),user.getRoles());
        return null;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserViewInfo getProfile(@Context HttpServletRequest request) {
        User user = profileService.getProfile(request);

        if (user == null) {
            throw new WebApplicationException(401);
        }

        return new UserViewInfo(user.getUsername(), user.getFullname(), TransferHelper.createRoleMap(user.getRoles()),user.getRoles());
    }

    @Path("changePassword")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public UserViewInfo changePassword(@Context HttpServletRequest request, ChangePasswordInput passwordInput) {
        profileService.changePassword(new RemoteClientInfo(request),
                                             passwordInput.getPassword(), passwordInput.getNewPassword());

        return getProfile(request);
    }
    @Path("resetPassword")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public UserViewInfo resetPassword(@Context HttpServletRequest request) {
      profileService.resetPassword(remoteClientInfo, id);

        return getProfile(request);
    }
    
}
