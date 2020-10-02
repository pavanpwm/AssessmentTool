package org.tool.teacher.dashboard;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import org.tool.auth.User;
import org.tool.auth.UserRepository;
import org.tool.reponse.ResponseMessage;
import org.tool.student.StudentEntity;
import org.tool.student.StudentRepository;
import org.tool.student.StudentSubjectEntity;
import org.tool.student.StudentSubjectRepository;
import org.tool.teacher.TeacherEntity;
import org.tool.teacher.TeacherRepository;
import org.tool.teacher.TeacherSubjectEntity;
import org.tool.teacher.TeacherSubjectRepository;

@RestController
public class TeacherDashboardController {

	@Autowired
	private TeacherRepository teacherRepo;

	@Autowired
	private TeacherSubjectRepository teacherSubjectRepo;
	
	@Autowired
	private StudentRepository studentRepo;
	
	@Autowired
	private StudentSubjectRepository studSubRepo;

	@Autowired
	private UserRepository userRepo;
	
	
	@Autowired
	private ResponseMessage responseMessage;
	
	
	boolean  loggedInTeacherHasThisSubject = false;
	
	
	
	
	
	
	@GetMapping("/redirect")
	public RedirectView redirect(HttpServletRequest request) {
		
		RedirectView redirectView = new RedirectView();
		
	    if (request.isUserInRole("ROLE_ADMIN")) {
		    redirectView.setUrl("/admin.html");
		    return redirectView;
	    }else {
	    	redirectView.setUrl("/dashboard.html");
		    return redirectView;
	    }
	}
	
	
	
	
	
	@PostMapping("/teacher/update/profile")
	@PreAuthorize("hasRole('ROLE_TEACHER')")
	public ResponseMessage updateTeacherProfile(@RequestBody TeacherEntity teacher, Principal principal, HttpServletRequest request) {       

		    String currentpassword = new BCryptPasswordEncoder(11).encode( request.getParameter("currentpassword") );
		
		    
		    if (teacherRepo.findTeacherEntityByEmail(principal.getName()).getPassword().equalsIgnoreCase(currentpassword)) {
		    			    	
		    	teacher.setId(teacherRepo.findTeacherEntityByEmail(principal.getName()).getId());		    	
		    	
				teacherRepo.save(teacher);
				
				// update teacher auth credentials in user table.
				User user = userRepo.findByUsername(principal.getName());
				user.setPassword(teacher.getPassword());
				userRepo.save( user );
				
				
			
				//send password to the mail id
				//MailService.send(teacher.getEmail(), "Registration Successful ", " Your user_id : " + teacher.getEmail() +  "  password : " + teacher.getPassword());
				
				
				

				//set response message and return it as response
				responseMessage.setStatus("success");
				responseMessage.setMessage("Password successfully changed.");
				return  responseMessage;
		    				
		    }else {
		    	
		    	//set response message and return it as response
				responseMessage.setStatus("failure");
				responseMessage.setMessage("Please enter corrent password. Else click forgot password");
				return  responseMessage;

		    	
		    }

	}
	
	
	
	
	
	
	
	
	
	
	@PostMapping("/teacher/update/password")
	@PreAuthorize("hasRole('ROLE_TEACHER')")
	public ResponseMessage updateTeacherPassword(@RequestBody TeacherEntity teacher, Principal principal, HttpServletRequest request) {       

		    String currentpassword = new BCryptPasswordEncoder(11).encode( request.getParameter("currentpassword") );
		
		    
		    if (teacherRepo.findTeacherEntityByEmail(principal.getName()).getPassword().equalsIgnoreCase(currentpassword)) {
		    			    	
		    	teacher.setId(teacherRepo.findTeacherEntityByEmail(principal.getName()).getId());
		    	teacher.setPassword(new BCryptPasswordEncoder(11).encode( teacher.getPassword() )); //
		    	
		    	
		    	
				teacherRepo.save(teacher);
				
				// update teacher auth credentials in user table.
				User user = userRepo.findByUsername(principal.getName());
				user.setPassword(teacher.getPassword());
				userRepo.save( user );
				
				
			
				//send password to the mail id
				//MailService.send(teacher.getEmail(), "Registration Successful ", " Your user_id : " + teacher.getEmail() +  "  password : " + teacher.getPassword());
				
				
				

				//set response message and return it as response
				responseMessage.setStatus("success");
				responseMessage.setMessage("Password successfully changed.");
				return  responseMessage;
		    				
		    }else {
		    	
		    	//set response message and return it as response
				responseMessage.setStatus("failure");
				responseMessage.setMessage("Please enter corrent password. Else click forgot password");
				return  responseMessage;

		    	
		    }

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	// to get subject list for the logged in teacher
	@GetMapping("/teacher/subject/list")
	@PreAuthorize("hasRole('ROLE_TEACHER')")
	public TeacherEntity getAllTeacherSubjectEntities(Principal principal) {
		
		TeacherEntity teacher = teacherRepo.findTeacherEntityByEmail(principal.getName());
		teacher.getSubjectList().forEach( s ->{
			s.setTeacher(null);		// to avoid infinite references
		});
		
		return teacher;

	}
	
	
	

	
	
	
	
	
	

	// this method will show you how to tackle infinite references
	@GetMapping("/teacher/subject/{subjectcode}/student/list")
	@PreAuthorize("hasRole('ROLE_TEACHER')")
	public StudentSubjectEntity getAllStudenSubjectEntities(@PathVariable("subjectcode") String id, Principal principal) {

		
		//first we need to check if the subject id matches with the current logged in user
		
		teacherRepo.findTeacherEntityByEmail(principal.getName())
		.getSubjectList()
		.forEach( eachSubject -> {
			if (eachSubject.getId().equalsIgnoreCase(id)) {
				loggedInTeacherHasThisSubject = true;
			}
		});
				 
		 
		 if (loggedInTeacherHasThisSubject) {
			 
			 //just to be safe
			 loggedInTeacherHasThisSubject = false;
			 StudentSubjectEntity  subject = studSubRepo.findStudentSubjectEntityById(id);
			 
			 for (int j = 0; j < subject.getStudentsList().size(); j++) {
				// clear subject list from each subject list property of student entity to avoid infinite references
				subject.getStudentsList().get(j).getSubjectList().clear();
				// unnecessary fields will be emptied or nulified so that JsonIgnore can ignore them in results.
				subject.getStudentsList().get(j).setEmail(null);
				subject.getStudentsList().get(j).setPassword(null);
				subject.getStudentsList().get(j).setPhone(null); // phone is BigInteger and not primitive so null is valid
			}
			 	 
		 return subject;
		 
		}else {
			return  null;
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	

	// below methods are for admins only only for admin..

	@GetMapping("/teacher/list")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<TeacherEntity> getAllTeacherEntities() {
		List<TeacherEntity> allTeachers = new ArrayList<TeacherEntity>();
		
		teacherRepo.findAll().forEach( t -> {
			t.getSubjectList().clear();
			allTeachers.add(t);
		});
		return allTeachers;

	}
	
	
	// this method will show you how to tackle infinite references
		@GetMapping("/student/list")
		@PreAuthorize("hasRole('ROLE_ADMIN')")
		public List<StudentEntity> getAllStudenEntities() {
			
			List<StudentEntity> studentList = new ArrayList<StudentEntity>();
			studentRepo.findAll().forEach(s ->{
				s.getSubjectList().clear();
				studentList.add(s);
			});
			
			return studentList;
		}
	
	

	@GetMapping("/all/teacher/subject/list")
	public List<TeacherSubjectEntity> getAllTeacherSubjectEntities() {
		List<TeacherSubjectEntity> t = new ArrayList<TeacherSubjectEntity>();
		teacherSubjectRepo.findAll().forEach(t::add);
		return t;

	}
	
	
	/*
	 * 
	 * 
	
	
	
	
	
	// this method will show you how to tackle infinite references
	@GetMapping("/student/subject/list")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<StudentSubjectEntity> getAllStudenSubjectEntities(@PathVariable String id) {

			 
			 List<StudentSubjectEntity> subjectList = new ArrayList<StudentSubjectEntity>();
				studSubRepo.findAll().forEach(subjectList::add);

				for (int i = 0; i < subjectList.size(); i++) {

					for (int j = 0; j < subjectList.get(i).getStudentsList().size(); j++) {

						// clear subject list from each subject list property of student entity
						subjectList.get(i).getStudentsList().get(j).getSubjectList().clear();

						// unnecessary fields will be emptied or nulified
						subjectList.get(i).getStudentsList().get(j).setEmail(null);
						subjectList.get(i).getStudentsList().get(j).setPhone(null); // phone is BigInteger and not primitive so null is valid
																					
					}
				}

				return subjectList;

		
		
	}
	
	
	
	
	*
	*
	*/

}
