package com.mycity4kids.ui.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by hemant on 22/1/16.
 */
public class CreateFamilyActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar mToolbar;

    private EditText mFamilyName, mKidsName, mSpouseName, mSpouseEmail;
    private LinearLayout mAdultContainer, mChildContainer;
    private TextView mAdditionalChild, mAdditionalAdult, mColorfrKid, mSpouseColor, colorView, mColorfrSpouse;
    private static TextView mKidsbdy;
    private ScrollView scrollView;
    private LinearLayout rootLayout;

    private static TextView BdayView;

    private int childCount = 0;
    private int adultCount = 0;

    private String color_selected = "";
    private String spouse_color = "";
    private String kid0_color = "";

    boolean isSpouseColor;
    boolean isKIDColor;
    static boolean isKIDBdy;

    private Dialog mColorPickerDialog;

    private HashMap<String, String> used_colors = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_family_activity);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Family Details");

        rootLayout = (LinearLayout) findViewById(R.id.root);
        mAdultContainer = (LinearLayout) findViewById(R.id.internal_adult_layout);
        mChildContainer = (LinearLayout) findViewById(R.id.internal_kid_layout);
        mFamilyName = (EditText) findViewById(R.id.family_name);
        mAdditionalChild = (TextView) findViewById(R.id.additional_child);
        mAdditionalAdult = (TextView) findViewById(R.id.additional_adult);
        scrollView = (ScrollView) findViewById(R.id.mainScroll);

        mSpouseEmail = (EditText) findViewById(R.id.spouse_email);
        mSpouseName = (EditText) findViewById(R.id.spouse_name);
        mColorfrSpouse = (TextView) findViewById(R.id.color_spouse);

        mKidsName = (EditText) findViewById(R.id.kids_name);
        mKidsbdy = (TextView) findViewById(R.id.kids_bdy);

        mColorfrKid = (TextView) findViewById(R.id.kidcolor);

        mAdditionalChild.setOnClickListener(this);
        mAdditionalAdult.setOnClickListener(this);
        mKidsbdy.setOnClickListener(this);
        mColorfrKid.setOnClickListener(this);
        mColorfrSpouse.setOnClickListener(this);
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.additional_adult:
                addNewAdult();
                break;

            case R.id.additional_child:
                addNewChild();
                break;
            case R.id.kidcolor:
                isKIDColor = true;
                showColorPickerDialog("", null);
                break;

            case R.id.color_spouse:
                isSpouseColor = true;
                showColorPickerDialog("", null);
                break;

            case R.id.kids_bdy:
                isKIDBdy = true;
                showDatePickerDialog();
                break;

        }
    }

    private void addNewAdult() {
        boolean addAdult = false;
        if ((mSpouseName.getText().toString().trim().equals("")) || (mSpouseEmail.getText().toString().trim().equals(""))) {

            ToastUtils.showToast(this, getResources().getString(R.string.enter_adult));
        } else {
            if (mAdultContainer.getChildCount() > 0) {


                for (int position = 0; position < mAdultContainer.getChildCount(); position++) {
                    View innerLayout = (View) mAdultContainer.getChildAt(position);

                    EditText adultname = (EditText) innerLayout.findViewById(R.id.spouse_name);
                    EditText adultemail = (EditText) innerLayout.findViewById(R.id.spouse_email);

                    if ((adultname.getText().toString().trim().equals("")) || (adultemail.getText().toString().trim().equals(""))) {

                        addAdult = false;
                        ToastUtils.showToast(this, getResources().getString(R.string.enter_adult));
                        break;

                    } else {
                        addAdult = true;
                    }
                }


                if (addAdult)
                    addDynamicAdult();

            } else {
                addDynamicAdult();
            }
        }
    }

    private void addNewChild() {

        boolean addChild = false;
        if ((mKidsName.getText().toString().trim().equals("")) || (mKidsbdy.getText().toString().trim().equals(""))) {

            ToastUtils.showToast(this, getResources().getString(R.string.enter_kid));
        } else {


            if (mChildContainer.getChildCount() > 0) {

                for (int position = 0; position < mChildContainer.getChildCount(); position++) {
                    View innerLayout = (View) mChildContainer.getChildAt(position);

                    EditText nameOfKidEdt = (EditText) innerLayout.findViewById(R.id.kids_name);
                    TextView dobOfKidSpn = (TextView) innerLayout.findViewById(R.id.kids_bdy);
                    final TextView kidcolor = (TextView) innerLayout.findViewById(R.id.kidcolor);

                    if ((nameOfKidEdt.getText().toString().trim().equals("")) || (dobOfKidSpn.getText().toString().trim().equals(""))) {
                        addChild = false;
                        ToastUtils.showToast(this, getResources().getString(R.string.enter_kid));
                        break;

                    } else {
                        addChild = true;
                    }
                }

                if (addChild)
                    addDynamicChild();

            } else {
                addDynamicChild();
            }

        }
    }

    private void addDynamicAdult() {
        ++adultCount;
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View convertView = (View) layoutInflater.inflate(R.layout.aa_addadult, null);
        convertView.setTag("adult" + adultCount);
        convertView.setId(adultCount);

        EditText adultname = (EditText) convertView.findViewById(R.id.spouse_name);
        EditText adultemail = (EditText) convertView.findViewById(R.id.spouse_email);
        final TextView adultColor = (TextView) convertView.findViewById(R.id.color_spouse);

        final TextView deleteView = (TextView) convertView.findViewById(R.id.cross);

        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdultContainer.removeView(convertView);
            }
        });

        adultColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorView = adultColor;
                showColorPickerDialog("adult" + convertView.getId(), adultColor);
            }
        });

        int digit = getRandomNumber();
        Drawable drawable = getResources().getDrawable(getResources()
                .getIdentifier("color_" + digit + "xxhdpi", "drawable", getPackageName()));
        adultColor.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
        used_colors.put("adult" + convertView.getId(), "" + digit);

        adultColor.setTag("" + digit);


//        if (kid0_color.equals("")) {
//
//            digit = getRandomNumber();
//            drawable = getResources().getDrawable(getResources()
//                    .getIdentifier("color_" + digit + "xxhdpi", "drawable", getPackageName()));
//            mColorfrKid.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
//            mColorfrKid.setTag("" + digit);
//        }

        mAdultContainer.addView(convertView);
        sendScrollDown();
    }

    private void addDynamicChild() {
        ++childCount;
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View convertView = (View) layoutInflater.inflate(R.layout.aa_addchild, null);
        convertView.setTag("kid" + childCount);
        convertView.setId(childCount);

        final TextView dobOfKidSpn = (TextView) convertView.findViewById(R.id.kids_bdy);
        final TextView kidcolor = (TextView) convertView.findViewById(R.id.kidcolor);

        final TextView deleteView = (TextView) convertView.findViewById(R.id.cross);

        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChildContainer.removeView(convertView);
            }
        });

        dobOfKidSpn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BdayView = dobOfKidSpn;
                showDatePickerDialog();
            }
        });


        kidcolor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorView = kidcolor;
                showColorPickerDialog("kid" + convertView.getId(), kidcolor);
            }
        });

        int digit;
        Drawable drawable;
//        if (kid0_color.equals("")) {
//
//            digit = getRandomNumber();
//            drawable = getResources().getDrawable(getResources()
//                    .getIdentifier("color_" + digit + "xxhdpi", "drawable", getPackageName()));
//            mColorfrKid.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
//            mColorfrKid.setTag("" + digit);
//            used_colors.put("kid0" + convertView.getId(), "" + digit);
//            kid0_color = "" + digit;
//        }


        digit = getRandomNumber();
        drawable = getResources().getDrawable(getResources()
                .getIdentifier("color_" + digit + "xxhdpi", "drawable", getPackageName()));

        kidcolor.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
        used_colors.put("kid" + convertView.getId(), "" + digit);
        kidcolor.setTag("" + digit);

        mChildContainer.addView(convertView);
        sendScrollDown();

    }

    private void sendScrollDown() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (null != scrollView)
                            scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        }).start();
    }

    public int getRandomNumber() {

        ArrayList<String> numbers = new ArrayList<>();
        numbers.clear();
        Iterator myVeryOwnIterator = used_colors.keySet().iterator();
        while (myVeryOwnIterator.hasNext()) {
            String key = (String) myVeryOwnIterator.next();
            String value = (String) used_colors.get(key);
            numbers.add(value);
        }
        int digit = 1;
        for (int i = 1; i <= 10; i++) {

            if (!numbers.contains("" + i)) {
                digit = i;
                break;
            }
        }
        return digit;

    }

    public void showColorPickerDialog(final String name, final TextView textview) {

        // custom dialog
        mColorPickerDialog = new Dialog(this);
        mColorPickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = mColorPickerDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = (View) layoutInflater.inflate(R.layout.aa_colorpicker, null);

        mColorPickerDialog.setContentView(view);
        mColorPickerDialog.setCancelable(true);

        mColorPickerDialog.findViewById(R.id.color1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //color_selected = "1";
                // setColor(name, textview);
                showSelectedcolorMessage(v, name, textview, "1");

            }
        });

        mColorPickerDialog.findViewById(R.id.color2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //color_selected = "2";
                // setColor(name, textview);

                showSelectedcolorMessage(v, name, textview, "2");

            }
        });

        mColorPickerDialog.findViewById(R.id.color3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //color_selected = "3";
                //setColor(name, textview);
                showSelectedcolorMessage(v, name, textview, "3");

            }
        });
        mColorPickerDialog.findViewById(R.id.color4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // color_selected = "4";
                //setColor(name, textview);
                showSelectedcolorMessage(v, name, textview, "4");
            }
        });
        mColorPickerDialog.findViewById(R.id.color5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // color_selected = "5";
                // setColor(name, textview);
                showSelectedcolorMessage(v, name, textview, "5");
            }
        });
        mColorPickerDialog.findViewById(R.id.color6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // color_selected = "6";
                // setColor(name, textview);
                showSelectedcolorMessage(v, name, textview, "6");

            }
        });
        mColorPickerDialog.findViewById(R.id.color7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //color_selected = "7";
                //setColor(name, textview);
                showSelectedcolorMessage(v, name, textview, "7");
            }
        });
        mColorPickerDialog.findViewById(R.id.color8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // color_selected = "8";
                // setColor(name, textview);
                showSelectedcolorMessage(v, name, textview, "8");
            }
        });
        mColorPickerDialog.findViewById(R.id.color9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // color_selected = "9";
                //setColor(name, textview);
                showSelectedcolorMessage(v, name, textview, "9");

            }
        });
        mColorPickerDialog.findViewById(R.id.color10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              /*  color_selected = "10";
                setColor(name, textview);*/
                showSelectedcolorMessage(v, name, textview, "10");

            }
        });


        mColorPickerDialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mColorPickerDialog.dismiss();
            }
        });


        Iterator myVeryOwnIterator = used_colors.keySet().iterator();
        while (myVeryOwnIterator.hasNext()) {
            String key = (String) myVeryOwnIterator.next();
            String value = (String) used_colors.get(key);
            //view.findViewWithTag(value).setEnabled(false);
            view.findViewWithTag(value).setAlpha(0.2f);
        }

        mColorPickerDialog.show();


    }

    public void setColor(String id, TextView v) {
        if (isSpouseColor) {

            used_colors.put("spouse1", color_selected);
            spouse_color = color_selected;
            isSpouseColor = false;

            mColorfrSpouse.setTag(color_selected);

            Drawable drawable = getResources().getDrawable(getResources()
                    .getIdentifier("color_" + color_selected + "xxhdpi", "drawable", getPackageName()));
            mColorfrSpouse.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);

//            if (kid0_color.equals("")) {
//                int digit = getRandomNumber();
//                drawable = getResources().getDrawable(getResources()
//                        .getIdentifier("color_" + digit + "xxhdpi", "drawable", getPackageName()));
//                mColorfrKid.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
//                mColorfrKid.setTag("" + digit);
//            }


        } else if (isKIDColor) {

            used_colors.put("kid0", color_selected);
            kid0_color = color_selected;
            isKIDColor = false;
            mColorfrKid.setTag(color_selected);


            Drawable drawable = getResources().getDrawable(getResources()
                    .getIdentifier("color_" + color_selected + "xxhdpi", "drawable", getPackageName()));
            mColorfrKid.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
            // remove this color from kid

        } else {

//            if (kid0_color.equals("")) {
//
//                int digit = getRandomNumber();
//                Drawable drawable = getResources().getDrawable(getResources()
//                        .getIdentifier("color_" + digit + "xxhdpi", "drawable", getPackageName()));
//                mColorfrKid.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
//                mColorfrKid.setTag("" + digit);
//            }

            used_colors.put(id, color_selected);
            // set on the custom view
            if (v != null) {
                //ToastUtils.showToast(this,"color set");
                Drawable drawable = getResources().getDrawable(getResources()
                        .getIdentifier("color_" + color_selected + "xxhdpi", "drawable", getPackageName()));
                v.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
                v.setTag("" + color_selected);

            }

        }
        mColorPickerDialog.dismiss();

    }

    public void showSelectedcolorMessage(View v, final String name, final TextView textview, String colorsSelected) {
        if (v.getAlpha() == 0.2f) {
            showSnackbar(rootLayout, getResources().getString(R.string.color_selected));
        } else {
            color_selected = colorsSelected;
            setColor(name, textview);
        }
    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        boolean cancel;
        final Calendar c = Calendar.getInstance();
        int curent_year = c.get(Calendar.YEAR);
        int current_month = c.get(Calendar.MONTH);
        int current_day = c.get(Calendar.DAY_OF_MONTH);


        @SuppressLint("NewApi")
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker


            //c.set(curent_year,current_month,current_day);

            long maxdate = Long.parseLong(convertDate(current_day + "-" + (current_month + 1) + "-" + curent_year)) * 1000;
            DatePickerDialog dlg = new DatePickerDialog(getActivity(), this, curent_year, current_month, current_day);
            dlg.getDatePicker().setMaxDate(c.getTimeInMillis());
            return dlg;

        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            String sel_date = "" + day + "-" + (month + 1) + "-" + year;
            if (isKIDBdy) {
                isKIDBdy = false;
                mKidsbdy.setError(null);

                if (chkTime(sel_date)) {
                    mKidsbdy.setText("" + day + "-" + (month + 1) + "-" + year);
                } else {
                    mKidsbdy.setText("" + current_day + "-" + (current_month + 1) + "-" + curent_year);
                }

            } else {
                if (BdayView != null)

                {
                    if (chkTime(sel_date)) {
                        BdayView.setText("" + day + "-" + (month + 1) + "-" + year);
                    } else {
                        BdayView.setText("" + current_day + "-" + (current_month + 1) + "-" + curent_year);
                    }

                }
            }


        }
    }

    public static String convertDate(String convertdate) {
        String timestamp = "";
        try {
            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            Date dateobj = (Date) formatter.parse(convertdate);
            timestamp = "" + (dateobj.getTime()) / 1000;
            return timestamp;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return timestamp;
    }

    public static boolean chkTime(String time) {
        boolean result = true;

        String currentime = "" + (System.currentTimeMillis() / 1000);
        if (Integer.parseInt(currentime) < Integer.parseInt(convertDate(time)))
            result = false;

        return result;
    }

}
