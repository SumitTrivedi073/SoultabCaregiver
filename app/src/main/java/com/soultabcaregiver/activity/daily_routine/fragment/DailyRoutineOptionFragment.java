package com.soultabcaregiver.activity.daily_routine.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.soultabcaregiver.Base.BaseFragment;
import com.soultabcaregiver.R;
import com.soultabcaregiver.activity.daily_routine.adapter.RoutineOptionsAdapter;
import com.soultabcaregiver.activity.daily_routine.model.DailyRoutineOptions;
import com.soultabcaregiver.activity.todotask.fragemets.TodoTaskListFragment;
import com.soultabcaregiver.sendbird_chat.ConversationFragment;
import com.soultabcaregiver.utils.GridSpacingItemDecorationUtils;
import com.soultabcaregiver.utils.Utility;

import java.util.ArrayList;


public class DailyRoutineOptionFragment extends BaseFragment {
	
	private static final int OPTION_DAILY_ROUTINE = 0;
	
	private static final int OPTION_TODO = 1;
	
	private RecyclerView rcvRoutineOptions;
	
	private ArrayList<DailyRoutineOptions> routineOptions = new ArrayList<>();
	
	private RoutineOptionsAdapter.OnRoutineOptionClickListener routineOptionListener =
			new RoutineOptionsAdapter.OnRoutineOptionClickListener() {
				@Override
				public void onRoutineItemClick(int position, DailyRoutineOptions options) {
					switch (options.getId()) {
						case OPTION_DAILY_ROUTINE:
							Utility.loadFragment(getActivity(), new DailyRoutineFragment(), true,
									DailyRoutineFragment.class.getSimpleName());
							break;
						case OPTION_TODO:
							Utility.loadFragment(getActivity(), new TodoTaskListFragment(),
									true, TodoTaskListFragment.class.getSimpleName());
							break;
					}
				}
			};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_daily_routine_option, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init(view);
		listener();
	}
	
	private void init(View view) {
		rcvRoutineOptions = view.findViewById(R.id.rcvRoutineOptions);
		setOptionsData();
	}
	
	private void listener() {
	}
	
	private void setOptionsData() {
		routineOptions = new ArrayList<>();
		routineOptions.add(new DailyRoutineOptions(OPTION_DAILY_ROUTINE, "Routine Task",
				R.drawable.ic_routine_task));
		routineOptions.add(
				new DailyRoutineOptions(OPTION_TODO, "To-Do Task", R.drawable.ic_to_do_task));
		rcvRoutineOptions.setLayoutManager(new GridLayoutManager(getActivity(), 2));
		rcvRoutineOptions.addItemDecoration(new GridSpacingItemDecorationUtils(2, 16, true));
		RoutineOptionsAdapter adapter =
				new RoutineOptionsAdapter(getActivity(), routineOptions, routineOptionListener);
		rcvRoutineOptions.setAdapter(adapter);
	}
	
}