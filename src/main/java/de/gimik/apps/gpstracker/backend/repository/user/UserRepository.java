package de.gimik.apps.gpstracker.backend.repository.user;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.gimik.apps.gpstracker.backend.model.User;



public interface UserRepository extends JpaRepository<User, Integer>, UserRepositoryCustom
{
	User findByUsername(String username);
	User findByUsernameAndPassword(String username,String password);
	List<User> findByIdIn(List<Integer> ids);
}