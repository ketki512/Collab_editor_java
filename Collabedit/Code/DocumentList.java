/**
 * @author Shikha Soni, 
 * 		   Ketki Trimukhe  
 * 
 * This program is used to maintain the list of documents
 * and its content for different users
 * 
 */

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

/*
 * This class displays a list of documents shared by the users 
 * and on double click opens the document and displays the content in it.
 *
 */

public class DocumentList {
	static JButton Open;
	String doc_name, username, password, content;
	Registry registry = null;
	DefaultListModel<String> Doclist = new DefaultListModel<>();
	ArrayList<Document> Doc_list;
	RMI rmi= null;

	
/*
 * Constructor which takes username, password and Rmi registry as parameters
 * 	
 */
	
	DocumentList(String username, String password, Registry registry)
			throws AccessException, RemoteException, NotBoundException {
		 rmi= (RMI) registry.lookup("server");
		Doc_list = (ArrayList<Document>)rmi.getList(username);
		System.out.println(Doc_list.size());
		for (int index = 0; index < Doc_list.size(); index++) {
			Doclist.addElement(Doc_list.get(index).docName);
		}
	}

/*
 * This method displays the document list and defines 
 * a mouse event to get the content on double clicking the document name.
 * 	
 */
	
	public void displayDocument() {
		String Documents[] = new String[20];// This document is a document
											// object list and the list will
											// store the doc object.string name

		for (int i = 0; i < 20; i++) { // this has to be replaced
			Documents[i] = "Document" + i;
		}

		String title = "All Documents";
		JFrame f = new JFrame(title);
		f.getDefaultCloseOperation();
		JList<String> list = new JList<String>(Doclist);
		JScrollPane scrollPane = new JScrollPane(list);

		Container contentPane = f.getContentPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);

		MouseListener mouseListener = new MouseAdapter() {

			public void mouseClicked(MouseEvent mouseEvent) {
				JList<?> theList = (JList<?>) mouseEvent.getSource();
				if (mouseEvent.getClickCount() == 2) {
					int index = theList.locationToIndex(mouseEvent.getPoint());
					if (index >= 0) {
						Object o = theList.getModel().getElementAt(index);
							try {
								System.out.println(o.toString());
								String write=rmi.content(o.toString());
								System.out.println(write);
								new EditorGUI(username, password, write, registry,
										o.toString());
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					}
				}
			}
		};
		list.addMouseListener(mouseListener);

		f.setSize(500, 500);
		f.setVisible(true);
	}

/*
 * This is the main method
 * 	
 */	
	
	public static void main(String args[]) {

	}
}