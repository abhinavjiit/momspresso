package com.mycity4kids.ui.adapter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.google.gson.Gson;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.autosuggest.AutoSuggestModelData;
import com.mycity4kids.models.autosuggest.AutoSuggestReviewResponse;
import com.mycity4kids.preference.SharedPrefUtils;

public class SuggestionAdapter extends ArrayAdapter<AutoSuggestModelData> implements Filterable {
		
	int categoryId;
	Context context;

	public SuggestionAdapter(Context context, int textViewResourceId,
			int categoryId) {
		super(context, textViewResourceId);
		this.context = context;
		this.categoryId = categoryId;
	}

	private ArrayList<AutoSuggestModelData> resultList;

	@Override
	public int getCount() {
		return resultList.size();
	}

	@Override
	public AutoSuggestModelData getItem(int index) {
		return resultList.get(index);
	}

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				if (constraint != null) {
					// Retrieve the autocomplete results.
					resultList = autocomplete(constraint.toString());
					if(resultList!=null){
						filterResults.values = resultList;
						filterResults.count = resultList.size();	
					}
					
				}
				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				if (results != null && results.count > 0) {
					
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}
		};
		return filter;
	}

	private ArrayList<AutoSuggestModelData> autocomplete(String input) {
		
		//((WriteReviewActivity) context).hideSoftKeyboard();
		
		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(AppConstants.WRITE_A_REVIEW_AUTO_SUGGEST_URL);
					

			sb.append(getAppendUrlForReview(input + "," + categoryId));

			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			return null;
		} catch (IOException e) {
			return null;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		AutoSuggestReviewResponse _autoSuggest = new Gson().fromJson(jsonResults.toString(), AutoSuggestReviewResponse.class);
				

		return _autoSuggest.getResult().getData().getSuggest();

	}

	private String getAppendUrlForReview(String queryString) {
		String[] params = queryString.split(",");
		String query = params[0];
		String categoryId = params[1];

		int cityId = SharedPrefUtils.getCurrentCityModel(context).getId();
		StringBuilder builder = new StringBuilder();

		if (!StringUtils.isNullOrEmpty(query)) {
			builder.append("query=").append(query);
		}

		builder.append("&city=").append(cityId);
		if (!StringUtils.isNullOrEmpty(categoryId)) {
			builder.append("&categoryId=").append(categoryId);
		}

		return builder.toString().replace(" ", "%20");

	}
}
