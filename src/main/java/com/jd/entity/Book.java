package com.jd.entity;

import java.util.Date;

public class Book {

	String id;
	String name;
	String message;
	Double price;
	Date creatDate;

	public Book(String id, String name, String message, Double price,
			Date creatDate) {
		this.id = id;
		this.name = name;
		this.message = message;
		this.price = price;
		this.creatDate = creatDate;
	}

	public Date getCreatDate() {
		return creatDate;
	}

	public void setCreatDate(Date creatDate) {
		this.creatDate = creatDate;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}
}