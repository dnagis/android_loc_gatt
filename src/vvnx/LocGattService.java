package vvnx.locgatt;

import android.app.Service;
import android.os.Bundle;
import android.util.Log;
import android.os.IBinder;
import android.os.Binder;
import android.content.Intent;
import android.content.Context;

import android.os.Message;
import android.os.Messenger;
import android.os.Handler;
import android.os.RemoteException;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.NotificationChannel;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import java.util.UUID;


public class LocGattService extends Service implements LocationListener {
	
	private static final String TAG = "LocGatt";
	
	public static final int MSG_SAY_HELLO = 1; //Pour tester le système de messages
	public static final int MSG_REG_CLIENT = 200;
	public static final int MSG_NEW_LOC = 300; //enregistrer le client dans le service
	public static final int MSG_STOP = 400;
	public static final int MSG_BT_CONNECTED = 500;
	public static final int MSG_BT_DISCONNECTED = 600;
	
	Notification mNotification;
	
	public LocationManager mLocationManager;		
	private static final int LOC_MIN_TIME = 10 * 1000; //long: minimum time interval between location updates, in milliseconds
    private static final int LOC_MIN_DIST = 0; //float: minimum distance between location updates, in meters
    
    private BluetoothManager bluetoothManager = null;	
	private BluetoothAdapter mBluetoothAdapter = null;	
	private BluetoothDevice monBTDevice = null;
	private BluetoothGatt mBluetoothGatt = null;
	
	private BluetoothGattCharacteristic mCharacteristic = null;	
	private static final UUID SERVICE_UUID = UUID.fromString("000000ff-0000-1000-8000-00805f9b34fb");
	private static final UUID CHARACTERISTIC_PRFA_UUID = UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb");
	private String BDADDR = "30:AE:A4:04:C3:5A";	
	
	
	/**
	 * système IPC Messenger / Handler basé sur le Binder
	 */
	  
	private Messenger mClient; // l'activité

	private class IncomingHandler extends Handler {
        

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
				//REG_CLIENT: juste un trick pour avoir un messenger vers l'activité (=client)
				case MSG_REG_CLIENT:
                    Log.d(TAG, "Service: handleMessage() -> REG_CLIENT");
                    mClient = msg.replyTo;
                    break;
				case MSG_STOP:
                    Log.d(TAG, "Service: handleMessage() -> STOP");
                    shutDown();
                    break;                    
                default:
                    super.handleMessage(msg);
            }
        }
    }

	 
	
	final Messenger mMessenger = new Messenger(new IncomingHandler()); //le messenger local
	

	@Override
	public IBinder onBind(Intent intent) {
		
		return mMessenger.getBinder(); //envoyé vers onServiceConnected() dans l'activité
	}    
    
    /**
     * 
     * 
     * Le service
     * 
     * 
     */
    
    
    
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand()");
		
		//Location
		mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOC_MIN_TIME, LOC_MIN_DIST, this);
		
		
	    //Foreground
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        String CHANNEL_ID = "MA_CHAN_ID";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "ma_channel", importance);
        channel.setSound(null, null);
        channel.setDescription("android_fait_chier_avec_sa_channel");
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
		
        mNotification = new Notification.Builder(this, CHANNEL_ID)  //  The builder requires the context
                .setSmallIcon(R.drawable.icon)  // the status icon
                .setTicker("NotifText")  // the status text
                .setContentTitle("locGatt")  // the label of the entry
                .setContentText("locGatt")  // the contents of the entry
                .build();	
			
		startForeground(1, mNotification);
		
		//Bluetooth
		connectmGatt();
		
		return START_NOT_STICKY;
	}
	

	
	
	
	public void shutDown() {
		if (mBluetoothGatt != null) {
			mBluetoothGatt.disconnect();
			mBluetoothGatt.close(); 
		}
		mLocationManager.removeUpdates(this);
		stopForeground(true);
		stopSelf();		
	}
  
  
  
  	/**
	 *
	 * implements LocationListener --> 4 méthodes 
	 * 
	 * 
	 **/    
    @Override	
    public void onLocationChanged(Location location) {
        Log.d(TAG, location.getLatitude() + ",  " + location.getLongitude() + ",  " + location.getAccuracy() + ",  " + location.getAltitude() + ",  " + location.getVerticalAccuracyMeters() + ",  "  + location.getTime());
        if (mCharacteristic != null) mBluetoothGatt.readCharacteristic(mCharacteristic);
        //ToDo: envoyer un message au client = activity
        
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
	
	
	/**
	 * 
	 * Bluetooth
	 * 
	 * 
	 * 
	 * */
	
	
	public void connectmGatt(){
		
		if (bluetoothManager == null) bluetoothManager = (BluetoothManager)getSystemService(BLUETOOTH_SERVICE);	
		if (mBluetoothAdapter == null) mBluetoothAdapter = bluetoothManager.getAdapter();	
		
		if (monBTDevice == null) monBTDevice = mBluetoothAdapter.getRemoteDevice(BDADDR);   
				
		mBluetoothGatt = monBTDevice.connectGatt(this, true, gattCallback); 	
	}
	
	
	private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			Log.i(TAG, "onConnectionStateChange()");
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				gatt.discoverServices(); //--> onServicesDiscovered()
				Message msg = Message.obtain(null, LocGattService.MSG_BT_CONNECTED);
				try {
					mClient.send(msg);
					} catch (RemoteException e) {
					e.printStackTrace();
				}
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				Message msg = Message.obtain(null, LocGattService.MSG_BT_DISCONNECTED);
				try {
					mClient.send(msg);
					} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	
		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
				Log.i(TAG, "onServicesDiscovered callback.");
				mCharacteristic = gatt.getService(SERVICE_UUID).getCharacteristic(CHARACTERISTIC_PRFA_UUID);
		}
	
		
		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
				Log.i(TAG, "onCharacteristicRead callback.");
				}
		};
	
	
	
}
