package de.gimik.apps.gpstracker.backend.web.RESTful;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;
import de.gimik.apps.gpstracker.backend.BackendException;
import de.gimik.apps.gpstracker.backend.model.User;
import de.gimik.apps.gpstracker.backend.service.ProfileService;
import de.gimik.apps.gpstracker.backend.service.RemoteClientInfo;
import de.gimik.apps.gpstracker.backend.service.RoleService;
import de.gimik.apps.gpstracker.backend.service.UserService;
import de.gimik.apps.gpstracker.backend.util.Constants;
import de.gimik.apps.gpstracker.backend.util.FileUtil;
import de.gimik.apps.gpstracker.backend.util.ServerConfig;
import de.gimik.apps.gpstracker.backend.web.viewmodel.PageInfo;
import de.gimik.apps.gpstracker.backend.web.viewmodel.ResultInfo;
import de.gimik.apps.gpstracker.backend.web.viewmodel.ResultObjecttInfo;
import de.gimik.apps.gpstracker.backend.web.viewmodel.TransferHelper;
import de.gimik.apps.gpstracker.backend.web.viewmodel.employees.UserInfo;
import de.gimik.apps.gpstracker.backend.web.viewmodel.user.ChangePasswordInput;
import de.gimik.apps.gpstracker.backend.web.viewmodel.user.UserInputInfo;
import de.gimik.apps.gpstracker.backend.web.viewmodel.user.UserViewInfo;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Path("/metadata")
public class MetadataResource {

    @Autowired
    private UserService userService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ServerConfig serverConfig;

    @Path("updateProfile")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ResultObjecttInfo updateProfile(@Context HttpServletRequest request, UserInputInfo userInfo) {
        User user = userService.findByUsername(userInfo.getUsername());
        User token = profileService.getProfile(request);
        if (token == null)
            throw new BackendException(Constants.ErrorCode.BAD_TOKEN, Constants.ERROR_MESSAGE.BAD_TOKEN);
        if (user == null)
            throw new BackendException(Constants.ErrorCode.USERNAME_NOT_EXIST);

        user = User.copyData(user, userInfo);
        userService.save(new RemoteClientInfo(), user);
        UserViewInfo result = new UserViewInfo(user, TransferHelper.createRoleMapForShow(user.getRoles()));
        return new ResultObjecttInfo(Constants.OK, Constants.SUCCESS, result);
    }
    @GET
    @Path("getAllUser")
    @Produces(MediaType.APPLICATION_JSON)
    public ResultInfo getAllEmployee(@Context HttpServletRequest request) {
        User token = profileService.getProfile(request);
        if (token == null)
            throw new BackendException(Constants.ErrorCode.BAD_TOKEN, Constants.ERROR_MESSAGE.BAD_TOKEN);
        List<UserViewInfo> userInfos = new ArrayList<UserViewInfo>();

        List<User> users = userService.getAllUser();
        userInfos = Lists.newArrayList(
                Iterables.transform(users, new Function<User, UserViewInfo>() {
                    @Override
                    public UserViewInfo apply(User item) {
                        return new  UserViewInfo(item, TransferHelper.createRoleMapForShow(item.getRoles()));
                    }
                }));
        return new ResultInfo(Constants.OK, Constants.SUCCESS,userInfos);
    }
}
