package com.soultabcaregiver.sinch_calling;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.soultabcaregiver.R;
import com.soultabcaregiver.utils.CustomProgressDialog;
import com.soultabcaregiver.utils.Utility;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment implements ServiceConnection {

    private SinchService.SinchServiceInterface mSinchServiceInterface;
    public  CustomProgressDialog progressDialog;

    public BaseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);

        getActivity().bindService(new Intent(getActivity(), SinchService.class), this,
                BIND_AUTO_CREATE);
        return textView;
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

    public  void showProgressDialog(Context context, String message){
        if(progressDialog == null) progressDialog = new CustomProgressDialog(context, message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public  void hideProgressDialog(){
        if(progressDialog != null) progressDialog.dismiss();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean granted = grantResults.length > 0;
        for (int grantResult : grantResults) {
            granted &= grantResult == PackageManager.PERMISSION_GRANTED;
        }
        if (granted) {
            //Toast.makeText(this, "You may now place a call", Toast.LENGTH_LONG).show();
        } else {

            Utility.ShowToast(getActivity(),"This application needs permission to use your microphone and camera to function properly.");
        }
    }
}
