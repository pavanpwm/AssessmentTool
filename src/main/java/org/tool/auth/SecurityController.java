package org.tool.auth;

import java.security.Principal;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import org.tool.reponse.ResponseMessage;
import org.tool.student.StudentEntity;
import org.tool.student.StudentRepository;
import org.tool.teacher.TeacherEntity;
import org.tool.teacher.TeacherRepository;

@RestController
@PermitAll
public class SecurityController {
	
	@Autowired
	private ResponseMessage respMessage;
	
	@Autowired
	private StudentRepository studentReposSec;
	
	@Autowired
	private TeacherRepository teacherRepoSec;
	
	@Autowired
	private UserRepository userRespoSec;
	
	
	
	//if user is already logged in and goes to login page, then he is redirected his dashboard, else will remain on login page
	@GetMapping("/check/login")
	public ResponseMessage checkLogin(Principal principal) {
		
		try {
			if(principal == null) {
				respMessage.setMessage("false");
				return respMessage;
			}
			respMessage.setMessage("true");
			return respMessage;
		} catch (Exception e) {
			respMessage.setMessage("error");
			return respMessage;
		}
	}
	


	// redirect to their respective dash boards according their roles.
	@GetMapping("/redirect")
	public RedirectView redirect(HttpServletRequest request) {

		RedirectView redirectView = new RedirectView();

		if (request.isUserInRole("ROLE_ADMIN")) {
			redirectView.setUrl("/admin.html");
			return redirectView;
		} else if (request.isUserInRole("ROLE_TEACHER")) {
			redirectView.setUrl("/tdashboard.html");
			return redirectView;
		} else {
			redirectView.setUrl("/sdashboard.html");
			return redirectView;
		}
	}
	
	
	
	
	@GetMapping("/forgot/password/send/otp")
	public ResponseMessage verifyEmail(HttpServletRequest request ) {
		
		String email = request.getParameter("email");
		
		System.out.println(email);
		
		try {
			if(studentReposSec.existsStudentEntityByEmail(email) || teacherRepoSec.existsTeacherEntityByEmail(email)) {
				request.getSession().setAttribute("email", email );
				String otp = RandomStringUtils.random(5, true, true);
				request.getSession().setAttribute("otp", otp );
				request.getSession().setAttribute("verified", false );
				//MailService.send(email, "One Time Password  ", " Your OTP is :  " + otp);
				respMessage.setMessage("sent");
				return respMessage;
			}
		  
		} catch (Exception e) {
			return null;
		}
		
		return null;
		
	}
	
	
	@GetMapping("/forgot/password/verify/otp/{otp}")
	public ResponseMessage verifyOtp(@PathVariable("otp") String otp, HttpServletRequest request ) {
		
		
		/*	
		 * 
		 * 
		 
		if ( request.getSession().getAttribute("otp").equals(otp) ) {
			request.getSession().removeAttribute("otp");
			request.getSession().setAttribute("verified", true );			
			responseMessage.setMessage("verified");
		}else {
			responseMessage.setMessage("not verified");
		}
		*
		*/
		
		request.getSession().setAttribute("verified", true );
		respMessage.setMessage("verified");
		return respMessage;
	}
	
	
	
	
	@GetMapping("/forgot/password/update/password")
	public ResponseMessage resetPass(HttpServletRequest request ) {

		try {
			
			if (request.getSession().getAttribute("verified").equals(true)) {
				
				request.getSession().removeAttribute("verified");
				
				
				if (studentReposSec.existsStudentEntityByEmail( request.getSession().getAttribute("email").toString() )) {
					StudentEntity student = studentReposSec.findByEmail( request.getSession().getAttribute("email").toString()  );
					User user = userRespoSec.findByUsername( request.getSession().getAttribute("email").toString() );
					
					student.setPassword(request.getParameter("password"));
					user.setPassword( new BCryptPasswordEncoder(11).encode( request.getParameter("password") ) );
					
					studentReposSec.save(student);
					userRespoSec.save(user);
					
					respMessage.setMessage("success");
					
					return respMessage;
					
				}else if (teacherRepoSec.existsTeacherEntityByEmail( request.getSession().getAttribute("email").toString() )) {
					
					TeacherEntity teacher = teacherRepoSec.findTeacherEntityByEmail( request.getSession().getAttribute("email").toString()  );
					User user = userRespoSec.findByUsername( request.getSession().getAttribute("email").toString() );
					
					teacher.setPassword(request.getParameter("password"));
					user.setPassword( new BCryptPasswordEncoder(11).encode( request.getParameter("password") ) );
					
					teacherRepoSec.save(teacher);
					userRespoSec.save(user);
					
					respMessage.setMessage("success");
					return respMessage;
					
				}
			}
			
			return null;
			
			
		} catch (Exception e) {
			return null;
		}

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
