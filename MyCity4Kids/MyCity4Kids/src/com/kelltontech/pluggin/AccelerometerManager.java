package com.kelltontech.pluggin;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.kelltontech.pluggin.AccelerometerManager.TiltChangeListener;

/**
 * This component can be extended to receive many events.
 * Currently code is added to receive only tilt event
 */
public class AccelerometerManager implements SensorEventListener {

	private final String TAG = "AccelerometerManager"; 
	
	private Sensor  mAccelerometer;
	private SensorManager mSensorManager;
	
	private TiltChangeListener mTiltChangeListener;
	private int mCurrentTiltPosition = TiltChangeListener.TILT_NONE;
	
	/**
	 * This value will be used to extend the range for {@link TiltChangeListener.TILT_NONE}
	 * Range will be extended from 0 to 2 * {@link AccelerometerManager#mHorizontalThreshold}
	 */
	private float mHorizontalThreshold;
	
	/**
	 * COnstructs a AccelerometerManager with {@link AccelerometerManager#mHorizontalThreshold} = 0.2f
	 * @param pContext
	 */
	public AccelerometerManager( Context pContext ) {
		mSensorManager = (SensorManager)pContext.getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mHorizontalThreshold = 0.2f;
	}
	
	/**
	 * Method to register a TiltChangeListener
	 * {@link AccelerometerManager#unregister()} must be called to avoid memory leaks.
	 */
	public void register( TiltChangeListener pTiltChangeListener ) {
		mTiltChangeListener = pTiltChangeListener;
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	/**
	 * Method to unregister the TiltChangeListener which is registered by {@link AccelerometerManager#register()}
	 */
	public void unregister() {
		mSensorManager.unregisterListener(this);
	}
	
	/**
	 * @return the mHorizontalThreshold
	 */
	public float getHorizontalThreshold() {
		return mHorizontalThreshold;
	}

	/**
	 * @param mHorizontalThreshold the mHorizontalThreshold to set
	 */
	public void setHorizontalThreshold(float pHorizontalThreshold) {
		this.mHorizontalThreshold = pHorizontalThreshold;
	}

	//////////////////////////// methods of SensorEventListener interface
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		float xAcceleration = event.values[0];
		//Log.d( TAG, "Acceleration-X: " + event.values[0] );
		int newTiltPosition = TiltChangeListener.TILT_NONE;
		if (xAcceleration < 0 - mHorizontalThreshold ) {
			newTiltPosition = TiltChangeListener.TILT_RIGHT;
		} else if (xAcceleration > mHorizontalThreshold ) {
			newTiltPosition = TiltChangeListener.TILT_LEFT;
		}
		if( newTiltPosition != mCurrentTiltPosition ) {
			if( mTiltChangeListener != null ) {
				mTiltChangeListener.onTiltChanged(mCurrentTiltPosition, newTiltPosition);
			}
			mCurrentTiltPosition = newTiltPosition;
		}
	}
	
	/**
	 * interface for parameter in {@link AccelerometerManager#register(TiltChangeListener)}
	 */
	public interface TiltChangeListener {
		int TILT_LEFT 	= 1;
		int TILT_NONE 	= 0;
		int TILT_RIGHT 	= -1;
		
		void onTiltChanged( int pOldTilt, int pCurrentTilt);
	}
}
