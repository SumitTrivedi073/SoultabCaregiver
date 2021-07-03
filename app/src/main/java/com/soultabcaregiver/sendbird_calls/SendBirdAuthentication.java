package com.soultabcaregiver.sendbird_calls;

import android.content.Context;
import android.text.TextUtils;

import com.sendbird.android.SendBird;
import com.sendbird.calls.AuthenticateParams;
import com.sendbird.calls.SendBirdCall;
import com.soultabcaregiver.FireBaseMessaging.CustomFireBaseMessasing;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.sendbird_calls.utils.PrefUtils;
import com.soultabcaregiver.utils.Utility;


public class SendBirdAuthentication {
	
	private static final String TAG = "SendBirdAuthentication";
	
	public static void autoAuthenticate(Context context, AutoAuthenticateHandler handler) {
		if (SendBirdCall.getCurrentUser() != null) {
			if (handler != null) {
				handler.onResult(SendBirdCall.getCurrentUser().getUserId());
			}
			return;
		}
		
		String userId = Utility.getSharedPreferences(context, APIS.caregiver_id);
		String userName = Utility.getSharedPreferences(context, APIS.Caregiver_name);
		String fcmToken = Utility.getSharedPreferences(context, Utility.FCM_TOKEN);
		
		if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(fcmToken)) {
			SendBird.connect(userId, (user, e) -> {
				if (e != null) {
					handler.onResult(null);
				}
				SendBirdCall.authenticate(new AuthenticateParams(userId), (user1, e1) -> {
					if (e1 != null) {
						if (handler != null) {
							handler.onResult(null);
						}
						return;
					}
					SendBird.updateCurrentUserInfo(userName, "", e2 -> {
						handler.onResult(userId);
					});
				});
			});
		} else {
			if (handler != null) {
				handler.onResult(null);
			}
		}
	}
	
	public static void authenticate(Context context, String userId, String accessToken,
	                                String userName, AuthenticateHandler handler) {
		if (userId == null) {
			if (handler != null) {
				handler.onResult(false);
			}
			return;
		}
		
		deAuthenticate(context, isSuccess -> {
			//this is for chat
			SendBird.connect(userId, (user, e) -> {
				if (e != null) {
					if (handler != null) {
						handler.onResult(false);
					}
					return;
				}
				// this is for audio/video calls
				SendBirdCall.authenticate(
						new AuthenticateParams(userId).setAccessToken(accessToken),
						(user1, e1) -> {
							if (e1 != null) {
								if (handler != null) {
									handler.onResult(false);
								}
								return;
							}
							SendBird.updateCurrentUserInfo(userName, "", e2 -> {
								PrefUtils.setAppId(context, SendBirdCall.getApplicationId());
								PrefUtils.setUserId(context, userId);
								PrefUtils.setAccessToken(context, accessToken);
								handler.onResult(true);
							});
						});
			});
			
			
		});
	}
	
	public static void deAuthenticate(Context context, DeAuthenticateHandler handler) {
		if (SendBirdCall.getCurrentUser() == null) {
			if (handler != null) {
				handler.onResult(false);
			}
			return;
		}
		doDeAuthenticate(context, handler);
	}
	
	private static void doDeAuthenticate(Context context, DeAuthenticateHandler handler) {
		SendBirdCall.deauthenticate(e -> {
			PrefUtils.setUserId(context, null);
			PrefUtils.setAccessToken(context, null);
			PrefUtils.setCalleeId(context, null);
			PrefUtils.setPushToken(context, null);
			
			if (handler != null) {
				handler.onResult(e == null);
			}
		});
	}
	
	public static void logout(Context context, LogoutHandler handler) {
		SendBird.disconnect(() -> deAuthenticate(context, isSuccess -> {
			CustomFireBaseMessasing.getPushToken((pushToken, e) -> {
				unregisterPushToken(pushToken, e1 -> {
					handler.onResult(true);
				});
			});
		}));
	}
	
	private static void unregisterPushToken(String pushToken, CompletionHandler handler) {
		SendBird.unregisterPushTokenForCurrentUser(pushToken, e -> {
			if (e != null) {
				return;
			}
			SendBirdCall.unregisterPushToken(pushToken, handler :: onCompleted);
		});
	}
	
	
	public interface LogoutHandler {
		
		void onResult(boolean isSuccess);
	}
	
	public interface AutoAuthenticateHandler {
		
		void onResult(String userId);
	}
	
	public interface AuthenticateHandler {
		
		void onResult(boolean isSuccess);
	}
	
	public interface DeAuthenticateHandler {
		
		void onResult(boolean isSuccess);
	}
	
	public interface CompletionHandler {
		
		void onCompleted(Exception e);
	}
}
