package org.tool.dashboard;

import java.security.Principal;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
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
import org.tool.question.collection.CollectionEntity;
import org.tool.question.collection.CollectionRepository;
import org.tool.question.collection.QuestionEntity;
import org.tool.question.collection.QuestionRepository;
import org.tool.reponse.ResponseMessage;
import org.tool.student.StudentSubjectEntity;
import org.tool.student.StudentSubjectRelationEntityRepository;
import org.tool.student.StudentSubjectRepository;
import org.tool.teacher.TeacherEntity;
import org.tool.teacher.TeacherRepository;
import org.tool.teacher.TeacherSubjectEntity;
import org.tool.teacher.TeacherSubjectRepository;
import org.tool.test.TestEntity;
import org.tool.test.TestRepository;

@RestController
@PreAuthorize("hasRole('ROLE_TEACHER')")
public class TeacherDashboardController {

	@Autowired
	private TeacherRepository teacherRepo;

	@Autowired
	private TeacherSubjectRepository teacherSubjectRepo;

	@Autowired
	private StudentSubjectRepository studSubRepo;

	
	@Autowired
	private CollectionRepository collectionRepo;

	@Autowired
	private QuestionRepository questionRepo;
	
	@Autowired
	private TestRepository testRepo;
	
	@Autowired
	private StudentSubjectRelationEntityRepository  studSubRelRepo;
	
	@Autowired
	private ResponseMessage responseMessage;

	
	
	
	
	@GetMapping("/teacher/details")
	public TeacherEntity getTeacherDetails(Principal principal) {
		
		try {
			TeacherEntity teacher = teacherRepo.findTeacherEntityByEmail(principal.getName());
			teacher.getSubjectList().clear();
			teacher.setPassword(null);
			return  teacher;
		} catch (Exception e) {
			return null;
		}
		
		
	}

	

	// to get subject list for the logged in teacher
	//this method will send both teacher details and also subject list
	@GetMapping("/teacher/subject/list")
	public List<TeacherSubjectEntity> getAllTeacherSubjectEntities(Principal principal) {
		
		try {
			TeacherEntity teacher = teacherRepo.findTeacherEntityByEmail(principal.getName());
			teacher.getSubjectList().forEach(s -> {
				s.setTeacher(null); // to avoid infinite references
			});
			return teacher.getSubjectList();
		} catch (Exception e) {
			return null;
		}

		
	}
	
	
	

	// to add a new subject
	// append new subject name to url
	@PutMapping("/teacher/add/subject/{subject}")
	public ResponseMessage addSubject(@PathVariable("subject") String subject, Principal principal) {
		
		try {
			TeacherSubjectEntity teacherSubject = new TeacherSubjectEntity();
			teacherSubject.setId(subject.replaceAll("\\s", "").toLowerCase()
					+ java.time.LocalTime.now().toString().replaceAll(":", "").replaceAll("\\.", "").substring(4));
			teacherSubject.setName(subject);
			teacherSubject.setTeacher(teacherRepo.findTeacherEntityByEmail(principal.getName()));
			teacherSubjectRepo.save(teacherSubject);

			responseMessage.setMessage("Successfully added a new subject");
			return responseMessage;

		} catch (Exception e) {;
			return null;
		}

		
	}
	
	

	// to remove a subject
	// append subject name to url
	@DeleteMapping("/teacher/delete/subject/{subjectId}")
	public ResponseMessage deleteSubject(@PathVariable("subjectId") String subjectId, Principal principal) {

		try {
			teacherSubjectRepo.deleteById(subjectId);
			responseMessage.setMessage("Deleted");
			return responseMessage;
		} catch (Exception e) {
			return null;
		}
		

	}
	
	
	
	
	
	@GetMapping("/teacher/subject/{subjectcode}/student/list")
	public StudentSubjectEntity getAllStudenSubjectEntities(@PathVariable("subjectcode") String id, Principal principal) {
		
		try {
			// first we need to check if the subject id matches with the current logged in user
			
			TeacherEntity teacher = teacherRepo.findTeacherEntityByEmail(principal.getName());
			
			for (int i = 0; i < teacher.getSubjectList().size(); i++) {
				if (teacher.getSubjectList().get(i).getId().equalsIgnoreCase(id)) {
					
					StudentSubjectEntity subject = studSubRepo.findStudentSubjectEntityById(id);
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
		} catch (Exception e) {
			return null;
		}
		
	}
	
	
	
	@Transactional
	@DeleteMapping("/teacher/delete/{subjectId}/{studentId}")
	public ResponseMessage removeStudentFromSubject(@PathVariable("studentId") String studentId,
			@PathVariable("subjectId") String subjectId, Principal principal) {
		
		try {
			studSubRelRepo.removeByStudentIdAndSubjectId(subjectId, studentId);
			responseMessage.setMessage("removed");
			return responseMessage;
		} catch (Exception e) {
			return null;
		}
		
		
		
	}
	

	
	@GetMapping("/teacher/collection/list")
	public List<CollectionEntity> getCollection(Principal principal){
		try {
			return collectionRepo.findByTeacherUsername(principal.getName());
		} catch (Exception e) {
			return null;
		}
		
	}
	
	
	
	
	
	@PostMapping("/teacher/add/collection")
	@PreAuthorize("hasRole('ROLE_TEACHER')")
	public ResponseMessage addCollection(@RequestBody CollectionEntity collection ,Principal principal) {
		
		try {
		
		if (teacherSubjectRepo.existsByIdAndTeacher(collection.getSubjectCode(), teacherRepo.findTeacherEntityByEmail(principal.getName()) )) {
			
			collection.setCollectionCode(
					  collection.getCollectionName() 
					+ java.time.LocalTime.now().toString().replaceAll(":", "").replaceAll("\\.", ""));
			collection.setTeacherUsername(principal.getName());
			collectionRepo.save(collection);
			responseMessage.setMessage("collection added");
			return responseMessage;
		 }
		 return null;
		 
		} catch (Exception e) {
			return null;
		}

		
	}
	


	
	@Transactional
	@DeleteMapping("/teacher/delete/collection/{collectionCode}")
	public ResponseMessage deleteCollection(@PathVariable("collectionCode") String collectionCode, Principal principal) {
		
		try {
			collectionRepo.deleteByCollectionCodeAndTeacherUsername(collectionCode, principal.getName());
			responseMessage.setMessage("deleted");
			return responseMessage;
		} catch (Exception e) {
			return null;
		}
		
	}
	
	

	
	@GetMapping("/teacher/{collection}/question/list")
	public List<QuestionEntity> getAllQuestions(@PathVariable("collection") String collection, Principal principal){
		try {
			return questionRepo.findByTeacherUsernameAndCollectionCode(principal.getName(), collection);
		} catch (Exception e) {
			return null;
		}
		
	}

	
	
	@PostMapping("/teacher/add/question")
	public ResponseMessage addQuestion(@RequestBody QuestionEntity question , Principal principal) {
		
		try {
			question.setTeacherUsername(principal.getName());
			questionRepo.save(question);
			responseMessage.setMessage("question added");
			return responseMessage;
		} catch (Exception e) {
			return null;
		}
		
	}
	

	
	@PutMapping("/teacher/update/question/{questionId}")
	public ResponseMessage updateQuestion(@RequestBody QuestionEntity question, @PathVariable("questionId") int questionId, Principal principal) {
		
		try {
			QuestionEntity q = questionRepo.findByTeacherUsernameAndQuestionId(principal.getName(), questionId);
		if (q != null) {
			q.setQuestionString(question.getQuestionString());
			q.setChoices(question.getChoices());
			q.setAnswer(question.getAnswer());
			questionRepo.save(q);
			responseMessage.setMessage("updated");
		}else {
			responseMessage.setMessage("error editing");
		}		
		return responseMessage;
		} catch (Exception e) {
			return null;
		}
		
		
	}

	
	
	
	@Transactional
	@DeleteMapping("/teacher/delete/question/{questionId}")
	public ResponseMessage deleteQuestion(@PathVariable("questionId") int questionId, Principal principal) {
		
		try {
			questionRepo.deleteByTeacherUsernameAndQuestionId(principal.getName(), questionId);
			responseMessage.setMessage("deleted");
			return responseMessage;
		} catch (Exception e) {
			return null; 
		}
		
		
	}
	

	
	
	
	@GetMapping("/teacher/test/list")
	public List<TestEntity> getAllTests(Principal principal){
		
		try {
			List<TestEntity> tests = testRepo.findByTeacherUsername(principal.getName());
		
		for (int i = 0; i < tests.size(); i++) {
			
			if ( tests.get(i).getStartDate().toLocalDate().isEqual( LocalDate.now() ) 
					&& LocalTime.now().isAfter( tests.get(i).getStartTime().toLocalTime() )
					&& LocalTime.now().isBefore( tests.get(i).getEndTime().toLocalTime() ) ){
					
					if (tests.get(i).getTestStatus().equalsIgnoreCase("pending")) {
						tests.get(i).setTestStatus("ongoing");
					}
			}else if ( tests.get(i).getStartDate().toLocalDate().isEqual( LocalDate.now() )
					&& LocalTime.now().isAfter( tests.get(i).getEndTime().toLocalTime() ) 
					|| LocalDate.now().isAfter(tests.get(i).getStartDate().toLocalDate()) ){
				
				if (tests.get(i).getTestStatus().equalsIgnoreCase("ongoing")) {
					tests.get(i).setTestStatus("completed");
				}
			}		
		}
		
		testRepo.saveAll(tests);

		return	tests;
		} catch (Exception e) {
			return null;
		}
		
		
	}
	

	
	
	@PostMapping("/teacher/add/test")
	public ResponseMessage addTest(@RequestBody TestEntity test, Principal principal) {
		
		try {
			
		int totalQuestionsavailable = questionRepo.findByTeacherUsernameAndCollectionCode(principal.getName(), test.getCollectionCode()).size();
		
		
		System.out.println( LocalDate.now()  + "     " + test.getStartDate().toLocalDate() + "     " +  LocalDate.now().isEqual(test.getStartDate().toLocalDate())    );
		System.out.println(test.getStartTime().after(Time.valueOf(LocalTime.now())));
		System.out.println(test.getEndTime().after(test.getStartTime()) );
		System.out.println(test.getTotalQuestions() <= totalQuestionsavailable);
		
		
		if ( LocalDate.now().isEqual(test.getStartDate().toLocalDate())
				&& test.getStartTime().toLocalTime().isAfter(LocalTime.now())
				&& test.getEndTime().toLocalTime().isAfter(test.getStartTime().toLocalTime())
				&& test.getTotalQuestions() <= totalQuestionsavailable ) {
			
			
			test.setTestCode(test.getTestName() + java.time.LocalTime.now().toString().replaceAll(":", "").replaceAll("\\.", ""));
			test.setTeacherUsername(principal.getName());
			test.setTestStatus("pending");
			testRepo.save(test);
			
			responseMessage.setMessage("test created");
			return responseMessage;
			
		}else if ( LocalDate.now().isAfter(test.getStartDate().toLocalDate())
				   && test.getEndTime().toLocalTime().isAfter(test.getStartTime().toLocalTime())
				   && test.getTotalQuestions() <= totalQuestionsavailable ) {
			
			
			test.setTestCode(test.getTestName() + java.time.LocalTime.now().toString().replaceAll(":", "").replaceAll("\\.", ""));
			test.setTeacherUsername(principal.getName());
			test.setTestStatus("pending");
			testRepo.save(test);
			
			responseMessage.setMessage("test created");
			return responseMessage;
		 }
		 return null;
		} catch (Exception e) {
			return null;
		}
		
		
	}
	
	
	
	
	
	@PutMapping("/teacher/update/test")
	public ResponseMessage updateTest(@RequestBody TestEntity test, Principal principal) {
		
		try {
			TestEntity t = testRepo.findByTestCode(test.getTestCode());
		if(t.getTeacherUsername().equalsIgnoreCase(principal.getName()) && t.getTestStatus().equalsIgnoreCase("pending") ) {
			
			testRepo.save(test);
			responseMessage.setMessage("test details updated");
			
		}else if(t.getTeacherUsername().equalsIgnoreCase(principal.getName()) && t.getTestStatus().equalsIgnoreCase("ongoing") ) {
			
			t.setEndTime(test.getEndTime());
			testRepo.save(t);
			responseMessage.setMessage("test time updated");
			
		}
		
		return responseMessage;
		} catch (Exception e) {
			return null;
		}
		
	} 
	
	
	
	@DeleteMapping("/teacher/delete/test/{testCode}")
	public ResponseMessage deleteTest(@PathVariable("testCode") String testCode, Principal principal) {
		
		try {
			TestEntity t = testRepo.findByTestCode(testCode);
		 if(t.getTeacherUsername().equalsIgnoreCase(principal.getName()) && t.getTestStatus().equalsIgnoreCase("pending") ) {
			
			testRepo.delete(t);
			responseMessage.setMessage("test deleted");
			return responseMessage;
		 }else {
			 return null; 
		 }
		} catch (Exception e) {
			return null;
		}
		
		
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * 
	 * 
	 * 
	 
	
	
	
	
	

	@PutMapping("/teacher/update/profile")
	public ResponseMessage updateTeacherProfile(@RequestBody TeacherEntity teacher, Principal principal,
			HttpServletRequest request) {

		String currentpassword = new BCryptPasswordEncoder(11).encode(request.getParameter("currentpassword"));

		if (userRepo.findByUsername(principal.getName()).getPassword().equalsIgnoreCase(currentpassword)) {

			teacher.setId(teacherRepo.findTeacherEntityByEmail(principal.getName()).getId());

			teacherRepo.save(teacher);

			// update teacher auth credentials in user table.
			User user = userRepo.findByUsername(principal.getName());
			user.setPassword(teacher.getPassword());
			userRepo.save(user);

			// send password to the mail id
			// MailService.send(teacher.getEmail(), "Registration Successful ", " Your
			// user_id : " + teacher.getEmail() + " password : " + teacher.getPassword());

			// set response message and return it as response
			responseMessage.setMessage("success");
			responseMessage.setMessage("Password successfully changed.");
			return responseMessage;

		} else {

			// set response message and return it as response
			responseMessage.setMessage("failure");
			responseMessage.setMessage("Please enter corrent password. Else click forgot password");
			return responseMessage;

		}

	}

	@PutMapping("/teacher/update/password")
	public ResponseMessage updateTeacherPassword(@RequestBody TeacherEntity teacher, Principal principal,
			HttpServletRequest request) {

		String currentpassword = new BCryptPasswordEncoder(11).encode(request.getParameter("currentpassword"));

		if (userRepo.findByUsername(principal.getName()).getPassword().equalsIgnoreCase(currentpassword)) {

			teacher.setId(teacherRepo.findTeacherEntityByEmail(principal.getName()).getId());
			teacher.setPassword(new BCryptPasswordEncoder(11).encode(teacher.getPassword())); //

			teacherRepo.save(teacher);

			// update teacher auth credentials in user table.
			User user = userRepo.findByUsername(principal.getName());
			user.setPassword(teacher.getPassword());
			userRepo.save(user);

			// send password to the mail id
			// MailService.send(teacher.getEmail(), "Registration Successful ", " Your
			// user_id : " + teacher.getEmail() + " password : " + teacher.getPassword());

			// set response message and return it as response
			responseMessage.setMessage("success");
			responseMessage.setMessage("Password successfully changed.");
			return responseMessage;

		} else {

			// set response message and return it as response
			responseMessage.setMessage("failure");
			responseMessage.setMessage("Please enter corrent password. Else click forgot password");
			return responseMessage;

		}

	}
	
	* 
	 * 
	 */
	
	
	

}
