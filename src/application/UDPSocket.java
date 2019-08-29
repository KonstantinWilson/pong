package application;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class UDPSocket implements Runnable{
	private Network network;
	private DatagramSocket udpSocket = null;
	private DatagramPacket packetSend;
	private DatagramPacket packetReceive;
	private byte[] buffer;
	private InetAddress inetAddress = null;
	private int port = 0;
	private char mode = 'n';

	private int packetCounterSend = 0;
	private int packetCounterReceive = 0;

	public static final char MODE_NULL = 'n';
	public static final char MODE_LISTEN = 'l';
	
	public UDPSocket(Network network){
		this.network = network;
		buffer = new byte[68];
		packetSend = new DatagramPacket(buffer, buffer.length);
		packetReceive = new DatagramPacket(buffer, buffer.length);
	}
	
	public void listen(InetAddress inetAddress, int port) throws Exception{
		this.inetAddress = inetAddress;
		this.port = port;
		udpSocket = new DatagramSocket(port);
		mode = MODE_LISTEN;
		System.out.println("UDPSocket.connect: Listening to :" + port + ".");
	}
	
	public void send(DataObject dataObject){
		dataObject.setId(this.packetCounterSend++);
		buffer = dataObject.toByteArray();
		
		packetSend.setAddress(inetAddress);
		packetSend.setData(buffer, 0, buffer.length);
		packetSend.setPort(network.getTCPSocket().getPortOther());
		try {
			udpSocket.send(packetSend);
			//System.out.println("Sent " + packetSend.getData().length + " bytes to " + packetSend.getAddress().toString() + ":" + packetSend.getPort() + ".");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void disconnect() throws IOException{
		mode = UDPSocket.MODE_NULL;
		packetSend = null;
		packetReceive = null;
		if(udpSocket != null) udpSocket.close();
	}
	
	@Override
	public void run() {
		while(mode == UDPSocket.MODE_LISTEN){
			try {
				udpSocket.receive(packetReceive);
				DataObject in = new DataObject(packetReceive.getData());
				if(in.getId() < this.packetCounterReceive){
					System.out.println(this.packetCounterReceive);
					System.out.println(in.getId());
				}
				else{
					this.packetCounterReceive = in.getId();
					network.update(in);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		mode = UDPSocket.MODE_NULL;
	}
	
	public void destroy() throws IOException{
		mode = UDPSocket.MODE_NULL;
		packetSend = null;
		packetReceive = null;
		if(udpSocket != null) udpSocket.close();
		inetAddress = null;
		port = 0;
		network = null;
	}

}
