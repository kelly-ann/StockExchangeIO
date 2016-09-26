package org.kelly_ann;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {
	
	// http://real-chart.finance.yahoo.com/table.csv?s=NLSN&a=00&b=1&c=2015&d=11&e=29&f=2015&g=m&ignore=.csv
	public static void main(String[] args) {
		try {
			
			System.out.println("Hi " + System.getProperty("user.name"));
			
		// download and unzip a remote file
			String urlString = "http://www.nseindia.com/content/historical/EQUITIES/2015/JUL/cm17JUL2015bhav.csv.zip";
			String zipFilePath = "C:/Nerdiness/Java/ATempDir/foo.zip";
			String destinationDirectory = "C:/Desktop/Nerdiness/Java/ATempDir";
			
			// return the list of unzipped files
			List<String> unzippedFileList = UnzipUtility.downloadAndUnzip(urlString, zipFilePath, destinationDirectory);
			
			// ensure the list has entries
			if(unzippedFileList != null){
				String csvFile= unzippedFileList.get(0);
				
				// instantiate and object of the OneDayMarketAction class with the indiv ticker info in the csv file
				OneDayMarketAction odma = new OneDayMarketAction(csvFile);
				
				// sort the indiv tickers
				List<OneDayMarketAction.OneTickerOneDay> listOfMovers = odma.getSortedMovers();
				//Collections.reverse(listOfMovers);  //reverses the order of the list's elements
				
				//print the results of the sorting
				// the smallest %'s are at the top and the largest are at the bottom
				for (OneDayMarketAction.OneTickerOneDay otod : listOfMovers) {
					System.out.println("Ticker=" + otod.getTicker() + ", Moved by " + otod.getPercentageChange() * 100+"%");
				}
				
				ExcelWriter xlWriter = new ExcelWriter(odma);
				xlWriter.createFile(System.getProperty("user.home") + File.separator + "Desktop/Nerdiness/Java/ATempDir/TestStockQuotesExcel.xls");
				
			}
			
			System.out.println("All done!");
			
			
		// zip a local file
			String zipFileCreated = UnzipUtility.zip( (System.getProperty("user.home") + File.separator 
														+ "Desktop/Nerdiness/Java/ATempDir/nlsnStockQuotes.csv"), 
					System.getProperty("user.home") + File.separator + "Desktop/Nerdiness/Java/ATempDir" );
			System.out.println("\n\nNewly created zip file: " + zipFileCreated);
			
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		} 
		
	}
	
}
