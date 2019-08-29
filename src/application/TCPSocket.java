package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPSocket implements Runnable{
	private Network network;
	private ServerSocket serverSocket = null;
	private Socket tcpSocket = null;
	private InetAddress inetAddress = null;
	private BufferedWriter writer = null;
	private int port = 0;
	private char mode = 'n';

	public static final char MODE_NULL = 'n';
	public static final char MODE_WAIT_FOR_CONNECTION = 'w';
	public static final char MODE_CONNECT = 'c';
	public static final char MODE_CONNECTED = 'C';
	public static final char MODE_LISTEN = 'l';
	
	public TCPSocket(Network network){
		this.network = network;
	}
	
	public void host(int port) throws IOException{
		serverSocket = new ServerSocket(port);
		mode = MODE_WAIT_FOR_CONNECTION;
	}
	
	public void connect(String address, int port) throws Exception{
		inetAddress = InetAddress.getByName(address);
		this.port = port;
		mode = MODE_CONNECT;
		System.out.println("TCPSocket.connect: Connecting to " + address + ":" + port + ".");
	}
	
	public void disconnect() throws IOException{
		mode = TCPSocket.MODE_NULL;
		if(serverSocket != null) serverSocket.close();
		if(tcpSocket != null) tcpSocket.close();
	}
	
	public void send(String m) throws IOException{
		System.out.println("send: >" + m + "<");
		writer.write(m);
		writer.newLine();
		writer.flush();
		System.out.println("TCP sent >> " + m);
	}
	
	@Override
	public void run() {
		if(mode == TCPSocket.MODE_WAIT_FOR_CONNECTION){
			try {
				Socket client = serverSocket.accept();
				if(client != null){
					tcpSocket = client;
					System.out.println(tcpSocket.getLocalPort() + " - " + tcpSocket.getPort());
					this.inetAddress = tcpSocket.getInetAddress();
					this.port = tcpSocket.getLocalPort();
					writer = new BufferedWriter(new OutputStreamWriter(tcpSocket.getOutputStream()));
					serverSocket.close();
					serverSocket = null;
					network.update("[cmd]ConnectedHost");
					mode = TCPSocket.MODE_LISTEN;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(mode == TCPSocket.MODE_CONNECT){
			try {
				tcpSocket = new Socket(inetAddress, port);
				writer = new BufferedWriter(new OutputStreamWriter(tcpSocket.getOutputStream()));
				System.out.println(tcpSocket.getLocalPort() + " - " + tcpSocket.getPort());
				this.port = tcpSocket.getLocalPort();
				network.update("[cmd]ConnectedClient");
				mode = TCPSocket.MODE_LISTEN;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(mode == TCPSocket.MODE_LISTEN){
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			while(reader != null && mode == TCPSocket.MODE_LISTEN){
				try {
					String input = reader.readLine();
					network.update(input);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					if(e.getMessage().equals("Connection reset"))
					{
						network.update("[cmd]ConnectionReset");
						mode = TCPSocket.MODE_NULL;
					}
					else e.printStackTrace();
				}
			}
		}
		mode = TCPSocket.MODE_NULL;
	}
	
	public void destroy() throws IOException{
		mode = TCPSocket.MODE_NULL;
		if(serverSocket != null) serverSocket.close();
		if(tcpSocket != null) tcpSocket.close();
		inetAddress = null;
		port = 0;
		network = null;
	}
	
	public InetAddress getInetAddress(){
		return this.tcpSocket.getInetAddress();
	}
	
	public int getPort(){
		return this.tcpSocket.getPort();
	}
	
	public int getPortOther(){
		return this.tcpSocket.getLocalPort();
	}
}
