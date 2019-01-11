package com.sunrisetest;
/*
 * Класс, который представляет собой сущность одной ячейки табличного файла
 * Содержит три поля, геттеры/сеттеры и конструктор
 * 
 * Поля:
 *  - field представляет собой поле, содержащие название ячейки (например: А1, B2, H4 и тд.)
 *  - value представляет собой поле, содержащие наполнение этой ячейки (например: А1+B2/H4, 10, 12.0, А1 и тд.)
 *  - resultvalue представляет собой поле, содержащие конечный результат формулы или ошибку после обработки ячейки 
 * 	  (например: 12.0(число), "incorrect link", "incorrect value", "incorrect field")
 * 
 * Конструктор:
 *  при создании экземпляра класса задаются только field и value.
 *  поле resultvalue по умолчанию = пустая строка
 *  
 */
public class Cell {
	
	private String field; 
	private String value;
	private String resultvalue="";
	
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getResultvalue() {
		return resultvalue;
	}
	public void setResultvalue(String resultvalue) {
		this.resultvalue = resultvalue;
	}
	
	public Cell(String field, String value) {
		super();
		this.field = field;
		this.value = value;
	}
	
}
