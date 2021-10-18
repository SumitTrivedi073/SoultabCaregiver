package com.soultabcaregiver.search;

import android.os.Bundle;

import com.soultabcaregiver.Base.BaseActivity;
import com.soultabcaregiver.R;

import androidx.fragment.app.FragmentTransaction;

public class PendingRequestsActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_activity);
		
		addPendingRequestFragment();
	}
	
	private void addPendingRequestFragment() {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.container, PendingRequestsFragment.newInstance());
		transaction.commit();
	}
	
	
}
