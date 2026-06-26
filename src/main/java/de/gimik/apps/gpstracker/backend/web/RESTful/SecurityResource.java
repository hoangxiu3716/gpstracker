package de.gimik.apps.gpstracker.backend.web.RESTful;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import de.gimik.apps.gpstracker.backend.BackendException;
import de.gimik.apps.gpstracker.backend.model.Employees;
import de.gimik.apps.gpstracker.backend.model.Role;
import de.gimik.apps.gpstracker.backend.model.User;
import de.gimik.apps.gpstracker.backend.security.DefaultUserDetails;
import de.gimik.apps.gpstracker.backend.service.RoleService;
import de.gimik.apps.gpstracker.backend.service.UserService;
import de.gimik.apps.gpstracker.backend.util.Constants;
import de.gimik.apps.gpstracker.backend.web.TokenUtils;
import de.gimik.apps.gpstracker.backend.web.viewmodel.ResultObjecttInfo;
import de.gimik.apps.gpstracker.backend.web.viewmodel.TokenInfo;
import de.gimik.apps.gpstracker.backend.web.viewmodel.TransferHelper;
import de.gimik.apps.gpstracker.backend.web.viewmodel.employees.UserInfo;
import de.gimik.apps.gpstracker.backend.web.viewmodel.user.UserViewInfo;

@Component
@Path("/security")
public class SecurityResource {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

    @Autowired
    @Qualifier("authenticationManager")
    private AuthenticationManager authManager;

    
    /**
     * Retrieves the currently logged in user.
     *
     * @return A transfer containing the username and the roles.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserViewInfo getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof String && principal.equals("anonymousUser")) {
            throw new WebApplicationException(401);
        }
        DefaultUserDetails userDetails = (DefaultUserDetails) principal;

        return new UserViewInfo(userDetails.getUser(), TransferHelper.createRoleMap(userDetails.getAuthorities()));
    }

    /**
     * Authenticates a user and creates an authentication token.
     *
     * @param username The name of the user.
     * @param password The password of the user.
     * @return A transfer containing the authentication token.
     */
    @Path("authenticate")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Pair<TokenInfo, Employees> authenticate(@FormParam("username") String username, @FormParam("password") String password) {
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication;
        try {
            authentication = this.authManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            /*
            * Reload user as password of authentication principal will be null after authorization and
            * password is needed for token generation
            */
            UserDetails userDetails = this.userService.loadUserByUsername(username);
            TokenInfo token = new TokenInfo(TokenUtils.createToken(userDetails,"",null,Constants.NONE));

            DefaultUserDetails defaultUserDetails = (DefaultUserDetails)userDetails;
            Pair<TokenInfo, Employees> result = null;
            if(defaultUserDetails.getEmployee() != null){
            	result = new ImmutablePair<TokenInfo, Employees>(token, defaultUserDetails.getEmployee());
            } else {
            	result = new ImmutablePair<TokenInfo, Employees>(token, null);            	
            }
            return result;
        } catch (org.springframework.security.core.AuthenticationException ex) {
            //System.out.println(ex);
        }
        return null;
    }
    
    @Path("login")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public ResultObjecttInfo login(@FormParam("username") String username, @FormParam("password") String password, @FormParam("appCode") String appCode) {
    	if (StringUtils.isEmpty(appCode) || StringUtils.isEmpty(password) || StringUtils.isEmpty(username))
			throw new BackendException(Constants.ERROR_MESSAGE.KEY_INPUT_REQUIRED);

        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication;
        try {
        	UserDetails userDetails = this.userService.loadUserByUsername(username);
        	if(userDetails!=null || authenticationToken ==null){
	            authentication = this.authManager.authenticate(authenticationToken);
	            SecurityContextHolder.getContext().setAuthentication(authentication);
	            /*
	            * Reload user as password of authentication principal will be null after authorization and
	            * password is needed for token generation
	            */
	            Object principal = authentication.getPrincipal();
	            if (principal instanceof String && principal.equals("anonymousUser")) {
	                throw new WebApplicationException(401);
	            }
	            DefaultUserDetails defaultUserDetails = (DefaultUserDetails) principal;
	            User  user = defaultUserDetails.getUser();
            	if(user.getUsername().equals(Constants.ADMIN))
            		return new ResultObjecttInfo(Constants.ErrorCode.WRONG_ROLE_LOGIN_THIS_APP,Constants.ERROR_MESSAGE.WRONG_ROLE_LOGIN_THIS_APP,"");
	            UserInfo userInfo = new UserInfo(user);
	           
	            return new ResultObjecttInfo(Constants.OK,Constants.SUCCESS,TokenUtils.createToken(userDetails,userInfo.getSessionType(),userInfo.getSessionTime(),appCode),userInfo);
        	}
        } catch (org.springframework.security.core.AuthenticationException ex) {
            ex.printStackTrace();
        }
        return new ResultObjecttInfo(Constants.ErrorCode.WRONG_USER_NAME_OR_PASS,Constants.ErrorCode.LOGIN_FAILURE,"");
    }

}
