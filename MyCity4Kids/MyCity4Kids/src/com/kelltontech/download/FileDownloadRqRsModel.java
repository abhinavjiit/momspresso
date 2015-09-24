package com.kelltontech.download;

import com.kelltontech.model.BaseModel;

/**
 * Simple storage class to download any file by its URL
 */
public class FileDownloadRqRsModel extends BaseModel {

	/**
	 * URL of file to be downloaded
	 */
	private String	fileUrl;

	/**
	 * fileData of downloaded file
	 */
	private byte[]	fileData;

	/**
	 * Access time of downloaded file
	 */
	private String	fileAccessTime;

	/**
	 * Total reference count of downloaded file
	 */
	private int		fileReferenceCount;

	/**
	 * requester, object for which file download is required
	 */
	private Object	requester;

	/**
	 * @param fileUrl
	 */
	public FileDownloadRqRsModel(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	
	/**
	 * @param fileUrl
	 */
	public FileDownloadRqRsModel(String fileUrl, Object requester) {
		this.fileUrl = fileUrl;
		this.requester = requester;
	}

	/**
	 * @return the fileUrl
	 */
	public String getFileUrl() {
		return fileUrl;
	}

	/**
	 * @param fileUrl
	 *            the fileUrl to set
	 */
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	/**
	 * @return the fileData
	 */
	public byte[] getFileData() {
		return fileData;
	}

	/**
	 * @param fileData
	 *            the fileData to set
	 */
	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}

	/**
	 * @return the access time
	 */
	public String getAccessTime() {
		return fileAccessTime;
	}

	/**
	 * @param sets
	 *            the URL
	 */
	public void setAccessTime(String access_time) {
		this.fileAccessTime = access_time;
	}

	/**
	 * @return the access time
	 */
	public int getReferenceCount() {
		return fileReferenceCount;
	}

	/**
	 * @param sets
	 *            the URL
	 */
	public void setReferenceCount(int ref_count) {
		this.fileReferenceCount = ref_count;
	}

	/**
	 * To be used by view as passed by view , while creating request
	 * 
	 * @return the requester
	 */
	public Object getRequester() {
		return requester;
	}

	/**
	 * @param requester
	 *            to set requester
	 */
	public void setRequester(Object requester) {
		this.requester = requester;
	}
}
