package com.kelltontech.utils.facebook.listener;

import com.kelltontech.utils.facebook.model.UserInfo;

/**
 * Login Facebook Listener
 * 
 * @author monish.agarwal
 *
 */
public interface FacebookLoginListener {

	public void doAfterLogin(UserInfo userInfo);

}
