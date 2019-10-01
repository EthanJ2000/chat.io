package com.example.chatio;

public class ChatMessage
{
	String receiver;
	String sender;
	String message;
	long timestamp;
	
	public ChatMessage()
	{
	}
	
	public String getReceiver()
	{
		return receiver;
	}
	
	public void setReceiver(String receiver)
	{
		this.receiver = receiver;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	public long getTimestamp()
	{
		return timestamp;
	}
	
	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}
	
	public ChatMessage(String receiver, String sender, String message, long timestamp)
	{
		this.receiver = receiver;
		this.sender = sender;
		this.message = message;
		this.timestamp = timestamp;
	}
	
	public String getSender()
	{
		return sender;
	}
	
	public void setSender(String sender)
	{
		this.sender = sender;
	}
}
