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

/* 
 * Класс, который обрабатывает ячейки входного файла
 * 
 * Логика проверки отдельно взятой ячейки следующая:
 * 1. Ячейка может содержат формулу (hasMath), ссылку на другую ячейку (hasLink - A1, A2..),
 *    одновременно ссылку и формулу, просто числовое значение, некорректно введенные данные
 *    (среди которых может быть неправильная формула, неправильная ссылка, неправильное значение)
 * 2. Метод проверки возвращает значение hasMath = true, если значение ячейки(value) содержит один из математических 
 *    операторов (+,-,*,/), указанных в массиве (operators), и возвращает (hasLink = true), если значение содержит одну 
 *    из букв латинского алфавита - массив symbols.
 * 3. В зависимсти от указанного выше случая происходит проверка формулы, либо ячейки, либо, в случае числа, в конечный результат
 *    (resulvalue) записывается окончательное значение. Проверка осуществляется рекурсивно, 
 *    поскольку ячейка на которую ссылается текущая ячейка, также может содержать ссылку или формулу со ссылками. 
 *    Рекурсивная проверка осуществляется для каждой ссылки и до тех пор, пока мы не перейдем по ссылке к ячейке, содержащей только конечное 
 *    числовое значение (т.е. без ссылок). Это значение возвращается на предыдущий шаг рекурсии и устанавливается в формуле вместо ссылки.
 *    Таким образом все ссылки заменяюся на конечные числовые значения в формуле.
 * 4. После этого, поле содержит мат.выражение и осуществляется попытка вычисления его числового значения по выражению. 
 *    Если формула корректна, то полученное в ходе вычисления числовое значение записывается в ячейку и возвращается на предыдущий шаг рекурсии, 
 *    либо в поле результат устанавливается сообщение о некорректной формуле.
 * 5. В конце мы получаем массив ячеек, который содержит поля (value) для каждой ячейки с соответствующим числовым значением, или 
 *    содержащими числовые формулы без ссылок, а в полях (resultvalue) - только числовые значения, либо информацию о произошедшей ошибке. 
 *     
 */

public class FileHandler {

	//массивы для проверки наличия в ячейке формулы или ссылки
	private static final String[] operators = "*,/,+,-".split(",");
	private static final String[] symbols = "A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,R,Q,S,T,U,V,W,X,Y,Z".split(",");
	
	public static List<Cell> getOutputCells() {
		return outputCells;
	}

	//геттеры и сеттеры некоторых полей 
	public static void setOutputCells(List<Cell> outputCells) {
		FileHandler.outputCells = outputCells;
	}

	public static List<Cell> getInputCells() {
		return inputCells;
	}

	public static void setInputCells(List<Cell> inputCells) {
		FileHandler.inputCells = inputCells;
	}
	
	//иниициализация списков для записи ячеек из входного и записи новых ячеек в выходной файлы
	static List<Cell> outputCells = new ArrayList<Cell>();
	static List<Cell> inputCells = new ArrayList<Cell>();
	
	//основной метод класса (реализует всю логику проверки полей ячейки) 
	public Cell checkCell(Cell cell) {
		
		//сначала мы полагаем, что в ячейке нет формулы и ссылки на другое поле
		boolean hasMath = false;
		boolean hasLink = false;	
		
		//а значение ячейки resultvalue равно нулю.
		double number = 0;
		
		//проверка на наличие формулы
		for(String operator : operators) {
			if(cell.getValue().contains(operator)) {
				hasMath = true;
				break;
			}
		}
		
		//проверка на наличие ссылки 
		for(String symbol : symbols) {
			if(cell.getValue().contains(symbol)) {
				hasLink = true;
				break;
			}	
		}
		
		//если найдена формула без ссылки 
		if(hasMath && !hasLink) {
			
			//используется движок джаваскрипт для вычисления формул записанных в строковом представлении
			//(был удивелен, что нельзя пропарсить формулу средствами Джава "на лету"). 
			String strnumber = cell.getValue();
			ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
			
			//производится попытка вычисления формулы
			try {
				number = (Double)(engine.eval(strnumber));
			}catch (ClassCastException e) {
				//Возможен случай, когда содержимое ячейки после вычисления имеет значение int (целочисленное)
				//а также возможно значение double (вещественное)
				//сначала пытаемся вычислить как double, а если не получается, то парсим значение в int 
				//и создаем новое значение данного int в формате double (таким образом, конечный результат всегда имеет тип double)
				try {
					number = new Double((Integer)(engine.eval(strnumber)));
				//в случае неудачных вычислений, указываем, что формула неправильная. 
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
			//устанавливаем resultvalue, для этого значение double 
			//переводим в строку. 
			cell.setResultvalue(number+"");
			return cell;
		}
		
		//проверка на отсутствие в ячейке и формулы и ссылок, сразу же устанавливается значение поля value в resultvalue
		if(!hasLink && !hasMath) {
			cell.setResultvalue(cell.getValue());
		}
		
		//если поле содержит ссылку 
		if(hasLink) {
			//ссылка может состоять из ссылок на несколько ячеек, поэтому создаем массив 
			//в котором будем хранить ссылки из формулы текущей ячейки (см. вспомогтельный метод separateFieldsInValue(String string)
			List<String> linkedFields = separateFieldsInValue(cell.getValue());
			
			//если после разделения формулы на ссылки и записи в массив он оказывается пустым, 
			//то это значит, что было установлено неправильное 
			//значение поля, а реальных ссылок на поля таблицы в этом поле не было.
			//в таком случае в поле результата устанавливается строка с указанием на некорректное значение
			//и последующим выходом из метода
			if(linkedFields.isEmpty()) {
				Cell cellinc = new Cell(cell.getField(),"Incorrect value");
				cellinc.setResultvalue("Incorrect value");
				return cellinc;
			}
			
			//если массив не пуст, значит ссылки были найдены и нужно проверить содержимое соответствующих им 
			//ячеек, но сначала скопировать формулу в переменную value
			String value = cell.getValue();
			//а также указать новую переменную ячейки для соответствующей ссылки 
			Cell linkedCell;
			Map<String, String> results = new HashMap<>();
			
			//поскольку ссылок может быть несколько, то процедура проверки каждой ссылки осуществляется в цикле
			for(String field : linkedFields) {
				String result = "";
				//проверка на существование такой ячейки в массиве доступных после чтения файла ячеек
				if(isLinkedFieldExist(field)) {
					//если такая ячейка там есть (существует) то сылка на нее передается в переменную linkedcell
					linkedCell = getLinkedFieldCell(field);
					//для нее повторяется процедура проверки (рекурсия).
					linkedCell = checkCell(linkedCell);
					results.put(field, linkedCell.getResultvalue()); 
				}else {
					//если сслыка указывает на недоступное во входном файле поле, то указывается ее некорректность
					cell.setValue("Incorrect link");
					cell.setResultvalue("Incorrect link");
					return cell;
				}
			}
			
			//когда все ссылки из формулы были проверены и получили соответствующие числовые значения 
			//или указания на их некорректность, то начальная формула содержащая ссылки методом changeEqv(String equation)
			//заменяется на формулу, содержащую только числа, чтобы можно было ее вычислить. 
			value = changeEqv(value, linkedFields, results);
			//текущей, проверяемой, ячейке присваивается эта формула и опять выполняется рекурсивная проверка
			//с целью вычислить ее значение. 
			cell.setValue(value);
			cell = checkCell(cell);
			return cell;
		}		
		return cell;
	}
	
	/*
	 * Далее слудеют вспомогательные методы, которые использовались в методе checkCell(Cell cell) для проверок формулы, 
	 * записи ячеек, входящих в состав формулы в массив, замена формулы, содержащей ссылки на ее числовой аналог.
	 */
	
	//метод выделяет из формулы ссылки, используя регулярные выражения и добавляет их в список, 
	//Данный список возвращает вызывающему методу
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
	
	//метод выполняет проверку для полученных ссылок на существование соответсвующих им ячеек
	//во входном файле (которые при чтении из файла были сгенерированы и записаны в массив, 
	//а массив был передан в inputCells) сеттером методе Мэйн). 
	//Если ссылка найдена то возвращается true и осуществляется выход из метода 
	private static boolean isLinkedFieldExist(String field) {
		boolean exist = false;
		for(Cell cell : inputCells) {
			exist =(cell.getField().equals(field)) ? true : false;
			if(exist)
			return exist;
		}
		return false;
	}
	
	//Далее, если ячейка найдена среди ячеек входного файла, данный метод возвращает ссылку на эту ячейку 
	//чтобы осуществить ее дальнейшую проверку
	private static Cell getLinkedFieldCell(String field){
		for(Cell cell : inputCells) {
			if(cell.getField().equals(field)) {
				return cell;
			}
		}
		return new Cell(field, "Incorrect field");
	}
	
	//Данный метод преобразует мат. выражение содержащее ссылки в формулу, содержащую исключительно цифры и мат операторы 
	//либо конечные результаты некорректного выполнения 
	private static String changeEqv(String value, List<String> linkedFields, Map<String, String> results) {

		Collections.sort(linkedFields, Collections.reverseOrder());
		
		//TODO (Regex)
		//Не смог подобрать удачного регулярного выражения, поэтому решил сначала отсортировать массив доступных ячеек по убыванию,
		//чтобы сначала заменять поля с самыми длинными именами ссылок и далее переходить к коротким. 
		//то есть сначала заменить АА1, а потом уже А1. 
		for(String sortedField : linkedFields) {
			try {
				value = value.replaceAll(sortedField+"(?!([a-zA-Z0-9]))", results.get(sortedField));
			}catch (NullPointerException e) {
				//ничего не происходит, если не получается заменить какое-то поле
			}
		}
		//возвращается новая формула c числами. 
		return value;
	}

}
