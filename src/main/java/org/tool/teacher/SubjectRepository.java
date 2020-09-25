package org.tool.teacher;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;




@Repository
public interface SubjectRepository extends CrudRepository<SubjectEntity, String> {
	
	
	

}
