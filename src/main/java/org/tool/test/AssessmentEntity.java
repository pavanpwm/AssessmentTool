package org.tool.test;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "assessment")
public class AssessmentEntity {

	
	@Id
	@Column(name = "id")
    @GeneratedValue( strategy = GenerationType.AUTO )
	private int id;
	
	@Column(name = "test_code")
	private String testCode;
	
	
	@Column(name = "student_name")
	private String studentName;
	
	@Column(name = "student_username")
	private String studentUsername;
	
	@Column(name = "subject_code")
	private String subjectCode;
	
	@Column(name = "topic")
	private String topic;
	
	@Column(name = "question_index")
	private int questionIndex;
	
	@Column(name = "question_id")
	private int questionId;
	
	@Column(name = "selected_choice")
	private String selectedChoice;
	
	@Column(name = "marks")
	private int marks;

	public AssessmentEntity() {
		// TODO Auto-generated constructor stub
	}

	public AssessmentEntity(int id, String testCode, String studentName, String studentUsername, String subjectCode,
			String topic, int questionIndex, int questionId, String selectedChoice, int marks) {
		super();
		this.id = id;
		this.testCode = testCode;
		this.studentName = studentName;
		this.studentUsername = studentUsername;
		this.subjectCode = subjectCode;
		this.topic = topic;
		this.questionIndex = questionIndex;
		this.questionId = questionId;
		this.selectedChoice = selectedChoice;
		this.marks = marks;
	}

	public AssessmentEntity(String testCode, String studentName, String studentUsername, String subjectCode,
			String topic, int questionIndex, int questionId) {
		super();
		this.testCode = testCode;
		this.studentName = studentName;
		this.studentUsername = studentUsername;
		this.subjectCode = subjectCode;
		this.topic = topic;
		this.questionIndex = questionIndex;
		this.questionId = questionId;
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

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getStudentUsername() {
		return studentUsername;
	}

	public void setStudentUsername(String studentUsername) {
		this.studentUsername = studentUsername;
	}

	public String getSubjectCode() {
		return subjectCode;
	}

	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public int getQuestionIndex() {
		return questionIndex;
	}

	public void setQuestionIndex(int questionIndex) {
		this.questionIndex = questionIndex;
	}

	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

	public String getSelectedChoice() {
		return selectedChoice;
	}

	public void setSelectedChoice(String selectedChoice) {
		this.selectedChoice = selectedChoice;
	}

	public int getMarks() {
		return marks;
	}

	public void setMarks(int marks) {
		this.marks = marks;
	}

	
	
	

	
	
}
