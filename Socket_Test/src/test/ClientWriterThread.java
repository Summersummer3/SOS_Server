package test;
import java.io.IOException;
import java.util.Scanner;

import test.ClientReaderThread;

public class ClientWriterThread extends Thread {
	private String outmsg;
	private Client client;
	private Scanner scan;
	
	public ClientWriterThread(Client client) {
		this.client = client;
	}
	
	public void run(){
		
		while(client.endFlag){
			try {
				System.out.print("Enter :");
				scan = new Scanner(System.in);
				try {
					client.bw.write(scan.nextLine());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
}
