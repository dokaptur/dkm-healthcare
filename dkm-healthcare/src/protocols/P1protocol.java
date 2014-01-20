package protocols;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import java.util.zip.*;

//import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import com.sun.rowset.CachedRowSetImpl;

import org.apache.commons.codec.digest.DigestUtils;

import others.Config;
import protocols.P2protocol;
import servers.BDServer;

/**
 * An application-level protocol for communication between user and data basis
 * servers.
 * 
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
		INFO, UPDATE, DOWNLOAD, UPLOAD;
	}
	
	BDServer bd;
	Site site;

	/**
	 * P1protocol constructor; we fill in addresses of servers from config
	 * object
	 * 
	 * @param site
	 *            - we tell protocol as which server we will use it
	 * @param config
	 *            - list of database servers addresses
	 * @param bd = an instance of BDServer class
	 */
	public P1protocol(Site site, Config config, BDServer bd) {
		this.site = site;
		this.config = config;
		this.bd = bd;

		dbAdresses.add(config.BD1addr);
		dbAdresses.add(config.BD2addr);
	}

	/**
	 * The most important method. Here is implemented whole communication.
	 * 
	 * @param socket
	 *            - for connection between Data Base and user
	 * @param request
	 *            - for connection between Data Base and user
	 * @param query
	 *            - argument of INFO and UPDATE requests
	 * @param pesel
	 *            - argument of DOWNLOAD request
	 * @param path
	 *            - argument of UPLOAD request
	 * @param con
	 *            - for connection between Data Base and user
	 * @return null if we are in DBServer or if error occurs. Otherwise Boolean
	 *         while pinging and ResultSet while asking DB about some info.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public Object talk(Socket socket, Request request, String query, String pesel, String pass, String path, Connection con)
			throws Exception {
		InputStream is = socket.getInputStream();
		OutputStream os = socket.getOutputStream();

		PrintWriter wr = new PrintWriter(os);
		BufferedReader sc = new BufferedReader(new InputStreamReader(is));
		Object obj = null;

		if (site == Site.DB) {

			wr.println("P1 Protocol. What do you want?");
			wr.flush();
			String ans = sc.readLine();
			System.out.println(ans);

			if (ans.equals("Give me info!")) {
				for (int i = 0; i < 10; i++) { // tries to send result max 10
												// times
					wr.println("Ok, give me query");
					wr.flush();

					// get CachedRowSetImpl object from DB
					String sql = sc.readLine();System.out.println(sql);
					CachedRowSetImpl crs = bd.executeQuery(sql);

					wr.println("Port number for new connection?");
					wr.flush();
					String s = sc.readLine();
					System.out.println(s);
					int port = Integer.parseInt(s);
					Socket objsocket = new Socket();
					objsocket
							.connect(
									new InetSocketAddress(socket
											.getInetAddress(), port), 100);
					ObjectOutputStream oos = new ObjectOutputStream(
							objsocket.getOutputStream());
					oos.writeObject(crs);
					oos.flush();
					oos.close();
					objsocket.close();

					crs.close();

					String ans2 = sc.readLine();
					System.out.println(ans2);
					if (ans2.equals("Ok, got it")) {
						wr.println("Ok, bye!");
						wr.flush();
						sc.close();
						wr.close();
						return null;
					} else if (ans2.equals("Something went wrong!")) {
						continue;
					} else {
						wr.println("Protocol error! Bye!");
						wr.flush();
						sc.close();
						wr.close();
						return null;
					}
				}
				wr.println("I can't. Tell it to admin. Bye!");
				wr.flush();
				sc.close();
				wr.close();
				return null;
			}

			if (ans.equals("Update")) {
				wr.println("Ok, give me query");
				wr.flush();

				// get CachedRowSetImpl object from DB
				String sql = sc.readLine(); System.out.println(sql);
				bd.executeUpdate(sql);
				P2protocol p2 = new P2protocol(config, P2protocol.Site.DBInit, bd);
				InetSocketAddress address = socket.getInetAddress().equals(dbAdresses.get(0).getAddress()) ? dbAdresses.get(1) : dbAdresses.get(0);
				p2.initTalk(P2protocol.Request.UPDATE, query, address);
				

				wr.println("OK!"); wr.flush();
				sc.close();
				wr.close();
				return null;
			}

			if (ans.equals("Download")) {
				// zip ./pesel/* into pack.zip and send through socket
				path = sc.readLine();
				try {
					int BUFFER = 2048;
					BufferedInputStream origin = null;
					FileOutputStream dest = new FileOutputStream(".//pack.zip");
					CheckedOutputStream checksum = new CheckedOutputStream(
							dest, new Adler32());
					ZipOutputStream out = new ZipOutputStream(
							new BufferedOutputStream(checksum));

					byte data[] = new byte[BUFFER];
					File f = new File(".//" + pesel + "//");
					String files[] = f.list();

					for (int i = 0; i < files.length; i++) {
						// System.out.println("Adding: "+files[i]);
						FileInputStream fi = new FileInputStream(files[i]);
						origin = new BufferedInputStream(fi, BUFFER);
						ZipEntry entry = new ZipEntry(files[i]);
						out.putNextEntry(entry);
						int count;
						while ((count = origin.read(data, 0, BUFFER)) != -1) {
							out.write(data, 0, count);
						}
						origin.close();
					}
					out.close();
				} catch (IOException e) {
					System.out.println("Sorry, error :P");
					return null;
				}

				byte[] buffer = new byte[2048];
				BufferedInputStream in = new BufferedInputStream(
						new FileInputStream(".//" + pesel + "pack.zip"));

				wr.println("Port number for new connection?");
				wr.flush();
				String s = sc.readLine();
				System.out.println(s);
				int port = Integer.parseInt(s);
				Socket objsocket = new Socket();
				objsocket.connect(new InetSocketAddress(
						socket.getInetAddress(), port), 100);
				BufferedOutputStream oos = new BufferedOutputStream(
						objsocket.getOutputStream());
				int count;

				while ((count = in.read(buffer)) > 0) {
					oos.write(buffer, 0, count);
					oos.flush();
				}

				return true;
			}

			if (ans.equals("Upload")) {
				// receive a file through a socket
				try {
					FileOutputStream fos = new FileOutputStream(path);
					//BufferedOutputStream out = new BufferedOutputStream(fos);

					wr.println("Port number for new connection?");
					wr.flush();
					String s = sc.readLine();
					System.out.println(s);
					int port = Integer.parseInt(s);
					Socket objsocket = new Socket();
					objsocket
							.connect(
									new InetSocketAddress(socket
											.getInetAddress(), port), 100);
					BufferedInputStream bis = new BufferedInputStream(
							objsocket.getInputStream());

					byte[] buffer = new byte[2048];

					while ((bis.read(buffer)) > 0) {
						fos.write(buffer);
					}
					bis.close();
					fos.close();
				} catch (Exception e) {
					System.out.println("Error, sorry");
					return null;
				}
			}

			sc.close();
			wr.close();
			throw (new ProtocolException());

		} else { // site = user
			wr.println("P1");
			wr.flush();
			String s = sc.readLine();
			System.out.println(s);
			if (!s.equals("P1 Protocol. What do you want?")) {
				sc.close();
				wr.close();
				return null;
			}
			if (request == Request.INFO) {
				if (!logIn(socket, Site.User, con, pesel, pass))
					return null;

				wr.println("Give me info!");
				wr.flush();
				s = sc.readLine();
				System.out.println(s);
				while (s.equals("Ok, give me query")) {
					wr.println(query);
					wr.flush();

					ServerSocket objss = new ServerSocket(0);
					s = sc.readLine();
					System.out.println(); // Port number for new connection?
					wr.println(objss.getLocalPort());
					wr.flush();
					Socket objsocket = objss.accept();
					ObjectInputStream obstr = new ObjectInputStream(
							objsocket.getInputStream());
					obj = obstr.readObject();
					obstr.close();
					objsocket.close();
					objss.close();

					if (obj != null && obj instanceof CachedRowSetImpl) {
						wr.println("Ok, got it");
						wr.flush();
					} else {
						wr.println("Something went wrong!");
						wr.flush();
					}
					s = sc.readLine();
					System.out.println(s);
				}
				wr.close();
				sc.close();
				if (s.equals("Ok, bye!")) {
					return (ResultSet) obj;
				} else
					return null;
			}

			if (request == Request.UPDATE) {
				if (!logIn(socket, Site.User, con, pesel, pass))
					return null;
				
					wr.println(query); wr.flush();
					s = sc.readLine();
					wr.close();
					sc.close();
				if (s.equals("OK!")) {
					return Boolean.TRUE;
				} else
					return Boolean.FALSE;
			}

			if (request == Request.DOWNLOAD) {
				if (!logIn(socket, Site.User, con, pesel, pass))
					return null;

				wr.println("Download!");
				wr.flush();
				wr.println(pesel);
				wr.flush();

				try {
					ServerSocket objss = new ServerSocket(0);
					s = sc.readLine();
					System.out.println(); // Port number for new connection?
					wr.println(objss.getLocalPort());
					wr.flush();
					Socket objsocket = objss.accept();
					ObjectInputStream obstr = new ObjectInputStream(
							objsocket.getInputStream());

					FileOutputStream fos = new FileOutputStream(path);
					//BufferedOutputStream out = new BufferedOutputStream(fos);
					byte[] buffer = new byte[2048];
					while ((obstr.read(buffer)) > 0) {
						fos.write(buffer);
					}
					fos.close();
				} catch (Exception e) {
					System.out.println("Error, sorry");
					return null;
				}

				return true;
			}

			if (request == Request.UPLOAD) {
				if (!logIn(socket, Site.User, con, pesel, pass))
					return null;

				wr.println("Upload!");
				wr.flush();

				ServerSocket objss = new ServerSocket(0);
				s = sc.readLine();
				System.out.println(); // Port number for new connection?
				wr.println(objss.getLocalPort());
				wr.flush();
				Socket objsocket = objss.accept();
				ObjectOutputStream obstr = new ObjectOutputStream(
						objsocket.getOutputStream());

				byte[] buffer = new byte[2048];
				BufferedInputStream in = new BufferedInputStream(
						new FileInputStream(path));
				int count;

				while ((count = in.read(buffer)) > 0) {
					obstr.write(buffer, 0, count);
					obstr.flush();
				}

				return true;
			}
		}

		return null;
	}

	public static boolean logIn(Socket socket, Site site, Connection con,
			String login, String pass) throws IOException, SQLException {
		InputStream is = socket.getInputStream();
		OutputStream os = socket.getOutputStream();

		PrintWriter wr = new PrintWriter(os);
		BufferedReader sc = new BufferedReader(new InputStreamReader(is));
		//Object obj = null;

		if (site == Site.DB) {
			String peselc = sc.readLine();
			ResultSet result = null;

			Statement stat = con.createStatement();
			stat.execute("SET search_path TO 'dkm-healthcare'");
			result = stat
					.executeQuery("select n, n_iteracja from osoby where pesel = '"
							+ peselc + "';");
			result.next();
			int n = result.getInt(1);
			String n_iter = result.getString(2);

			wr.println(n);
			wr.flush();

			String pas = sc.readLine();

			if (DigestUtils.md5Hex(pas).equals(n_iter)) {
				stat.executeQuery("Update osoby set n=" + (n - 1) + ", n_iteracja = '"
						+ pas + "where pesel='" + peselc + "';");
				wr.println("OK");
				wr.flush();
			} else
				wr.println("Incorrect pass");
		} else { // site=user
			wr.println(login);
			wr.flush();

			int n = Integer.parseInt(sc.readLine());

			String res = pass;

			for (int i = 0; i < n; i++)
				res = DigestUtils.md5Hex(res);

			wr.println(res);
			wr.flush();

			String ans = sc.readLine();
			if (ans.equals("OK"))
				return true;
			else
				return false;
		}

		return false;
	}
}
