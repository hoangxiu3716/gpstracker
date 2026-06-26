package de.gimik.apps.gpstracker.backend.repository.role;


import org.springframework.data.jpa.repository.JpaRepository;

import de.gimik.apps.gpstracker.backend.model.Role;
import de.gimik.apps.gpstracker.backend.model.User;

/**
 * Created by trung on 30.08.2014.
 */
public interface RoleRepository extends JpaRepository<Role, Long>, RoleRepositoryCustom {
    Role findByName(String name);
//    void deleteByUser(User user);
}
