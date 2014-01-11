package servers;

import java.io.IOException;
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
	String url = "jdbc:postgresql:dudu";
	String user = "dkm";
	String passwd = "dkm";
	Connection conn; 
	SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
	SSLServerSocketFactory sfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
	
	public CachedRowSetImpl executeQuery(String query){
		
		try{
		    Class.forName("org.postgresql.Driver");
		    } catch (ClassNotFoundException cnfe){
		      System.out.println("Could not find the JDBC driver!");
		      System.exit(1);
		    }
		ResultSet result = null;
		try {
		    conn = DriverManager.getConnection(url, user, passwd);
		    Statement stat = conn.createStatement();
		    stat.execute("SET search_path TO 'dkm-healthcare'");
//		    System.out.println(query);
		    result = stat.executeQuery(query);
		} catch (SQLException sqle) {
		       System.out.println("Could not connect");
		       sqle.printStackTrace();
		       System.exit(1);
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				System.out.println("Could not close connection");
			}
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
	
	public BDServer() {
		try {
			conn = DriverManager.getConnection(url, user, passwd);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		BDServer bd = new BDServer();
		try {
			SSLServerSocket server = (SSLServerSocket) bd.sfactory.createServerSocket(bd.config.BD1addr.getPort());
			while (true) {
				SSLSocket socket = (SSLSocket) server.accept();
				ServerSocketThread thread = new ServerSocketThread(socket, ServerType.DB1, bd.config, bd.conn);
				thread.run();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}

}
