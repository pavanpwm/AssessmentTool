package org.tool.teacher;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "subjects")
public class SubjectEntity {
	
	@Id
	@Column(name = "id")
	private String id;
	
	@Column(name = "name")
	private String name;
	
	
	
	@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "teacher_id")
    private TeacherEntity teacher_id;

	
	
	
	public SubjectEntity() {
	}
	
	
	public SubjectEntity(String name) {
		this.name = name;
	}

	public SubjectEntity(String id, String name, TeacherEntity teacher_id) {
		this.id = id;
		this.name = name;
		this.teacher_id = teacher_id;
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

	public TeacherEntity getTeacher() {
		return teacher_id;
	}

	public void setTeacher(TeacherEntity teacher_id) {
		this.teacher_id = teacher_id;
	}


	@Override
	public String toString() {
		return "SubjectEntity [id=" + id + ", name=" + name + ", teacher_id=" + teacher_id + "]";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
