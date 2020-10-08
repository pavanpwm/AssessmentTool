package org.tool.student;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "students_to_subjects")
public class StudentSubjectRalationEntity {
	
	
	@Id
	@Column(name = "s_id")
	private String studentId;
	
	@Column(name = "ss_id")
	private String subjectId;
	
	
	

	public StudentSubjectRalationEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public StudentSubjectRalationEntity(String studentId, String subjectId) {
		super();
		this.studentId = studentId;
		this.subjectId = subjectId;
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
