package org.tool.teacher;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;





@Entity
@Table(name = "teacher_details")
public class TeacherEntity {
	
	//unique_id(auto-generated-java),name,phone,email,password,registrationStatus	
	
	@Id
	@Column(name = "teacher_id")
	private long teacher_id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "phone")
	private long phone;
	
	@Column(name = "password")
	private String password;
	
	
	
	@OneToMany(mappedBy = "teacher_id", cascade = {
		        CascadeType.ALL
		    })
	 private List< SubjectEntity > subjectList;
	
	
	

	public TeacherEntity() {
		
	}
	

	
	

	public TeacherEntity( long teacher_id, String name, String email, long phone, String password, 
			List<SubjectEntity> subjectList) {
		this.teacher_id = teacher_id;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.password = password;
		this.subjectList = subjectList;
	}





	public long getId() {
		return teacher_id;
	}

	public void setId(long teacher_id) {
		this.teacher_id = teacher_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public long getPhone() {
		return phone;
	}

	public void setPhone(long phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public List<SubjectEntity> getSubjectList() {
		return subjectList;
	}


	public void setSubjects(List<SubjectEntity> subjectList) {
		this.subjectList = subjectList;
	}
	
	public void addSubject(SubjectEntity subject) {
		this.subjectList.add(subject);
	}
	
	public void removeSubject(SubjectEntity subject) {
		this.subjectList.remove(subject);
	}





	@Override
	public String toString() {
		
		return "TeacherEntity [teacher_id=" + teacher_id + ", name=" + name + ", email=" + email + ", phone=" + phone
				+ ", password=" + password + ", subjectList=" + subjectList + "]";
	}
	
	
	
	
	
	
	

}
