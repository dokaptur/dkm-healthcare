package protocols;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import java.util.zip.*;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import com.sun.rowset.CachedRowSetImpl;

import others.Config;

/**
 * An application-level protocol for communication between user and data basis servers.
 * @author retax
 */

public class P1protocol {
	
	/**
	 * Object to create SSLSockets
	 */
	SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
	
	/**
	 * object with configuration
	 */
	Config config;
	
	/**
	 * addresses of DataBasis servers
	 */
	public ArrayList<InetSocketAddress> dbAdresses = new ArrayList<>();
	
	/**
	 * timeout for connection
	 */
	int timeout = 1000;
	
	/**
	 * enum to determine if we are DBServer or user
	 */
	public static enum Site {
		User, DB;
	}
		
	/**
	 * enum to determine what do we want from DBServer
	 */
	public static enum Request {
		PING, INFO, DOWNLOAD, UPLOAD;
	}
	
	Site site;
	
	/**
	 * P1protocol constructor;
	 * we fill in addresses of servers from config object
	 * @param site - we tell protocol as which server we will use it
         * @param config - list of database servers addresses
	 */
	public  P1protocol(Site site, Config config) {
		this.site = site;
		this.config = config;
		
		dbAdresses.add(config.BD1addr);
		dbAdresses.add(config.BD2addr);
	}
	
	/**
	 * The most important method. Here is implemented whole communication.
	 * @param socket - for connection between Data Base and user
         * @param request - for connection between Data Base and user
         * @param query - for connection between Data Base and user
         * @param con - for connection between Data Base and user
	 * @return null if we are in DBServer or if error occurs. Otherwise Boolean while pinging and ResultSet while asking DB about some info.  
	 *
	 * @throws Exception
	 */
	public Object talk(Socket socket, Request request, String query, Connection con) throws Exception {	
		InputStream is = socket.getInputStream();
		OutputStream os = socket.getOutputStream();
		
		PrintWriter wr = new PrintWriter (os);
		BufferedReader sc = new BufferedReader(new InputStreamReader(is));
		Object obj = null;
		
		if (site == Site.DB) {
		
                        wr.println("P1 Protocol. What do you want?"); wr.flush();
			
			String ans = sc.readLine(); System.out.println(ans);
			if (ans.equals("Ping!")) {
				wr.println("Pong!"); wr.flush();
				sc.close(); wr.close();
				return null;
			}
			if (ans.equals("Give me info!")) {
                                for (int i=0; i<10; i++) { // tries to send result max 10 times
					wr.println("Ok, give me query"); wr.flush();
					
					// get CachedRowSetImpl object from DB
					String sql = sc.readLine(); System.out.println(sql);
					ResultSet result = null;
					Statement stat = con.createStatement();
					stat.execute("SET search_path TO 'dkm-healthcare'");
					CachedRowSetImpl crs = new CachedRowSetImpl();
					try {
						result = stat.executeQuery(sql);
					} catch (Exception e) {
						result = null;
					}
					crs.populate(result);
					
					wr.println("Port number for new connection?"); wr.flush();
					String s = sc.readLine(); System.out.println(s);
					int port = Integer.parseInt(s);
					Socket objsocket = new Socket();
					objsocket.connect(new InetSocketAddress(socket.getInetAddress(), port), 100);
					ObjectOutputStream oos = new ObjectOutputStream(objsocket.getOutputStream());
					oos.writeObject(crs); oos.flush();
					oos.close(); objsocket.close();
					
					crs.close();
					
					String ans2 = sc.readLine(); System.out.println(ans2);
					if (ans2.equals("Ok, got it")) {
						wr.println("Ok, bye!"); wr.flush();
						sc.close(); wr.close();
						return null;
					} 
					else if (ans2.equals("Something went wrong!")) {
						continue;
					}
					else {
						wr.println("Protocol error! Bye!"); wr.flush();
						sc.close(); wr.close();
						return null;
					}
				}
				wr.println("I can't. Tell it to admin. Bye!"); wr.flush();
				sc.close(); wr.close();
				return null;
			}
                        
                        if (ans.equals("Download")) {
                            //zip ./pesel/* into pack.zip and send through socket
                            sc.close(); wr.close();
                            try{
                                int BUFFER=2048;
                                BufferedInputStream origin = null;
                                FileOutputStream dest = new FileOutputStream(".//pack.zip");
                                CheckedOutputStream checksum = new CheckedOutputStream(dest, new Adler32());
                                ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(checksum));

                                byte data[] = new byte[BUFFER];
                                //any idea how to get user's pesel accessible here???
                                File f = new File(".//pesel//");
                                String files[] = f.list();

                                for (int i=0; i<files.length; i++) {
                                   //System.out.println("Adding: "+files[i]);
                                   FileInputStream fi = new FileInputStream(files[i]);
                                   origin = new BufferedInputStream(fi, BUFFER);
                                   ZipEntry entry = new ZipEntry(files[i]);
                                   out.putNextEntry(entry);
                                   int count;
                                   while((count = origin.read(data, 0, BUFFER)) != -1) {
                                      out.write(data, 0, count);
                                   }
                                   origin.close();
                                }
                                out.close();
                            }catch(IOException e){
                                System.out.println("Sorry, error :P");
                                return null;
                            }
                            
                            byte[] buffer=new byte[2048];
                            BufferedInputStream in = new BufferedInputStream(new FileInputStream(".//pack.zip"));
                            int count;
                            
                            while ((count = in.read(buffer)) > 0) {
                                os.write(buffer, 0, count);
                                os.flush();
                            }
                            
                            return true;
                        }
                        
                        if (ans.equals("Upload")) {
                            //receive a file trough a socket 
                            try{
                                String path="";
                                //let the user choose the filename?
                                
                                FileOutputStream fos = new FileOutputStream(path);
                                BufferedOutputStream out = new BufferedOutputStream(fos);
                                byte[] buffer = new byte[2048];
                                int count;
                                while((count=is.read(buffer)) >0){
                                    fos.write(buffer);
                                }
                                fos.close();
                            }catch(Exception e){
                                System.out.println("Error, sorry");
                                return null;
                            }
                        }
                        
			sc.close(); wr.close();
			throw (new ProtocolException());
			
		} else { // site = user
			wr.println("P1"); wr.flush();
			String s = sc.readLine(); System.out.println(s);
			if (!s.equals("P1 Protocol. What do you want?")) {
				sc.close(); wr.close();
				return null;
			}
			if (request == Request.PING) {
				wr.println("Ping!"); wr.flush();
				s = sc.readLine(); System.out.println(s);
				if (!s.equals("Pong!")) {
					sc.close(); wr.close();
					return null;
				}
				sc.close(); wr.close();
				return new Boolean(true);
			}
                        if(request==Request.INFO){
				wr.println("Give me info!"); wr.flush();
				s = sc.readLine(); System.out.println(s);
				while (s.equals("Ok, give me query")) {
					wr.println(query); wr.flush();
					
					ServerSocket objss = new ServerSocket(0);
					s = sc.readLine(); System.out.println(); // Port number for new connection?
					wr.println(objss.getLocalPort()); wr.flush();
					Socket objsocket = objss.accept();
					ObjectInputStream obstr = new ObjectInputStream(objsocket.getInputStream());
					obj = obstr.readObject();
					obstr.close(); objsocket.close(); objss.close();
					
					if (obj != null && obj instanceof CachedRowSetImpl) {
						wr.println("Ok, got it"); wr.flush();
					} else {
						wr.println("Something went wrong!"); wr.flush();
					}
					s = sc.readLine(); System.out.println(s);
				}
				wr.close(); sc.close();
				if (s.equals("Ok, bye!")) {
					return (ResultSet) obj;
				} else return null;
				
			}
                        
                        if(request==Request.DOWNLOAD){
                            wr.println("Download!"); wr.flush();
                            sc.close();wr.close();
				
                            try{
                                FileOutputStream fos = new FileOutputStream(".//pack.zip");
                                BufferedOutputStream out = new BufferedOutputStream(fos);
                                byte[] buffer = new byte[2048];
                                int count;
                                while((count=is.read(buffer)) >0){
                                    fos.write(buffer);
                                }
                                fos.close();
                            }catch(Exception e){
                                System.out.println("Error, sorry");
                                return null;
                            }

                            return true;
                        }
                        
                        if(request==Request.UPLOAD){
                            wr.println("Upload!"); wr.flush();
                            sc.close();wr.close();
                            
                            String path="";
                            //how to choose which file to send???
                            
                            byte[] buffer=new byte[2048];
                            BufferedInputStream in = new BufferedInputStream(new FileInputStream(path));
                            int count;
                            
                            while ((count = in.read(buffer)) > 0) {
                                os.write(buffer, 0, count);
                                os.flush();
                            }
				
                            return true;
                        }
		}
                
            return null;
	}
}
