package vvnx.locgatt;

import android.app.Activity;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Message;
import android.os.Messenger;
import android.os.Handler;
import android.os.RemoteException;

import android.widget.TextView;
import android.widget.Switch;

import android.util.Log;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.ComponentName;





public class LocGattActivity extends Activity {
	
	

	boolean mSceBound = false;
	Messenger mService = null;
	private static final String TAG = "LocGatt";
	
	int nlocs = 0;

	TextView tv_lastloc, tv_nlocs;
	Switch switch_1; //avant j'utilisais android.widget.CheckBox;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.mon_activity, null);
        setContentView(view);
        tv_lastloc = findViewById(R.id.tv_lastloc); 
        tv_nlocs = findViewById(R.id.tv_nlocs); 

        switch_1 = findViewById(R.id.switch_1);
        
        Intent i = new Intent(this, LocGattService.class);
        startService(i); //nécessaire ou pas???
        bindService(i, connection, Context.BIND_AUTO_CREATE); //Déclenche onBind() dans le service
    }
    
	@Override
	public void onResume() {
	    super.onResume();
	    Log.d(TAG, "onResume()");

		}
    
    //Bouton "STOP"
    public void ActionPressBouton_stop(View v) {
		Log.d(TAG, "press bouton stop"); 	
		if (!mSceBound) return;
		//Envoi d'un message vers le service mService
		Message msg = Message.obtain(null, LocGattService.MSG_STOP, 0, 0);
		try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
	}
	
	//Bouton "TEST"
	public void ActionPressBouton_test(View v) {
		//Envoi d'un message vers le service mService
		if (!mSceBound) return;
		Message msg = Message.obtain(null, LocGattService.MSG_TEST, 0, 0);
		try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }	
	}
	
	
	/**
	 * système IPC Messenger / Handler basé sur le Binder
	 */ 
	 
	private Handler mIncomingHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LocGattService.MSG_NEW_LOC_SENT:
                    Log.d(TAG, "Activity: handler -> MSG_NEW_LOC_SENT");
					Date d = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("dd MMM HH:mm:ss");	
					tv_lastloc.setText("LAST LOC SENT: "+ sdf.format(d));
					tv_nlocs.setText("N LOCS: " + ++nlocs);
                    break;
                case LocGattService.MSG_BT_CONNECTED:
                    Log.d(TAG, "Activity: handler -> MSG_BT_CONNECTED");
                    switch_1.setChecked(true);
                    break;
                case LocGattService.MSG_BT_DISCONNECTED:
                    Log.d(TAG, "Activity: handler -> MSG_BT_DISCONNECTED");
                    switch_1.setChecked(false);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };	
	
	
	
	private final Messenger mMessenger = new Messenger(mIncomingHandler);
	
	
	 /** callbacks for service binding */
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
			Log.d(TAG, "onServiceConnected()");
            mService = new Messenger(service); 
            mSceBound = true;
            //Enregistrer un handler ici pour que le service puisse l'appeler, l'envoyer au service
            Message msg = Message.obtain(null, LocGattService.MSG_REG_CLIENT);
            msg.replyTo = mMessenger; //pour dire au service où envoyer ses messages
            try {
                mService.send(msg);
            } catch (RemoteException e) {
                Log.w(TAG, "Unable to register client to service.");
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
			Log.d(TAG, "onServiceDisconnected()");
			mService = null;
			mSceBound = true;
        }
    };
	
	
	
	

}
