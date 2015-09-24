package com.mycity4kids.models.forgot;

public class AddAListingRequest {

	private String businessName;
	private String contactNumber;

	/**
	 * @return String businessName
	 */
	public String getBusinessName() {
		return businessName;
	}

	/**
	 * @param String
	 *            businessName the businessName to set
	 */
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	/**
	 * @return String contactNumber
	 */
	public String getContactNumber() {
		return contactNumber;
	}

	/**
	 * @param String
	 *            contactNumber the contactNumber to set
	 */
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

}
