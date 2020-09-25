package org.tool.student;
  
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "student_subjects")

public class StudentSubjectEntity {
	
	@Id
	@Column(name = "subject_id")
	private String id;
	
	@Column(name = "name")
	private String name;
	
	
	@ManyToMany(mappedBy = "subjectList", targetEntity = StudentEntity.class, cascade =  CascadeType.ALL )
	@JsonManagedReference
    private List<StudentEntity> studentsList = new ArrayList<StudentEntity>();

	
	
	
	public StudentSubjectEntity() {
	}
	
	
	public StudentSubjectEntity(String name) {
		this.name = name;
	}


	public StudentSubjectEntity(String id, String name, List<StudentEntity> studentsList) {
		super();
		this.id = id;
		this.name = name;
		this.studentsList = studentsList;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public List<StudentEntity> getStudentsList() {
		return studentsList;
	}


	public void setStudentsList(List<StudentEntity> studentsList) {
		this.studentsList = studentsList;
	}


	@Override
	public String toString() {
		return "StudentSubjectEntity [id=" + id + ", name=" + name + ", studentsList=" + studentsList + "]";
	}


	


	


	

	
	

}
