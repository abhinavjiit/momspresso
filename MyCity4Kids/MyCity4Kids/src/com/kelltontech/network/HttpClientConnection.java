package com.kelltontech.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Comparator;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.util.Log;

import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.DataUtils;

/**
 * @author sachin.gupta
 */
public class HttpClientConnection extends Thread
{
	private final String LOG_TAG = "HttpClientConnection";
	
	private static HttpClientConnection instance;
	
	private HttpClientConnection() {
		defaultStatusCodeChecker = new StatusCodeChecker() {
			@Override
			public boolean isSuccess(int statusCode) {
				return statusCode == 200;
			}
		};;
	}
	
	public static HttpClientConnection getInstance() {
		if (instance == null) {
			instance = new HttpClientConnection();
			instance.execute();
		}
		return instance;
	}
	
	private StatusCodeChecker defaultStatusCodeChecker;
	private int defaultRequestTimeOut = 60000; // 60 sec timeout
	
	/**
	 * @param defaultStatusCodeChecker the defaultStatusCodeChecker to set
	 */
	public void setDefaultStatusCodeChecker(StatusCodeChecker defaultStatusCodeChecker) {
		this.defaultStatusCodeChecker = defaultStatusCodeChecker;
	}
	
	/**
	 * @param defaultRequestTimeOut the defaultRequestTimeOut in miliseconds to set
	 */
	public void setDefaultRequestTimeOut(int defaultRequestTimeOut) {
		this.defaultRequestTimeOut = defaultRequestTimeOut;
	}

	private boolean isRunning;
	private Vector<ServiceRequest> highPriorityQueue;
	private Vector<ServiceRequest> lowPriorityQueue;
	
	public void execute() {
		highPriorityQueue = new Vector<ServiceRequest>();
		lowPriorityQueue = new Vector<ServiceRequest>();
		isRunning = true;
		start();
	}
	
	private ServiceRequest currentRequest;

	/**
	 * {@link ServiceRequest} with {@link PRIORITY#HIGH} are executed before {@link ServiceRequest} with {@link PRIORITY#LOW}
	 */
	public static interface PRIORITY
	{
		/**
		 * When-ever a new {@link ServiceRequest} with {@link PRIORITY#LOW} is added,
		 * it gets lower priority than previous requests with same priority.
		 */
		public static byte LOW = 0;
		/**
		 * When-ever a new {@link ServiceRequest} with {@link PRIORITY#HIGH} is added,
		 * it gets higher priority than previous requests with same priority.
		 */
		public static byte HIGH = 1;
	}

	public static interface HTTP_METHOD
	{
		public static byte GET 		= 0;
		public static byte POST 	= 1;
		public static byte PUT 		= 2;
		public static byte DELETE 	= 3;
	}

	/**
	 * Specific instance of StatusCodeChecker can be set in ServiceRequest
	 */
	public static interface StatusCodeChecker {
		boolean isSuccess( int statusCode );
	}
	
	@Override
	public void run() {
		while (isRunning) {
			if (nextRequest()) {
				executeRequest();
			} else {
				try {
					Thread.sleep(10 * 60 * 1000);// 10 min sleep
				} catch (InterruptedException e) {
					Log.i(LOG_TAG, "" + e);
				}
			}
		}
	}

	private boolean nextRequest() {
		if(highPriorityQueue.size() > 0  ){
			currentRequest = (ServiceRequest)highPriorityQueue.remove(0);
		}
		else if(lowPriorityQueue.size() > 0) {
			currentRequest = (ServiceRequest)lowPriorityQueue.remove(0);
		} else {
			currentRequest = null;
		}
		
		return currentRequest != null;
	}

	public void executeRequest() {
		
		/*if(highPriorityQueue.size()>1 || lowPriorityQueue.size()>1)
		{*/
		if( currentRequest.isCancelled() ) {
			return;
		}
		//}
		/**
		 * Check if device is connected.
		 */
		Activity activity = currentRequest.getResponseController().getActivity();
		if( activity != null && ! ConnectivityUtils.isNetworkEnabled(activity) ) {
			notifyError("Device is out of network coverage.", null);
			return;
		}
		
		HttpClient httpClient = new DefaultHttpClient();
	//	httpClient.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		int requestTimeOut = currentRequest.getRequestTimeOut();
		if( requestTimeOut <= 0 ) {
			requestTimeOut = defaultRequestTimeOut;
		} 
		if( requestTimeOut > 0 ) {
			HttpParams params = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, requestTimeOut);
			HttpConnectionParams.setSoTimeout(params, requestTimeOut);
		}

		HttpResponse httpResponse = null;

		Log.i( LOG_TAG, "Request URL: " + currentRequest.getUrl());

		try {
			HttpRequestBase getOrPost = null;
			
			switch (currentRequest.getHttpMethod()) {
			case HTTP_METHOD.POST:{
				getOrPost = new HttpPost();
				if(currentRequest.getPostData() != null) {
					
					((HttpPost)getOrPost).setEntity(currentRequest.getPostData() );
				}
				break;
			}
			case HTTP_METHOD.GET:{
				getOrPost = new HttpGet();
				break;
			}
			case HTTP_METHOD.PUT:{
				getOrPost = new HttpPut();
				if(currentRequest.getPostData() != null) {
					
					((HttpPut)getOrPost).setEntity(currentRequest.getPostData() );
					
				}
				break;
			}
			case HTTP_METHOD.DELETE:{
				getOrPost = new HttpDelete();
				break;
			}
			
			}
			
			getOrPost.setURI(new URI(currentRequest.getUrl()));
			
			String[] headerNames = currentRequest.getHeaderNames();
	    	if( headerNames != null ) {
	    		String[] headerValues = currentRequest.getHeaderValues();
	    		for( int i = 0; i < headerNames.length; i++ ) {
	    			getOrPost.addHeader(headerNames[i], headerValues[i]);
	    			Log.i( LOG_TAG, "Header: " + headerNames[i] + " = " + headerValues[i]);
	    		}
	    	}
	    	
			httpResponse = httpClient.execute(getOrPost);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			
			Log.i( LOG_TAG, "Response Received : " + statusCode );

			if( currentRequest.isCancelled() ) {
				return;
			}
			
			Response response = new Response();
			response.setDataType(currentRequest.getDataType());
			response.setRequestData(currentRequest.getRequestData());
			response.setHttpHeaders(httpResponse.getAllHeaders());
			response.setHttpResponseCode(statusCode);
			
			StatusCodeChecker statusCodeChecker = currentRequest.getStatusCodeChecker();
			if( statusCodeChecker == null ) {
				statusCodeChecker = defaultStatusCodeChecker;
			} 
			if( statusCodeChecker != null ) {
				response.setSuccess(statusCodeChecker.isSuccess(statusCode));
			}
			
			InputStream responseContentStream = httpResponse.getEntity() == null ? null : httpResponse.getEntity().getContent();
			
			if( responseContentStream != null ) {
				if(currentRequest.getIsCompressed()) {
					responseContentStream = new GZIPInputStream(responseContentStream);
				}
				if( currentRequest.isCancelled() ) {
					return;
				}
				response.setResponseData(DataUtils.convertStreamToBytes(responseContentStream));
			}
				
			if( currentRequest.isCancelled() ) {
				return;
			}
			currentRequest.getResponseController().handleResponse(response);
		}
		catch (URISyntaxException e1) {
			notifyError("Invalid URL.", e1);
		}
		catch (UnknownHostException e) {
			notifyError("Server not found.", e);
		}
		catch (SocketException esoc) {
			notifyError("Time out.", esoc );
		}
		catch (IOException e) {
			notifyError("There are IO error.", e );
		}
		catch (Exception e) {
			notifyError("There are some problem.", e );
		}
		finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	/**
	 * @param message
	 * @param exception
	 */
	private void notifyError( String errorMessage, Exception exception) {
		if( exception == null ) {
			Log.e( LOG_TAG, "Error Response: " + errorMessage );
		} else {
			Log.e( LOG_TAG, "Error Response: " + errorMessage, exception );
		}
		Response response = new Response();
		response.setRequestData(currentRequest.getRequestData());
		response.setDataType(currentRequest.getDataType());
		response.setErrorMessage(errorMessage);
		response.setSuccess(false);
		response.setException(exception);
		if( currentRequest.isCancelled() ) {
			return;
		}
		currentRequest.getResponseController().handleResponse(response);
	}

    public void addRequest (ServiceRequest request) {
    	try {
	    	if(request.getPriority() == PRIORITY.HIGH) {
	    		highPriorityQueue.add(0,request);
	    	} else {
	    		lowPriorityQueue.addElement(request);
	    	}
	    	interrupt();
	    } catch(Exception ex){ 
	    	Log.e( LOG_TAG, "addRequest()", ex ); 
	    }
    }

	/**
	 * @return the currentRequest
	 */
	public ServiceRequest getCurrentRequest() {
		return currentRequest;
	}
	
	/**
	 * @return the nextRequest
	 */
	public ServiceRequest getNextRequest() {
		if(highPriorityQueue.size() > 0){
			return highPriorityQueue.get(0);
		} else if(lowPriorityQueue.size() > 0) {
			return lowPriorityQueue.get(0);
		} else {
			return null;
		}
	}
	
	/**
	 * @return true if pRequest is found and removed from high/low queue.
	 */
	public boolean removeRequest(ServiceRequest pRequest, Comparator<ServiceRequest> pComparator) {
		ServiceRequest tempRq = null;
		Vector<ServiceRequest> targetQueue = lowPriorityQueue;
		if( pRequest.getPriority() == PRIORITY.HIGH ) {
			targetQueue = highPriorityQueue;
		}
		for( int i = 0; i < targetQueue.size(); i++ ) {
			try { tempRq = targetQueue.get(i); }
			catch(Exception e) { return false; }
			if( tempRq != null && pComparator.compare(tempRq, pRequest) == 0 ) {
				return targetQueue.removeElement(tempRq);
			}
		}
		return false;
	}
}
