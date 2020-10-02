package org.tool.student;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.tool.auth.User;
import org.tool.auth.UserRepository;
import org.tool.reponse.ResponseMessage;
import org.tool.teacher.TeacherSubjectEntity;
import org.tool.teacher.TeacherSubjectRepository;








@RestController
public class StudentController {
	
	
	
	@Autowired
	private StudentRepository studRepo;
	
	@Autowired
	private StudentSubjectRepository studSubRepo;
	
	@Autowired
	private TeacherSubjectRepository teachSubRepo;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private ResponseMessage responseMessage;
	

	
	
	
	
	
	
	
	// this method will show you how to tackle infinite references
	@GetMapping("/student/subject/list")
	@PreAuthorize("hasRole('ROLE_STUDENT')")
	public List<StudentSubjectEntity> getAllStudenSubjectEntities() {
				
		List<StudentSubjectEntity> subjectList = new ArrayList<StudentSubjectEntity>();
		studSubRepo.findAll().forEach(subjectList::add);
		
		for (int i = 0; i < subjectList.size(); i++) {
						
			for (int j = 0; j < subjectList.get(i).getStudentsList().size(); j++) {
				
				//clear subject list from each subject list property of student entity
				subjectList.get(i).getStudentsList().get(j).getSubjectList().clear();
				
				//unnecessary fields will be emptied or nulified
				subjectList.get(i).getStudentsList().get(j).setEmail(null);
				subjectList.get(i).getStudentsList().get(j).setPhone(null);  // phone is BigInteger and not primitive so null is valid
			}
		}
		
		return subjectList;
	}
	
	/*
	 
    Example output from above method

	 {
        "id": "11112318966",
        "name": "11",
        "studentsList": [
            {
                "id": "112628481",
                "name": "s1",
                "usn": "s1"
            },
            {
                "id": "112645260",
                "name": "s2",
                "usn": "s2"
            }
        ]
    }


	 */
	
	
	
	
	
	
	
	
	
	@PostMapping("/student/register")
	public ResponseMessage registerStudent(@RequestBody StudentEntity student ) {
		
		
		//check if student email already exists in student table
		if(! studRepo.existsStudentEntityByEmail(student.getEmail())) {
			

			//generate and set id
			student.setId(java.time.LocalTime.now().toString().replaceAll(":", "").replaceAll("\\.", ""));
			
			//generate and set password
			student.setPassword( RandomStringUtils.random(10, true, true) );
			
			
			
			//now check if subjects entered by student exist in db and update the subject list accordingly
			
			here :{
				
				int subjectListSize = student.getSubjectList().size();
				
				for (int i = 0; i < subjectListSize ; i++) {
					
					TeacherSubjectEntity teacherSubject = new TeacherSubjectEntity();
					
					if ( teachSubRepo.existsTeacherSubjectEntityById( student.getSubjectList().get(i).getId()) ) {
						
						teacherSubject = teachSubRepo.findTeacherSubjectEntityById( student.getSubjectList().get(i).getId() );
						student.getSubjectList().get(i).setName( teacherSubject.getName() );
						
						student.getSubjectList().get(i).getStudentsList().add(student);
											
					}else {
						
						student.getSubjectList().remove(i);						
						break here;
						
					}
				}
				
			}
			
			// saving student auth credentials in user table.
			User user = new User( student.getEmail(),  new BCryptPasswordEncoder(11).encode( student.getPassword()) , "ROLE_STUDENT" , true, true, true, true);
			userRepo.save(user);

			//save student data to student table
			studRepo.save(student);

			//MailService.send(student.getEmail(), "Registration Successful ", " Your user_id : " + student.getEmail() +  "  password : " + student.getPassword());
									
			responseMessage.setStatus("success");
			responseMessage.setMessage("Your User ID and Password are sent to your mail. Please use them to login and change password for successful registration.");
			return  responseMessage;
			
		}else {
			
			// if email already exists in db
			responseMessage.setStatus("failure");
			responseMessage.setMessage("User already registered. Please register with a different email or click forgot password button to reset your password.");
			return responseMessage;
		}
	
		
		
	}
	
	
	 
	
	
	

}
