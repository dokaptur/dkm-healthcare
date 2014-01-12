package servers;

import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import com.sun.rowset.CachedRowSetImpl;

import others.Config;
import servers.ServerSocketThread.ServerType;

public class BDServer {
	
	Config config = new Config();
	String url = "jdbc:postgresql:dkm";
	String user = "dudu";
	String passwd = "ciap2000";
	Connection conn; 
	SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
	SSLServerSocketFactory sfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
	
	public CachedRowSetImpl executeQuery(String query){
		
		
		ResultSet result = null;
		try {
		    Statement stat = conn.createStatement();
		    stat.execute("SET search_path TO 'dkm-healthcare'");
		    result = stat.executeQuery(query);
		} catch (SQLException sqle) {
		       System.out.println("Could not connect");
		       sqle.printStackTrace();
		       System.exit(1);
		} 
		CachedRowSetImpl crs = null;
		try {
			crs = new CachedRowSetImpl();
			crs.populate(result);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return crs;
	}
	
	public void executeUpdate(String query) {
		try {
		    Statement stat = conn.createStatement();
		    stat.execute("SET search_path TO 'dkm-healthcare'");
		    stat.executeUpdate(query);
		} catch (SQLException sqle) {
		       System.out.println("Could not connect");
		       sqle.printStackTrace();
		       System.exit(1);
		}
	}
	
	public BDServer() {
		try {
		    Class.forName("org.postgresql.Driver");
		    
		    conn = DriverManager.getConnection(url, user, passwd);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		BDServer bd = new BDServer();
		/*try {
			SSLServerSocket server = (SSLServerSocket) bd.sfactory.createServerSocket(bd.config.BD1addr.getPort());
			while (true) {
				SSLSocket socket = (SSLSocket) server.accept();
				ServerSocketThread thread = new ServerSocketThread(socket, ServerType.DB1, bd.config, bd.conn);
				thread.run();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */
		
		
		try {
		ServerSocket server = new ServerSocket(bd.config.BD1addr.getPort());
		while (true) {
			Socket socket = server.accept();
			ServerSocketThread thread = new ServerSocketThread(socket, ServerType.DB1, bd.config, bd.conn);
			thread.run();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}

}
