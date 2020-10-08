package org.tool.teacher;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.tool.auth.User;
import org.tool.auth.UserRepository;
import org.tool.reponse.ResponseMessage;

@RestController
public class TeacherController {

	@Autowired
	private TeacherRepository tRepo;

	@Autowired
	private TeacherSubjectRepository tSubRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ResponseMessage responseMessage;

	@PostMapping("/teacher/register")
	public ResponseMessage registerTeacher(@RequestBody TeacherEntity teacher) {

		// check if email already exists in db.
		if (!tRepo.existsTeacherEntityByEmail(teacher.getEmail())) {

			// generate an id and set it for the teacher
			teacher.setId(java.time.LocalTime.now().toString().replaceAll(":", "").replaceAll("\\.", ""));

			// generate and set password RandomStringUtils from apache commons lang library
			teacher.setPassword(RandomStringUtils.random(10, true, true));

			// iterate through teacher subjects that we got in post request body
			for (TeacherSubjectEntity s : teacher.getSubjectList()) {
				// generate an id for each subject with prefix as subject name itself
				s.setId(s.getName().replaceAll("\\s", "").toLowerCase()
						+ java.time.LocalTime.now().toString().replaceAll(":", "").replaceAll("\\.", "").substring(4));
				s.setTeacher(teacher);
			}

			// send password to the mail id
			// MailService.send(teacher.getEmail(), "Registration Successful ", " Your
			// user_id : " + teacher.getEmail() + " password : " + teacher.getPassword());

			// saving teacher auth credentials in user table.
			User user = new User(teacher.getEmail(), new BCryptPasswordEncoder(11).encode(teacher.getPassword()),
					"ROLE_TEACHER", true, true, true, true);
			userRepo.save(user);

			// save the data to db
			tRepo.save(teacher);

			// set response message and return it as resposne
			responseMessage.setStatus("success");
			responseMessage.setMessage(
					"Your User ID and Password are sent to your mail. Please use them to login and change password for successful registration.");
			return responseMessage;

		} else {

			// if email already exists in db then, send below message
			responseMessage.setStatus("failure");
			responseMessage.setMessage(
					"User already registered. Please register with a different email or click forgot password button to reset your password.");
			return responseMessage;
		}

	}

}
