package com.soultabcaregiver.sinch_calling;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.soultabcaregiver.sinch_calling.SinchService;
import com.soultabcaregiver.utils.CustomProgressDialog;
import com.soultabcaregiver.utils.Utility;


public abstract class BaseActivity extends AppCompatActivity implements ServiceConnection {

    public Context mContext;
    private SinchService.SinchServiceInterface mSinchServiceInterface;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getApplicationContext().bindService(new Intent(this, SinchService.class), this,
                BIND_AUTO_CREATE);

    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
            onServiceConnected();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = null;
            onServiceDisconnected();
        }
    }

    protected void onServiceConnected() {
        // for subclasses
    }

    protected void onServiceDisconnected() {
        // for subclasses
    }

    protected SinchService.SinchServiceInterface getSinchServiceInterface() {
        return mSinchServiceInterface;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean granted = grantResults.length > 0;
        for (int grantResult : grantResults) {
            granted &= grantResult == PackageManager.PERMISSION_GRANTED;
        }
        if (granted) {
            //Toast.makeText(this, "You may now place a call", Toast.LENGTH_LONG).show();
        } else {

            Utility.ShowToast(this,"This application needs permission to use your microphone and camera to function properly.");
        }
    }

    private CustomProgressDialog progressDialog;

    public void showProgressDialog(String message){
        if(progressDialog == null) progressDialog = new CustomProgressDialog(this, message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void hideProgressDialog(){
        if(progressDialog != null) progressDialog.dismiss();
    }

    public void HideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
