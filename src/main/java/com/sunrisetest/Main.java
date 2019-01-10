package com.sunrisetest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sunrisetest.util.FileHandler;
import com.sunrisetest.util.FileReaderCSV;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FileReaderCSV fr = new FileReaderCSV();
		String input = "", output = "";
		
		try {
			input = args[0];
			output = args[1];
		
		}catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("incorrect input/output file(s)");
		}
		
		input = (input.equals("")) ? "example1.csv": input;
		
		List<Cell> cells = fr.readfile("example1.csv");
		System.out.println(cells);
		
		FileHandler hf = new FileHandler();
		
		hf.setInputCells(cells);
		
		for(Cell cell : cells) {
			System.out.println("field: "+cell.getField()+"; value="+cell.getValue()); 
		}
		System.out.println("columns: "+fr.getColumnN());
		System.out.println("rows: "+fr.getRowN());
		
		for(Cell cell: cells) {
			Cell newCell = hf.checkCell(cell);
			System.out.println("field: "+newCell.getField()+"; value="+newCell.getValue()+ ", result="+newCell.getResultvalue());
		}

	}

}
