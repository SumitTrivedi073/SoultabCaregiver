package com.soultabcaregiver.activity.docter.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.soultabcaregiver.R;
import com.soultabcaregiver.activity.docter.AddDoctorActivity;
import com.soultabcaregiver.sinch_calling.BaseFragment;
import com.soultabcaregiver.utils.NonSwipeableViewPager;

import java.util.ArrayList;
import java.util.List;

public class DoctorFragment extends BaseFragment {

    View view;
    Context mContext;
    ViewPagerAdapter adapter;
    private Toolbar toolbar_main;
    private TabLayout tabLayout;
    private NonSwipeableViewPager viewPager;
    private TextView tab_txt;
    private FloatingActionButton Add_doctor_btn;

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

        Add_doctor_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AddDoctorActivity.class);
                startActivity(intent);
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

}
