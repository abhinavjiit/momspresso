package com.kelltontech.ui;

import com.kelltontech.network.Response;

/**
 * @author sachin.gupta
 */
public interface IScreen {
	/**
     * Subclass should over-ride this method to update the UI with response.
     * Caller of this method should be calling this method only from UI thread.
     * @param response
     */
	void handleUiUpdate( Response response );

}

