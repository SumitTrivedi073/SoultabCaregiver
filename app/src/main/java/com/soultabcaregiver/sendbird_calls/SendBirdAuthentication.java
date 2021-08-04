package com.soultabcaregiver.sendbird_calls;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.SendBirdPushHelper;
import com.sendbird.calls.AuthenticateParams;
import com.sendbird.calls.SendBirdCall;
import com.soultabcaregiver.FireBaseMessaging.CustomFireBaseMessaging;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.sendbird_calls.utils.PrefUtils;
import com.soultabcaregiver.sendbird_chat.utils.PushUtils;
import com.soultabcaregiver.utils.Utility;

public class SendBirdAuthentication {
	
	private static final String TAG = "SendBirdAuthentication";
	
	public static void autoAuthenticate(Context context, AutoAuthenticateHandler handler) {
		String userId = Utility.getSharedPreferences(context, APIS.caregiver_id);
		String userName = Utility.getSharedPreferences(context, APIS.Caregiver_name);
		String profilePic = Utility.getSharedPreferences(context, APIS.profile_image);
		String fcmToken = PrefUtils.getPushToken();
		
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
					registerPushToken(fcmToken, e2 -> {
						if (e2 != null) {
							Log.e(TAG, "AutoAuthenticate registerPush Failed " + e2.getMessage());
							
						}
						SendBird.updateCurrentUserInfo(userName, profilePic, e3 -> {
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
	
	public static void registerPushToken(String fcmToken,
	                                     SendBirdAuthentication.CompletionHandler handler) {
		PushUtils.registerPushHandler(new CustomFireBaseMessaging());
		SendBirdCall.registerPushToken(fcmToken, false, handler :: onCompleted);
		//		SendBird.registerPushTokenForCurrentUser(pushToken, (pushTokenRegistrationStatus,
		//		e) -> {
		//			if (e != null) {
		//				handler.onCompleted(e);
		//				return;
		//			}
		//
		//		});
	}
	
	public static void authenticate(Context context, String userId, String userName,
	                                String profileUrl, AuthenticateHandler handler) {
		
		String fcmToken = PrefUtils.getPushToken();
		
		if (userId == null) {
			if (handler != null) {
				handler.onResult(false);
			}
			return;
		}
		
		deAuthenticate(isSuccess -> {
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
					registerPushToken(fcmToken, e2 -> {
						SendBird.updateCurrentUserInfo(userName, profileUrl, e3 -> {
							PrefUtils.setAppId(context, SendBirdCall.getApplicationId());
							PrefUtils.setUserId(context, userId);
							handler.onResult(true);
						});
					});
				});
			});
			
			
		});
	}
	
	private static void deAuthenticate(DeAuthenticateHandler handler) {
		if (SendBirdCall.getCurrentUser() == null) {
			if (handler != null) {
				handler.onResult(false);
			}
			return;
		}
		doDeAuthenticate(handler);
	}
	
	private static void doDeAuthenticate(DeAuthenticateHandler handler) {
		SendBirdCall.deauthenticate(e -> {
			if (handler != null) {
				handler.onResult(e == null);
			}
		});
	}
	
	public static void logout(LogoutHandler handler) {
		CustomFireBaseMessaging.getPushToken((pushToken, e) -> {
			unregisterPushToken(pushToken, e1 -> {
				deAuthenticate(isSuccess -> {
					SendBird.disconnect(() -> handler.onResult(true));
				});
			});
		});
	}
	
	private static void unregisterPushToken(String pushToken, CompletionHandler handler) {
		PushUtils.unregisterPushHandler(new SendBirdPushHelper.OnPushRequestCompleteListener() {
			@Override
			public void onComplete(boolean b, String s) {
				SendBirdCall.unregisterPushToken(pushToken, handler :: onCompleted);
			}
			
			@Override
			public void onError(SendBirdException e) {
				handler.onCompleted(e);
			}
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
