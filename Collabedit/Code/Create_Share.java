/**
 * @author Shikha Soni, 
 * 		   Ketki Trimukhe  
 * 
 * This program is basically to create new document for a user 
 * and check the shared documents  
 * 
 * 
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 * This class is used to display an interface to create documents and
 * view the shared documents of users. 
 * 
 */

public class Create_Share {
	JFrame frame1;
	JPanel panel;
	int window;
	JButton createButton, requestButton, sharedButton, openDoc;
	String username, password;
	Registry registry= null;
	RMI rmi=null;

/*
 * Constructor which takes username, password and Rmi registry as parameters
 * 	
 */
	
	Create_Share(String username, String password, Registry registry) {
		//this.window = window;
		this.password= password;
		this.username=username;
		this.registry=registry;
	}

/*
 * This method creates the User interface for user interaction
 * 	
 */
	
	public void view() {
		frame1 = new JFrame("");
		frame1.setSize(500, 500);
		frame1.getDefaultCloseOperation();

		panel = new JPanel();
		
		panel.setLayout(null);
		createButton = new JButton("Create");
		createButton.setBounds(100, 50, 300, 80);
		
		createButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("Create")) {
					new addDoc(username, password, registry).view();
					frame1.setVisible(false);
				}
			}

		});
		
			sharedButton = new JButton("Shared Documents");
			sharedButton.setBounds(100, 350, 300, 80);
			sharedButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (e.getActionCommand().equals("Shared Documents")) {
						try {
							new DocumentList(username, password, registry).displayDocument();;
						} catch (RemoteException | NotBoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						frame1.setVisible(false);
					}
				}
			});
			panel.add(createButton);
			panel.add(sharedButton);
			frame1.add(panel);
			frame1.setVisible(true);
		
	}
}
