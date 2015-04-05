package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Time;
import java.util.Scanner;

public class Client {
	Socket socket;
	private String IP = "192.168.1.104";
	private int PORT = 8888;
	private Scanner scan;
	
	OutputStream out;
	BufferedWriter bw;
	private InputStream in;
	protected BufferedReader br;
	private String username;
	volatile boolean endFlag = true;
	private String outmsg;
	
	public Client(){
		
		try {
			socket = new Socket(IP,PORT);
			System.out.print("please enter your username:");
			scan = new Scanner(System.in);
			username = scan.nextLine();
			
			in = socket.getInputStream();
			br = new BufferedReader(new InputStreamReader(in));
			out = socket.getOutputStream();
			bw = new BufferedWriter(new OutputStreamWriter(out));
			bw.write(username+"\n");                   //server使用按行读取，则写入bufferedwriter时记得
//													每一行数据都要换行
			bw.flush();
			
			
			while(br.readLine().equals("0")){
				System.out.println("login fail, username doesn't exist!");
				System.out.print("please enter again：");
				scan = new Scanner(System.in);
				username = scan.nextLine();
				bw.write(username+"\n");  
				bw.flush();
				
			}

			System.out.println("Login success!");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {   //一问一答时不必使用多线程，聊天室务必使用线程！
		Client client =  new Client();
		Thread receiver = new ClientReaderThread(client);
		Thread sender = new ClientWriterThread(client);
		
		receiver.start();
		sender.start();
		
		} 
		
	
	
	
}
