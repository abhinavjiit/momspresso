package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.AutoSuggestController;
import com.mycity4kids.dbtable.CategoryListTable;
import com.mycity4kids.dbtable.LocalityTable;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.fragmentdialog.LoginFragmentDialog;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.autosuggest.AutoSuggestResponse;
import com.mycity4kids.models.category.CategoryModel;
import com.mycity4kids.models.category.GroupCategoryModel;
import com.mycity4kids.models.category.SubCategory;
import com.mycity4kids.models.logout.LogoutResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.AutoSuggestTransparentDialogActivity;
import com.mycity4kids.ui.activity.BusinessListActivityKidsResources;
import com.mycity4kids.ui.activity.TopPicksActivity;
import com.mycity4kids.ui.activity.WriteReviewActivity;
import com.mycity4kids.ui.adapter.CategoryListAdapter;
import com.mycity4kids.ui.adapter.SubLocalityAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class FragmentHomeCategory extends BaseFragment implements OnClickListener {
    private ExpandableListView mCategoryExpandList;
    private CategoryListTable mCategoryListTable;
    //	private Header header;
    private TextView mNoResultTxt;
    private ArrayList<GroupCategoryModel> groupCategoryList;
    private HashMap<GroupCategoryModel, ArrayList<CategoryModel>> categoryData;
    private ImageView mBusinessSearch;
    public LinearLayout mParentLout;
    private AutoSuggestController mAutoSuggestController;
    private ProgressBar mProgressBar;
    ListView mSearchList;
    EditText mQuerySearchEtxt;
    boolean isContainCommaQuery = false;
    private EditText mLocalitySearchEtxt;
    private LinearLayout mLocalitySearchLout;

    //
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.category_activity, null);
        Utils.pushOpenScreenEvent(getActivity(), "Kids Resources Dashboard", SharedPrefUtils.getUserDetailModel(getActivity()).getId() + "");

        try {
            mLocalitySearchEtxt = (EditText) view.findViewById(R.id.locality_search);

            // mLocalitySearchEtxt.setOnFocusChangeListener(FragmentHomeCategory.this);
            mLocalitySearchLout = (LinearLayout) view.findViewById(R.id.localityLayout);
            //	Log.i("isHiddern", String.valueOf(dialog.isHidden()));
            mNoResultTxt = (TextView) view.findViewById(R.id.no_result);
            mBusinessSearch = (ImageView) view.findViewById(R.id.business_search_img);
            mParentLout = (LinearLayout) view.findViewById(R.id.parent_laout);
            mSearchList = (ListView) view.findViewById(R.id.searchList);
            ((LinearLayout) view.findViewById(R.id.searchLout)).setOnClickListener(this);
            mQuerySearchEtxt = (EditText) view.findViewById(R.id.query_search);
            mQuerySearchEtxt.addTextChangedListener(textWatcher);
            mLocalitySearchEtxt.addTextChangedListener(textWatcher);
            ((ImageView) view.findViewById(R.id.write_a_review)).setOnClickListener(this);
            ((ImageView) view.findViewById(R.id.parentingStop)).setOnClickListener(this);

//			setHeader();
            mCategoryExpandList = (ExpandableListView) view.findViewById(R.id.expandable_list);
            new HashMap<CategoryModel, ArrayList<SubCategory>>();
            categoryData = new HashMap<GroupCategoryModel, ArrayList<CategoryModel>>();
            mCategoryListTable = new CategoryListTable((BaseApplication) getActivity().getApplicationContext());
            mAutoSuggestController = new AutoSuggestController(getActivity(), this);

            mLocalitySearchLout.setVisibility(View.GONE);

            mQuerySearchEtxt.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (mLocalitySearchLout.getVisibility() != View.VISIBLE)
                        mLocalitySearchLout.setVisibility(View.VISIBLE);
                    return false;
                }
            });

//            mLocalitySearchEtxt.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    mLocalitySearchEtxt.setText("");
//                }
//            });

            mSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int pos, long id) {

                    mSearchList.setVisibility(View.GONE);
                    mCategoryExpandList.setVisibility(View.VISIBLE);
                    if (parent.getAdapter() instanceof ArrayAdapter<?>) {

                        /**
                         * its related to search query adapter:
                         */
                        String whichAdapterDataIsLoaded = (String) parent.getTag();
                        String listItem = (String) parent.getAdapter().getItem(pos).toString();
                        //Log.d("check", "check ArrayAdapter ");
                        //Log.d("check", "check listItem " + listItem);
                        mQuerySearchEtxt.removeTextChangedListener(textWatcher);
                        if (!listItem.contains(",")) {
                            Log.d("check", "check listItem null");
                            isContainCommaQuery = false;
                            mQuerySearchEtxt.setText(listItem);
                            mSearchList.setVisibility(View.GONE);
                            mCategoryExpandList.setVisibility(View.VISIBLE);
                        } else {
                            isContainCommaQuery = true;
                            StringTokenizer tokens = new StringTokenizer(listItem, ",");
                            String first = tokens.nextToken();
                            String second = tokens.nextToken();
                            //Log.d("check", "check first " + first);
                           // Log.d("check", "check second " + second);
                            if (!StringUtils.isNullOrEmpty(first) && !StringUtils.isNullOrEmpty(second)) {
                                mQuerySearchEtxt.setText(first.trim());
                                mLocalitySearchEtxt.setText(second.trim());

                            }
                            mSearchList.setVisibility(View.GONE);
                            mCategoryExpandList.setVisibility(View.VISIBLE);
                            mLocalitySearchLout.setVisibility(View.VISIBLE);
                        }
                        mQuerySearchEtxt.addTextChangedListener(textWatcher);


                    } else if (parent.getAdapter() instanceof SubLocalityAdapter) {
                        // String listItem = (String)parent.getAdapter().getItem(pos).toString();
                        //Log.d("check", "check SubLocalityAdapter ");
                        ///if(pos == 0 && ((SubLocalityAdapter)parent.getAdapter()).getSublocalities().get(0).equals("Current Location")) {
                        String queryData = mQuerySearchEtxt.getText().toString();
                        String locality = mLocalitySearchEtxt.getText().toString();
                        // mLocalitySearchEtxt.setText(listItem);
                        if (!queryData.equals("")) {

                            Intent intent = new Intent(getActivity(), BusinessListActivityKidsResources.class);
                            intent.putExtra("query", queryData);
                            if (locality.equals("") || !isContainCommaQuery) {
                                String localityData = (String) parent.getAdapter().getItem(pos);
                                if (!StringUtils.isNullOrEmpty(localityData)) {

                                    if (pos != 0) {
                                        intent.putExtra("locality", localityData);
                                    }

                                }
                            } else {
                                intent.putExtra("locality", locality);
                            }

                            intent.putExtra("isSearchListing", true);
                            startActivity(intent);
                        } else {
                            ToastUtils.showToast(getActivity(), getString(R.string.no_query));

                        }
                        ///	}else{
                        /*	mLocalitySearchEtxt.removeTextChangedListener(textWatcher);
                            String listItem = ((SubLocalityAdapter)parent.getAdapter()).getSublocalities().get(pos);
							mLocalitySearchEtxt.setText(listItem);
							mLocalitySearchEtxt.addTextChangedListener(textWatcher);
						}*/
                    }
                    hideSearchList();

                }
            });
            mBusinessSearch.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    String queryData = mQuerySearchEtxt.getText().toString();
                    String localityData = mLocalitySearchEtxt.getText().toString();
                    if (StringUtils.isNullOrEmpty(queryData)) {
                        ToastUtils.showToast(getActivity(), getString(R.string.no_query));
                        return;
                    }
                    if (!StringUtils.isNullOrEmpty(queryData) || !StringUtils.isNullOrEmpty(localityData)) {
                        //  if (!StringUtils.isNullOrEmpty(queryData)) {

                        //if(isContainCommaQuery || localityData.equals(""))
                        //	{
                        hideSearchList();
                        Intent intent = new Intent(getActivity(), BusinessListActivityKidsResources.class);
                        intent.putExtra("query", queryData);
                        intent.putExtra("locality", localityData);
                        intent.putExtra("isSearchListing", true);
                        startActivity(intent);

                        //	}else{
                        //		showToast("Please give correct data!");
                        //		return;
                        //	}
                        //	}
                    }
                   /* Intent intent = new Intent(getActivity(), AutoSuggestTransparentDialogActivity.class);
                    startActivity(intent);*/
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

//	private void setHeader() {
//		header=(Header)findViewById(R.id.header);
//		header.inflateHeader();
//		header.openSlidingDrawer();
//		header.updateHeaderText("", false);
//		header.updateSelectedCity(SharedPrefUtils.getCurrentCityModel(FragmentHomeCategory.this).getName(), true);
//
//	}

    @Override
    public void onResume() {
        super.onResume();
        developHomeList();
    }

//	@Override
//	protected void onRestart() {
//		super.onRestart();
//		header.updateSelectedCity(SharedPrefUtils.getCurrentCityModel(FragmentHomeCategory.this).getName(), true);
//	}

//	@Override
//	protected void onStop() {
//		super.onStop();
//		if(header!=null){
//			header.closeDrawer();
//		}
//	}

    @Override
    protected void updateUi(Response response) {
        removeProgressDialog();
        if (response == null) {
            ToastUtils.showToast(getActivity(), getString(R.string.went_wrong));
            return;
        }
        AutoSuggestResponse responseData1 = (AutoSuggestResponse) response.getResponseObject();
        Log.d("check", "onTextChanged updateUi " + responseData1);
        String message1 = responseData1.getResult().getMessage();
        if (responseData1.getResponseCode() == 200) {
            ArrayList<String> queryList = responseData1.getResult().getData().getSuggest();
            Log.d("check", "onTextChanged updateUi queryList " + queryList.size());
            if (!queryList.isEmpty()) {
                mCategoryExpandList.setVisibility(View.GONE);
                mSearchList.setVisibility(View.VISIBLE);

                //mLoutProgress.setVisibility(View.GONE);
                ArrayAdapter<String> mQueryAdapter = new ArrayAdapter<String>(getActivity(), R.layout.text_for_locality, queryList);
                mSearchList.setAdapter(mQueryAdapter);
                mQueryAdapter.notifyDataSetChanged();
            } else {
                //  mLoutProgress.setVisibility(View.GONE);
                mCategoryExpandList.setVisibility(View.VISIBLE);
                mSearchList.setVisibility(View.GONE);
            }

        } else if (responseData1.getResponseCode() == 400) {

            mSearchList.setVisibility(View.GONE);
            mCategoryExpandList.setVisibility(View.VISIBLE);
        }
        // mLoutProgress.setVisibility(View.GONE);
        switch (response.getDataType()) {
            case AppConstants.LOGOUT_REQUEST:
                LogoutResponse responseData = (LogoutResponse) response.getResponseObject();
                String message = responseData.getResult().getMessage();
                if (responseData.getResponseCode() == 200) {
                    /**
                     * delete table from local also;
                     */
                    UserTable _tables = new UserTable((BaseApplication) getActivity().getApplicationContext());
                    _tables.deleteAll();
                    if (StringUtils.isNullOrEmpty(message)) {
                        ToastUtils.showToast(getActivity(), getString(R.string.went_wrong));

                    } else {
                        ToastUtils.showToast(getActivity(), message);
                    }

                } else if (responseData.getResponseCode() == 400) {
                    if (StringUtils.isNullOrEmpty(message)) {
                        ToastUtils.showToast(getActivity(), getString(R.string.went_wrong));
                    } else {
                        ToastUtils.showToast(getActivity(), message);
                    }
                }
                break;

            default:
                break;
        }
    }

    private void developHomeList() {
        categoryData = new HashMap<GroupCategoryModel, ArrayList<CategoryModel>>();
        groupCategoryList = mCategoryListTable.getGroupData();

        // remove events & parenting blogs added by khushboo
        ArrayList<GroupCategoryModel> modifyList = new ArrayList<>();
        modifyList.clear();
        modifyList = groupCategoryList;


        try {
            ArrayList<Integer> list = new ArrayList<>();

//            for (int i = 0; i < modifyList.size(); i++) {
//                if (modifyList.get(i).getCategoryGroup().startsWith("Events") || modifyList.get(i).getCategoryGroup().startsWith("Parenting")) {
//                  modifyList.remove(i);
//                }
//            }



            for (int i = 0; i < modifyList.size(); i++) {
                if (modifyList.get(i).getCategoryGroup().startsWith("Events")){
                    modifyList.remove(i);
                }
            }

            for (int i = 0; i < modifyList.size(); i++) {
                if (modifyList.get(i).getCategoryGroup().startsWith("Parenting")){
                    modifyList.remove(i);
                }
            }


            groupCategoryList = modifyList;
        } catch (Exception e) {
            e.printStackTrace();
        }


        for (GroupCategoryModel _groupModel : groupCategoryList) {
            ArrayList<CategoryModel> childCategoryList = mCategoryListTable.getCategoryData(_groupModel.getCategoryGroup());
            if (childCategoryList.size() <= 1) {
                childCategoryList = new ArrayList<CategoryModel>();
            }

            categoryData.put(_groupModel, childCategoryList);
        }
        if (groupCategoryList.isEmpty()) {
            mNoResultTxt.setVisibility(View.VISIBLE);
            //mCategoryExpandList.setVisibility(View.GONE);
        } else {
            mNoResultTxt.setVisibility(View.GONE);
            //mCategoryExpandList.setVisibility(View.VISIBLE);


            CategoryListAdapter _categoryAdapter = new CategoryListAdapter(getActivity(), groupCategoryList, categoryData);
            mCategoryExpandList.setAdapter(_categoryAdapter);
            _categoryAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.parentingStop:
                startActivity(new Intent(getActivity(), TopPicksActivity.class));//ParentingArticlesActivity
                break;
            case R.id.write_a_review:
                UserTable _table = new UserTable((BaseApplication) getActivity().getApplicationContext());
                int count = _table.getCount();
                if (count <= 0) {
                /*Bundle args=new Bundle();
                args.putInt(Constants.CATEGORY_ID, mCategoryId);
				args.putInt(Constants.PAGE_TYPE, mEventOrBusiness);
				args.putString(Constants.BUSINESS_OR_EVENT_ID, detailsResponse.getId());
				args.putString(Constants.DISTANCE, mDistance);*/
                    LoginFragmentDialog fragmentDialog = new LoginFragmentDialog();
                    //	fragmentDialog.setArguments(args);
                    fragmentDialog.show(getActivity().getSupportFragmentManager(), "");
                    return;
                }

                startActivity(new Intent(getActivity(), WriteReviewActivity.class));
                break;
            case R.id.alphaView:
                mCategoryExpandList.bringToFront();
                break;
            case R.id.searchLout:
                Intent intent = new Intent(getActivity(), AutoSuggestTransparentDialogActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }


    }

    public void replaceFragment(final Fragment fragment, Bundle bundle, boolean isAddToBackStack) {
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {

                try {
                    String backStateName = fragment.getClass().getName();
                    boolean fragmentPopped = getActivity().getSupportFragmentManager().popBackStackImmediate(backStateName, 0);

                    if (!fragmentPopped) { // fragment not in back stack, create
                        // it.
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, fragment);
                        ft.addToBackStack(backStateName);
                        ft.commit();
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        });
    }

    TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            Log.d("check", "onTextChanged");
            /**
             * this will call a query listing from api.
             */
            if (mQuerySearchEtxt.getText().hashCode() == s.hashCode()) {
                if (ConnectivityUtils.isNetworkEnabled(getActivity())) {
                    if (!StringUtils.isNullOrEmpty(s.toString())) {
                        //	String queryData=s.toString();
                        /*if(queryData.contains(",")){
                            StringTokenizer tokens = new StringTokenizer(queryData, ",");
							String first = tokens.nextToken();
							String second = tokens.nextToken();
							if(!StringUtils.isNullOrEmpty(first) && !StringUtils.isNullOrEmpty(second)){
								mQuerySearchEtxt.setText(first);
								mLocalitySearchEtxt.setText(second);
							}

						}else{*/
                        // mLoutProgress.setVisibility(View.VISIBLE);
                        Log.d("check", "mAutoSuggestController " + mAutoSuggestController);
                        if (mAutoSuggestController != null)
                            mAutoSuggestController.setCanceled(true);
                        mAutoSuggestController.getData(AppConstants.BUSINESS_AUTO_SUGGEST_REQUEST, s.toString());
                        //	}

                    } else {
                        mSearchList.setVisibility(View.GONE);
                        mCategoryExpandList.setVisibility(View.VISIBLE);
                    }
                } else {
                    //showToast(getString(R.string.error_network));
                    return;
                }

                /**
                 * it will call locality list from local db.
                 */
            } else if (mLocalitySearchEtxt.getText().hashCode() == s.hashCode()) {
                Log.d("check", "locality");
                SubLocalityAdapter adapter = null;
                LocalityTable _localitiesTable = new LocalityTable((BaseApplication) getActivity().getApplicationContext());
                ArrayList<String> localitiesName = new ArrayList<String>();
                localitiesName.add("Near Me");
                ArrayList<String> localitiesNameDb = _localitiesTable.getLocalitiesName(s.toString().trim());
                if (localitiesNameDb != null && localitiesName.size() != 0) {
                    localitiesName.addAll(localitiesNameDb);
                }
                if (localitiesName.size() == 1) {
                    adapter = new SubLocalityAdapter(getActivity(), localitiesName);
                    mSearchList.setAdapter(adapter);
                }

                if (s != null && !(s.toString().equals(""))) {

                    if (!localitiesName.isEmpty()) {
                        if (adapter == null) {
                            adapter = new SubLocalityAdapter(getActivity(), localitiesName);
                        }
                        adapter.notifyDataSetChanged();
                        mSearchList.setVisibility(View.VISIBLE);
                        mCategoryExpandList.setVisibility(View.GONE);
                        mSearchList.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        mSearchList.setTag(Constants.LOCALITY_LIST_TAG);

                    } else {
                        mSearchList.setVisibility(View.GONE);
                        mCategoryExpandList.setVisibility(View.VISIBLE);
                        // mLoutProgress.setVisibility(View.GONE);

                    }


                } else {
                    mSearchList.setVisibility(View.GONE);
                    mCategoryExpandList.setVisibility(View.VISIBLE);
                }

            }

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }
    };

    public void hideSearchList() {
        mSearchList.setVisibility(View.INVISIBLE);
//        mQuerySearchEtxt.setText("");
//        mLocalitySearchEtxt.setText("");
//        mLocalitySearchLout.setVisibility(View.GONE);
    }

}