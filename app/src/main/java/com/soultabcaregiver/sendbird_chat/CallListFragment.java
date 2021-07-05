package com.soultabcaregiver.sendbird_chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.soultabcaregiver.R;
import com.soultabcaregiver.sinch_calling.BaseFragment;

public class CallListFragment extends BaseFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_call_list, container, false);
	}
}
