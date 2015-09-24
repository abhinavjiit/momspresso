package com.kelltontech.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileUtils {
	
	public FileUtils() {
		
	}
	
	/**
	 * This function upload the large file to server with other POST values.
	 * @param filename
	 * @param targetUrl
	 * @return
	 */
	public static String uploadFileToServer(String filename, String targetUrl) {
	    String response = "error";
	    HttpURLConnection connection = null;
	    DataOutputStream outputStream = null;
 
	    String pathToOurFile = filename;
	    String urlServer = targetUrl;
	    String lineEnd = "\r\n";
	    String twoHyphens = "--";
	    String boundary = "*****";
 
	    int bytesRead, bytesAvailable, bufferSize;
	    byte[] buffer;
	    int maxBufferSize = 1 * 1024;
	    try {
	        FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile));
	        URL url = new URL(urlServer);
	        connection = (HttpURLConnection) url.openConnection();
 
	        // Allow Inputs & Outputs
	        connection.setDoInput(true);
	        connection.setDoOutput(true);
	        connection.setUseCaches(false);
	        connection.setChunkedStreamingMode(1024);
	        // Enable POST method
	        connection.setRequestMethod("POST");
 
	        connection.setRequestProperty("Connection", "Keep-Alive");
	        connection.setRequestProperty("Content-Type",
	                "multipart/form-data; boundary=" + boundary);
 
	        outputStream = new DataOutputStream(connection.getOutputStream());
	        outputStream.writeBytes(twoHyphens + boundary + lineEnd);
 
	        String token = "anyvalye";
	        outputStream.writeBytes("Content-Disposition: form-data; name=\"Token\"" + lineEnd);
	        outputStream.writeBytes("Content-Type: text/plain;charset=UTF-8" + lineEnd);
	        outputStream.writeBytes("Content-Length: " + token.length() + lineEnd);
	        outputStream.writeBytes(lineEnd);
	        outputStream.writeBytes(token + lineEnd);
	        outputStream.writeBytes(twoHyphens + boundary + lineEnd);
 
	        String taskId = "anyvalue";
	        outputStream.writeBytes("Content-Disposition: form-data; name=\"TaskID\"" + lineEnd);
	        outputStream.writeBytes("Content-Type: text/plain;charset=UTF-8" + lineEnd);
	        outputStream.writeBytes("Content-Length: " + taskId.length() + lineEnd);
	        outputStream.writeBytes(lineEnd);
	        outputStream.writeBytes(taskId + lineEnd);
	        outputStream.writeBytes(twoHyphens + boundary + lineEnd);
 
	        String connstr = null;
	        connstr = "Content-Disposition: form-data; name=\"UploadFile\";filename=\""
	                + pathToOurFile + "\"" + lineEnd;
 
	        outputStream.writeBytes(connstr);
	        outputStream.writeBytes(lineEnd);
 
	      	bytesAvailable = fileInputStream.available();
	        bufferSize = Math.min(bytesAvailable, maxBufferSize);
	        buffer = new byte[bufferSize];
 
	        // Read file
	        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	        System.out.println("Image length " + bytesAvailable + "");
	        try {
	            while (bytesRead > 0) {
	                try {
	                    outputStream.write(buffer, 0, bufferSize);
	                } catch (OutOfMemoryError e) {
	                    e.printStackTrace();
	                    response = "outofmemoryerror";
	                    return response;
	                }
	                bytesAvailable = fileInputStream.available();
	                bufferSize = Math.min(bytesAvailable, maxBufferSize);
	                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            response = "error";
	            return response;
	        }
	        outputStream.writeBytes(lineEnd);
	        outputStream.writeBytes(twoHyphens + boundary + twoHyphens
	                + lineEnd);
 
	        // Responses from the server (code and message)
	        int serverResponseCode = connection.getResponseCode();
	        String serverResponseMessage = connection.getResponseMessage();
	        System.out.println("Server Response Code " + " " + serverResponseCode);
	        System.out.println("Server Response Message "+ serverResponseMessage);
 
	        if (serverResponseCode == 200) {
	            response = "true";
	        }else
	        {
	        	response = "false";
	        }
 
	        fileInputStream.close();
	        outputStream.flush();
 
	        connection.getInputStream();
	        //for android InputStream is = connection.getInputStream();
	        java.io.InputStream is = connection.getInputStream();
 
			int ch;
			StringBuffer b = new StringBuffer();
			while( ( ch = is.read() ) != -1 ){
				b.append( (char)ch );
			}
 
			String responseString = b.toString();
			System.out.println("response string is" + responseString); //Here is the actual output
 
	        outputStream.close();
	        outputStream = null;
 
	    } catch (Exception ex) {
	        // Exception handling
	        response = "error";
	        System.out.println("Send file Exception" + ex.getMessage() + "");
	        ex.printStackTrace();
	    }
	    return response;
	}
}