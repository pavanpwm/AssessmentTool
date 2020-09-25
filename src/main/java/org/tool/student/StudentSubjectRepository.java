package org.tool.student;


import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;




@Repository
public interface StudentSubjectRepository extends CrudRepository<StudentSubjectEntity, String> {
	
	
	boolean existsSubjectSubjectEntiyById(String id);
	
	StudentSubjectEntity findStudentSubjectEntityById(String id);
	
	
	
	

}
