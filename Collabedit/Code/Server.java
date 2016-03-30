/**
 * @author Shikha Soni, 
 * 		   Ketki Trimukhe  
 * 
 * This program is the server program  
 * 
 * 
 */

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Server extends UnicastRemoteObject implements RMI {
	private static final long serialVersionUID = 1L;
	Registry registry;

	Map<String, Document> docs;

	// username and password
	HashMap<String, String> user_pass = new HashMap<String, String>();


/*
 * Constructor	
 */	
	protected Server() throws RemoteException {
		super();
		registry = null;
		docs = new HashMap<String, Document>();
		// TODO Auto-generated constructor stub
	}

/*
 * This method registers a new user by putting the 
 * username and password in the map.
 * 	
 */	
	public void register(String username, String pass) {
		user_pass.put(username, pass);
	}

/*
 * 
 * This is the main method
 * 	
 */
	public static void main(String[] args) throws NumberFormatException,
			RemoteException, UnknownHostException, InterruptedException {
		
		 ArrayList<String> temp = new ArrayList<String>();
			temp.add("abc");
		 System.out.println(new Document("sample", "sample", temp));
		
		Server s = new Server();
		s.createServer(Integer.parseInt(args[0]));
		while (true) {
			Thread.sleep(100);
		}

	}

/*
 * This method is used to start the server and the rmi registry
 * 	
 */	
	private void createServer(int port) throws RemoteException,
			UnknownHostException {
		registry = LocateRegistry.getRegistry(port);
		registry.rebind("server", this);
		System.out.println(InetAddress.getLocalHost().getHostAddress());

	}
/*
 * RMI binding
 * (non-Javadoc)
 * @see RMI#proxyRebind(java.lang.String, RMI)
 */
	@Override
	public void proxyRebind(String id, RMI rmi) throws RemoteException {
		registry.rebind(id, rmi);
	}

/*
 * Method to register new users
 * 	(non-Javadoc)
 * @see RMI#registerUser(java.lang.String, java.lang.String)
 */
	@Override
	public void registerUser(String username, String password)
			throws RemoteException {
		user_pass.put(username, password);
	}

	@Override
	
/*
 * Method to add documents
 * 
 * 	(non-Javadoc)
 * @see RMI#addDoc(java.lang.String, Document)
 */
	public void addDoc(String doc_id, Document d) throws RemoteException {
		docs.put(doc_id, d);
	}

	@Override
/*
 * 
 * Method to get list of documents with the users
 * (non-Javadoc)
 * @see RMI#getList(java.lang.String)
 */
	public ArrayList<Document> getList(String username) throws RemoteException {
		ArrayList<Document> userDocs = new ArrayList<Document>();
		Iterator it = docs.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			Document d = (Document) pair.getValue();
			
			if (d.users.contains(username)) {
				userDocs.add(d);
			}
		}
		return userDocs;
	}

	@Override
/*
 * Method to update the contents of the document
 * 	(non-Javadoc)
 * @see RMI#updateDoc(java.lang.String, java.lang.String, java.lang.String)
 */
	public void updateDoc(String docName, String content, String username)
			throws RemoteException {
		Document d = docs.get(docName);
		d.content = content;
		//call the update for all the users 
	}

	@Override
/*
 * Method to check whether the username nad password match and are valid
 * 	(non-Javadoc)
 * @see RMI#checkUser(java.lang.String, java.lang.String)
 */
	public boolean checkUser(String username, String password)
			throws RemoteException {
		if (user_pass.get(username).equals(password)) {
			return true;
		} else {
			return false;
		}
	}

	
/*
 * Method to get the name of the document
 * 	(non-Javadoc)
 * @see RMI#content(java.lang.String)
 */
	public String content(String docName) {
		Document d = docs.get(docName);
		return d.content;
	}

	@Override
	public boolean check(String username) throws RemoteException {
		if(user_pass.containsKey(username)){
			return true;
		}
		else
		return false;
	}
}
