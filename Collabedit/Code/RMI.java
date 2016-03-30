/**
 * @author Shikha Soni, 
 * 		   Ketki Trimukhe  
 * 
 * This is the Rmi interface   
 * 
 * 
 */
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;



public interface RMI extends Remote {

	public void proxyRebind(String id, RMI rmi) throws RemoteException;
	
	public boolean checkUser(String username, String password) throws RemoteException;
	
	public void updateDoc(String docName, String content, String username) throws RemoteException;
	
	public void registerUser(String username, String password) throws RemoteException;
	
	public void addDoc(String doc_id, Document d) throws RemoteException;
	
	public ArrayList<Document> getList(String username) throws RemoteException;
	
	public boolean check(String username) throws RemoteException;
	
	public String content(String docName) throws RemoteException;
	
}
