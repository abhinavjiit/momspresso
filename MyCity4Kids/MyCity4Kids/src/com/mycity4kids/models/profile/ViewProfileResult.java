package com.mycity4kids.models.profile;

import com.mycity4kids.models.CommonMessage;


	public class ViewProfileResult extends CommonMessage{
		private ProfileModel data;

		/**
		 * @return the data
		 */
		public ProfileModel getData() {
			return data;
		}

		/**
		 * @param data the data to set
		 */
		public void setData(ProfileModel data) {
			this.data = data;
		}
	
}