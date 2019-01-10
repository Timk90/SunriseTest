package com.sunrisetest.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.sunrisetest.Cell;

public class FileHandler {

	private static final String[] operators = "*,/,+,-".split(",");
	private static final String[] symbols = "A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,R,Q,S,T,U,V,W,X,Y,Z".split(",");
	
	public static List<Cell> getOutputCells() {
		return outputCells;
	}

	public static void setOutputCells(List<Cell> outputCells) {
		FileHandler.outputCells = outputCells;
	}

	public static List<Cell> getInputCells() {
		return inputCells;
	}

	public static void setInputCells(List<Cell> inputCells) {
		FileHandler.inputCells = inputCells;
	}

	static List<Cell> outputCells = new ArrayList<Cell>();
	static List<Cell> inputCells = new ArrayList<Cell>();
	
	public Cell checkCell(Cell cell) {
		boolean hasMath = false;
		boolean hasLink = false;	
	
		double number = 0;
		
		
		
		for(String operator : operators) {
			if(cell.getValue().contains(operator)) {
				hasMath = true;
				break;
			}
		}
		
		for(String symbol : symbols) {
			if(cell.getValue().contains(symbol)) {
				hasLink = true;
				break;
			}	
		}
		
		if(hasMath && !hasLink) {
			String strnumber = cell.getValue();
			ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
			
			
			try {
				number = (Double)(engine.eval(strnumber));
			}catch (ClassCastException e) {
				try {
					number = new Double((Integer)(engine.eval(strnumber)));
				} catch (ScriptException e1) {
					// TODO Auto-generated catch block
					cell.setResultvalue("Incorrect equation");
					cell.setValue("Incorrect equation");
					//e1.printStackTrace();
					return cell;
				}
			}catch (ScriptException e) {
				// TODO Auto-generated catch block
				cell.setResultvalue("Incorrect equation");
				cell.setValue("Incorrect equation");
				//e.printStackTrace();
				return cell;
			}
			cell.setResultvalue(number+"");
			return cell;
		}
		
		if(!hasLink && !hasMath) {
			cell.setResultvalue(cell.getValue());
		}
		
		if(hasLink) {
			List<String> linkedFields = separateFieldsInValue(cell.getValue());
			
			if(linkedFields.isEmpty()) {
				Cell cellinc = new Cell(cell.getField(),"Incorrect value");
				cellinc.setResultvalue("Incorrect value");
				return cellinc;
			}
			
			
			String value = cell.getValue();
			Cell linkedCell;
			Map<String, String> results = new HashMap<>();
			for(String field : linkedFields) {
				String result = "";
				if(isLinkedFieldExist(field)) {
					linkedCell = getLinkedFieldCell(field);
					linkedCell = checkCell(linkedCell);
					results.put(field, linkedCell.getResultvalue()); 
				}else {
					cell.setValue("Incorrect link");
					cell.setResultvalue("Incorrect link");
					return cell;
				}
			}
			
			value = changeEqv(value, linkedFields, results);
			cell.setValue(value);
			cell = checkCell(cell);
			return cell;
		}		
		return cell;
	}
	
	
	private static List<String> separateFieldsInValue(String value) {
		List<String> fields = new ArrayList<>();
		String regex = "[A-Z]{1,}[1-9]{1,}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(value);
		while(matcher.find()) {
			String tmp = value.substring(matcher.start(), matcher.end());
			fields.add(tmp);
		}
		return fields;
	}
	
	private static boolean isLinkedFieldExist(String field) {
		boolean exist = false;
		for(Cell cell : inputCells) {
			exist =(cell.getField().equals(field)) ? true : false;
			if(exist)
			return exist;
		}
		return false;
	}
	
	private static Cell getLinkedFieldCell(String field){
		for(Cell cell : inputCells) {
			if(cell.getField().equals(field)) {
				return cell;
			}
		}
		return new Cell(field, "Incorrect field");
	}
	
	private static String changeEqv(String value, List<String> linkedFields, Map<String, String> results) {

		Collections.sort(linkedFields, Collections.reverseOrder());
		//System.out.println(linkedFields);
		for(String sortedField : linkedFields) {
			try {
				value = value.replaceAll(sortedField+"(?!([a-zA-Z0-9]))", results.get(sortedField));
			}catch (NullPointerException e) {
				
			}
		}
		
		return value;
	}

}
