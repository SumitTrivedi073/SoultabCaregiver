package com.soultabcaregiver.activity.docter.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.soultabcaregiver.Base.BaseFragment;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.activity.docter.AddDoctorActivity;
import com.soultabcaregiver.utils.NonSwipeableViewPager;
import com.soultabcaregiver.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class DoctorFragment extends BaseFragment {

    public static DoctorFragment instance;
    View view;
    Context mContext;
    ViewPagerAdapter adapter;
    private Toolbar toolbar_main;
    private TabLayout tabLayout;
    private NonSwipeableViewPager viewPager;
    private TextView tab_txt;
    private FloatingActionButton Add_doctor_btn;
    RelativeLayout doctor_show_Relative, doctor_hide_Relative;


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

        instance = DoctorFragment.this;
        InitCompo();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();

        }
        //   new ReminderCreateClass(getActivity());

    }

    private void InitCompo() {
        toolbar_main = view.findViewById(R.id.toolbar_main);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar_main);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tab_txt = view.findViewById(R.id.tab_txt);
        Add_doctor_btn = view.findViewById(R.id.Add_doctor_btn);
        doctor_show_Relative = view.findViewById(R.id.doctor_show_Relative);
        doctor_hide_Relative = view.findViewById(R.id.doctor_hide_Relative);


        doctorhide_show(Utility.getSharedPreferences(mContext, APIS.doctor_hide_show));

        Add_doctor_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.getSharedPreferences(mContext, APIS.doctor_hide_show).equals(APIS.Edit)) {

                    Intent intent = new Intent(mContext, AddDoctorActivity.class);
                    startActivity(intent);
                }else {
                    Utility.ShowToast(mContext, mContext.getResources().getString(R.string.only_view_permission));
                }
            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == 1) {
                    tab_txt.setText(mContext.getResources().getString(R.string.doctor_appointment));

                } else {
                    tab_txt.setText(mContext.getResources().getString(R.string.doctor_list));

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    private void setupViewPager(ViewPager viewPager) {
        //if tablayout use inside fragment so instead of getFragmentManager() Use getChildFragmentManager()
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new DoctorListFragment(), "Doctor List");
        adapter.addFragment(new MyDoctorListFragment(), "My Doctor List");
        adapter.addFragment(new DoctorAppointmentFragment(), "Doctor Appointment");

        viewPager.setAdapter(adapter);
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    public void doctorhide_show(String doctorhide_show) {
        if (doctorhide_show!=null) {
            if (doctorhide_show.equals(APIS.Hide)) {
                doctor_show_Relative.setVisibility(View.GONE);
                doctor_hide_Relative.setVisibility(View.VISIBLE);
                Add_doctor_btn.setVisibility(View.GONE);
            } else if (doctorhide_show.equals(APIS.View)) {
                doctor_show_Relative.setVisibility(View.VISIBLE);
                doctor_hide_Relative.setVisibility(View.GONE);
                Add_doctor_btn.setVisibility(View.VISIBLE);
            } else if (doctorhide_show.equals(APIS.Edit)) {
                doctor_show_Relative.setVisibility(View.VISIBLE);
                doctor_hide_Relative.setVisibility(View.GONE);
                Add_doctor_btn.setVisibility(View.VISIBLE);
            }
        }else {
            doctor_show_Relative.setVisibility(View.VISIBLE);
            doctor_hide_Relative.setVisibility(View.GONE);
            Add_doctor_btn.setVisibility(View.VISIBLE);
        }
    }
}
