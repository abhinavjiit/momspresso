package com.mycity4kids.reminders;

import android.app.Activity;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;

public class AlarmDialogActivity extends Activity {

    private int mAlarmType;
    private Ringtone r;
    private Vibrator vibrator;
    private String mAlarmDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.pushOpenScreenEvent(AlarmDialogActivity.this, "Alarm Dialogue", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");

        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_activity_alarm);
			mAlarmType = getIntent().getIntExtra(Constants.EXTRA_ALARM_TYPE, Constants.REMINDER_TYPE_APPOINTMENT);
            mAlarmDescription = getIntent().getStringExtra(Constants.EXTRA_ALARM_DESC);
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1000 * 60);

            findViewById(R.id.txvCancel).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelAlarm();
                }
            });

            ((TextView) findViewById(R.id.txvAlarmType)).setText(mAlarmType == Constants.REMINDER_TYPE_APPOINTMENT ? "Appointment" : "Tasks");
            ((TextView) findViewById(R.id.txvAlarmDesc)).setText(mAlarmDescription + "");

            Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alert == null) {
                // alert is null, using backup
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                // I can't see this ever being null (as always have a default
                // notification)
                // but just incase
                if (alert == null) {
                    // alert backup is null, using 2nd backup
                    alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                }
            }
            r = RingtoneManager.getRingtone(this, alert);
            r.play();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    cancelAlarm();
                    finish();
                }
            }, 1000 * 60);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        // done intentionally
    }

    protected void cancelAlarm() {
        try {
            r.stop();
            vibrator.cancel();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
