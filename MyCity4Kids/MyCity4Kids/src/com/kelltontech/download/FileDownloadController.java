/*package com.kelltontech.download;

import java.util.ArrayList;

import android.app.Activity;
import android.util.Log;

import com.kelltontech.controller.BaseController;
import com.kelltontech.network.HttpClientConnection;
import com.kelltontech.network.HttpClientConnection.StatusCodeChecker;
import com.kelltontech.network.Response;
import com.kelltontech.network.ServiceRequest;
import com.kelltontech.ui.IScreen;
import com.kelltontech.utils.StringUtils;

*//**
 * Controller implementation to download files in a queue
 * 
 * @author sachin.gupta
 *//*
public class FileDownloadController extends BaseController {

	private static final String					LOG_TAG			= "FileDownloadController";

	private static final String					MSG_QUEUE_EMPTY	= "File Download Requests Queue is empty now.";

	*//**
	 * Default status code checker to ensure use of status code 200 for download
	 * requests
	 *//*
	private StatusCodeChecker					mStatusCodeChecker;

	private ArrayList<FileDownloadRqRsModel>	mFileDownloadRequestsQueue;

	private FileCacheTable						mFileCacheTable;

	*//**
	 * @param activity
	 * @param pScreen
	 *//*
	public FileDownloadController(Activity activity, IScreen pScreen) {
		super(activity, pScreen);

		mFileDownloadRequestsQueue = new ArrayList<FileDownloadRqRsModel>();


		mStatusCodeChecker = new StatusCodeChecker() {
			@Override
			public boolean isSuccess(int statusCode) {
				return statusCode == 200;
			}
		};
	}

	@Override
	public ServiceRequest getData(Object requestData) {

		FileDownloadRqRsModel newRequestModel = null;
		String fileUrl = null;

		if (requestData instanceof FileDownloadRqRsModel) {
			newRequestModel = (FileDownloadRqRsModel) requestData;
			fileUrl = newRequestModel.getFileUrl();

			FileDownloadRqRsModel cachedModel = mFileCacheTable.getFile(fileUrl);

			// in case image does exist in database call back will be return to
			// screen
			if (cachedModel != null) {
				Response response = new Response();
				response.setSuccess(true);
				newRequestModel.setFileData(cachedModel.getFileData());
				response.setRequestData(newRequestModel);
				response.setResponseObject(newRequestModel);
				response.setResponseData(cachedModel.getFileData());
				sendResponseToScreen(response);
				return null;
			}
		}

		// else a call back will be registered to UI Screen after downloading &
		// saving image to database
		if (StringUtils.isNullOrEmpty(fileUrl)) {
			Log.d(LOG_TAG, "File URL is null or blank.");
			return null;
		} else {
			Log.d(LOG_TAG, "Requested URL:" + fileUrl);
			fileUrl = StringUtils.getFormattedURL(fileUrl);
		}

		if (!isSameRequestExists(newRequestModel)) {

			mFileDownloadRequestsQueue.add(newRequestModel);

			ServiceRequest serviceRq = new ServiceRequest();
			serviceRq.setUrl(fileUrl);
			serviceRq.setRequestData(requestData);
			serviceRq.setResponseController(this);
			serviceRq.setPriority(HttpClientConnection.PRIORITY.LOW);
			serviceRq.setHttpMethod(HttpClientConnection.HTTP_METHOD.GET);
			serviceRq.setStatusCodeChecker(mStatusCodeChecker);

			HttpClientConnection.getInstance().addRequest(serviceRq);
			return serviceRq;
		}
		return null;
	}

	*//**
	 * @param newRequest
	 * @return
	 *//*
	private boolean isSameRequestExists(FileDownloadRqRsModel newRequest) {
		for (FileDownloadRqRsModel existingRequest : mFileDownloadRequestsQueue ) {
			if (existingRequest.getFileUrl().equals(newRequest.getFileUrl())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void handleResponse(Response response) {
		FileDownloadRqRsModel fileDownloadRqRsModel = mFileDownloadRequestsQueue.remove(0);

		if (response.getResponseData() == null || response.getResponseData().length == 0) {
			response.setSuccess(false);
		} else {
			if (response.getRequestData() instanceof FileDownloadRqRsModel) {
				FileDownloadRqRsModel fileModel = (FileDownloadRqRsModel) response.getRequestData();
				if (fileModel != null) {
					fileModel.setFileData(response.getResponseData());
					mFileCacheTable.addFile(fileModel);
				}
			}
			fileDownloadRqRsModel.setFileData(response.getResponseData());
		}

		response.setResponseObject(fileDownloadRqRsModel);
		sendResponseToScreen(response);

		*//**
		 * To signal the requesting screen that all requests are done.
		 *//*
		if (mFileDownloadRequestsQueue.isEmpty()) {
			Log.d(LOG_TAG, MSG_QUEUE_EMPTY);
			Response queueEmptySignalResponse = new Response();
			queueEmptySignalResponse.setResponseObject(MSG_QUEUE_EMPTY);
			queueEmptySignalResponse.setSuccess(true);
			sendResponseToScreen(queueEmptySignalResponse);
		}
	}

	@Override
	public void parseResponse(Response response) {
		// Nothing to do here
	}
}
*/