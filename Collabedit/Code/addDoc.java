/**
 * @author Shikha Soni, 
 * 		   Ketki Trimukhe  
 * 
 * This program is used to add documents to the document list  
 * 
 * 
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


/*
 * This class is used to design a UI for adding a document and 
 * adding users who can access the document
 * 
 * 
 */

public class addDoc {
	JTextField docNameField, userName;
	JLabel docNameLabel, userIDLabel;
	JButton openDoc, addUser, create, edit, goBack;
	JLabel selectedUsers, Finaldoc, useNames;
	JPanel panel1, panel2;
	int doc_num = 0, numberOfUsers;
	String doc_name = "Document" + doc_num;
	ArrayList<String> suggestedUsers = /* {"srs6573","kt512"}; */new ArrayList<String>();
	ArrayList<String> addedUsers = new ArrayList<String>();
	JFrame frame, confirm_Box;
	int yCordinate = 150;
	int window;
	Registry registry = null;
	RMI rmi;

	String username, password;

	
/*
 * Constructor which takes username, password and Rmi registry as parameters
 * 	
 */	
	addDoc(String username, String password, Registry registry) {
		// this.window = window;
		this.username = username;
		this.password = password;
		this.registry = registry;
		try {
			rmi = (RMI) registry.lookup("server");
		} catch (RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


/*
 * This method is the interface for the user to enter document details
 * and adding users to that document
 * 	
 */	
	public void view() {
		frame = new JFrame("New Document");
		frame.setSize(500, 500);
		frame.getDefaultCloseOperation();
		panel1 = new JPanel();
		panel1.setLayout(null);
		docNameLabel = new JLabel("Document Name");
		docNameLabel.setBounds(10, 100, 100, 30);

		docNameField = new JTextField(20);
		docNameField.setBounds(150, 100, 100, 30);

		userIDLabel = new JLabel("Search user ID");
		userIDLabel.setBounds(10, 200, 100, 30);

		userName = new JTextField();
		userName.setBounds(150, 200, 100, 30);

		openDoc = new JButton("Create");
		openDoc.setBounds(200, 300, 100, 30);

		addUser = new JButton("Add more");
		addUser.setBounds(300, 200, 100, 30);

		goBack = new JButton("Previous page");
		goBack.setBounds(150, 400, 200, 30);

		goBack.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("Previous page")) {
					new Create_Share(username, password, registry);
				}
			}
		});

		AddingUsers();
		openDoc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doc_name = docNameField.getText();
				if (e.getActionCommand().equals("Create")) {

					if (addedUsers.isEmpty()) {
						JOptionPane.showMessageDialog(null,
								"Enter atleast one user", "Error",
								JOptionPane.ERROR_MESSAGE);
					} else if (doc_name == null) {
						JOptionPane.showMessageDialog(null,
								"Document field cannot be empty", "Error",
								JOptionPane.ERROR_MESSAGE);
					} else {
						
							new EditorGUI(username, password, null, registry,
									doc_name);
						
						addedUsers.add(username);
						Document d = new Document(doc_name, null, addedUsers);
						try {
							rmi.addDoc(doc_name, d);
						} catch (RemoteException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						frame.setVisible(false);
					}
					// send that person a request
				}
			}
		});
		panel1.add(docNameField);
		panel1.add(docNameLabel);
		panel1.add(userIDLabel);
		panel1.add(userName);
		panel1.add(openDoc);
		panel1.add(addUser);
		panel1.add(goBack);
		frame.add(panel1);
		frame.setVisible(true);
	}

/*
 * This method adds entered users to access the document created above
 * 
 * 	
 */	
	public void AddingUsers() {
		userNameListener(userName);
		addUser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ((e.getActionCommand().equals("Add more"))) {
					/*if (!rmi.check(username)) {
						JOptionPane.showMessageDialog(null,
								"Username does not exist", "Error",
								JOptionPane.ERROR_MESSAGE);
					} else {*/
						addedUsers.add(userName.getText());
						if (!suggestedUsers.contains(userName.getText())) {
							suggestedUsers.add(userName.getText());
						//}
						userName.setText(null);
						userNameListener(userName);
						AddingUsers();
					}

				}
			}
		});
	}

/*
 * This method is to get the document of the particular user
 * 
 */
	
	public void userNameListener(JTextField userName) {
		userName.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				// Do nothing
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (e.getOffset() + e.getLength() == e.getDocument()
						.getLength())
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							predict(userName);
						}
					});
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// Do nothing
			}
		});
	}

/*
 * This method is used to predict certain usernames of 
 * users who have already used the system before
 * 	
 */
	
	private void predict(JTextField field) {
		String text = field.getText();
		String prediction = null;
		for (String client : suggestedUsers) {
			if (client.startsWith(text) && !client.equals(text)) {
				if (prediction != null)
					return;
				prediction = client;
			}
		}
		if (prediction != null) {
			field.setText(prediction);
			field.setCaretPosition(text.length());
			field.select(text.length(), prediction.length());
		}
	}

/*
 * This is the main method
 * 	
 */
	public static void main(String[] args) {
		// new addDoc(1).view();
	}
}
