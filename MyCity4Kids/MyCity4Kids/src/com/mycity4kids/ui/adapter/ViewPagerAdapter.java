package com.mycity4kids.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mycity4kids.ui.fragment.SubCategoryFragment;

/**
 * View Pager Adapter to be used in search result 
 * @author kapil.vij
 *
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {


	public ViewPagerAdapter(FragmentManager fragment) {  
		super(fragment);  
	}  

	@Override  
	public Fragment getItem(int position) {  
		Fragment fragment = null;  
		switch (position) {  
		case 0:  
			fragment = new SubCategoryFragment();
			return fragment;
			
		/*case 1:  
			return fragment = new SubCategoryFragment();  
		case 2:  
			return fragment = new SubCategoryFragment();  
		case 3:
		{
			return fragment = new SubCategoryFragment();

		}*/
		default:  
			break;  
		}  
		return fragment; 
	}  

	@Override  
	public int getCount() {  
		//TODO: if came from search result size is 3 else 4.
		return 1;
	}  

	/*@Override  
	public CharSequence getPageTitle(int position) {  
		switch (position) {  
		case 0:  
			return getString(R.string.lbl_catalog);//"CATALOGS";  
			//   case 1:  
			//      return "MY HOME";  
		case 1:  
			return getString(R.string.lbl_my_wishlist);//"MY WISHLISTS"; 
		case 2:  
			return getString(R.string.lbl_my_follow);//"MY FOLLOWS"; 
		case 3:  
			return getString(R.string.lbl_product_i_love);//"PRODUCTS I LOVE"; 
		}  
		return null;  
	} */ 


}
