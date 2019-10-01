package com.example.chatio;

import android.util.Log;

public class Message
{
	public enum ItemType{
		RECEIVED_TYPE,SENT_TYPE
	}
	private String message;
	private ItemType type;
	private long timestamp;
	private static final String TAG = "Message";
	
	public String getMessage()
	{
		Log.i(TAG, "getMessage: "+message);
		return message;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	public ItemType getType()
	{
		Log.i(TAG, "getType: "+type);
		return type;
	}
	
	public long getTimestamp()
	{
		return timestamp;
	}
	
	public void setType(ItemType type)
	{
		this.type = type;
	}
	
	public Message(String message, ItemType type, long timestamp)
	{
		this.message = message;
		this.type = type;
		this.timestamp = timestamp;
	}
}
