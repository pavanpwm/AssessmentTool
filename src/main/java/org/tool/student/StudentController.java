package org.tool.student;


import java.util.ArrayList;
import java.util.List;

import javax.security.auth.Subject;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.tool.mail.service.MailService;
import org.tool.reponse.ResponseMessage;
import org.tool.teacher.TeacherSubjectEntity;
import org.tool.teacher.TeacherSubjectRepository;








@RestController
public class StudentController {
	
	
	
	@Autowired
	private StudentRepository studRepo;
	
	@Autowired
	private StudentSubjectRepository subRepo;
	
	@Autowired
	private TeacherSubjectRepository teachSubRepo;
	
	@Autowired
	private ResponseMessage resp;
	
	


	
	
	@GetMapping("/list/student")
	public List<StudentEntity> getAllStudenEntities() {
		
		List<StudentEntity> s = new ArrayList<StudentEntity>();
		studRepo.findAll().forEach(s::add);
		
		return s;
	}
	
	
	
	@GetMapping("/list/student/subjects")
	public List<StudentSubjectEntity> getAllStudenSubjectEntities() {
		
		List<StudentSubjectEntity> s = new ArrayList<StudentSubjectEntity>();
		subRepo.findAll().forEach(s::add);
		
		return s;
	}
	
	
	
	
	
	
	
	
	
	
	
	@PostMapping("/register/student")
	public ResponseMessage registerStudent(@RequestBody StudentEntity student ) {
		
		
		if(! studRepo.existsStudentEntityByEmail(student.getEmail())) {
			
			student.setId(Integer.parseInt(java.time.LocalTime.now().toString().replaceAll(":", "").replaceAll("\\.", "")));
			student.setPassword(RandomStringUtils.random(10, true, true));
			
			
			List<StudentSubjectEntity>  tempList = new ArrayList<StudentSubjectEntity>();
			tempList.addAll(student.getSubjectList());
			student.getSubjectList().clear();
			
			
			
			
			
			
			for (int i = 0; i < tempList.size(); i++) {
				
				TeacherSubjectEntity teacherSubject = new TeacherSubjectEntity();
				StudentSubjectEntity studentSubject = new StudentSubjectEntity();
				
				if (  teachSubRepo.existsTeacherSubjectEntityById( tempList.get(i).getId()) ) {
					
					teacherSubject = teachSubRepo.findTeacherSubjectEntityById( tempList.get(i).getId() );
					
					studentSubject.setName( teacherSubject.getName() );
					studentSubject.setId( tempList.get(i).getId() );
					
					student.getSubjectList().add(studentSubject);
				}
			}
			
			
			
			
			
			
			
			
			for (int i = 0; i < student.getSubjectList().size(); i++) {
				student.getSubjectList().get(i).getStudentsList().add(student);
			}
			
			
			studRepo.save(student);

			//MailService.send(student.getEmail(), "Registration Successful ", " Your user_id : " + student.getEmail() +  "  password : " + student.getPassword());
									
			resp.setStatus("success");
			resp.setMessage("Your User ID and Password are sent to your mail. Please use them to login and change password for successful registration.");
			return  resp;
			
		}else {
			
			resp.setStatus("failure");
			resp.setMessage("User already registered. Please register with a different email or click forgot password button to reset your password.");
			return resp;
		}
	
		
		
	}
	
	
	 
	
	
	

}
