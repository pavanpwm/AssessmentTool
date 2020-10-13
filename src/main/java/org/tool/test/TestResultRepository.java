package org.tool.test;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface TestResultRepository extends CrudRepository<TestResultsEntity, Integer>{

	List<TestResultsEntity> findByTestCode(String testCode);
	
}
