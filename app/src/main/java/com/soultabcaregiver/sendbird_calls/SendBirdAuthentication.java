package com.soultabcaregiver.sendbird_calls;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.sendbird.android.SendBird;
import com.sendbird.calls.AuthenticateParams;
import com.sendbird.calls.SendBirdCall;
import com.soultabcaregiver.FireBaseMessaging.CustomFireBaseMessasing;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.sendbird_calls.utils.PrefUtils;
import com.soultabcaregiver.sendbird_chat.utils.PushUtils;
import com.soultabcaregiver.utils.Utility;

public class SendBirdAuthentication {
	
	private static final String TAG = "SendBirdAuthentication";
	
	public static void autoAuthenticate(Context context, AutoAuthenticateHandler handler) {
		String userId = Utility.getSharedPreferences(context, APIS.caregiver_id);
		String userName = Utility.getSharedPreferences(context, APIS.Caregiver_name);
		String fcmToken = Utility.getSharedPreferences(context, Utility.FCM_TOKEN);
		
		if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(fcmToken)) {
			SendBird.connect(userId, (user, e) -> {
				if (e != null) {
					Log.e(TAG, "AutoAuthenticate Connect Failed " + e.getMessage());
					handler.onResult(null);
					return;
				}
				SendBirdCall.authenticate(new AuthenticateParams(userId), (user1, e1) -> {
					if (e1 != null) {
						Log.e(TAG, "AutoAuthenticate Auth Failed " + e1.getMessage());
						if (handler != null) {
							handler.onResult(null);
						}
						return;
					}
					registerPushToken(e2 -> {
						if (e2 != null) {
							Log.e(TAG, "AutoAuthenticate registerPush Failed " + e2.getMessage());
							
						}
						SendBird.updateCurrentUserInfo(userName, "", e3 -> {
							if (e3 != null) {
								Log.e(TAG,
										"AutoAuthenticate UpdateCurrentUser Failed " + e3.getMessage());
							}
							
							handler.onResult(userId);
						});
					});
					
				});
			});
		} else {
			if (handler != null) {
				handler.onResult(null);
			}
		}
	}
	
	public static void registerPushToken(SendBirdAuthentication.CompletionHandler handler) {
		PushUtils.registerPushHandler(new CustomFireBaseMessasing());
		handler.onCompleted(null);
		//		SendBird.registerPushTokenForCurrentUser(pushToken, (pushTokenRegistrationStatus,
		//		e) -> {
		//			if (e != null) {
		//				handler.onCompleted(e);
		//				return;
		//			}
		//			SendBirdCall.registerPushToken(pushToken, false, handler :: onCompleted);
		//		});
	}
	
	public static void authenticate(Context context, String userId, String userName,
	                                AuthenticateHandler handler) {
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
				SendBirdCall.authenticate(new AuthenticateParams(userId), (user1, e1) -> {
					if (e1 != null) {
						if (handler != null) {
							handler.onResult(false);
						}
						return;
					}
					registerPushToken(e2 -> {
						SendBird.updateCurrentUserInfo(userName, "", e3 -> {
							PrefUtils.setAppId(context, SendBirdCall.getApplicationId());
							PrefUtils.setUserId(context, userId);
							handler.onResult(true);
						});
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
			PrefUtils.setCalleeId(context, null);
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
