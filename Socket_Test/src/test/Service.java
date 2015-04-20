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












import com.mysql.jdbc.UpdatableResultSet;
import com.tencent.xinge.ClickAction;
import com.tencent.xinge.Message;
import com.tencent.xinge.Style;
import com.tencent.xinge.XingeApp;

import net.sf.json.JSONObject;
import test.*;

public class Service implements Runnable {
	private final long ID = 2100103049;
	private final String KEY = "6990b8d57df583359201589bdb5aaba6";
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
	private String TAG;
	private String url = "jdbc:mysql://localhost:3306/android_server";
	private final String USERNAME = "root";
	private final String PASSWORD = "412765442";
	private String sql = "";
	
	public Service(Socket socket){
		this.socket = socket;
		try {
			in = this.socket.getInputStream();
			br = new BufferedReader(new InputStreamReader(in,"GBK"));
			out = socket.getOutputStream();
			bw = new BufferedWriter(new OutputStreamWriter(out,"GBK"));
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
					JSONObject json = new JSONObject();  
					json = JSONObject.fromObject(br.readLine());
					String Latitude = json.getString("Latitude");
					String Longitude = json.getString("Longitude");
					System.out.println(Latitude+"  "+Longitude);
					//push message intent
					XingeApp xinge = new XingeApp(ID, KEY);
					Message message = new Message();
					message.setType(Message.TYPE_NOTIFICATION);
					message.setTitle("紧急求救信息！！！！");
					message.setContent("有人遇到了危险，点击查看求救人位置！");
					Style style = new Style(1, 1, 1, 1, 0, 1, 0, 0);
					message.setStyle(style);
					ClickAction action = new ClickAction();
					action.setActionType(ClickAction.TYPE_INTENT);
					action.setIntent("intent:#Intent;action=android.intent.action.SENDTO;S.tv2="
									+ Longitude + ";S.tv1=" + Latitude + ";end");
					message.setAction(action);
					xinge.pushAllDevice(0, message);
					
				}
				
				if(flagmsg.equals("2")){
					Class.forName("com.mysql.jdbc.Driver");
					Connection conn = DriverManager.getConnection(url,USERNAME,PASSWORD);
					Statement stmt = conn.createStatement();
					if(registerInSQL(conn, stmt)==1){
						bw.write("2\n");
						bw.flush();
						
						System.out.println("New register succeed!");
					}else{
						bw.write("0\n");
						bw.flush();
						
					}
					
					
					
				}
				
				if(flagmsg.equals("bye")){
					
					socket.shutdownInput();
					bw.close();
					out.close();
					socket.close();
					Server.sList.remove(Server.sList.size()-1);
					System.out.println(username + " has left!");	
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
	 
	 public int registerInSQL(Connection conn,Statement stmt) throws IOException, SQLException{
		 
			JSONObject json = new JSONObject();  
			json = JSONObject.fromObject(br.readLine());
			try {
				username = json.get("username").toString();
				password = json.get("password").toString();
				TAG = json.get("TAG").toString();	
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(username+" "+password + " " + TAG);
			
			sql = "insert into admin (username, password, TAG) values ('"+ username +"', '"+ password + "', '" + TAG + "')";
			
			try{
			return stmt.executeUpdate(sql);
			}
			catch(com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e){
				return 0;
				
			}
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

