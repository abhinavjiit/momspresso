package com.kelltontech.utils.facebook.listener;


import java.util.List;

import com.kelltontech.utils.facebook.model.FriendList;

/**
 * Get user Friend List Listener
 * 
 * @author monish.agarwal
 *
 */
public interface FacebookFriendListListener 
{
	public void doAfterFriendList(List<FriendList> friendLists);
}
