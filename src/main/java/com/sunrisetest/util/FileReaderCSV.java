package com.sunrisetest.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.sunrisetest.Cell;

public class FileReaderCSV {
	
	//структура входного файла записывается в Map
	//где key - соответствует номеру строки, а value - количеству столбцов в этой строке
	Map<Integer, Integer> filestructure = new TreeMap<>();
	public Map<Integer, Integer> getStructure(){
		return filestructure;
	}

	//основной метод, осуществляющий чтение входного файла, генерацию номеров ячеек и записи в них соответствующего им значения
	//из входного файла. Ячейки записываются в список List
	public List<Cell> readfile(String filename){
		List<Cell> cells = new ArrayList<>();
		
		//попытка доступа к файлу, где filename - его имя 
		try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
			
			String line;
			int linecounter = 0;
			
			while((line = br.readLine()) != null) {
				//строки разбиваются на отдельные ячейки сплиттером = ";"
				String[] splitedLine = line.split(";");
				int columncounter = splitedLine.length;
				//запускается цикл генерации для каждого элемента ячейки названия соответствующего ему имени поля (A,B,CC, ZXK и тд) 
				//методом generateFieldRecursively(j), зависящего от номера столбца 
				for(int j = 0; j<splitedLine.length; j++) {
					String field = generateFieldRecursively(j);
					//в конечное имя поля добавляется также номер строки, начиная с 1 (как в excel). 
					cells.add(new Cell(field+(linecounter+1), splitedLine[j]));
				}
				linecounter++;
				//увеличение номера линии и запись в структурную Мэп. 
				//отсчет строк также начинается с 1, поэтому сначала инкрементирование.
				filestructure.put(linecounter, columncounter);
			}
		//обрабаотываются возможные исключения 		
		}catch (FileNotFoundException e) {
			System.out.println("There is no file with this Filename");
		}catch (IOException e) {
			System.out.println("I/O error. Try again");			
		}catch (Exception e) {
			System.out.println("Unknown exception");	
		}
		//
		return cells;
	}
	
	//Генерация буквенной части имени поля. Поскольку заранее неизвестна структура входного файла, т.е. количества столбцов ячеек, 
	//то генерация была реализована рекурсивно. В качестве символов доступны только 26 возможных символов латинского алфавита, при
	//недостаточном количестве добавляется еще одна буква и возможных комбинаций становится в 26 раз больше и тд. 
	public static String generateFieldRecursively(int number) {
		StringBuilder sb = new StringBuilder();
		final String FIELD_SIGNS = "A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, R, Q, S, T, U, V, W, X, Y, Z"; //26 symbols
		String[] chars = FIELD_SIGNS.split(",");
		
		//вычисляется целая часть от деления на 26 (указывает на то, нужно ли добавлять следующую букву или нет) 
		int div = number/26; 
		//остаток от деления на 26, указывает на текущую букву, которую нужно добавить в поле
		int mod = number%26;

		//если число столбцов больше 26, то процедура повторяется рекурсивно, с указанием полученного целого числа от деления на 26 
		//в качестве входного параметра
		if(div > 0)
			sb.append(generateFieldRecursively(div-1));
		//если же число меньше 26, то просто записывается соответствующая ему буква в стринг билдер, который передается на выход из
		//метода на предыдущий шаг рекурсии или вызов ветода при чтении из файла. 
		if(div > 0 && mod == 0)
			return sb.append(chars[mod].trim()).toString();
		
		sb.append(chars[mod].trim());
		return sb.toString();
	}

}
