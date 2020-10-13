package org.tool.test;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssessmentRepository extends CrudRepository<AssessmentEntity, Integer>{
	
	List<AssessmentEntity> findByTestCodeAndStudentUsername(String testCode, String studentUsername);
	
	boolean existsByTestCodeAndStudentUsername(String testCode, String studentUsername);

	AssessmentEntity findByTestCodeAndQuestionIdAndStudentUsername(String testCode, int questionId, String studentUsername);
	
	List<AssessmentEntity> findByTestCode(String testCode);
}
