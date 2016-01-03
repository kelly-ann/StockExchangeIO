package org.kelly_ann;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class ExcelWriter {
	
	// this class will write out to an MS Excel spreadsheet using the Apache library called POI
	// we will add POI's jar files into the project in the "libs" directory 
	// then add them to the classpath as "External Archives" so the JVM knows they should be used.
	
	private OneDayMarketAction odma;
	
	// this constructor will take in an obj of the day's market action so that it can use it's info
	public ExcelWriter(OneDayMarketAction odma) {
		this.odma = odma;
	}
	
	public void createFile(String excelFileName){
		
		// 1. get the list of tickers that moved the most
		List<OneDayMarketAction.OneTickerOneDay> otod = odma.getSortedMovers();
		
		// 2. create a data structure with the info that we wish to write to excel
		
		// this is a bit complicated
		// 2.a create an excel workbook data structure in Poi
		HSSFWorkbook workbook = new HSSFWorkbook();
		
		// 2.b create an excel worksheet in this workbook
		HSSFSheet worksheet = workbook.createSheet("Summary");
		
		// 2.c get the data that we wish to write in the form of rows
		// Poi need this to be setup a map with strings-object[] key-value pairs
		// in this map: key=row#, val=object array of values
		// note: each key represents a different row in the excel spreadsheet
		Map<String, Object[]> dataInRows = new HashMap<>();
		
		// write the header row
		// note: excel index starts from 1 not 0.
		dataInRows.put("1", new Object[] {"Ticker", "Close", "Prev Close", "%Change"});
		
		
		for(int i = 2; i < 7; i++) {
			// write the detail rows
			dataInRows.put(String.valueOf(i), new Object[] {otod.get(i-2).getTicker(), 
																otod.get(i-2).getClose(),
																otod.get(i-2).getPrevClose(),
																otod.get(i-2).getPercentageChange()});
		}
		
		// 2.d write the values to the cells of the excel worksheet via Poi
		// also a bit complicated
		
		// 2.d i. get the rows 1-by-1
		for(int rowNum = 0; rowNum < 4; rowNum++){
			String key = (rowNum + 1) + ""; // trick to convert the row number from an int to a String
			
			// 2.d ii. for each row create a Poi obj
			Row row = worksheet.createRow(rowNum + 1);
			Object[] values = dataInRows.get(key); // returns the Object[] associated with the key in the dataInRows map
			
			// 2.d iii. for each cell in each row create a Poi cell obj from the Poi row obj
			int cellNum = 0;
			for(Object oneObject : values){
				Cell cell = row.createCell(cellNum++);
				
				// this casts the value of the cell depending on what type of value is passed in
				// note: we ask the objects "if" they are of a particular type because if we attempt to downcast from
				// and Object to a specific type (i.e. String, Double, Date, Boolean, etc.) and it is not that type
				// we will get a ClassCastException error thrown
				if(oneObject instanceof String){
					cell.setCellValue((String) oneObject);
				} 
				else if(oneObject instanceof Double) {
					cell.setCellValue((Double) oneObject);
				} 
				else if(oneObject instanceof Date) {
					cell.setCellValue((Date) oneObject);
				} 
				else if(oneObject instanceof Boolean) {
						cell.setCellValue((Boolean) oneObject);
				}
			}
			
			// 3. use POI to actually write this data to Excel
			// this is relatively straightforward boilerplate file-handling stuff
			try {
				// create an output stream to to the file name passed in by the caller
				FileOutputStream fos = new FileOutputStream(new File(excelFileName));
				//write to the workbook from the program
				workbook.write(fos);
				// close output streams
				workbook.close();
				fos.close();
			}
			catch (FileNotFoundException e){
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		// print confirmation
		System.out.println("Excel written successfully");
				
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
