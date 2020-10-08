package org.tool.dashboard;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.tool.auth.UserRepository;
import org.tool.question.collection.CollectionRepository;
import org.tool.question.collection.QuestionRepository;
import org.tool.reponse.ResponseMessage;
import org.tool.student.StudentEntity;
import org.tool.student.StudentRepository;
import org.tool.student.StudentSubjectEntity;
import org.tool.student.StudentSubjectRepository;
import org.tool.teacher.TeacherRepository;
import org.tool.teacher.TeacherSubjectEntity;
import org.tool.teacher.TeacherSubjectRepository;
import org.tool.test.TestEntity;
import org.tool.test.TestRepository;

@RestController
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

	
	
	
	@GetMapping("/student/details")
	@PreAuthorize("hasRole('ROLE_STUDENT')")
	public StudentEntity getStudentDetails(Principal principal) {
		
		StudentEntity student = studentRepoS.findByEmail(principal.getName());
		student.setPassword(null);
		student.getSubjectList().clear();
		
		return student;
	}
	

	
	// this method will show you how to tackle infinite references
	@GetMapping("/student/subject/list")
	@PreAuthorize("hasRole('ROLE_STUDENT')")
	public List<StudentSubjectEntity> getAllStudenSubjectEntities(Principal principal) {

		StudentEntity student = studentRepoS.findByEmail(principal.getName());
		for (int i = 0; i < student.getSubjectList().size(); i++) {
			student.getSubjectList().get(i).getStudentsList().clear();
		}
		return student.getSubjectList();
	}
	
	
	@PostMapping("/student/add/subject")
	@PreAuthorize("hasRole('ROLE_STUDENT')")
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
	
	
	
	@DeleteMapping("/student/delete/subject/{subjectId}")
	@PreAuthorize("hasRole('ROLE_STUDENT')")
	public ResponseMessage  deleteStudentSubject(@PathVariable("subjectId") String subjectId , Principal principal) {
		
		StudentEntity student = studentRepoS.findByEmail(principal.getName());
		for (int i = 0; i < student.getSubjectList().size(); i++) {
			if (student.getSubjectList().get(i).getId().equalsIgnoreCase(subjectId)) {
				student.getSubjectList().remove(i);
				studentRepoS.save(student);
				responseMessageS.setStatus("deleted");
				break;
			}else {
				responseMessageS.setStatus("failed");
			}
		}
		return responseMessageS;
	}
	
	
	
	
	
	
	
	
	@GetMapping("/student/subject/{subjectId}/tests")
	@PreAuthorize("hasRole('ROLE_STUDENT')")
	public List<TestEntity>  getStudentSubjectTests(@PathVariable("subjectId") String subjectId , Principal principal){
		return testRepoS.findBySubjectCode(subjectId);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// profile update methods here
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
