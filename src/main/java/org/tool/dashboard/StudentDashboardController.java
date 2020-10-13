package org.tool.dashboard;

import java.security.Principal;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.tool.auth.UserRepository;
import org.tool.question.collection.CollectionRepository;
import org.tool.question.collection.QuestionEntity;
import org.tool.question.collection.QuestionRepository;
import org.tool.reponse.ResponseMessage;
import org.tool.student.StudentEntity;
import org.tool.student.StudentRepository;
import org.tool.student.StudentSubjectEntity;
import org.tool.student.StudentSubjectRelationEntityRepository;
import org.tool.student.StudentSubjectRepository;
import org.tool.teacher.TeacherRepository;
import org.tool.teacher.TeacherSubjectEntity;
import org.tool.teacher.TeacherSubjectRepository;
import org.tool.test.AssessmentEntity;
import org.tool.test.AssessmentRepository;
import org.tool.test.TestEntity;
import org.tool.test.TestRepository;

@RestController
@PreAuthorize("hasRole('ROLE_STUDENT')")
public class StudentDashboardController {

	@Autowired
	private TeacherRepository teacherRepoS;

	@Autowired
	private TeacherSubjectRepository teacherSubjectRepoS;

	@Autowired
	private StudentRepository studentRepoS;

	@Autowired
	private StudentSubjectRepository studSubRepoS;

	@Autowired
	private UserRepository userRepoS;

	@Autowired
	private CollectionRepository collectionRepoS;

	@Autowired
	private QuestionRepository questionRepoS;

	@Autowired
	private TestRepository testRepoS;

	@Autowired
	private ResponseMessage responseMessageS;
	
	@Autowired
	private StudentSubjectRelationEntityRepository studSubRelRepoS;
	
	@Autowired
	private AssessmentRepository assessmentRepoS;

	

	
	
	
	@GetMapping("/student/details")
	public StudentEntity getStudentDetails(Principal principal) {
		
		StudentEntity student = studentRepoS.findByEmail(principal.getName());
		student.setPassword(null);
		student.getSubjectList().clear();
		
		return student;
	}
	

	
	// this method will show you how to tackle infinite references
	@GetMapping("/student/subject/list")
	public List<StudentSubjectEntity> getAllStudenSubjectEntities(Principal principal) {

		StudentEntity student = studentRepoS.findByEmail(principal.getName());
		for (int i = 0; i < student.getSubjectList().size(); i++) {
			student.getSubjectList().get(i).getStudentsList().clear();
		}
		return student.getSubjectList();
	}
	
	
	@PostMapping("/student/add/subject")
	public ResponseMessage addStudentSubject(@RequestBody StudentSubjectEntity studentSubject, Principal principal) {
		
		StudentEntity student = studentRepoS.findByEmail(principal.getName());
		TeacherSubjectEntity teacherSubject;
		if ( teacherSubjectRepoS.existsTeacherSubjectEntityById( studentSubject.getId()) ) {
			
			teacherSubject = teacherSubjectRepoS.findTeacherSubjectEntityById( studentSubject.getId() );
			studentSubject.setName( teacherSubject.getName() );
			studentSubject.getStudentsList().add(student);
			student.getSubjectList().add(studentSubject);
			studentRepoS.save(student);
			
			responseMessageS.setStatus("added");
		}else {
			responseMessageS.setStatus("failed");
		}
		
		return responseMessageS;
	}
	
	
	
	@Transactional
	@DeleteMapping("/student/delete/{subjectId}/{studentId}")
	public ResponseMessage removeStudentFromSubject(@PathVariable("studentId") String studentId,
			@PathVariable("subjectId") String subjectId, Principal principal) {
		
		studSubRelRepoS.removeByStudentIdAndSubjectId(subjectId, studentId);
	    responseMessageS.setStatus("removed");
		return responseMessageS;
		
	}
	
	
	
	
	@GetMapping("/student/subject/{subjectcode}/student/list")
	public StudentSubjectEntity getAllStudenSubjectEntities(@PathVariable("subjectcode") String id,
			Principal principal) {

		// first we need to check if the subject id matches with the current logged in user
		
		StudentEntity student =   studentRepoS.findByEmail(principal.getName());
		
		for (int i = 0; i < student.getSubjectList().size(); i++) {
			if (student.getSubjectList().get(i).getId().equalsIgnoreCase(id)) {
				StudentSubjectEntity subject = studSubRepoS.findStudentSubjectEntityById(id);
				for (int j = 0; j < subject.getStudentsList().size(); j++) {
					// clear subject list from each subject list property of student entity to avoid infinite references
					subject.getStudentsList().get(j).getSubjectList().clear();		//to tackle infinite references
					// unnecessary fields will be emptied or nullified so that JsonIgnore can ignore them in results.
					subject.getStudentsList().get(j).setEmail(null);
					subject.getStudentsList().get(j).setPassword(null);
					subject.getStudentsList().get(j).setPhone(null); // phone is BigInteger and not primitive so null is valid
				}
				return subject;
			}
		}
		
		return null;
		
	}
	
	
	
	
	
	
	
	
	@GetMapping("/student/test/list")
	public List<TestEntity>  getStudentSubjectTests(Principal principal){
		
		List<TestEntity> tests = new ArrayList<TestEntity>();
		
		studentRepoS.findByEmail(principal.getName()).getSubjectList().forEach(eachSubject -> {
			tests.addAll( testRepoS.findBySubjectCode( eachSubject.getId() ) ); 
				
		});
		
		return tests;
	}
	
	
	
	
	
	@GetMapping("/student/test/details/{testCode}")
	public TestEntity getTestByIdForStudent(@PathVariable("testCode") String testCode, Principal principal) {
		
		StudentEntity student = studentRepoS.findByEmail(principal.getName());
			for (int i = 0; i < student.getSubjectList().size(); i++) {
				if (testRepoS.existsByTestCodeAndSubjectCode(testCode, student.getSubjectList().get(i).getId()) ) {
					return testRepoS.findByTestCode(testCode);
				}
			}
		return null;
	}
	

	
	
	
	
	
	@GetMapping("/student/generate/questions/{testCode}")
	public List<AssessmentEntity> generateAssessmentQuestions( @PathVariable("testCode") String testCode, Principal principal ){
		
		StudentEntity student = studentRepoS.findByEmail(principal.getName());
		for (int i = 0; i < student.getSubjectList().size(); i++) {
			if (testRepoS.existsByTestCodeAndSubjectCode(testCode, student.getSubjectList().get(i).getId()) ) {

				TestEntity test = testRepoS.findByTestCode(testCode);
				
				if ( test.getStartDate().equals( Date.valueOf( LocalDate.now() ) ) 
					&& Time.valueOf( LocalTime.now() ).after( test.getStartTime() )
					&& Time.valueOf( LocalTime.now() ).before( test.getEndTime() ) ){
					
					if (test.getTestStatus().equalsIgnoreCase("pending")) {
						test.setTestStatus("ongoing");
					}
					
					if (assessmentRepoS.existsByTestCodeAndStudentUsername(testCode, principal.getName())) {
						return assessmentRepoS.findByTestCodeAndStudentUsername(testCode, principal.getName());
						
					}else {
						List<QuestionEntity> questionList = 
								questionRepoS.findByTeacherUsernameAndCollectionCode( test.getTeacherUsername(), test.getCollectionCode());
						Collections.shuffle(questionList);
						
						for (int j = 0; j < test.getTotalQuestions(); j++) {
							AssessmentEntity asessmentQuestion =
							  new AssessmentEntity(test.getTestCode(), student.getName(), principal.getName(), test.getSubjectCode(), questionList.get(i).getTopic(), i , questionList.get(i).getQuestionId());
							assessmentRepoS.save(asessmentQuestion);
						}
						
					  return assessmentRepoS.findByTestCodeAndStudentUsername(testCode, principal.getName());
					}
				}else if ( test.getStartDate().equals( Date.valueOf( LocalDate.now() ) ) 
						&& Time.valueOf( LocalTime.now() ).after( test.getEndTime() ) 
						|| test.getStartDate().before( Date.valueOf( LocalDate.now() ) )){
					
					if (test.getTestStatus().equalsIgnoreCase("ongoing")) {
						test.setTestStatus("completed");
					}
				}
			}
		}
		return null;
	}
	
	
	
	
	
	
	
	
	@GetMapping("/student/fetch/question/details/{testCode}")
	public List<QuestionEntity> fetchAssessmentQuestions( @PathVariable("testCode") String testCode, Principal principal ){
	
		StudentEntity student = studentRepoS.findByEmail(principal.getName());
		for (int i = 0; i < student.getSubjectList().size(); i++) {
			if (testRepoS.existsByTestCodeAndSubjectCode(testCode, student.getSubjectList().get(i).getId()) ) {

				TestEntity test = testRepoS.findByTestCode(testCode);
				
				if ( test.getStartDate().equals( Date.valueOf( LocalDate.now() ) ) 
					&& Time.valueOf( LocalTime.now() ).after( test.getStartTime() )
					&& Time.valueOf( LocalTime.now() ).before( test.getEndTime() ) ){
					
					if (test.getTestStatus().equalsIgnoreCase("pending")) {
						test.setTestStatus("ongoing");
					}
					
					List<AssessmentEntity> generatedQuestionList = 
							assessmentRepoS.findByTestCodeAndStudentUsername(testCode, principal.getName());
					
					List<QuestionEntity> questionList = new ArrayList<QuestionEntity>();
					
					for (int j = 0; j < generatedQuestionList.size(); j++) {
						
					 QuestionEntity question = 	questionRepoS.findByQuestionIdAndCollectionCode(generatedQuestionList.get(i).getQuestionId(), test.getCollectionCode());
					 question.setAnswer(null);
					 question.setCollectionCode(null);
					 question.setQuestionId(0);
					 question.setTeacherUsername(null);
					 
					 questionList.add(question);
					}
					return questionList;
					
				}else if ( test.getStartDate().equals( Date.valueOf( LocalDate.now() ) ) 
						&& Time.valueOf( LocalTime.now() ).after( test.getEndTime() ) 
						|| test.getStartDate().before( Date.valueOf( LocalDate.now() ) )){
					
					if (test.getTestStatus().equalsIgnoreCase("ongoing")) {
						test.setTestStatus("completed");
					}
				}
			}
		}
		return null;
	}
	
	
	
	@PutMapping("/student/save/question/choice")
	public void saveQuestion(@RequestBody AssessmentEntity question, Principal principal) {
		
		StudentEntity student = studentRepoS.findByEmail(principal.getName());
		for (int i = 0; i < student.getSubjectList().size(); i++) {
			if (testRepoS.existsByTestCodeAndSubjectCode(question.getTestCode(), student.getSubjectList().get(i).getId()) ) {

				TestEntity test = testRepoS.findByTestCode(question.getTestCode());
				
				if ( test.getStartDate().equals( Date.valueOf( LocalDate.now() ) ) 
					&& Time.valueOf( LocalTime.now() ).after( test.getStartTime() )
					&& Time.valueOf( LocalTime.now() ).before( test.getEndTime() ) ){
					
					AssessmentEntity q = 
							assessmentRepoS.findByTestCodeAndQuestionIdAndStudentUsername(question.getTestCode(), question.getQuestionId(), principal.getName());
					q.setSelectedChoice(question.getSelectedChoice());
					assessmentRepoS.save(q);
					
				}else if ( test.getStartDate().equals( Date.valueOf( LocalDate.now() ) ) 
						&& Time.valueOf( LocalTime.now() ).after( test.getEndTime() ) 
						|| test.getStartDate().before( Date.valueOf( LocalDate.now() ) )){
					
					if (test.getTestStatus().equalsIgnoreCase("ongoing")) {
						test.setTestStatus("completed");
					}
				}
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
