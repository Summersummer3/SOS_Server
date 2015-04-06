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
	private String IP = "localhost";
	private int PORT = 8888;
	private static Scanner scan;
	
	OutputStream out;
	static BufferedWriter bw;
	InputStream in;
	protected BufferedReader br;
	private String username;
	volatile boolean endFlag = true;
		
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
			bw.write(username+"\n");                   //serverʹ�ð��ж�ȡ����д��bufferedwriterʱ�ǵ�
//													ÿһ�����ݶ�Ҫ����
			bw.flush();
			
			
			while(br.readLine().equals("0")){
				System.out.println("login fail, username doesn't exist!");
				System.out.print("please enter again��");
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
	
	public void sender(String outmsg){
		
		try {
			bw.write(outmsg + "\n");
			bw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) throws InterruptedException {   //һ��һ��ʱ����ʹ�ö��̣߳����������ʹ���̣߳�
	     
		String outmsg;
		Client client =  new Client();

		Thread receiver = new ClientReaderThread(client);
		receiver.start();
		
		while(true){
			System.out.println("Enter :");
			scan = new Scanner(System.in);
			outmsg = scan.nextLine();
			if(client.endFlag){
				client.sender(outmsg);
			}else{
				break;
			}
			
		}
		System.out.println("Socket is end!");
		
		
		
		} 
		
	
	
	
}
