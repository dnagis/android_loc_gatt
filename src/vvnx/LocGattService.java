package vvnx.locgatt;

import android.app.Service;
import android.os.Bundle;
import android.util.Log;
import android.os.IBinder;
import android.content.Intent;
import android.content.Context;


import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.NotificationChannel;


public class LocGattService extends Service implements LocationListener {
	
	private static final String TAG = "LocGatt";
	
	Notification mNotification;
	
	public LocationManager mLocationManager;		
	private static final int LOC_MIN_TIME = 10 * 1000; //long: minimum time interval between location updates, in milliseconds
    private static final int LOC_MIN_DIST = 0; //float: minimum distance between location updates, in meters
	
	@Override
  public int onStartCommand(Intent intent, int flags, int startId) {
	Log.d(TAG, "onStartCommand()");
	mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOC_MIN_TIME, LOC_MIN_DIST, this);
	
	
	    //https://developer.android.com/training/notify-user/channels
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        String CHANNEL_ID = "MA_CHAN_ID";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "ma_channel", importance);
        channel.setSound(null, null);
        channel.setDescription("android_fait_chier_avec_sa_channel");
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
		
		// Build the notification object.
        mNotification = new Notification.Builder(this, CHANNEL_ID)  //  The builder requires the context
                .setSmallIcon(R.drawable.icon)  // the status icon
                .setTicker("NotifText")  // the status text
                .setContentTitle("locGatt")  // the label of the entry
                .setContentText("locGatt")  // the contents of the entry
                .build();	
	
	
	
	startForeground(1, mNotification);
	return START_NOT_STICKY;
  }

  @Override
  public IBinder onBind(Intent intent) {
    //TODO for communication return IBinder implementation
    return null;
  }
  
  
  
  	/**
	 *
	 * implements LocationListener --> il faut les 4 méthodes 
	 * 
	 * 
	 **/    
    @Override	
    public void onLocationChanged(Location location) {
        Log.d(TAG, location.getLatitude() + ",  " + location.getLongitude() + ",  " + location.getAccuracy() + ",  " + location.getAltitude() + ",  " + location.getVerticalAccuracyMeters() + ",  "  + location.getTime());
}
        
	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	
	
	
}
