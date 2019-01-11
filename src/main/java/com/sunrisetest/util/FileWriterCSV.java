package com.sunrisetest.util;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.sunrisetest.Cell;

/*
 * Метод осуществляет запись, полученных результатов вычислений, записанных в массив в новый файл 
 * соответствующий нужному формату - Разделитель ";". При этом в ходе записи выдерживается структура входного файла. 
 * 
 */

public class FileWriterCSV {

	//метод, осуществляющий запись ячеек из списка выходных ячеек в новый файл 
	//в метод передается массив с новыми ячейками, имя выходного файла и структура ячеек входного файла
	//структура представлена в виде Map в которой key - соответствует номеру строки, а value - количеству столбцов в этой строке
	public String writefile(List<Cell> cells, String filename, Map<Integer, Integer> structure){
		
		
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(filename, false))) {
			int columnN = 1;
			int rowN = 1;
			
		
			for(int i = 0; i < cells.size(); i++) {
				if(columnN == structure.get(rowN)) {
					bw.write(cells.get(i).getResultvalue()+";\n");
					rowN++;
					columnN=1;
				}else {
					bw.write(cells.get(i).getResultvalue()+";");
					columnN++;
				}
				
			}
			
			bw.close();
			
		}catch (FileNotFoundException e) {
			System.out.println("There is no file with this filename");
			return "error while writing file";
		}catch (IOException e) {
			System.out.println("I/O error. Try again");	
			return "error while writing file";
		}catch (Exception e) {
			System.out.println("Unknown exception");
			e.printStackTrace();
			return "error while writing file";
		}
		return "file has been succesfully written";
	}
	
}
