package com.soultabcaregiver.sendbird_chat;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sendbird.android.AdminMessage;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.android.SendBird;
import com.sendbird.android.UserMessage;
import com.soultabcaregiver.R;
import com.soultabcaregiver.sendbird_chat.utils.DateUtils;
import com.soultabcaregiver.sendbird_chat.utils.FileUtils;
import com.soultabcaregiver.sendbird_chat.utils.ImageUtils;
import com.soultabcaregiver.sendbird_chat.utils.TextUtils;
import com.soultabcaregiver.sendbird_chat.utils.TypingIndicator;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

class ChatListAdapter extends RecyclerView.Adapter<ChatListViewHolder> {
	
	private final Context mContext;
	
	private List<GroupChannel> mChannelList = new ArrayList<>();
	
	private OnItemClickListener mItemClickListener;
	
	private OnItemLongClickListener mItemLongClickListener;
	
	public ChatListAdapter(Context context) {
		this.mContext = context;
	}
	
	@NotNull
	@Override
	public ChatListViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent,
	                                             int viewType) {
		View view =
				LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_group_channel,
						parent, false);
		return new ChatListViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(@NonNull @NotNull ChatListViewHolder holder, int position) {
		holder.bind(mContext, mChannelList.get(position), mItemClickListener,
				mItemLongClickListener);
	}
	
	@Override
	public int getItemCount() {
		return mChannelList.size();
	}
	
	void addLast(GroupChannel channel) {
		mChannelList.add(channel);
		notifyItemInserted(mChannelList.size() - 1);
	}
	
	public void setChannels(List<GroupChannel> list) {
		mChannelList = list;
		notifyDataSetChanged();
	}
	
	// If the channel is not in the list yet, adds it.
	// If it is, finds the channel in current list, and replaces it.
	// Moves the updated channel to the front of the list.
	void updateOrInsert(BaseChannel channel) {
		if (!(channel instanceof GroupChannel)) {
			return;
		}
		
		GroupChannel groupChannel = (GroupChannel) channel;
		
		for (int i = 0; i < mChannelList.size(); i++) {
			if (mChannelList.get(i).getUrl().equals(groupChannel.getUrl())) {
				mChannelList.remove(mChannelList.get(i));
				mChannelList.add(0, groupChannel);
				notifyDataSetChanged();
				Log.v(ChatListAdapter.class.getSimpleName(), "Channel replaced.");
				return;
			}
		}
		
		mChannelList.add(0, groupChannel);
		notifyDataSetChanged();
	}
	
	public void load() {
		try {
			File appDir = new File(mContext.getCacheDir(), SendBird.getApplicationId());
			appDir.mkdirs();
			
			File dataFile = new File(appDir, TextUtils.generateMD5(
					SendBird.getCurrentUser().getUserId() + "channel_list") + ".data");
			
			String content = FileUtils.loadFromFile(dataFile);
			String[] dataArray = content.split("\n");
			
			// Reset channel list, then add cached data.
			mChannelList.clear();
			for (int i = 0; i < dataArray.length; i++) {
				mChannelList.add((GroupChannel) BaseChannel.buildFromSerializedData(
						Base64.decode(dataArray[i], Base64.DEFAULT | Base64.NO_WRAP)));
			}
			
			notifyDataSetChanged();
		} catch (Exception e) {
			// Nothing to load.
		}
	}
	
	public void save() {
		try {
			StringBuilder sb = new StringBuilder();
			
			// Save the data into file.
			File appDir = new File(mContext.getCacheDir(), SendBird.getApplicationId());
			appDir.mkdirs();
			
			File hashFile = new File(appDir, TextUtils.generateMD5(
					SendBird.getCurrentUser().getUserId() + "channel_list") + ".hash");
			File dataFile = new File(appDir, TextUtils.generateMD5(
					SendBird.getCurrentUser().getUserId() + "channel_list") + ".data");
			
			if (mChannelList != null && mChannelList.size() > 0) {
				// Convert current data into string.
				GroupChannel channel = null;
				for (int i = 0; i < Math.min(mChannelList.size(), 100); i++) {
					channel = mChannelList.get(i);
					sb.append("\n");
					sb.append(Base64.encodeToString(channel.serialize(),
							Base64.DEFAULT | Base64.NO_WRAP));
				}
				// Remove first newline.
				sb.delete(0, 1);
				
				String data = sb.toString();
				String md5 = TextUtils.generateMD5(data);
				
				try {
					String content = FileUtils.loadFromFile(hashFile);
					// If data has not been changed, do not save.
					if (md5.equals(content)) {
						return;
					}
				} catch (IOException e) {
					// File not found. Save the data.
				}
				
				FileUtils.saveToFile(dataFile, data);
				FileUtils.saveToFile(hashFile, md5);
			} else {
				FileUtils.deleteFile(dataFile);
				FileUtils.deleteFile(hashFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void setOnItemClickListener(OnItemClickListener listener) {
		mItemClickListener = listener;
	}
	
	void setOnItemLongClickListener(OnItemLongClickListener listener) {
		mItemLongClickListener = listener;
	}
	
	interface OnItemClickListener {
		
		void onItemClick(GroupChannel channel);
	}
	
	interface OnItemLongClickListener {
		
		void onItemLongClick(GroupChannel channel);
	}
	
	
}

class ChatListViewHolder extends RecyclerView.ViewHolder {
	
	TextView channelName, lastMessageText, unreadCountText, dateText;
	
	ImageView coverImage;
	
	LinearLayout typingIndicatorContainer;
	
	public ChatListViewHolder(View itemView) {
		super(itemView);
		channelName = itemView.findViewById(R.id.channel_name);
		lastMessageText = itemView.findViewById(R.id.text_group_channel_list_message);
		unreadCountText = itemView.findViewById(R.id.text_group_channel_list_unread_count);
		dateText = itemView.findViewById(R.id.text_group_channel_list_date);
		coverImage = itemView.findViewById(R.id.image_group_channel_list_cover);
		
		typingIndicatorContainer =
				itemView.findViewById(R.id.container_group_channel_list_typing_indicator);
	}
	
	void bind(final Context context, final GroupChannel channel,
	          @Nullable final ChatListAdapter.OnItemClickListener clickListener,
	          @Nullable final ChatListAdapter.OnItemLongClickListener longClickListener) {
		
		channelName.setText(TextUtils.getGroupChannelTitle(channel));
		
		setChannelImage(context, channel, coverImage);
		
		int unreadCount = channel.getUnreadMessageCount();
		// If there are no unread messages, hide the unread count badge.
		if (unreadCount == 0) {
			unreadCountText.setVisibility(View.INVISIBLE);
		} else {
			unreadCountText.setVisibility(View.VISIBLE);
			unreadCountText.setText(String.valueOf(channel.getUnreadMessageCount()));
		}
		
		BaseMessage lastMessage = channel.getLastMessage();
		if (lastMessage == null || !lastMessage.getCustomType().isEmpty()) {
			lastMessage = loadLastReadableMsg(channel.getUrl(), context);
		}
		
		if (lastMessage != null) {
			dateText.setVisibility(View.VISIBLE);
			lastMessageText.setVisibility(View.VISIBLE);
			
			// Display information about the most recently sent message in the channel.
			dateText.setText(String.valueOf(DateUtils.formatDateTime(lastMessage.getCreatedAt())));
			
			// Bind last message text according to the type of message. Specifically, if
			// the last message is a File Message, there must be special formatting.
			if (lastMessage instanceof UserMessage) {
				lastMessageText.setText(lastMessage.getMessage());
			} else if (lastMessage instanceof AdminMessage) {
				lastMessageText.setText(lastMessage.getMessage());
			} else {
				String lastMessageString = String.format(
						context.getString(R.string.group_channel_list_file_message_text),
						lastMessage.getSender().getNickname());
				lastMessageText.setText(lastMessageString);
			}
		} else {
			dateText.setVisibility(View.INVISIBLE);
			lastMessageText.setVisibility(View.INVISIBLE);
		}
		
		/*
		 * Set up the typing indicator.
		 * A typing indicator is basically just three dots contained within the layout
		 * that animates. The animation is implemented in the {@link TypingIndicator#animate()
		 * class}
		 */
		ArrayList<ImageView> indicatorImages = new ArrayList<>();
		indicatorImages.add(typingIndicatorContainer.findViewById(R.id.typing_indicator_dot_1));
		indicatorImages.add(typingIndicatorContainer.findViewById(R.id.typing_indicator_dot_2));
		indicatorImages.add(typingIndicatorContainer.findViewById(R.id.typing_indicator_dot_3));
		
		TypingIndicator indicator = new TypingIndicator(indicatorImages, 600);
		indicator.animate();
		
		// debug
		//            typingIndicatorContainer.setVisibility(View.VISIBLE);
		//            lastMessageText.setText(("Someone is typing"));
		
		// If someone in the channel is typing, display the typing indicator.
		if (channel.isTyping()) {
			typingIndicatorContainer.setVisibility(View.VISIBLE);
			lastMessageText.setText(("Someone is typing"));
		} else {
			// Display typing indicator only when someone is typing
			typingIndicatorContainer.setVisibility(View.GONE);
		}
		
		// Set an OnClickListener to this item.
		if (clickListener != null) {
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					clickListener.onItemClick(channel);
				}
			});
		}
		
		// Set an OnLongClickListener to this item.
		if (longClickListener != null) {
			itemView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					longClickListener.onItemLongClick(channel);
					
					// return true if the callback consumed the long click
					return true;
				}
			});
		}
	}
	
	private void setChannelImage(Context context, GroupChannel channel, ImageView coverImage) {
		String imageUrl = channel.getCoverUrl();
		if (channel.getMemberCount() == 2) {
			for (Member member : channel.getMembers()) {
				if (member.getUserId().equals(SendBird.getCurrentUser().getUserId())) {
					continue;
				}
				imageUrl = member.getProfileUrl();
			}
		}
		if (imageUrl.isEmpty()) {
			if (channel.getMemberCount() <= 2) {
				coverImage.setImageDrawable(
						ContextCompat.getDrawable(context, R.drawable.icon_avatar));
			} else {
				coverImage.setImageDrawable(
						ContextCompat.getDrawable(context, R.drawable.icon_avatar_group));
			}
			return;
		}
		ImageUtils.displayRoundImageFromUrl(context, imageUrl, coverImage);
	}
	
	public BaseMessage loadLastReadableMsg(String channelUrl, Context context) {
		try {
			File appDir = new File(context.getCacheDir(), SendBird.getApplicationId());
			appDir.mkdirs();
			
			File dataFile = new File(appDir, TextUtils.generateMD5(
					SendBird.getCurrentUser().getUserId() + channelUrl) + ".data");
			
			String content = FileUtils.loadFromFile(dataFile);
			String[] dataArray = content.split("\n");
			
			// Reset message list, then add cached messages.
			for (int i = 1; i < dataArray.length; i++) {
				BaseMessage baseMessage = BaseMessage.buildFromSerializedData(
						Base64.decode(dataArray[i], Base64.DEFAULT | Base64.NO_WRAP));
				if (baseMessage.getCustomType().isEmpty()) {
					return baseMessage;
				}
			}
		} catch (Exception e) {
			// Nothing to load.
		}
		return null;
	}
}
