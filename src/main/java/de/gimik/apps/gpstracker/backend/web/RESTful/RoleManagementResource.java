package de.gimik.apps.gpstracker.backend.web.RESTful;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.gimik.apps.gpstracker.backend.model.Role;
import de.gimik.apps.gpstracker.backend.service.RoleService;
import de.gimik.apps.gpstracker.backend.web.viewmodel.PageInfo;
import de.gimik.apps.gpstracker.backend.web.viewmodel.TransferHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.util.List;

@Component
@Path("/manage/role")
public class RoleManagementResource {
    @Autowired
    private RoleService roleService;

    @Path("list")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public PageInfo list() throws JsonGenerationException, JsonMappingException {
        return TransferHelper.convertToRolePage(roleService.getAll());
    }

    @Path("listManagementRoles")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Role> listManagementRoles() throws JsonGenerationException, JsonMappingException {
        return roleService.getAllUserRoles();
    }

}
