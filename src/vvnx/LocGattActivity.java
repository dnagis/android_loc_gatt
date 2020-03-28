package vvnx.locgatt;

import android.app.Activity;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import java.text.SimpleDateFormat;

import android.widget.TextView;

import android.util.Log;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.ComponentName;





public class LocGattActivity extends Activity {
	
	
	LocGattService mLocGattService;
	boolean mSceBound = false;
	private static final String TAG = "LocGatt";
	


	TextView textview_1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.mon_activity, null);
        setContentView(view);
        textview_1 = findViewById(R.id.textview_1); 
        
        Intent i = new Intent(this, LocGattService.class);
        startService(i); //nécessaire ou pas???
        bindService(i, connection, Context.BIND_AUTO_CREATE);
    }
    
	@Override
	public void onResume() {
	    super.onResume();
	    Log.d(TAG, "onResume()");

		}
    
    public void ActionPressBouton_stop(View v) {
		Log.d(TAG, "press bouton stop"); 	
		if (mSceBound) {
			mLocGattService.shutDown(); 
		}	
	}
	
	
	 /** callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
			Log.d(TAG, "onServiceConnected()");
            mLocGattService = ((LocGattService.LocalBinder) service).getService(); //récupère une instance du service. On peut alors appeler des fonctions dessus
            mSceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
			Log.d(TAG, "onServiceDisconnected()");
			mSceBound = true;
        }
    };
	
	
	
	

}
