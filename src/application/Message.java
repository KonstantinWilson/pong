package application;

public class Message{
	private String message;
	
	public Message(String s){
		message = s;
	}
	
	public void setMessage(String s){
		message = s;
	}
	
	public String getMessage(){
		return message;
	}
	
	public String toString(){
		return message;
	}
}
