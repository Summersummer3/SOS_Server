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



import test.*;

public class Service implements Runnable {

	private Socket socket;
	private InputStream in;
	private BufferedReader br;
	private OutputStream out;
	private BufferedWriter bw;
	private String inmsg;
	private String outmsg;
	private String username;
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 public void run() {
		try {

			
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
			this.getMessage();
			
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {
					
			e.printStackTrace();
		} catch (SQLException e) {

			e.printStackTrace();
		}
				
			
	}
	 
	 public void getMessage(){
		 
			try {
				while((inmsg = br.readLine())!=null){    //read方法实现io接受阻塞！！ 
					
					if(!inmsg.equals("bye")){
						System.out.println(inmsg);
						sendAllClient();
						
					}else{
						bw.write("bye\n");
						bw.flush();
						socket.shutdownInput();
						bw.close();
						out.close();
						socket.close();
						Server.sList.remove(Server.sList.size()-1);
						System.out.println(username + " has left!");
						return;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		 
		 
	 }
	 
	 public boolean usernameSelect(Connection conn,Statement stmt) throws IOException, SQLException{
		 
		username =  br.readLine();
		sql = "select username from admin where username = '"+username+"'";
		ResultSet rs = stmt.executeQuery(sql);
		return rs.next();
	 }
	
	 public void sendAllClient(){
		 Socket mSocket;
		 try {
			for(int i=0;i<Server.sList.size();i++){
				outmsg = username + " say:"+inmsg+"\n";
				mSocket = Server.sList.get(i);
				out = mSocket.getOutputStream(); 
				bw = new BufferedWriter(new OutputStreamWriter(out));
				bw.write(outmsg);
				bw.flush();
				bw.close();
				out.close();
			 }
			out = socket.getOutputStream();
			bw = new BufferedWriter(new OutputStreamWriter(out));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 
	 }
}    

