package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import test.*;

public class ClientReaderThread extends Thread {
	private String inmsg;
	private InputStream in;
	private BufferedReader br;
	private Client client;
	
	
	public ClientReaderThread(Client client) {
		
		this.client = client; 
		br = new BufferedReader(new InputStreamReader(this.client.in));
		}
	
	@Override
	public void run() {
		try {
			while(!(inmsg = br.readLine()).equals("bye")){
					System.out.println("<From server>"+inmsg);
			}
				client.endFlag = false;
				client.bw.close();
				client.out.close();
				client.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
		
		
	
	

}
