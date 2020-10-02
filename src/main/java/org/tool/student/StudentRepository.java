package org.tool.student;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;






@Repository
public interface StudentRepository extends CrudRepository<StudentEntity, Integer> {
	
	boolean existsStudentEntityByEmail(String email);
	
	
	

}
