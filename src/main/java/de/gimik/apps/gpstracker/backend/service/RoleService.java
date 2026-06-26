package de.gimik.apps.gpstracker.backend.service;

import java.util.List;
import java.util.Map;

import de.gimik.apps.gpstracker.backend.model.Role;
import de.gimik.apps.gpstracker.backend.model.User;

public interface RoleService {
    List<Role> getAll();

    List<Role> getAllUserRoles();

    Role getByID(long roleID);

    Role getByName(String name);

	List<Role> parseRoles(Map<String, Boolean> roleMap);

//	void deleteByUser(User user);
}
