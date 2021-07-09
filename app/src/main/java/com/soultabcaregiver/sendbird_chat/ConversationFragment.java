package com.soultabcaregiver.sendbird_chat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.sendbird.android.AdminMessage;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.UserMessage;
import com.soultabcaregiver.Base.BaseFragment;
import com.soultabcaregiver.R;
import com.soultabcaregiver.WebService.APIS;
import com.soultabcaregiver.sendbird_calls.SendbirdCallService;
import com.soultabcaregiver.sendbird_chat.utils.ConnectionManager;
import com.soultabcaregiver.sendbird_chat.utils.FileUtils;
import com.soultabcaregiver.sendbird_chat.utils.TextUtils;
import com.soultabcaregiver.sendbird_chat.utils.UrlPreviewInfo;
import com.soultabcaregiver.sendbird_chat.utils.WebUtils;
import com.soultabcaregiver.utils.Utility;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ConversationFragment extends BaseFragment {
	
	private static final int INTENT_REQUEST_CHOOSE_MEDIA = 301;
	
	private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 13;
	
	private static final String CONNECTION_HANDLER_ID = "CONNECTION_HANDLER_GROUP_CHAT";
	
	private static final String CHANNEL_HANDLER_ID = "CHANNEL_HANDLER_GROUP_CHANNEL_CHAT";
	
	private static final int CHANNEL_LIST_LIMIT = 30;
	
	public static final String EXTRA_GROUP_CHANNEL_URL = "GROUP_CHANNEL_URL";
	
	public static final String EXTRA_CALLEE_ID = "EXTRA_CALLEE_ID";
	
	private InputMethodManager mIMM;
	
	private GroupChannel mChannel;
	
	private String mChannelUrl;
	
	private String mCalleeId;
	
	private ConstraintLayout mRootLayout;
	
	private RecyclerView mRecyclerView;
	
	private GroupChatAdapter mChatAdapter;
	
	private LinearLayoutManager mLayoutManager;
	
	private EmojiEditText mMessageEditText;
	
	private ImageView mMessageSendButton;
	
	private TextView titleTextView;
	
	private RelativeLayout backButton;
	//	private ImageButton mUploadFileButton;
	
	private boolean mIsTyping;
	
	private EmojiPopup emojiPopup;
	
	private ImageView smileyBtn, attachmentBtn, galleryBtn, cameraBtn, microPhoneBtn, alertBtn;
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		// Set this as true to restore background connection management.
		SendBird.setAutoBackgroundDetection(true);
		
		if (requestCode == INTENT_REQUEST_CHOOSE_MEDIA && resultCode == Activity.RESULT_OK) {
			// If user has successfully chosen the image, show a dialog to confirm upload.
			if (data == null) {
				return;
			}
			
			sendFileWithThumbnail(data.getData());
		}
	}
	
	@Override
	public void onViewCreated(@NonNull @NotNull View view,
	                          @Nullable @org.jetbrains.annotations.Nullable
			                          Bundle savedInstanceState) {
		
		mIMM = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		
		// Get channel URL from GroupChannelListFragment.
		mChannelUrl = getArguments().getString(EXTRA_GROUP_CHANNEL_URL);
		
		mCalleeId = getArguments().getString(EXTRA_CALLEE_ID);
		
		backButton.setOnClickListener(v -> {
			getActivity().onBackPressed();
		});
		
		mMessageEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				mMessageSendButton.setEnabled(s.length() > 0);
			}
		});
		
		mMessageSendButton.setEnabled(false);
		mMessageSendButton.setOnClickListener(v -> {
			//				if (mCurrentState == STATE_EDIT) {
			//					String userInput = mMessageEditText.getText().toString();
			//					if (userInput.length() > 0) {
			//						if (mEditingMessage != null) {
			//							editMessage(mEditingMessage, userInput);
			//						}
			//					}
			//					setState(STATE_NORMAL, null, -1);
			//				} else {
			String userInput = mMessageEditText.getText().toString();
			if (userInput.length() > 0) {
				sendUserMessage(userInput);
				mMessageEditText.setText("");
			}
			//				}
		});
		
		//		mUploadFileButton.setOnClickListener(new View.OnClickListener() {
		//			@Override
		//			public void onClick(View v) {
		//				requestMedia();
		//			}
		//		});
		
		mChatAdapter = new GroupChatAdapter(getContext());
		setUpChatListAdapter();
		
		// Load messages from cache.
		mChatAdapter.load(mChannelUrl);
		
		setUpRecyclerView();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		ConnectionManager.addConnectionManagementHandler(
				Utility.getSharedPreferences(getContext(), APIS.caregiver_id),
				CONNECTION_HANDLER_ID, reconnect -> refresh());
		
		mChatAdapter.setContext(getContext());
		
		// Gets channel from URL user requested
		
		SendBird.addChannelHandler(CHANNEL_HANDLER_ID, new SendBird.ChannelHandler() {
			@Override
			public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
				if (baseChannel.getUrl().equals(mChannelUrl)) {
					mChatAdapter.markAllMessagesAsRead();
					// Add new message to view
					mChatAdapter.addFirst(baseMessage);
					//TODO this is for Sumit to save Images in chat
					if (baseMessage instanceof FileMessage) {
					
					}
				}
			}
			
			@Override
			public void onMessageDeleted(BaseChannel baseChannel, long msgId) {
				super.onMessageDeleted(baseChannel, msgId);
				if (baseChannel.getUrl().equals(mChannelUrl)) {
					mChatAdapter.delete(msgId);
				}
			}
			
			@Override
			public void onMessageUpdated(BaseChannel channel, BaseMessage message) {
				super.onMessageUpdated(channel, message);
				if (channel.getUrl().equals(mChannelUrl)) {
					mChatAdapter.update(message);
				}
			}
			
			@Override
			public void onReadReceiptUpdated(GroupChannel channel) {
				if (channel.getUrl().equals(mChannelUrl)) {
					mChatAdapter.notifyDataSetChanged();
				}
			}
			
			@Override
			public void onDeliveryReceiptUpdated(GroupChannel channel) {
				if (channel.getUrl().equals(mChannelUrl)) {
					mChatAdapter.notifyDataSetChanged();
				}
			}
			
			@Override
			public void onTypingStatusUpdated(GroupChannel channel) {
				if (channel.getUrl().equals(mChannelUrl)) {
					List<Member> typingUsers = channel.getTypingMembers();
					//					displayTyping(typingUsers);
				}
			}
		});
	}
	
	@Override
	public void onPause() {
		setTypingStatus(false);
		Utility.hideKeyboard(getContext());
		emojiPopup.dismiss();
		super.onPause();
	}
	
	/**
	 * Notify other users whether the current user is typing.
	 *
	 * @param typing Whether the user is currently typing.
	 */
	private void setTypingStatus(boolean typing) {
		if (mChannel == null) {
			return;
		}
		
		if (typing) {
			mIsTyping = true;
			mChannel.startTyping();
		} else {
			mIsTyping = false;
			mChannel.endTyping();
		}
	}
	
	@Override
	public void onDestroy() {
		// Save messages to cache.
		mChatAdapter.save();
		ConnectionManager.removeConnectionManagementHandler(CONNECTION_HANDLER_ID);
		SendBird.removeChannelHandler(CHANNEL_HANDLER_ID);
		super.onDestroy();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.video_call) {
			SendbirdCallService.dial(getContext(), mCalleeId, null, true, true, mChannelUrl);
			//			RoomParams params = new RoomParams(RoomType.SMALL_ROOM_FOR_VIDEO);
			//			SendBirdCall.createRoom(params, (room, e) -> {
			//				if (room == null || e != null) {
			//					return;
			//				}
			//				room.enter(new EnterParams().setAudioEnabled(true).setVideoEnabled
			//				(true), e1 -> {
			//
			//				});
			//			});
			return true;
		} else if (itemId == android.R.id.home) {
			getActivity().onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void refresh() {
		if (mChannel == null) {
			GroupChannel.getChannel(mChannelUrl, new GroupChannel.GroupChannelGetHandler() {
				@Override
				public void onResult(GroupChannel groupChannel, SendBirdException e) {
					if (e != null) {
						// Error!
						e.printStackTrace();
						return;
					}
					
					mChannel = groupChannel;
					mChatAdapter.setChannel(mChannel);
					mChatAdapter.loadLatestMessages(CHANNEL_LIST_LIMIT,
							new BaseChannel.GetMessagesHandler() {
								@Override
								public void onResult(List<BaseMessage> list, SendBirdException e) {
									mChatAdapter.markAllMessagesAsRead();
								}
							});
					updateActionBarTitle();
				}
			});
		} else {
			mChannel.refresh(new GroupChannel.GroupChannelRefreshHandler() {
				@Override
				public void onResult(SendBirdException e) {
					if (e != null) {
						// Error!
						e.printStackTrace();
						return;
					}
					
					mChatAdapter.loadLatestMessages(CHANNEL_LIST_LIMIT,
							new BaseChannel.GetMessagesHandler() {
								@Override
								public void onResult(List<BaseMessage> list, SendBirdException e) {
									mChatAdapter.markAllMessagesAsRead();
								}
							});
					updateActionBarTitle();
				}
			});
		}
	}
	
	private void updateActionBarTitle() {
		String title = "";
		
		if (mChannel != null) {
			title = TextUtils.getGroupChannelTitle(mChannel);
		}
		
		// Set action bar title to name of channel
		titleTextView.setText(title);
	}
	
	private void sendUserMessage(String text) {
		if (mChannel == null) {
			return;
		}
		
		List<String> urls = WebUtils.extractUrls(text);
		if (urls.size() > 0) {
			sendUserMessageWithUrl(text, urls.get(0));
			return;
		}
		
		UserMessage tempUserMessage = mChannel.sendUserMessage(text, (userMessage, e) -> {
			if (e != null) {
				// Error!
				Toast.makeText(getContext(),
						"Send failed with error " + e.getCode() + ": " + e.getMessage(),
						Toast.LENGTH_SHORT).show();
				mChatAdapter.markMessageFailed(userMessage);
				return;
			}
			
			// Update a sent message to RecyclerView
			mChatAdapter.markMessageSent(userMessage);
		});
		
		// Display a user message to RecyclerView
		mChatAdapter.addFirst(tempUserMessage);
	}
	
	private void setUpChatListAdapter() {
		mChatAdapter.setItemClickListener(new GroupChatAdapter.OnItemClickListener() {
			@Override
			public void onUserMessageItemClick(UserMessage message) {
				// Restore failed message and remove the failed message from list.
				if (mChatAdapter.isFailedMessage(message)) {
					//retryFailedMessage(message);
					return;
				}
				
				// Message is sending. Do nothing on click event.
				if (mChatAdapter.isTempMessage(message)) {
					return;
				}
				
				if (message.getCustomType().equals(GroupChatAdapter.URL_PREVIEW_CUSTOM_TYPE)) {
					try {
						UrlPreviewInfo info = new UrlPreviewInfo(message.getData());
						Intent browserIntent =
								new Intent(Intent.ACTION_VIEW, Uri.parse(info.getUrl()));
						startActivity(browserIntent);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			
			@Override
			public void onFileMessageItemClick(FileMessage message) {
				// Load media chooser and remove the failed message from list.
				if (mChatAdapter.isFailedMessage(message)) {
					//					retryFailedMessage(message);
					return;
				}
				
				// Message is sending. Do nothing on click event.
				if (mChatAdapter.isTempMessage(message)) {
					return;
				}
				
				onFileMessageClicked(message);
			}
		});
		
		mChatAdapter.setItemLongClickListener(new GroupChatAdapter.OnItemLongClickListener() {
			@Override
			public void onUserMessageItemLongClick(UserMessage message, int position) {
				//				if (message.getSender().getUserId().equals(PreferenceUtils
				//				.getUserId())) {
				//					showMessageOptionsDialog(message, position);
				//				}
			}
			
			@Override
			public void onFileMessageItemLongClick(FileMessage message) {
			}
			
			@Override
			public void onAdminMessageItemLongClick(AdminMessage message) {
			}
		});
	}
	
	private void setUpRecyclerView() {
		mLayoutManager = new LinearLayoutManager(getContext());
		mLayoutManager.setReverseLayout(true);
		mRecyclerView.setLayoutManager(mLayoutManager);
		mRecyclerView.setAdapter(mChatAdapter);
		mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				if (mLayoutManager.findLastVisibleItemPosition() == mChatAdapter.getItemCount() - 1) {
					mChatAdapter.loadPreviousMessages(CHANNEL_LIST_LIMIT, null);
				}
			}
		});
	}
	
	@SuppressLint ("StaticFieldLeak")
	private void sendUserMessageWithUrl(final String text, String url) {
		if (mChannel == null) {
			return;
		}
		
		new WebUtils.UrlPreviewAsyncTask() {
			
			@Override
			protected void onPostExecute(UrlPreviewInfo info) {
				if (mChannel == null) {
					return;
				}
				
				UserMessage tempUserMessage = null;
				BaseChannel.SendUserMessageHandler handler =
						new BaseChannel.SendUserMessageHandler() {
							@Override
							public void onSent(UserMessage userMessage, SendBirdException e) {
								if (e != null) {
									// Error!
									
									Toast.makeText(getContext(),
											"Send failed with error " + e.getCode() + ": " + e.getMessage(),
											Toast.LENGTH_SHORT).show();
									
									mChatAdapter.markMessageFailed(userMessage);
									return;
								}
								
								// Update a sent message to RecyclerView
								mChatAdapter.markMessageSent(userMessage);
							}
						};
				
				try {
					// Sending a message with URL preview information and custom type.
					String jsonString = info.toJsonString();
					tempUserMessage = mChannel.sendUserMessage(text, jsonString,
							GroupChatAdapter.URL_PREVIEW_CUSTOM_TYPE, handler);
				} catch (Exception e) {
					// Sending a message without URL preview information.
					tempUserMessage = mChannel.sendUserMessage(text, handler);
				}
				
				// Display a user message to RecyclerView
				mChatAdapter.addFirst(tempUserMessage);
			}
		}.execute(url);
	}
	
	private void onFileMessageClicked(FileMessage message) {
		String type = message.getType().toLowerCase();
		//		if (type.startsWith("image")) {
		//			Intent i = new Intent(this, PhotoViewerActivity.class);
		//			i.putExtra("url", message.getUrl());
		//			i.putExtra("type", message.getType());
		//			startActivity(i);
		//		} else if (type.startsWith("video")) {
		//			Intent intent = new Intent(this, MediaPlayerActivity.class);
		//			intent.putExtra("url", message.getUrl());
		//			startActivity(intent);
		//		} else {
		//			showDownloadConfirmDialog(message);
		//		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_conversation, container, false);
		setupControls(view);
		return view;
	}
	
	//	@Override
	//	public boolean onCreateOptionsMenu(Menu menu) {
	//		MenuInflater inflater = getMenuInflater();
	//		inflater.inflate(R.menu.conversation_menu, menu);
	//		return true;
	//	}
	
	private void setupControls(View view) {
		mRootLayout = view.findViewById(R.id.layout_group_chat_root);
		mRecyclerView = view.findViewById(R.id.recycler_group_chat);
		mMessageEditText = view.findViewById(R.id.edittext_group_chat_message);
		mMessageSendButton = view.findViewById(R.id.button_group_chat_send);
		//		mUploadFileButton = findViewById(R.id.button_group_chat_upload);
		titleTextView = view.findViewById(R.id.chatTitle);
		backButton = view.findViewById(R.id.back_btn);
		
		smileyBtn = view.findViewById(R.id.smileyBtn);
		attachmentBtn = view.findViewById(R.id.attachmentBtn);
		galleryBtn = view.findViewById(R.id.galleryBtn);
		cameraBtn = view.findViewById(R.id.cameraBtn);
		microPhoneBtn = view.findViewById(R.id.microPhoneBtn);
		alertBtn = view.findViewById(R.id.alertBtn);
		
		emojiPopup = EmojiPopup.Builder.fromRootView(mRootLayout).setOnSoftKeyboardOpenListener(
				keyBoardHeight -> {
				
				}).setOnSoftKeyboardCloseListener(() -> {
			
		}).build(mMessageEditText);
		smileyBtn.setOnClickListener(v -> {
			emojiPopup.toggle();
		});
		
	}
	
	public static ConversationFragment newInstance(String channelUrl) {
		Bundle args = new Bundle();
		ConversationFragment fragment = new ConversationFragment();
		args.putString(EXTRA_GROUP_CHANNEL_URL, channelUrl);
		fragment.setArguments(args);
		return fragment;
	}
	
	/**
	 * Sends a File Message containing an image file. Also requests thumbnails to be generated in
	 * specified sizes.
	 *
	 * @param uri The URI of the image, which in this case is received through an Intent request.
	 */
	private void sendFileWithThumbnail(Uri uri) {
		if (mChannel == null) {
			return;
		}
		
		// Specify two dimensions of thumbnails to generate
		List<FileMessage.ThumbnailSize> thumbnailSizes = new ArrayList<>();
		thumbnailSizes.add(new FileMessage.ThumbnailSize(240, 240));
		thumbnailSizes.add(new FileMessage.ThumbnailSize(320, 320));
		
		Hashtable<String, Object> info = FileUtils.getFileInfo(getContext(), uri);
		
		if (info == null || info.isEmpty()) {
			Toast.makeText(getContext(), "Extracting file information failed.",
					Toast.LENGTH_LONG).show();
			return;
		}
		
		final String name;
		if (info.containsKey("name")) {
			name = (String) info.get("name");
		} else {
			name = "Sendbird File";
		}
		final String path = (String) info.get("path");
		final File file = new File(path);
		final String mime = (String) info.get("mime");
		final int size = (Integer) info.get("size");
		
		if (path == null || path.equals("")) {
			Toast.makeText(getContext(), "File must be located in local storage.",
					Toast.LENGTH_LONG).show();
		} else {
			BaseChannel.SendFileMessageHandler fileMessageHandler =
					new BaseChannel.SendFileMessageHandler() {
						@Override
						public void onSent(FileMessage fileMessage, SendBirdException e) {
							if (e != null) {
								Toast.makeText(getContext(),
										"" + e.getCode() + ":" + e.getMessage(),
										Toast.LENGTH_SHORT).show();
								mChatAdapter.markMessageFailed(fileMessage);
								return;
							}
							
							mChatAdapter.markMessageSent(fileMessage);
						}
					};
			
			// Send image with thumbnails in the specified dimensions
			FileMessage tempFileMessage =
					mChannel.sendFileMessage(file, name, mime, size, "", null, thumbnailSizes,
							fileMessageHandler);
			
			mChatAdapter.addTempFileMessageInfo(tempFileMessage, uri);
			mChatAdapter.addFirst(tempFileMessage);
		}
	}
	
	public void showSoftKeyboard(View view) {
		if (view.requestFocus()) {
			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
					Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
		}
	}
	
	private void requestMedia() {
		if (ContextCompat.checkSelfPermission(getContext(),
				Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			// If storage permissions are not granted, request permissions at run-time,
			// as per < API 23 guidelines.
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				requestStoragePermissions();
			}
		} else {
			Intent intent = new Intent();
			
			intent.setType("*/*");
			
			intent.setAction(Intent.ACTION_GET_CONTENT);
			
			// Always show the chooser (if there are multiple options available)
			startActivityForResult(Intent.createChooser(intent, "Select Media"),
					INTENT_REQUEST_CHOOSE_MEDIA);
			
			// Set this as false to maintain connection
			// even when an external Activity is started.
			SendBird.setAutoBackgroundDetection(false);
		}
	}
	
	//	/**
	//	 * Display which users are typing.
	//	 * If more than two users are currently typing, this will state that "multiple users" are
	//	 typing.
	//	 *
	//	 * @param typingUsers The list of currently typing users.
	//	 */
	//	private void displayTyping(List<Member> typingUsers) {
	//
	//		if (typingUsers.size() > 0) {
	//			mCurrentEventLayout.setVisibility(View.VISIBLE);
	//			String string;
	//
	//			if (typingUsers.size() == 1) {
	//				string = String.format(getString(R.string.user_typing), typingUsers.get(0)
	//				.getNickname());
	//			} else if (typingUsers.size() == 2) {
	//				string = String.format(getString(R.string.two_users_typing), typingUsers.get
	//				(0).getNickname(), typingUsers.get(1).getNickname());
	//			} else {
	//				string = getString(R.string.users_typing);
	//			}
	//			mCurrentEventText.setText(string);
	//		} else {
	//			mCurrentEventLayout.setVisibility(View.GONE);
	//		}
	//	}
	
	@RequiresApi (api = Build.VERSION_CODES.M)
	private void requestStoragePermissions() {
		if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
				Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
			// Provide an additional rationale to the user if the permission was not granted
			// and the user would benefit from additional context for the use of the permission.
			// For example if the user has previously denied the permission.
			Snackbar.make(mRootLayout,
					"Storage access permissions are required to upload/download files.",
					Snackbar.LENGTH_LONG).setAction("Okay", new View.OnClickListener() {
				
				@Override
				public void onClick(View view) {
					requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
							PERMISSION_WRITE_EXTERNAL_STORAGE);
				}
			}).show();
		} else {
			// Permission has not been granted yet. Request it directly.
			requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
					PERMISSION_WRITE_EXTERNAL_STORAGE);
		}
	}
	
	private void showDownloadConfirmDialog(final FileMessage message) {
		if (ContextCompat.checkSelfPermission(getContext(),
				Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			// If storage permissions are not granted, request permissions at run-time,
			// as per < API 23 guidelines.
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				requestStoragePermissions();
			}
		} else {
			new AlertDialog.Builder(getContext()).setMessage("Download file?").setPositiveButton(
					R.string.download, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == DialogInterface.BUTTON_POSITIVE) {
								FileUtils.downloadFile(getContext(), message.getUrl(),
										message.getName());
							}
						}
					}).setNegativeButton(R.string.cancel_text, null).show();
		}
		
	}
	
}
