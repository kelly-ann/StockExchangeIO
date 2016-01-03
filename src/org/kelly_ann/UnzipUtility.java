package org.kelly_ann;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by Kelly-Ann on 12/26/15
 */

public class UnzipUtility {
	// this class is used to abstract 2 operations from the rest of the code:
	// 1. downloading the file from the stock exchange
	// 2. unzipping the downloaded file to some location on our machine
	
	// we first figured out which functions we would need then the inputs and outputs that those resp. functions would need
	
	// Note: all these member functions are static since they don't need object to be instantiated b/c the code within these functions is not object specific
	
	private static final int BUFFER_SIZE = 4096;
	
	
	// this will take in the url of a zip file to download, a local dir to download it to, and the destination to unzip it to
	// it will output  a list of the unzipped files
	public static List<String> downloadAndUnzip(String urlString, String zipFilePath, String destDirectory) throws IOException {
		
		@SuppressWarnings("unused")
		List<String> unzippedFileList = new ArrayList<>();
		
		// create URL object for pointing to the zip file/resource on the internet
		URL zipFileUrl = new URL(urlString);
		
		// note: we need the user agent so that the NSE (india's stock exchange) will allow the file to be downloaded
		// why? it will identify the traffic we send as coming from a browser (i.e. via human input)
		// got this from: www.whatsmyuseragent.com (can also Google it)
		String myUserAgentString = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36";
		
		// create an object to represent the url connection then open the connection (i.e. connect to the link)
		URLConnection c = zipFileUrl.openConnection();
		
		// set the request's "User-Agent" property
		// this will make it so that the website will think that the request is coming from a browser
		c.setRequestProperty("User-Agent", myUserAgentString);
		
		// more standard boiler-plate code coming up
		// this will download the zip file from the url to the file path specified on the local computer 
		// note:  a channel from java.nio.channels is an object to represent the connection to entities that 
		// are capable of doing i/o operations (e.g. files & sockets).
		
		//create a channel to the zip file online
		ReadableByteChannel zipByteChannel = Channels.newChannel(c.getInputStream());
		
		// create an output stream to write the zip file to
		FileOutputStream fos = new FileOutputStream(zipFilePath);
		
		//get the channel associated with this file output stream and transfer the bytes from 
		// the zip's byte channel (online) into the file channel (locally).
		fos.getChannel().transferFrom(zipByteChannel, 0, Long.MAX_VALUE);
		fos.close();
		
		// now do the actual unzipping of the file and return the list of unzipped files
		return unzip(zipFilePath, destDirectory);
		
	}
	
	// this will take the name of the zip file and the destination directory to extract it to and 
	// return a list of the files that were unzipped.
	public static List<String> unzip(String zipFilePath, String destDirectory) throws IOException {
		
		// this will keep track of the indiv. files that have been unzipped from the zip file
		List<String> unzippedFileList = new ArrayList<>();
		
		// create a low-level file object corresponding to the destination directory
		File destDir = new File(destDirectory);
		
		//create directory if it doesn't exist yet
		if(!destDir.exists()){
			destDir.mkdir();
		}
		
		// now we can use the method we just wrote below (i.e. extractFile())
		// we will iterate over the individual files in the zip file and extract each one using the extractFile() method 
		
		// need to be able to read from a zip file
		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
		
		// object corresponding to a specific file in the zip archive
		// note: this will return both files and directories
		ZipEntry zipEntry = zipIn.getNextEntry();
		
		while(zipEntry != null) {
			String filePath = destDirectory + File.separator + zipEntry.getName(); //File.seperator is a static variable that is OS-specific
			//System.out.println(filePath); // this will show what each entry is
			
			// if we have a file and not a dir, extract it and add it name to the unzipped file list
			if(!zipEntry.isDirectory()) {
				String oneUnzippedFile = extractFile(zipIn, filePath);
				unzippedFileList.add(oneUnzippedFile);
			}
			// if it is a directory, create the dir and drilling down into the file path 
			else {
				File dir = new File(filePath);
				dir.mkdir();
			}
			
			// move this iterator to point to the next value, or else we will have an infinite loop condition
			zipEntry = zipIn.getNextEntry();
		}
		
		zipIn.close();
		
		return unzippedFileList;
		
	}
	
	
	// this method will take in a zip input stream for a zip file and a file path to extract the files in the zip file to
	// note this is private because it is an "implementation detail" of the unzip() method so the outside code need not know about it
	private static String extractFile(ZipInputStream zipIn, String filePath) throws IOException {
		
		// standard boiler-plate code
		// create a new buffered output stream from the file output steam of the specified file path
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
		
		// binary files need to be created in chunks of bytes.  
		// this is a constant so we create a final variable to hold this value for easy changes if needed.
		byte[] bytesIn = new byte[BUFFER_SIZE];
		
		// read the zip file and write the file to the destination path
		int read = 0;
		while((read=zipIn.read(bytesIn)) != -1) {
			
			// write to the buffered output stream from the byte[] starting at index 0 for a length of "read" (i.e. the # of bytes read in).
			bos.write(bytesIn, 0, read);
			
		}
		bos.close();
		
		// if we get here it means that we have successfully unzipped the file
		
		// return the path of the unzipped file that we just wrote for the calling method to keep track of
		return filePath;
		
	}
	
	
	// adding this as my own K-A personal understanding test method
	// steps to zip a file are: read in with a input stream, add file name to a ZipEntry and output it to a ZipOutputStream
	public static String zip(String unzippedFileLocation, String zipFileDest) throws IOException {
		
		// object to represent the unzipped file
		File unzippedFile = new File(unzippedFileLocation);
		
		// this needs the "/" before it for the file name to work later
		String filename = unzippedFileLocation.substring(unzippedFileLocation.lastIndexOf("/") + 1, unzippedFileLocation.lastIndexOf("."));
		
		// object to represent the zipped file's destination directory
		File destDir = new File(zipFileDest);
				
		// if the destination directory doesn't exist, create it
		if(!destDir.exists()) {
			destDir.mkdir();
		}
		
		// create a new file with the .zip extension
		File zippedFile = new File(destDir + File.separator + filename + ".zip");
		zippedFile.createNewFile();
		
		//add the file into a ZipEntry obj
		// note: if you want to recreate the entire path of the file w/i 
		//the zip file you can use unzippedFile.getAbsolutePath() instead.
		ZipEntry zipEntry = new ZipEntry(unzippedFile.getName());
		
		// note: the zip outputstream will overwrite a file if it is there already.
		// so do all the stream api's in java.
		ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zippedFile)));
		//System.out.println("After zos created: " + zippedFile.getAbsolutePath());
		
		// add the zip entry into the zip output stream
		zos.putNextEntry(zipEntry);
		
		// create an input stream to read in the contents of the unzippedFile
		FileInputStream unzippedFileIn = new FileInputStream(unzippedFile);
		
		// create a buffer of bytes
		byte[] inBuffer = new byte[BUFFER_SIZE];
		
		int read = 0;
		while((read = unzippedFileIn.read(inBuffer)) != -1) {
			zos.write(inBuffer, 0, read);
		}
		// close streams
		unzippedFileIn.close();
		zos.closeEntry();
		zos.close();
		
		return zippedFile.getAbsolutePath();
		
	}
}
