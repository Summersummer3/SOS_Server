package test;

import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;







import net.sf.json.JSONObject;
import test.*;

public class Service implements Runnable {

	private Socket socket;
	private InputStream in;
	private BufferedReader br;
	private OutputStream out;
	private BufferedWriter bw;
	private String flagmsg;
	private String inmsg;
	private String outmsg;
	private String username;
	private String password;
	private String url = "jdbc:mysql://localhost:3306/android_server";
	private final String USERNAME = "root";
	private final String PASSWORD = "412765442";
	private String sql = "";
	
	public Service(Socket socket){
		this.socket = socket;
		try {
			in = this.socket.getInputStream();
			br = new BufferedReader(new InputStreamReader(in));
			out = socket.getOutputStream();
			bw = new BufferedWriter(new OutputStreamWriter(out));
			System.out.println("connection is ready......");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 public void run() {
		try {
			
			while((flagmsg = br.readLine())!=null){
				System.out.println("here!!");
				if(flagmsg.equals("0")){
					Class.forName("com.mysql.jdbc.Driver");
					Connection conn = DriverManager.getConnection(url,USERNAME,PASSWORD);
					Statement stmt = conn.createStatement();
					
					while(!usernameSelect(conn, stmt)){
						bw.write("0\n");
						bw.flush();
					}
					
					inmsg = username + " comes to the server,now total connection : " + 
						Server.sList.size();
					
					System.out.println(inmsg);
					bw.write("1\n");
					bw.flush();
				}
				
				if(flagmsg.equals("1")){
					inmsg = br.readLine();
					System.out.println(inmsg);
					
				}
			}
			
			
		
			
			
//			this.getMessage();
			
		} catch (Exception e) {
			e.printStackTrace();
			try {
				socket.shutdownInput();
				bw.close();
				out.close();
				socket.close();
				Server.sList.remove(Server.sList.size()-1);
				System.out.println(username + " has left!");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
				
			
	}
	 
//	 public void getMessage(){
//		 
//			try {
//				
//				while(!(inmsg = br.readLine()).equals("bye")){    //read方法实现io接受阻塞！！ 
//						System.out.println(username+" says:"+inmsg);
//						sendAllClient();
//					}
//				bw.write("bye\n");
//				bw.flush();
//				socket.shutdownInput();
//				bw.close();
//				out.close();
//				socket.close();
//				Server.sList.remove(Server.sList.size()-1);
//				System.out.println(username + " has left!");
//				
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		
//		 
//		 
//	 }
	 
	 public boolean usernameSelect(Connection conn,Statement stmt) throws IOException, SQLException{
		 
		JSONObject json = new JSONObject();  
		json = JSONObject.fromObject(br.readLine());
		try {
			username = json.get("user_name").toString();
			password = json.get("pass_word").toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(username+" "+password);
		
		sql = "select * from admin where username = '"+username+"'"+"and password = '"+password+"'";
		ResultSet rs = stmt.executeQuery(sql);
		return rs.next();
	 }
	
//	 public void sendAllClient(){
//		 Socket mSocket;
//		 OutputStream outToAll;
//		 BufferedWriter bwToAll;
//		 try {
//			for(int i=0;i<Server.sList.size();i++){
//				outmsg = username + " say:"+inmsg+"\n";
//				mSocket = Server.sList.get(i);
//				System.out.println(mSocket.getInetAddress());
//				outToAll = mSocket.getOutputStream(); 
//				bwToAll = new BufferedWriter(new OutputStreamWriter(outToAll));
//				bwToAll.write(outmsg);
//				bwToAll.flush();
//				
//			}
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		 
//		 
//	 }
}    

