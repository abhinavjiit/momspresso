package com.kelltontech.ui.widget;

import java.util.Vector;

import android.view.View;
import android.widget.LinearLayout;

/**
 * @author sachin.gupta
 * @param <T>
 */
public abstract class CustomAdapter<T> {
	private LinearLayout	mOwnerLinearLayout;
	private Vector<T>		mDataToBeShown;
	private final int		mNumHeaders;

	/**
	 * @param pOwnerLinearLayout
	 * @param pNumHeaders
	 * 
	 *            After creating AttendeeListAdapter, no Views should be added/removed
	 *            in pOwnerLinearLayout
	 */
	public CustomAdapter(LinearLayout pOwnerLinearLayout, final int pNumHeaders) {
		if (pOwnerLinearLayout == null || pNumHeaders < 0)
			throw new IllegalArgumentException();
		mOwnerLinearLayout = pOwnerLinearLayout;
		mNumHeaders = pNumHeaders;
		mDataToBeShown = new Vector<T>(); // to avoid null check in various
											// methods
	}

	/**
	 * receive data to be shown in view and show all data b/w numHeaders and
	 * footers*. If it is called more than once, it will remove all old data and
	 * views.
	 * 
	 * @param pValuesToBeShown
	 * 
	 *            Vector pValuesToBeShown will not remain connected to this
	 *            adapter after this method. AttendeeListAdapter will maintain its own
	 *            Vector for these values. To modify data in view, other methods
	 *            of AttendeeListAdapter class need to be called.
	 */
	public void setDataAndViews(Vector<T> pValuesToBeShown) {
		removeAllDataAndViews();
		if (pValuesToBeShown != null)
			mDataToBeShown = new Vector<T>(pValuesToBeShown.size());

		mDataToBeShown.addAll(pValuesToBeShown);
		int _size = mDataToBeShown.size();

		for (int _indexInData = 0; _indexInData < _size; _indexInData++) {
			mOwnerLinearLayout.addView(createViewForDataAt(_indexInData, null), mNumHeaders + _indexInData);
		}
	}

	/**
	 * @return vector with current data elements
	 */
	public Vector<T> getCurrentData() {
		Vector<T> _copyOfCurrentData = new Vector<T>(mDataToBeShown.size());
		_copyOfCurrentData.addAll(mDataToBeShown);
		return _copyOfCurrentData;
	}

	/**
	 * delete All data From Data And View
	 */
	public void removeAllDataAndViews() {
		mOwnerLinearLayout.removeViews(mNumHeaders, mDataToBeShown.size());
		mDataToBeShown.clear();
	}

	/**
	 * add child after all current children and before footers*.
	 * 
	 * @param _passenger
	 */
	public int addDataAndView(T pNewDataElement) {
		mDataToBeShown.add(pNewDataElement);
		int _indexInData = mDataToBeShown.indexOf(pNewDataElement);
		mOwnerLinearLayout.addView(createViewForDataAt(_indexInData, null), mNumHeaders + _indexInData);
		return _indexInData;
	}

	/**
	 * @param pExistingDataElement
	 * @param addAsNewIfNotFound
	 * 
	 * @return _indexInData, -1 if addAsNewIfNotFound is false and data is not
	 *         found
	 */
	public int updateDataAndView(T pExistingDataElement, boolean addAsNewIfNotFound) {
		int _indexInData = mDataToBeShown.indexOf(pExistingDataElement);

		if (_indexInData == -1 && addAsNewIfNotFound) {
			_indexInData = addDataAndView(pExistingDataElement);
		} else {
			View _existingView = mOwnerLinearLayout.getChildAt(mNumHeaders + _indexInData);
			createViewForDataAt(_indexInData, _existingView);
		}
		return _indexInData;
	}

	/**
	 * @param pExistingDataElement
	 * @param addAsNewIfNotFound
	 * 
	 * @return _indexInData, -1 if addAsNewIfNotFound is false and data is not
	 *         found
	 */
	public int setDataAndViewAt(int pIndexInData, T pExistingDataElement) {
		int _indexInData = mDataToBeShown.indexOf(pExistingDataElement);

		if (_indexInData == -1) {
			_indexInData = addDataAndView(pExistingDataElement);
		} else {
			View _existingView = mOwnerLinearLayout.getChildAt(mNumHeaders + _indexInData);
			createViewForDataAt(_indexInData, _existingView);
		}
		return _indexInData;
	}

	protected abstract View createViewForDataAt(int pIndexInData, View pExistingView);

	/**
	 * remove child from data and view.
	 * 
	 * @param indexInData
	 */
	public void removeDataAndViewAt(int indexInData) {
		mDataToBeShown.remove(indexInData);
		int _indexInLnr = mNumHeaders + indexInData;
		mOwnerLinearLayout.removeViewAt(_indexInLnr);
	}

	public T getDataAt(int pIndexInData) {
		return mDataToBeShown.elementAt(pIndexInData);
	}

	public View getViewAt(int pIndexInData) {
		return mOwnerLinearLayout.getChildAt(mNumHeaders + pIndexInData);
	}

	public int getCount() {
		return mDataToBeShown.size();
	}
}
