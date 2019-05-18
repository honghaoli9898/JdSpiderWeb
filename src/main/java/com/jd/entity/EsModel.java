package com.jd.entity;

import java.util.Date;

public class EsModel {

	private String id;
	private Integer age;
	private String name;
	private Date date;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "EsModel{" + "id='" + id + '\'' + ", age=" + age + ", name='"
				+ name + '\'' + ", date=" + date + '}';
	}
}
