package org.tool.test;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
@PreAuthorize("permitAll()")
public class TestResultsController {

	@Autowired
	private TestResultRepository testResultRepoT;
	
	@Autowired
	private TestRepository testRepoT;

	@Autowired
	private AssessmentRepository asessmentRepoT;

	
	@GetMapping("/test/results/{testCode}")
	public List<TestResultsEntity> getResults(@PathVariable("testCode") String testCode){
		
		TestEntity test = testRepoT.findByTestCode( testCode );
		
		if ( test.getStartDate().equals( Date.valueOf( LocalDate.now() ) ) 
				&& Time.valueOf( LocalTime.now() ).after( test.getEndTime() ) 
				|| test.getStartDate().before( Date.valueOf( LocalDate.now() ) )){
		
			if (test.isResultsGenerated()) {
				
				List<TestResultsEntity> listOfResults = testResultRepoT.findByTestCode(testCode);
				
				for (int i = 0; i < listOfResults.size(); i++) {
					listOfResults.get(i).setStudentUsername(null);
					listOfResults.get(i).setTestCode(null);
				}
				
				return listOfResults;
			
			}else {
				
				List<AssessmentEntity> assessment = asessmentRepoT.findByTestCode(testCode);
				
				HashMap<String, Integer> resultMap = new HashMap<String, Integer>();
				HashMap<String, String> emailToName = new HashMap<String, String>();
				
				for (int i = 0; i < assessment.size(); i++) {
					
					if ( !resultMap.containsKey(assessment.get(i).getStudentUsername())  ) {
						resultMap.put(assessment.get(i).getStudentUsername(),  assessment.get(i).getMarks());
						emailToName.put(assessment.get(i).getStudentUsername(), assessment.get(i).getStudentName());
					}else {
						int add = resultMap.get(assessment.get(i).getStudentUsername());
						resultMap.replace(assessment.get(i).getStudentUsername(), add + assessment.get(i).getMarks());
					}
				}
				
				List<TestResultsEntity> listOfResults = new ArrayList<TestResultsEntity>();
				resultMap.forEach((k, v)->{
					
					TestResultsEntity r = 
							new TestResultsEntity(testCode, k, emailToName.get(k), v);
					listOfResults.add(r);
				});
				
				testResultRepoT.saveAll(listOfResults);
				test.setResultsGenerated(true);
				testRepoT.save(test);
				
				for (int i = 0; i < listOfResults.size(); i++) {
					listOfResults.get(i).setStudentUsername(null);
					listOfResults.get(i).setTestCode(null);
				}
				
				return listOfResults;
			}
		}
		return null;
	}
	
	
	
	
}
