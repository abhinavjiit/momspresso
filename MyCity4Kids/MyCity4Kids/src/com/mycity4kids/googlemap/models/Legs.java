package com.mycity4kids.googlemap.models;

import java.util.ArrayList;

public class Legs {

	private ArrayList<Steps> steps;
	private String end_address;

	/**
	 * @return the end_address
	 */
	public String getEnd_address() {
		return end_address;
	}

	/**
	 * @param end_address the end_address to set
	 */
	public void setEnd_address(String end_address) {
		this.end_address = end_address;
	}

	/**
	 * @return the steps
	 */
	public ArrayList<Steps> getSteps() {
		return steps;
	}

	/**
	 * @param steps the steps to set
	 */
	public void setSteps(ArrayList<Steps> steps) {
		this.steps = steps;
	}
	
}
