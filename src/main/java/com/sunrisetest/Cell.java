package com.sunrisetest;

public class Cell {
	
	String field; 
	String value;
	String resultvalue="";
	
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
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		Cell cell = (Cell)obj;
		boolean flag = (cell.value.equals(this.value)) ? true : false;
		return flag;
	}
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}
	
	
}
