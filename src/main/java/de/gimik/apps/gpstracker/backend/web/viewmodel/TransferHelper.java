package de.gimik.apps.gpstracker.backend.web.viewmodel;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import de.gimik.apps.gpstracker.backend.model.Employees;
import de.gimik.apps.gpstracker.backend.model.Role;
import de.gimik.apps.gpstracker.backend.model.User;
import de.gimik.apps.gpstracker.backend.web.viewmodel.employees.EmployeesInfo;
import de.gimik.apps.gpstracker.backend.web.viewmodel.user.UserViewInfo;
import org.springframework.data.domain.Page;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TransferHelper {
    public static PageInfo convertToPageTransfer(final Page page) {
        PageInfo pageInfo = new PageInfo();
        if (page == null)
            return pageInfo;

        pageInfo.setData(page.getContent());
        pageInfo.setNumber(page.getNumber());
        pageInfo.setSize(page.getSize());
        pageInfo.setTotalElements(page.getTotalElements());
        pageInfo.setTotalPages(page.getTotalPages());

        return pageInfo;
    }

    public static PageInfo convertToUserInfoPage(Page<User> userPage) {
        PageInfo page = convertToPageTransfer(userPage);

        List<Object> userInfos = Lists.newArrayList(
                Iterables.transform(userPage.getContent(), new Function<User, Object>() {
                    @Override
                    public Object apply(User user) {
                        UserViewInfo userViewInfo = new UserViewInfo(user, createRoleMapForShow(user.getRoles()),user.getRoles());
                        userViewInfo.setId(user.getId());
                        return userViewInfo;
                    }
                }));

        page.setData(userInfos);
        return page;
    }


    public static Map<String, Boolean> createRoleMapForShow(List<Role> datas) {
        Map<String, Boolean> roles = new HashMap<>();
        for (Role role : datas) {
            roles.put(role.getDescription(), Boolean.TRUE);
//            String role = authority.getAuthority();
//            role = role.toLowerCase();
//            role = role.replace("role_", "");
//            roles.put(role, Boolean.TRUE);
        }

        return roles;
    }


    
    public static Map<String, Boolean> createRoleMap(Collection<? extends GrantedAuthority> authorities) {
        Map<String, Boolean> roles = new HashMap<>();
        for (GrantedAuthority authority : authorities) {
            roles.put(authority.getAuthority(), Boolean.TRUE);
//            String role = authority.getAuthority();
//            role = role.toLowerCase();
//            role = role.replace("role_", "");
//            roles.put(role, Boolean.TRUE);
        }

        return roles;
    }
    public static Map<String, Boolean> createRoleMap(List<Role> roleDatas) {
        Map<String, Boolean> roles = new HashMap<>();
        for (Role authority : roleDatas) {
            roles.put(authority.getAuthority(), Boolean.TRUE);
//            String role = authority.getAuthority();
//            role = role.toLowerCase();
//            role = role.replace("role_", "");
//            roles.put(role, Boolean.TRUE);
        }

        return roles;
    }
 
    public static PageInfo convertToRolePage(List<Role> roles) {
        PageInfo pageInfo = new PageInfo();

        List<Object> roleInfos = Lists.newArrayList(
                Iterables.transform(roles, new Function<Role, Object>() {
                    @Override
                    public Object apply(Role role) {
                        return role;
                    }
                }));

        pageInfo.setData(roleInfos);
        pageInfo.setNumber(0);
        pageInfo.setSize(roles.size());
        pageInfo.setTotalElements(roles.size());
        pageInfo.setTotalPages(1);

        return pageInfo;
    }


    public static PageInfo convertToEmployeeInfoPage(Page<Employees> statePage) {
        PageInfo page = convertToPageTransfer(statePage);

        List<Object> stateInfos = Lists.newArrayList(
                Iterables.transform(statePage.getContent(), new Function<Employees, Object>() {
                    @Override
                    public Object apply(Employees employee) {
                        return new EmployeesInfo(employee,true);
                    }
                }));

        page.setData(stateInfos);

        return page;
    }

  
   
}
