package com.mycity4kids.googlemap.models;

import java.util.ArrayList;

public class Directions {
	
	private ArrayList<Routes> routes;
	private String status;
	/**
	 * @return the routes
	 */
	public ArrayList<Routes> getRoutes() {
		return routes;
	}
	/**
	 * @param routes the routes to set
	 */
	public void setRoutes(ArrayList<Routes> routes) {
		this.routes = routes;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

}
