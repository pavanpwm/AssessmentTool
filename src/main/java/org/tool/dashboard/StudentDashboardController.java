package org.tool.dashboard;

import java.security.Principal;
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
import org.tool.question.collection.QuestionEntity;
import org.tool.question.collection.QuestionRepository;
import org.tool.reponse.ResponseMessage;
import org.tool.student.StudentEntity;
import org.tool.student.StudentRepository;
import org.tool.student.StudentSubjectEntity;
import org.tool.student.StudentSubjectRelationEntityRepository;
import org.tool.student.StudentSubjectRepository;
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
	private TeacherSubjectRepository teacherSubjectRepoS;

	@Autowired
	private StudentRepository studentRepoS;

	@Autowired
	private StudentSubjectRepository studSubRepoS;

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
		
		try {
			StudentEntity student = studentRepoS.findByEmail(principal.getName());
		student.setPassword(null);
		student.getSubjectList().clear();
		
		return student;
		} catch (Exception e) {
			return null;
		}
		
		
	}
	

	
	// this method will show you how to tackle infinite references
	@GetMapping("/student/subject/list")
	public List<StudentSubjectEntity> getAllStudenSubjectEntities(Principal principal) {

		try {
			StudentEntity student = studentRepoS.findByEmail(principal.getName());
		for (int i = 0; i < student.getSubjectList().size(); i++) {
			student.getSubjectList().get(i).getStudentsList().clear();
		}
		return student.getSubjectList();
		} catch (Exception e) {
			return null;
		}
		
	}
	
	
	@PostMapping("/student/add/subject")
	public ResponseMessage addStudentSubject(@RequestBody StudentSubjectEntity studentSubject, Principal principal) {
		
		try {
			StudentEntity student = studentRepoS.findByEmail(principal.getName());
			TeacherSubjectEntity teacherSubject;
			
		if ( teacherSubjectRepoS.existsTeacherSubjectEntityById( studentSubject.getId()) ) {
			
			teacherSubject = teacherSubjectRepoS.findTeacherSubjectEntityById( studentSubject.getId() );
			studentSubject.setName( teacherSubject.getName() );
			studentSubject.getStudentsList().add(student);
			student.getSubjectList().add(studentSubject);
			studentRepoS.save(student);
			responseMessageS.setMessage("added");
			return responseMessageS;
		 }
		} catch (Exception e) {
			return null;
		}
		
		return null;
		
	}
	
	
	
	@Transactional
	@DeleteMapping("/student/delete/subject/{subjectId}")
	public ResponseMessage removeStudentFromSubject(@PathVariable("subjectId") String subjectId, Principal principal) {
		
		try {
			studSubRelRepoS.removeByStudentIdAndSubjectId(subjectId, studentRepoS.findByEmail(principal.getName()).getId() );
			responseMessageS.setMessage("removed");
			return responseMessageS;
		} catch (Exception e) {
			System.out.print(e);
			return null;
		}
		
		
	}
	
	
	
	
	@GetMapping("/student/subject/{subjectcode}/student/list")
	public StudentSubjectEntity getAllStudenSubjectEntities(@PathVariable("subjectcode") String id,
			Principal principal) {
		
		try {
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
		} catch (Exception e) {
			return null;
		}
		return null;
		
	}
	
	
	
	
	
	
	
	
	@GetMapping("/student/test/list")
	public List<TestEntity>  getStudentSubjectTests(Principal principal){
		
		try {
			List<TestEntity> tests = new ArrayList<TestEntity>();
		
		studentRepoS.findByEmail(principal.getName()).getSubjectList().forEach(eachSubject -> {
			tests.addAll( testRepoS.findBySubjectCode( eachSubject.getId() ) ); 
				
		});
		
		
		for (int i = 0; i < tests.size(); i++) {
			
			if ( tests.get(i).getStartDate().toLocalDate().isEqual( LocalDate.now() ) 
					&& LocalTime.now().isAfter( tests.get(i).getStartTime().toLocalTime() )
					&& LocalTime.now().isBefore( tests.get(i).getEndTime().toLocalTime() ) ){
					
					if (tests.get(i).getTestStatus().equalsIgnoreCase("pending")) {
						tests.get(i).setTestStatus("ongoing");
					}
			}else if ( tests.get(i).getStartDate().toLocalDate().isEqual(LocalDate.now()  ) 
				 	&& LocalTime.now().isAfter( tests.get(i).getEndTime().toLocalTime() ) 
					|| LocalDate.now().isAfter(tests.get(i).getStartDate().toLocalDate())  ){
				
				if (tests.get(i).getTestStatus().equalsIgnoreCase("ongoing")) {
					tests.get(i).setTestStatus("completed");
				}
			}		
		}
		
		testRepoS.saveAll(tests);
		
			return tests;
		} catch (Exception e) {
			return null;
		}
		
		
	}
	
	
	
	
	
	@GetMapping("/student/test/details/{testCode}")
	public TestEntity getTestByIdForStudent(@PathVariable("testCode") String testCode, Principal principal) {
		
		try {
			StudentEntity student = studentRepoS.findByEmail(principal.getName());
			for (int i = 0; i < student.getSubjectList().size(); i++) {
				if (testRepoS.existsByTestCodeAndSubjectCode(testCode, student.getSubjectList().get(i).getId()) ) {
					return testRepoS.findByTestCode(testCode);
				}
			}
		} catch (Exception e) {
			return null;
		}
		
		return null;
		
	}
	

	
	
	
	
	
	@GetMapping("/student/generate/questions/{testCode}")
	public List<AssessmentEntity> generateAssessmentQuestions( @PathVariable("testCode") String testCode, Principal principal ){
		
		
		try {
			StudentEntity student = studentRepoS.findByEmail(principal.getName());
		for (int i = 0; i < student.getSubjectList().size(); i++) {
			
			if (testRepoS.existsByTestCodeAndSubjectCode(testCode, student.getSubjectList().get(i).getId()) ) {

				TestEntity test = testRepoS.findByTestCode(testCode);
				
				if ( test.getStartDate().toLocalDate().isEqual( LocalDate.now() ) 
					&& LocalTime.now().isAfter( test.getStartTime().toLocalTime() )
					&& LocalTime.now().isBefore( test.getEndTime().toLocalTime() ) ){
					
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
							  new AssessmentEntity(test.getTestCode(), student.getName(), principal.getName(), test.getSubjectCode(), questionList.get(j).getTopic(), j+1 , questionList.get(j).getQuestionId());
							assessmentRepoS.save(asessmentQuestion);
						}
						
					  return assessmentRepoS.findByTestCodeAndStudentUsername(testCode, principal.getName());
					}
				}else if ( test.getStartDate().toLocalDate().isEqual( LocalDate.now() ) 
						&& LocalTime.now().isAfter( test.getEndTime().toLocalTime() )
						|| test.getStartDate().toLocalDate().isBefore(LocalDate.now()) ){
					
					if (test.getTestStatus().equalsIgnoreCase("ongoing")) {
						test.setTestStatus("completed");
					}
				}
				
				break;
			}
		}
		} catch (Exception e) {
			return null; 
		}
		
		return null;
	}
	
	
	
	
	
	
	
	
	@GetMapping("/student/fetch/question/details/{testCode}/{questionId}")
	public QuestionEntity fetchAssessmentQuestions( @PathVariable("testCode") String testCode,@PathVariable("questionId") int questionId, Principal principal ){
	
		StudentEntity student = studentRepoS.findByEmail(principal.getName());
		for (int i = 0; i < student.getSubjectList().size(); i++) {
			if (testRepoS.existsByTestCodeAndSubjectCode(testCode, student.getSubjectList().get(i).getId()) ) {

				TestEntity test = testRepoS.findByTestCode(testCode);
				
				if ( test.getStartDate().toLocalDate().isEqual( LocalDate.now() )
					&& LocalTime.now().isAfter( test.getStartTime().toLocalTime() )
					&& LocalTime.now().isBefore( test.getEndTime().toLocalTime() ) ){
					
					if (test.getTestStatus().equalsIgnoreCase("pending")) {
						test.setTestStatus("ongoing");
					}
					
						
					 QuestionEntity question = 	questionRepoS.findByQuestionIdAndCollectionCode(questionId, test.getCollectionCode());
					 question.setAnswer(null);
					 question.setCollectionCode(null);
					 //question.setQuestionId(0);
					 question.setTeacherUsername(null);
					 
				
					return question;
					
				}else if (  test.getStartDate().toLocalDate().isEqual( LocalDate.now() ) 
						&& LocalTime.now().isAfter( test.getEndTime().toLocalTime() )
						|| test.getStartDate().toLocalDate().isBefore(LocalDate.now()) ){
					
					if (test.getTestStatus().equalsIgnoreCase("ongoing")) {
						test.setTestStatus("completed");
					}
				}
			}
		}
		return null;
	}
	
	
	
	@PutMapping("/student/save/question/choice")
	public ResponseMessage saveQuestion(@RequestBody AssessmentEntity question, Principal principal) {
		
		StudentEntity student = studentRepoS.findByEmail(principal.getName());
		for (int i = 0; i < student.getSubjectList().size(); i++) {
			if (testRepoS.existsByTestCodeAndSubjectCode(question.getTestCode(), student.getSubjectList().get(i).getId()) ) {

				TestEntity test = testRepoS.findByTestCode(question.getTestCode());
				
				if ( test.getStartDate().toLocalDate().isEqual( LocalDate.now() )
						&& LocalTime.now().isAfter( test.getStartTime().toLocalTime() )
						&& LocalTime.now().isBefore( test.getEndTime().toLocalTime() ) ){
					
					AssessmentEntity q = 
							assessmentRepoS.findByTestCodeAndQuestionIdAndStudentUsername(question.getTestCode(), question.getQuestionId(), principal.getName());
					q.setSelectedChoice(question.getSelectedChoice());
					
					if (!question.getSelectedChoice().isEmpty()) {
						if (questionRepoS.findByQuestionIdAndCollectionCode(question.getQuestionId(), test.getCollectionCode()).getAnswer().equals(question.getSelectedChoice())) {
							  q.setMarks(test.getMarksFcaq());
						}else {
							  q.setMarks(test.getMarksFwap());
						}
					}else if (question.getSelectedChoice().isEmpty()){
						 q.setMarks(0);
					}
					assessmentRepoS.save(q);
					
				}else if (  test.getStartDate().toLocalDate().isEqual( LocalDate.now() ) 
						&& LocalTime.now().isAfter( test.getEndTime().toLocalTime() )
						|| test.getStartDate().toLocalDate().isBefore(LocalDate.now()) ){
					
					if (test.getTestStatus().equalsIgnoreCase("ongoing")) {
						test.setTestStatus("completed");
					}
				}
			}
		}
		
		return responseMessageS;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
