package org.tool.student;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.JoinColumn;




/*
 
Now, we are using a new annotation @JsonIdentityInfo. In the previous articles, we have StackOverflow errors due to circular references. 
We have been using @JsonIgnore, @JsonManagedReference, and @JsonBackReference to take care of the error. This new annotation, @JsonIdentityInfo,
will handle the circular reference errors for us.

 */



@Entity
@Table(name = "students")

public class StudentEntity {
	

	
	@Id
	@Column(name = "student_id")
	private long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "s_id")
	private String sId;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "phone")
	private long phone;
	
	@Column(name = "password")
	private String password;
	
	

	@ManyToMany(targetEntity = StudentSubjectEntity.class, cascade = CascadeType.ALL)
	@JoinTable(
	        name = "students_and_subjects",
	        joinColumns = {
	            @JoinColumn(name = "student_id")
	        },
	        inverseJoinColumns = {
	            @JoinColumn(name = "subject_id")
	        }
	    )
	 @JsonBackReference
	 private List< StudentSubjectEntity > subjectList = new ArrayList<StudentSubjectEntity>();
	
	
	

	public StudentEntity() {
		
	}
	
	
	
	

	


	public StudentEntity(long id, String name, String sId, String email, long phone, String password,
			List<StudentSubjectEntity> subjectList) {
		this.id = id;
		this.name = name;
		this.sId = sId;
		this.email = email;
		this.phone = phone;
		this.password = password;
		this.subjectList = subjectList;
	}
	


	public long getId() {
		return id;
	}



	public void setId(long id) {
		this.id = id;
	}



	public String getName() {
		return name;
	}

	

	public void setName(String name) {
		this.name = name;
	}



	public String getsId() {
		return sId;
	}

	
	
	public void setsId(String sId) {
		this.sId = sId;
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




	public List<StudentSubjectEntity> getSubjectList() {
		return subjectList;
	}




	public void setSubjectList(List<StudentSubjectEntity> subjectList) {
		this.subjectList = subjectList;
	}



	public void addSubject(StudentSubjectEntity subject) {
		this.subjectList.add(subject);
	}
	
	
	
	
	public void removeSubject(StudentSubjectEntity subject) {
		this.subjectList.remove(subject);
	}








	@Override
	public String toString() {
		return "StudentEntity [id=" + id + ", name=" + name + ", sId=" + sId + ", email=" + email + ", phone=" + phone
				+ ", password=" + password + ", subjectList=" + subjectList + "]";
	}





	
	
	
	
	
	
	
	

}
