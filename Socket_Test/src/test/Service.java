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
				
				Class.forName("com.mysql.jdbc.Driver");
				Connection conn = DriverManager.getConnection(url,USERNAME,PASSWORD);
				Statement stmt = conn.createStatement();
				
				/*server service use: 
				1.login.
				2.push SO SMessage.
				3.register.
				4.set help users.*/	
				
				
				if(flagmsg.equals("0")){
					
					
					if(!usernameSelect(conn, stmt)){
						bw.write("0\n");
						bw.flush();
					}else{
					
					inmsg = username + " comes to the server,now total connection : " + 
						Server.sList.size();
					
					System.out.println(inmsg);
					bw.write("1\n");
					bw.flush();
					}
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
					message.setContent("你的朋友" + username + "遇到了危险，点击查看求救位置！");
					Style style = new Style(1, 1, 1, 1, 0, 1, 0, 0);
					message.setStyle(style);
					ClickAction action = new ClickAction();
					action.setActionType(ClickAction.TYPE_INTENT);
					action.setIntent("intent:#Intent;action=android.intent.action.SENDTO;S.tv2="
									+ Longitude + ";S.tv1=" + Latitude + ";end");
					message.setAction(action);
					
					//查找出求救人的紧急求救用户
					String[] names = SelectHelpUserName(conn, stmt);
					
					if(!names[0].equals("null")){
						System.out.println(xinge.pushSingleAccount(0, names[0], message));
					}
					if(!names[0].equals("null")){
						System.out.println(xinge.pushSingleAccount(0, names[1], message));
					}
					if(!names[0].equals("null")){
						System.out.println(xinge.pushSingleAccount(0, names[2], message));
					}
				}
				
				if(flagmsg.equals("2")){
					
					if(helpListRegisterInSQL(conn, stmt)==1){
						bw.write("2\n");
						bw.flush();
						
						System.out.println("New helpListRegister succeed!");
					}else{
						bw.write("0\n");
						bw.flush();
						
					}
						
				}
				

				if(flagmsg.equals("3")){
					
					int result = registerInSQL(conn, stmt);
					if(result==1){
						bw.write("3\n");
						bw.flush();
						
						System.out.println("New register succeed!");
					}
					
					if(result==11){
						bw.write("11\n");
						bw.flush();
						
					}
					
					if(result==12){
						bw.write("12\n");
						bw.flush();
						
					}
					
					if(result==13){
						bw.write("13\n");
						bw.flush();
						
					}
					
					if(result==0){
						bw.write("13\n");
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
	 
	 

	private String[] SelectHelpUserName(Connection conn,Statement stmt) throws SQLException {
		String HelpUserName1 = "";
		String HelpUserName2 = "";
		String HelpUserName3 = "";
		
		sql = "select HelpUserName1 from HelpList where username ='"+username+"'";
		ResultSet rs1 = stmt.executeQuery(sql);
		if(rs1.next()){                 //必须先判断rs是否有下一个，才能取值
			HelpUserName1 = rs1.getString("HelpUserName1");
		}
		
		sql = "select HelpUserName2 from HelpList where username ='"+username+"'";
		ResultSet rs2 = stmt.executeQuery(sql);
		if(rs2.next()){
			HelpUserName2 = rs2.getString("HelpUserName2");
		}
		
		sql = "select HelpUserName3 from HelpList where username ='"+username+"'";
		ResultSet rs3 = stmt.executeQuery(sql);
		if(rs3.next()){
			HelpUserName3 = rs3.getString("HelpUserName3");
		}
		
		return new String[]{HelpUserName1,HelpUserName2,HelpUserName3};
		
	}

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
	 
	 public int helpListRegisterInSQL(Connection conn, Statement stmt) throws SQLException {
		 	
		 	String helpUserName1 = "";
		 	String helpUserName2 = "";
		 	String helpUserName3 = "";
		 	
		 	
			JSONObject json = new JSONObject();  
			try {
				
				json = JSONObject.fromObject(br.readLine());
			
				helpUserName1 = json.get("helpUserName1").toString();
				helpUserName2 = json.get("helpUserName2").toString();
				helpUserName3 = json.get("helpUserName3").toString();	
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(helpUserName1+" "+helpUserName2 + " " + helpUserName3);
			
			if(!helpUserName1.equals("null")){
				sql = "select * from admin where username = '"+helpUserName1+"'";
				ResultSet rs1 = stmt.executeQuery(sql);
				if(!rs1.next()){
					return 11;
				}
			}
			
			if(!helpUserName2.equals("null")){
				sql = "select * from admin where username = '"+helpUserName2+"'";
				ResultSet rs2 = stmt.executeQuery(sql);
				if(!rs2.next()){
					return 12;
				}
			}
			
			if(!helpUserName2.equals("null")){
				sql = "select * from admin where username = '"+helpUserName3+"'";
				ResultSet rs3 = stmt.executeQuery(sql);
				if(!rs3.next()){
					return 13;
				}
			}
			
			sql = "insert into HelpList (username, HelpUserName1, HelpUserName2, HelpUserName3) values ('" + username + "', '"
			+ helpUserName1 +"', '"+ helpUserName2 + "', '" + helpUserName3 + "') where username = '" + username + "'";
			
			try{
				System.out.println(sql);
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

