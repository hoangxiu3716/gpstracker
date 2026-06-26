package de.gimik.apps.gpstracker.backend.web;

import org.apache.commons.io.IOUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import com.google.gson.Gson;

import de.gimik.apps.gpstracker.backend.service.UserService;
import de.gimik.apps.gpstracker.backend.util.Constants;
import de.gimik.apps.gpstracker.backend.web.viewmodel.ResultInfo;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.io.PrintWriter;


public class AuthenticationTokenProcessingFilter extends GenericFilterBean {

    private final UserService userservice;


    public AuthenticationTokenProcessingFilter(UserService userService) {
        this.userservice = userService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest httpRequest = AuthenticationTokenProcessingFilter.getAsHttpRequest(request);

        String authToken = AuthenticationTokenProcessingFilter.extractAuthTokenFromRequest(httpRequest);
        
//        System.out.println(IOUtils.toString(httpRequest.getReader()));
        if(!StringUtils.isEmpty(authToken)){
	        String userName = TokenUtils.getUserNameFromToken(authToken);
	
	        if (userName != null) {
	
	            UserDetails userDetails = this.userservice.loadUserByUsername(userName);
	            if(userDetails == null) {
	            	setUnauthorizedResponse((HttpServletResponse) response);
	                return;
	            }
	            if (TokenUtils.validateToken(authToken, userDetails)) {
	
	                UsernamePasswordAuthenticationToken authentication =
	                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
	                SecurityContextHolder.getContext().setAuthentication(authentication);
	            }
	        }
        }
       
        chain.doFilter(request, response);
    }

    public static void setUnauthorizedResponse(HttpServletResponse response) {
        response.setStatus(Constants.OK);
        PrintWriter writer;
		try {
			writer = response.getWriter();
			ResultInfo error = new ResultInfo(Constants.ErrorCode.BAD_TOKEN,Constants.ERROR_MESSAGE.BAD_TOKEN);
	        writer.write(new Gson().toJson(error));
	        writer.flush();
	        writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        

    }
    
    public static HttpServletRequest getAsHttpRequest(ServletRequest request) {
        if (!(request instanceof HttpServletRequest)) {
            throw new RuntimeException("Expecting an HTTP request");
        }

        return (HttpServletRequest) request;
    }


    public static String extractAuthTokenFromRequest(HttpServletRequest httpRequest) {
        /* Get token from header */
        String authToken = httpRequest.getHeader("X-Auth-Token");

		/* If token not found get it from request parameter */
        if (authToken == null) {
            authToken = httpRequest.getParameter("token");
        }

        return authToken;
    }
}