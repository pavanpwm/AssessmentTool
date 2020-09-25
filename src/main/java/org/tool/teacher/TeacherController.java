package org.tool.teacher;


import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.tool.mail.service.MailService;
import org.tool.reponse.ResponseMessage;








@RestController
public class TeacherController {
	
	
	
	@Autowired
	private TeacherRepository tRepo;
	
	@Autowired
	private TeacherSubjectRepository sRepo;
	
	@Autowired
	private ResponseMessage resp;
	
	
	
	
	


	@GetMapping("/list/teacher")
	public List<TeacherEntity> getAllTeacherEntities() {
		List<TeacherEntity> t =  new ArrayList<TeacherEntity>();
		tRepo.findAll().forEach(t::add);
		return  t;
	}

	
	
	
	
	
	
	
	@PostMapping("/register/teacher")
	public ResponseMessage registerTeacher(@RequestBody TeacherEntity teacher ) {
		

		
		
		if(! tRepo.existsTeacherEntityByEmail(teacher.getEmail())) {
			
			teacher.setId(Integer.parseInt(java.time.LocalTime.now().toString().replaceAll(":", "").replaceAll("\\.", "")));
			
			List<TeacherSubjectEntity> list  =  new ArrayList<TeacherSubjectEntity>();
			for (TeacherSubjectEntity s : teacher.getSubjectList()) {
				TeacherSubjectEntity subject = new TeacherSubjectEntity();
				subject.setId( s.getName() + java.time.LocalTime.now().toString().replaceAll(":", "").replaceAll("\\.", "") );
				subject.setName( s.getName() );
				subject.setTeacher(teacher);
				list.add(subject);
			}

			teacher.getSubjectList().clear();
			teacher.setSubjects(list);
			
			teacher.setPassword(RandomStringUtils.random(10, true, true));
			
			//MailService.send(teacher.getEmail(), "Registration Successful ", " Your user_id : " + teacher.getEmail() +  "  password : " + teacher.getPassword());
			
			tRepo.save(teacher);
			
			
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
