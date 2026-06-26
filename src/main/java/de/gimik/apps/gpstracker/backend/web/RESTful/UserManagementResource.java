package de.gimik.apps.gpstracker.backend.web.RESTful;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.gimik.apps.gpstracker.backend.BackendException;
import de.gimik.apps.gpstracker.backend.model.Role;
import de.gimik.apps.gpstracker.backend.model.User;
import de.gimik.apps.gpstracker.backend.security.MD5Encoder;
import de.gimik.apps.gpstracker.backend.service.ProfileService;
import de.gimik.apps.gpstracker.backend.service.RemoteClientInfo;
import de.gimik.apps.gpstracker.backend.service.RoleService;
import de.gimik.apps.gpstracker.backend.service.UserService;
import de.gimik.apps.gpstracker.backend.util.Constants;
import de.gimik.apps.gpstracker.backend.web.TokenUtils;
import de.gimik.apps.gpstracker.backend.web.viewmodel.PageInfo;
import de.gimik.apps.gpstracker.backend.web.viewmodel.ResultInfo;
import de.gimik.apps.gpstracker.backend.web.viewmodel.ResultObjecttInfo;
import de.gimik.apps.gpstracker.backend.web.viewmodel.TransferHelper;
import de.gimik.apps.gpstracker.backend.web.viewmodel.user.UserInputInfo;
import de.gimik.apps.gpstracker.backend.web.viewmodel.user.UserViewInfo;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
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
@Path("/manage/user")
public class UserManagementResource {

    @Autowired
    private UserService userService;
    @Autowired
    private ProfileService profileService;

    @Autowired
    private RoleService roleService;
    @Autowired
    private MD5Encoder passwordEncoder;
    @DELETE
    @Path("{id}")
    public void delete(@Context HttpServletRequest request, @PathParam("id") Integer id) {
    	User user = userService.getByID(id);
    	if(user != null) {
	        userService.delete(new RemoteClientInfo(request), id);
    	}
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ResultObjecttInfo create(@Context HttpServletRequest request, UserInputInfo userInfo) {
    	if(StringUtils.isEmpty(userInfo.getUsername()) || StringUtils.isEmpty(userInfo.getPassword()))
    		throw new BackendException(Constants.ERROR_MESSAGE.KEY_INPUT_REQUIRED);
        User user = new User();

//        user.setUsername(userInfo.getUsername());
//        user.setPassword(userInfo.getPassword());
//        user.setFullname(userInfo.getFullname());
        user = User.copyData(user, userInfo);
        user.setRoles(parseRoles(userInfo));
        user.setPassword(userInfo.getPassword());
        user.setUsername(userInfo.getUsername());
        userService.addNewUser(new RemoteClientInfo(request), user);
//        UserViewInfo data =  new UserViewInfo(user.getUsername(), user.getFullname(), TransferHelper.createRoleMap(user.getRoles()),user.getRoles());
        return new ResultObjecttInfo(Constants.OK, Constants.SUCCESS);	
    }

    private List<Role> parseRoles(UserInputInfo userInfo) {
       return parseRoles(userInfo, false);
    }
    
    private List<Role> parseRoles(UserInputInfo userInfo,boolean hasRoleEmployee) {
        if (userInfo.getRoles() != null) {
            List<Role> roles = new ArrayList<>();

            for (Map.Entry<String, Boolean> roleMapEntry : userInfo.getRoles().entrySet()) {
                if (roleMapEntry.getValue()) {
                    String roleName = roleMapEntry.getKey();
                    if(!hasRoleEmployee &&  StringUtils.isEmpty(roleName))
                		continue;
                    Role role = roleService.getByName(roleName);

                    if (role != null) {
                        roles.add(role);
                    }
                }
            }

            return roles;
        }

        return null;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public ResultObjecttInfo update(@Context HttpServletRequest request, @PathParam("id") Long id, UserInputInfo userInfo) {
        User user = userService.findByUsername(userInfo.getUsername());

        if (user == null)
            throw new WebApplicationException(404);
        boolean hasRoleEmployee = false;
        for(Role role : user.getRoles()){
        	if(role.getName() != null &&  role.getName().equals(Constants.ROLE_CUSTOMER)){
        		hasRoleEmployee = true;
        		break;
        	}
        }
        user.setUsername(userInfo.getUsername());
        user.setFullname(userInfo.getFullname());
        user.setRoles(parseRoles(userInfo,hasRoleEmployee));

        userService.save(new RemoteClientInfo(request), user);
//            return new UserViewInfo(user.getUsername(), user.getFullname(), TransferHelper.createRoleMap(user.getRoles()),user.getRoles());
        return new ResultObjecttInfo(Constants.OK, Constants.SUCCESS);	
    }

    @Path("list")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public PageInfo list(
            @DefaultValue("0") @QueryParam("page") int pageIndex,
            @DefaultValue("10") @QueryParam("size") int pageSize,
            @QueryParam("field") String field,
            @QueryParam("direction") String direction,
            @QueryParam("filter") String filters,
            @QueryParam("appId") Integer appId
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public UserViewInfo read(@PathParam("id") int id) {
        User user = userService.getByID(id);

        if (user == null) {
            throw new WebApplicationException(404);
        }

        return new UserViewInfo(user, TransferHelper.createRoleMap(user.getRoles()),user.getRoles());
    }
    
	@GET
	@Path("updateDeviceIdKey")
	@Produces(MediaType.APPLICATION_JSON)
	public ResultInfo updateDeviceIdKey(@Context HttpServletRequest request,
			@QueryParam("deviceIdKey") String deviceIdKey) {
		User user = profileService.getProfile(request);
		if (user == null)
			throw new BackendException(Constants.ErrorCode.BAD_TOKEN, Constants.ERROR_MESSAGE.BAD_TOKEN);
		user.setDeviceIdKey(deviceIdKey);
		userService.save(new RemoteClientInfo(request), user);
		return new ResultInfo(Constants.OK, Constants.SUCCESS);
	}
	
	@POST
	@Path("changePassword")
	@Produces(MediaType.APPLICATION_JSON)
	public ResultObjecttInfo changePassword(@Context HttpServletRequest request, @FormParam("password") String password,
			@FormParam("newPassword") String newPassword,
			@FormParam("appCode") String appCode) {
		if (StringUtils.isEmpty(password) || StringUtils.isEmpty(newPassword) || StringUtils.isEmpty(appCode))
			throw new BackendException(Constants.ERROR_MESSAGE.KEY_INPUT_REQUIRED);
		User user = profileService.getProfile(request);
		if (user == null)
			throw new BackendException(Constants.ErrorCode.USER_NOT_EXIST, Constants.ERROR_MESSAGE.USER_NOT_EXIST);
		String passwordEncode = passwordEncoder.encode(password);
		if (!passwordEncode.equals(user.getPassword()))
			throw new BackendException(Constants.ErrorCode.PASSWORD_INCORRECT, Constants.ERROR_MESSAGE.PASSWORD_INCORRECT);
		String newPassEncode = passwordEncoder.encode("1234");
		user.setPassword(newPassEncode);
		user.setPasswordChanged(true);
		UserDetails userDetails = this.userService.loadUserByUsername(user.getUsername());
    	if(userDetails!=null ){
    		userService.save(new RemoteClientInfo(request), user);
    		return new ResultObjecttInfo(Constants.OK,Constants.SUCCESS,TokenUtils.createToken(userDetails,user.getSessionType(),user.getSessionTime(),appCode,newPassEncode));
    	}
		
    	return new ResultObjecttInfo(Constants.ErrorCode.USER_NOT_EXIST,Constants.ERROR_MESSAGE.USER_NOT_EXIST);
	}
	@POST
	@Path("resetPassword")
	@Produces(MediaType.APPLICATION_JSON)
	public ResultObjecttInfo resetPassword(@Context HttpServletRequest request, @FormParam("userId") Integer userId) {
		if (userId == null)
			throw new BackendException(Constants.ERROR_MESSAGE.KEY_INPUT_REQUIRED);
		User token = profileService.getProfile(request);
		if (token == null || (!token.isAdmin() ))
			throw new BackendException(Constants.ErrorCode.BAD_TOKEN, Constants.ERROR_MESSAGE.BAD_TOKEN);
		User user = userService.getByID(userId);
		if(user == null )
			throw new BackendException(Constants.ErrorCode.USER_NOT_EXIST, Constants.ERROR_MESSAGE.USER_NOT_EXIST);
		
		String password =  passwordEncoder.encode("1234");
//		String passwordEncode = password;
		user.setPassword(password);
		userService.save(new RemoteClientInfo(request), user);
	
    	return new ResultObjecttInfo(Constants.OK,Constants.SUCCESS);
	}
	@GET
	@Path("getAllEmployee")
	@Produces(MediaType.APPLICATION_JSON)
	public ResultInfo getAllEmployee(@Context HttpServletRequest request) {

		List<UserViewInfo> userInfos = new ArrayList<UserViewInfo>();

		 List<User> users = userService.getAllEmployee();
		userInfos = Lists.newArrayList(
		        Iterables.transform(users, new Function<User, UserViewInfo>() {
		            @Override
		            public UserViewInfo apply(User item) {
		                return new  UserViewInfo(item, TransferHelper.createRoleMapForShow(item.getRoles()));
		            }
				}));
		return new ResultInfo(Constants.OK, Constants.SUCCESS,userInfos);
	}
    @POST
    @Path("deleteUser")
    @Produces(MediaType.APPLICATION_JSON)
    public ResultObjecttInfo deleteUser(@Context HttpServletRequest request, @FormParam("userId") Integer userId) {
        if (userId == null)
            throw new BackendException(Constants.ERROR_MESSAGE.KEY_INPUT_REQUIRED);
        User token = profileService.getProfile(request);
        if (token == null || (!token.isAdmin() ))
            throw new BackendException(Constants.ErrorCode.BAD_TOKEN, Constants.ERROR_MESSAGE.BAD_TOKEN);
        User user = userService.getByID(userId);
        if(user == null )
            throw new BackendException(Constants.ErrorCode.USER_NOT_EXIST, Constants.ERROR_MESSAGE.USER_NOT_EXIST);
        user.setDeleted(true);
        user.setActive(false);
        userService.save(new RemoteClientInfo(request), user);

        return new ResultObjecttInfo(Constants.OK,Constants.SUCCESS);
    }
}
