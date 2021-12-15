package com.soultabcaregiver.activity.daily_routine.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.soultabcaregiver.Base.BaseFragment;
import com.soultabcaregiver.R;
import com.soultabcaregiver.activity.todotask.fragemets.TodoTaskListFragment;
import com.soultabcaregiver.utils.Utility;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class DailyRoutineOptionFragment extends BaseFragment {
	
	LinearLayout routineTask, todoTask;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init(view);
		listener();
	}
	
	private void init(View view) {
		routineTask = view.findViewById(R.id.routineTask);
		todoTask = view.findViewById(R.id.todoTask);
	}
	
	private void listener() {
		routineTask.setOnClickListener(
				view -> Utility.loadFragment(requireActivity(), new DailyRoutineFragment(), true,
						null));
		todoTask.setOnClickListener(
				view -> Utility.loadFragment(requireActivity(), new TodoTaskListFragment(), true,
						null));
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_daily_routine_option, container, false);
	}
	
}