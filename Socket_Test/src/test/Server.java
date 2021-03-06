package test;


import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class Server {

	private static ServerSocket server;
	private final static int PORT = 8000;
	private static ExecutorService thread_pool;
	protected static List<Socket> sList = new ArrayList<Socket>();

	
	public static void main(String[] args) {
		try {
				Socket client = null;
				server = new ServerSocket(PORT);
				thread_pool = Executors.newCachedThreadPool();
				System.out.println("server is starting..." + InetAddress.getLocalHost());
				
				while(true){
					
					client = server.accept();
					sList.add(client);
					thread_pool.execute(new Service(client));
					System.out.println("1 come!");
					
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} 

}
