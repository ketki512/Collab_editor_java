/**
 * @author Shikha Soni, 
 * 		   Ketki Trimukhe  
 * 
 * This program is used to create a document object 
 * when a new editor window is opened.
 * 
 * 
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * Class to create document objects for every new document.
 * 
 */

public class Document implements Serializable {

	private static final long serialVersionUID = 1L;
	public String docName;
	public String content;
	public List<String> users;

/*
 * Constructor consisting of document name, content and list of users as parameters.
 * 
 * 	
 */
	public Document(String docName, String content, List<String> users){
		this.docName=docName;
		this.content=content;
		this.users = new ArrayList<String>();
		this.users.addAll(users);
	}
	
/*
 * Method used to add users to the document
 * 	
 */
	public void addUser(String user) {
		users.add(user);
	}

/*
 * Method to check whether the document belongs to a particular user or not.
 * 	
 */
	public boolean contains(String username) {
		for (String user : users) {
			if (user.equals(username)) {
				return true;
			}
		}
		return false;
	}
	public String toString(){
		return this.docName;
	}
	
 
/*
 * This is the main method
 * 
 * 	
 */
	public static void main(String[] args){
	 
	 ArrayList<String> temp = new ArrayList<String>();
		temp.add("abc");
	 System.out.println(new Document("sample", "sample", temp));
 }
}
