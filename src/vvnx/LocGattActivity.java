package vvnx.locgatt;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import java.text.SimpleDateFormat;

import android.widget.TextView;

import android.util.Log;

import android.content.Context;
import android.content.Intent;






public class LocGattActivity extends Activity {
	
	private static final String TAG = "LocGatt";
	


	TextView textview_1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.mon_activity, null);
        setContentView(view);
        textview_1 = findViewById(R.id.textview_1); 
        
        Intent i= new Intent(this, LocGattService.class);
        startService(i);

    }
    
	@Override
	public void onResume() {
	    super.onResume();
	    Log.d(TAG, "onResume()");

		}
    
    public void ActionPressBouton_1(View v) {
		Log.d(TAG, "press bouton - 1"); 		
	}
	

}
