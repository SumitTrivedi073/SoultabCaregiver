package com.soultabcaregiver.activity.docter.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.soultabcaregiver.R;
import com.soultabcaregiver.activity.docter.ActAllDoctorAppointment;
import com.soultabcaregiver.activity.docter.AddDoctorActivity;
import com.soultabcaregiver.activity.docter.DoctorListActivity;
import com.soultabcaregiver.sinch_calling.BaseFragment;

public class DoctorFragment extends BaseFragment implements View.OnClickListener {

    View view;
    LinearLayout lyDoctor, lyDocAppoint, add_Doctor;
    Context mContext;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_doctor, container, false);

        InitCompo();
        Listener();
        return view;
    }
    private void Listener() {
        lyDoctor.setOnClickListener(this);
        add_Doctor.setOnClickListener(this);
        lyDocAppoint.setOnClickListener(this);
    }

    private void InitCompo() {
        lyDoctor = view.findViewById(R.id.ly_doctor);
        add_Doctor = view.findViewById(R.id.add_Doctor);
        lyDocAppoint = view.findViewById(R.id.ly_doc_appoin);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ly_doctor:
                startActivity(new Intent(mContext, DoctorListActivity.class));
                break;
            case R.id.add_Doctor:
                startActivity(new Intent(mContext, AddDoctorActivity.class));
                break;
            case R.id.ly_doc_appoin:
                startActivity(new Intent(mContext, ActAllDoctorAppointment.class));
                break;
        }

    }

}
