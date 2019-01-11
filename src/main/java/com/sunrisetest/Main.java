package com.sunrisetest;

import java.util.ArrayList;
import java.util.List;

import com.sunrisetest.util.FileHandler;
import com.sunrisetest.util.FileReaderCSV;
import com.sunrisetest.util.FileWriterCSV;

/*
 * Главный класс и метод откуда запускается приложение.
 */

public class Main {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		/*
		 * создается экземпляр класса, который будет считывать данные входного файла
		 * (начальная таблица CSV с формулами и ссылками) 
		 */
		FileReaderCSV fr = new FileReaderCSV(); 
		
		//Имена входного и выходного файлов (при инициализации пустые)
		String input = ""; 
	    String output = "";
	    
	    //Имена входного и выходного списка ячеек (при инициализации пустые)
		List<Cell> inputcells; //в список будут добавлены все ячейки входного файла
		List<Cell> outputcells = new ArrayList<>(); //в список будут добавлены все ячейки выходного файла
		
		/*
		 * При запуске jar архива из командной строки производится попытка
		 * считывания имен файлов 
		 */
		try {
			input = args[0];
			output = args[1];
			/*
			 * Возможно указать только имя входного файла,
			 * тогда имя выходного файла сгенерируется автоматически как имя выходного с приставкой аут: "out_"+input
			 */
			if(output.length() == 0) {
				output = "out_"+input;
			}
		}catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("incorrect input/output file(s)");
		}
		
		/*
		 * Для тестовых целей я поместил файл с готовой таблицей в корневой
		 * каталог проекта и сразу же задал имя выходного файла 
		 */
		input = (input.equals("")) ? "example1.csv": input;
		output = (output.equals("")) ? "out_example1.csv": output;
		
		//используется созданный экземпляр класса FileReaderCSV для
		//считывания массива (списка) ячеек из входного файла 
		inputcells = fr.readfile(input);
		
		//создается экземпляр класса обработчика данных масива ячеек
		FileHandler fh = new FileHandler();
		//утсанавливается массив ячеек для обработчика
		fh.setInputCells(inputcells);
		
		//Для наглядности выводится начальное содржимое файла до обработки 
		System.out.println("======= Initial cells' view ========");
		for(Cell cell : inputcells) {
			System.out.println("field: "+cell.getField()+"; value="+cell.getValue()+ ", result="+cell.getResultvalue()); 
		}
		
		//производится обработка содержимого файла
		for(Cell cell: inputcells) {
			Cell newCell = fh.checkCell(cell);
			outputcells.add(newCell);
		}
		
		//Для наглядности выводится содржимое файла после обработки 
		System.out.println();
		System.out.println("======== Final cells' view ========");
		for(Cell cell: outputcells) {
			System.out.println("field: "+cell.getField()+"; value="+cell.getValue()+ ", result="+cell.getResultvalue());
		}
		
		//создается экземпляр класса осуществляющего запись результата обработки в выходной файл 
		System.out.println();
		FileWriterCSV fw = new FileWriterCSV();
		System.out.println(fw.writefile(outputcells, output, fr.getStructure()));
		
	}

}
