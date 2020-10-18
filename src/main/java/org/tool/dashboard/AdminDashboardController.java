package org.tool.dashboard;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tool.mail.service.MailService;
import org.tool.student.StudentEntity;
import org.tool.student.StudentRepository;
import org.tool.teacher.TeacherEntity;
import org.tool.teacher.TeacherRepository;
import org.tool.teacher.TeacherSubjectEntity;
import org.tool.teacher.TeacherSubjectRepository;


@RestController
public class AdminDashboardController {
	
	@Autowired
	TeacherRepository  teacherRepoA;
	
	@Autowired
	TeacherSubjectRepository teacherSubjectRepoA;
	
	@Autowired
	StudentRepository studentRepoA;
	

	
	
	
		
		@GetMapping("/teacher/list")
		@PreAuthorize("hasRole('ROLE_ADMIN')")
		public List<TeacherEntity> getAllTeacherEntities() {
			List<TeacherEntity> allTeachers = new ArrayList<TeacherEntity>();

			teacherRepoA.findAll().forEach(t -> {
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
			studentRepoA.findAll().forEach(s -> {
				s.getSubjectList().clear();
				studentList.add(s);
			});

			return studentList;
		}

		

		@GetMapping("/all/teacher/subject/list")
		public List<TeacherSubjectEntity> getAllTeacherSubjectEntities() {
			try {
				List<TeacherSubjectEntity> t = new ArrayList<TeacherSubjectEntity>();
				teacherSubjectRepoA.findAll().forEach(t::add);
				return t;
			} catch (Exception e) {
				return null;
				
			}
		}
		
		
		@GetMapping("/homepage/contactme")
		public String homePageContactMe(@RequestParam(name = "name") String name, @RequestParam(name = "email") String email, @RequestParam(name = "comment") String comment ) {
			try {
				MailService.send("service.assessment.tool@gmail.com", "Contact Me", name + "/n" + email + "/n" + comment);				
				return "sent";
			} catch (Exception e) {
				return null;
				
			}
		}
	

}
