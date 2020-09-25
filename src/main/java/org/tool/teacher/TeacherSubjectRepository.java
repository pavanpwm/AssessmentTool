package org.tool.teacher;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;




@Repository
public interface TeacherSubjectRepository extends CrudRepository<TeacherSubjectEntity, String> {
	
	boolean existsTeacherSubjectEntityById(String id);
	
	TeacherSubjectEntity findTeacherSubjectEntityById(String id);
	

}
