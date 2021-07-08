package com.soultabcaregiver.sendbird_chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.soultabcaregiver.Base.BaseFragment;
import com.soultabcaregiver.R;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

public class ChatFragment extends BaseFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_chat, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull @NotNull View view,
	                          @Nullable @org.jetbrains.annotations.Nullable
			                          Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		transaction.add(R.id.container, new ChatListFragment());
		transaction.commit();
	}
	
	public void navigateToCreateGroupFragment() {
		FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
		transaction.add(R.id.container, CreateGroupFragment.newInstance());
		transaction.addToBackStack(CreateGroupFragment.class.getName());
		transaction.commit();
	}
}
