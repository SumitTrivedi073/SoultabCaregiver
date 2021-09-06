package com.soultabcaregiver.sendbird_group_call;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

public class GroupCallMessage {
	
	@SerializedName ("userIds") String userIds;
	
	@SerializedName ("channelUrl") String channelUrl;
	
	@SerializedName ("roomId") String roomId;
	
	@SerializedName ("groupName") String groupName;
	
	public GroupCallMessage(String userIds, String channelUrl, String roomId, String groupName) {
		this.userIds = userIds;
		this.channelUrl = channelUrl;
		this.roomId = roomId;
		this.groupName = groupName;
	}
	
	@NotNull
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
