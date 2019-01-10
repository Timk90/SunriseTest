package com.sunrisetest.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sunrisetest.Cell;

public class FileReaderCSV {
	private int columnN = 0; //max number of columns
	private int rowN = 0; // max number of rows
	
	public int getColumnN() {
		return columnN;
	}

	public int getRowN() {
		return rowN;
	}

	public List<Cell> readfile(String filename){
		List<Cell> cells = new ArrayList<>();
		
		try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
			
			String line;
			int linecounter = 0;
			while((line = br.readLine()) != null) {
				String[] splitedLine = line.split(";");
				int columncounter = splitedLine.length;
				if(columncounter > columnN) {
					columnN = columncounter;
				}
				
				for(int j = 0; j<splitedLine.length; j++) {
					String field = generateFieldRecursively(j);
					cells.add(new Cell(field+(linecounter+1), splitedLine[j]));
				}
				linecounter++;
			}
			rowN = linecounter;
						
		}catch (FileNotFoundException e) {
			System.out.println("There is no file with this Filename");
		}catch (IOException e) {
			System.out.println("I/O error. Try again");			
		}catch (Exception e) {
			System.out.println("Unknown exception");	
		}
		
		return cells;
	}
	
	public static String generateFieldRecursively(int number) {
		StringBuilder sb = new StringBuilder();
		final String FIELD_SIGNS = "A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, R, Q, S, T, U, V, W, X, Y, Z"; //26 symbols
		String[] chars = FIELD_SIGNS.split(",");
		
		int div = number/26; 
		int mod = number%26;

		if(div > 0)
			sb.append(generateFieldRecursively(div-1));
		
		if(div > 0 && mod == 0)
			return sb.append(chars[mod].trim()).toString();
		
		sb.append(chars[mod].trim());
		return sb.toString();
	}
	
	public static void main(String[] args) {

		FileReaderCSV fr = new FileReaderCSV();

		//for(int i = 0; i < 750; i++)
		//System.out.println(fr.generateFieldRecursively(i));

		List<Cell> cells = fr.readfile("example.csv");
		
		for(Cell cell : cells) {
			System.out.println("field: "+cell.getField()+"; value="+cell.getValue()); 
		}
		System.out.println("columns: "+fr.getColumnN());
		System.out.println("rows: "+fr.getRowN());
	}
	

}
