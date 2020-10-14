package org.tool.student;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "students_to_subjects")
public class StudentSubjectRelationEntity {
	
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Column(name = "s_id")
	private String studentId;
	
	@Column(name = "ss_id")
	private String subjectId;

	public StudentSubjectRelationEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public StudentSubjectRelationEntity(int id, String studentId, String subjectId) {
		super();
		this.id = id;
		this.studentId = studentId;
		this.subjectId = subjectId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
	
	
	

	
	

	
	
}
