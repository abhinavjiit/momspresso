package com.kelltontech.network;

import org.apache.http.Header;

/**
 * Storage class to pass data from {@link HttpClientConnection} to children of {@link BaseDataHandler}
 */
public class Response {
	/**
	 * Constant that is set in {@link ServiceRequest#setDataType(int)}
	 */
	private int dataType;
	/**
	 * requestData that is set in {@link ServiceRequest#setRequestData(Object)}
	 */
	private Object requestData;
	/**
	 * true if {@link HttpClientConnection} has successfully fetched the requested data
	 */
	private boolean isSuccess;
	/**
	 * response in form of bytes
	 */
	private byte[] responseData;
	/**
	 * response in form of object (Usually after parsing)
	 */
	private Object responseObject;
	/**
	 * response headers
	 */
	private int httpResponseCode;
	/**
	 * response headers
	 */
	private Header[] httpHeaders;
	/**
	 * exception, if occurred while fetching the response
	 */
	private Exception exception;
	/**
	 * errorMessage, either received from web-service or any other
	 */
	private String errorMessage;
	
	/**
	 * @return the dataType
	 */
	public int getDataType() {
		return dataType;
	}
	/**
	 * @return the requestData
	 */
	public Object getRequestData() {
		return requestData;
	}
	/**
	 * @param requestData the requestData to set
	 */
	public void setRequestData(Object requestData) {
		this.requestData = requestData;
	}
	/**
	 * @param dataType the dataType to set
	 */
	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
	/**
	 * @return the isSuccess
	 */
	public boolean isSuccess() {
		return isSuccess;
	}
	/**
	 * @param isSuccess the isSuccess to set
	 */
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	/**
	 * @return the responseData
	 */
	public byte[] getResponseData() {
		return responseData;
	}
	/**
	 * @param responseData the responseData to set
	 */
	public void setResponseData(byte[] responseData) {
		this.responseData = responseData;
	}
	/**
	 * @return the responseObject
	 */
	public Object getResponseObject() {
		return responseObject;
	}
	/**
	 * @param responseObject the responseObject to set
	 */
	public void setResponseObject(Object responseObject) {
		this.responseObject = responseObject;
	}
	/**
	 * @return the httpResponseCode
	 */
	public int getHttpResponseCode() {
		return httpResponseCode;
	}
	/**
	 * @param httpResponseCode the httpResponseCode to set
	 */
	public void setHttpResponseCode(int httpResponseCode) {
		this.httpResponseCode = httpResponseCode;
	}
	/**
	 * @return the httpHeaders
	 */
	public Header[] getHttpHeaders() {
		return httpHeaders;
	}
	/**
	 * @param httpHeaders the httpHeaders to set
	 */
	public void setHttpHeaders(Header[] httpHeaders) {
		this.httpHeaders = httpHeaders;
	}
	/**
	 * @return the exception
	 */
	public Exception getException() {
		return exception;
	}
	/**
	 * @param exception the exception to set
	 */
	public void setException(Exception exception) {
		this.exception = exception;
	}
	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}

