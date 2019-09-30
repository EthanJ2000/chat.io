package com.example.chatio;

public class ChatMessage
{
	String receiver;
	String sender;
	String message;
	
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
	
	public ChatMessage(String sender, String message, String receiver)
	{
		this.sender = sender;
		this.message = message;
		this.receiver = receiver;
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
