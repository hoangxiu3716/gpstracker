package de.gimik.apps.gpstracker.backend.repository.employees;


import org.springframework.data.jpa.repository.JpaRepository;

import de.gimik.apps.gpstracker.backend.model.Employees;
import de.gimik.apps.gpstracker.backend.model.User;




public interface EmployeesRepository extends JpaRepository<Employees, Integer>, EmployeesRepositoryCustom {
	Employees findByIdAndUser(Integer id,User user);
	Employees findByUser(User user);
}