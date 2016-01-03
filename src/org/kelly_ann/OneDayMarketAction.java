package org.kelly_ann;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// this is the outer class
public class OneDayMarketAction {
	// this will represent the entire market action for 1 given day
	// the inner class will be a static nested class which represents one stock (out of many) on a given day.
	
	// this is the static nested INNER class
	public static class OneTickerOneDay {
		
		private String ticker;
		private String series;
		private double open;
		private double close;
		private double high;
		private double low;
		private double prevClose;
		
		public OneTickerOneDay(String[] oneQuote) {
			setTicker(oneQuote[0]);
			setSeries(oneQuote[1]);
			setOpen(Double.parseDouble(oneQuote[2]));
			setClose(Double.parseDouble(oneQuote[3]));
			setHigh(Double.parseDouble(oneQuote[4]));
			setLow(Double.parseDouble(oneQuote[5]));
			setPrevClose(Double.parseDouble(oneQuote[6]));
			
		}
		
		public double getPercentageChange() {
			if(this.getPrevClose() != 0) {
				return(this.getClose() - this.getPrevClose()) / this.getPrevClose();
			}
			return Double.NaN;
		}

		
		public String getTicker() {
			
			return ticker;
		}

		
		public void setTicker(String ticker) {
			
			this.ticker = ticker;
		}

		
		public String getSeries() {
			
			return series;
		}

		
		public void setSeries(String series) {
			
			this.series = series;
		}

		
		public double getOpen() {
			
			return open;
		}

		
		public void setOpen(double open) {
			
			this.open = open;
		}

		
		public double getClose() {
			
			return close;
		}

		
		public void setClose(double close) {
			
			this.close = close;
		}

		
		public double getHigh() {
			
			return high;
		}

		
		public void setHigh(double high) {
			
			this.high = high;
		}

		
		public double getLow() {
			
			return low;
		}

		
		public void setLow(double low) {
			
			this.low = low;
		}

		
		public double getPrevClose() {
			
			return prevClose;
		}

		
		public void setPrevClose(double prevClose) {
			
			this.prevClose = prevClose;
		}
		
		
	}
	
	
	
	//internally we want to be able to say 'please give me the market action today for ticker=XYZ'
	// the map will enable us to do this via it's key-value pair
	// this variable is used in the constructor and the getSortedMovers() methods
	private Map<String, OneTickerOneDay> mapOfTickers = new HashMap<>();
	
	// the CSV file name that this action came from
	@SuppressWarnings("unused")
	private String fileName;
	
	// this is the constructor for the OnDayMarketAction outer class
	// it sets up the tickers by reading in from a CSV file
	public OneDayMarketAction(String csvFile) {
		// this constructor will:
		// 1. parse the csv file
		// 2. go thru the file line-by-line and create object of the OneTickerOneDay
		// 3. populate the internal map "mapOfTickers"
		
		// 1. parse the csv file
		this.fileName = csvFile;
		BufferedReader br = null;
		String line = null; // this is our iterator variable
		String csvSplitBy = ","; // the delimiter for the CSV file
		int lineNum = 0;
		
		try{
			//create a file handle to be able to read the CSV file
			br = new BufferedReader(new FileReader(csvFile));
					
			//read the entire file from beginning to end
			while ((line = br.readLine()) != null) {
				lineNum++;
				// if the lineNum is 1, we know this is the header so we ignore it
				if(lineNum > 1){
					// split the row into an array of strings using the delimiter
					String[] oneQuote = line.split(csvSplitBy);
					
					// create an object to hold the activity of 1 single ticket on a given day
					// each row holds one tickers activity
					OneDayMarketAction.OneTickerOneDay otod = new OneDayMarketAction.OneTickerOneDay(oneQuote);
					
					//skip rows that are not the ticker of an equity
					// compare too return <0 if not equal and 0 if the strings match
					if(otod.getSeries().compareTo("EQ") == 0){
						
						//add equity object into the day's map
						mapOfTickers.put(oneQuote[0], otod);
					}
				}
				
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		finally{
			try {
				// closing a file can throw errors so we surround with a try/catch block
				br.close();
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
				
		
	}
	
	
	// method that returns a list of sorted ticker objects
	// it uses the StockMoveComparator class below to do so
	public List<OneTickerOneDay> getSortedMovers(){
		
		//create unsorted list
		// to do so: take the map and get all the vals of the mapOfTickers (which are objects) and put them into an ArrayList
		List<OneTickerOneDay> listOfMarketAction = new ArrayList<>(this.mapOfTickers.values());
		
		//sort the list (param 1) using a specified comparator (param 2)
		Collections.sort(listOfMarketAction, new StockMoveComparator());
		
		// return the sorted list to the calling program
		return listOfMarketAction;
		
	}
	
	
	
	// this is a static nested INNER class that compares indiv tickers
	// compare() returns: 1 if obj1 > obj2, returns 0 if obj1 equals obj2, returns -1 if obj1 < obj2.
	// note: >, equals, < are decided by the programmer and the logic is in the compare() method.
	// this implementation will use % change to compare tickers.
	// note: the Comparator uses a "template parameter" Compare<T> so it can compare many objs.
	public static class StockMoveComparator implements Comparator<OneTickerOneDay> {

		@Override
		public int compare(OneTickerOneDay o1, OneTickerOneDay o2) {
			
			double pctChange1 = o1.getPercentageChange();
			double pctChange2 = o2.getPercentageChange();
			
			if(pctChange1 > pctChange2) { // obj1 greater than
				return 1;
			}
			else if(pctChange1 < pctChange2){ // obj1 less than
				return -1;
			}
			else {	// obj1 equals obj2
				return 0;
			}
			
		}
		
	}
	
}
