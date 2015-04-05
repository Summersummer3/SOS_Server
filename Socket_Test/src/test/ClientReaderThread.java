package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;
import test.*;

public class ClientReaderThread extends Thread {
	private String inmsg;
	private Client client;
	
	
	public ClientReaderThread(Client client) {
		this.client = client; 
	}
	
	@Override
	public void run() {
		try {
			while(client.endFlag){

				if((inmsg = client.br.readLine())!="bye"){
					System.out.println("<From server>"+inmsg);
				}
				else{
					client.endFlag = false;
				}
				}
			
			client.bw.close();
			client.out.close();
			client.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	

}
