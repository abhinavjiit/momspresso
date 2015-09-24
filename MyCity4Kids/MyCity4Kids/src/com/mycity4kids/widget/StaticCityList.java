package com.mycity4kids.widget;

import java.util.ArrayList;

import com.mycity4kids.models.city.MetroCity;

public class StaticCityList {
	public static ArrayList<MetroCity> _hardCodedCity=new ArrayList<MetroCity>();
	
	static
	{
		_hardCodedCity.add(new MetroCity(1, "Delhi-Ncr"));
		_hardCodedCity.add(new MetroCity(2, "Bangalore"));
		_hardCodedCity.add(new MetroCity(3, "Mumbai"));
		_hardCodedCity.add(new MetroCity(4, "Pune"));
		_hardCodedCity.add(new MetroCity(5, "Hyderabad"));
		_hardCodedCity.add(new MetroCity(6, "Chennai"));
		
	}

}
