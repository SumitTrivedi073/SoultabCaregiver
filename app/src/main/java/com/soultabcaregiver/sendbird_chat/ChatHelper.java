package com.soultabcaregiver.sendbird_chat;

import com.sendbird.android.GroupChannel;

import java.util.List;

public class ChatHelper {
	
	public static void createGroupChannel(List<String> userIds, boolean distinct,
	                                      GroupChannelCreateCallBack callBack) {
		GroupChannel.createChannelWithUserIds(userIds, distinct, (groupChannel, e) -> {
			if (e != null) {
				// Error!
				return;
			}
			callBack.onResult(groupChannel);
		});
	}
	
	public interface GroupChannelCreateCallBack {
		
		void onResult(GroupChannel groupChannel);
	}
	
}
