package application;

import java.io.IOException;

public class Network{
	private TCPSocket tcpSocket;
	private UDPSocket udpSocket;
	
	Model model = null;
	
	public Network(Model model){
		this.model = model;
		tcpSocket = new TCPSocket(this);
		udpSocket = new UDPSocket(this);
	}
	
	public void host(int port){
		try {
			tcpSocket.host(port);
			//Platform.runLater(tcpSocket);
			Thread thread = new Thread(tcpSocket);
			thread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void connect(String address, int port){
		try {
			System.out.println("Network: Connecting to " + address + ":" + port + ".");
			tcpSocket.connect(address, port);
			Thread thread = new Thread(tcpSocket);
			thread.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void disconnect(){
		try {
			tcpSocket.disconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void update(Object o){
		if(o.getClass().getSimpleName().equals("String")){
			String s = (String)o;
			if(s.equals("[cmd]ConnectedHost") || s.equals("[cmd]ConnectedClient")){
				System.out.println("Starting UDP.");
				try {
					udpSocket.listen(tcpSocket.getInetAddress(), tcpSocket.getPort());
					Thread thread = new Thread(udpSocket);
					thread.start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		model.networkUpdate(o);
	}
	
	public void sendMessage(String m){
		try {
			tcpSocket.send(m);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendData(DataObject dataObject){
		udpSocket.send(dataObject);
	}
	
	public void destroy(){
		try {
			tcpSocket.destroy();
			udpSocket.destroy();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			tcpSocket = null;
			udpSocket = null;
		}
	}
	
	public TCPSocket getTCPSocket(){
		return this.tcpSocket;
	}
}