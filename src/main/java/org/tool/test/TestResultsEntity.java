package org.tool.test;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "reults")
@JsonInclude(Include.NON_EMPTY)
public class TestResultsEntity {
	
	
	@Id
	@Column(name = "id")
    @GeneratedValue( strategy = GenerationType.AUTO )
	private int id;
	
	@Column(name = "test_code")
	private String testCode;
	
	@Column(name = "student_username")
	private String studentUsername;
	
	@Column(name = "student_name")
	private String studentName;
	
	@Column(name = "score")
	private int score;
	

	public TestResultsEntity() {
		super();
		// TODO Auto-generated constructor stub
	}


	public TestResultsEntity(int id, String testCode, String studentUsername, String studentName, int score) {
		super();
		this.id = id;
		this.testCode = testCode;
		this.studentUsername = studentUsername;
		this.studentName = studentName;
		this.score = score;
	}
	
	public TestResultsEntity(String testCode, String studentUsername, String studentName, int score) {
		this.testCode = testCode;
		this.studentUsername = studentUsername;
		this.studentName = studentName;
		this.score = score;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getTestCode() {
		return testCode;
	}


	public void setTestCode(String testCode) {
		this.testCode = testCode;
	}


	public String getStudentUsername() {
		return studentUsername;
	}


	public void setStudentUsername(String studentUsername) {
		this.studentUsername = studentUsername;
	}


	public String getStudentName() {
		return studentName;
	}


	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}


	public int getScore() {
		return score;
	}


	public void setScore(int score) {
		this.score = score;
	}
	
	
	

}
