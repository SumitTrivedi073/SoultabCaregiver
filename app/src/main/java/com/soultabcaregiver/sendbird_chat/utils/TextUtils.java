package com.soultabcaregiver.sendbird_chat.utils;

import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.android.SendBird;
import com.sendbird.android.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class TextUtils {
	
	public static String getGroupChannelTitle(GroupChannel channel) {
		List<Member> members = channel.getMembers();
		
		if (members.size() == 2) {
			StringBuilder names = new StringBuilder();
			for (Member member : members) {
				if (member.getUserId().equals(SendBird.getCurrentUser().getUserId())) {
					continue;
				}
				
				names.append(", ").append(member.getNickname());
			}
			return names.delete(0, 2).toString();
		} else {
			
			if (!channel.getName().isEmpty()) {
				return channel.getName();
			}
			
			int count = 0;
			StringBuilder names = new StringBuilder();
			for (User member : members) {
				if (member.getUserId().equals(SendBird.getCurrentUser().getUserId())) {
					continue;
				}
				
				count++;
				names.append(", ").append(member.getNickname());
				
				if (count >= 10) {
					break;
				}
			}
			return names.delete(0, 2).toString();
		}
	}
	
	/**
	 * Calculate MD5
	 *
	 * @param data
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String generateMD5(String data) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		digest.update(data.getBytes());
		byte messageDigest[] = digest.digest();
		
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < messageDigest.length; i++)
			hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
		
		return hexString.toString();
	}
	
	public static boolean isEmpty(CharSequence text) {
		return text == null || text.length() == 0;
	}
}
