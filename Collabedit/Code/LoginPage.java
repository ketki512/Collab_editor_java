/**
 * @author Shikha Soni, 
 * 		   Ketki Trimukhe  
 * 
 * This program is used as a login page for existing users 
 * and registration for new users
 * 
 * 
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;

/*
 * This class is used to create UI for login and registration page
 * 
 */

public class LoginPage extends UnicastRemoteObject implements RMI {

	private static final long serialVersionUID = 1L;
	Registry registry;

/*
 * Constructor 
 * 	
 */
	
	protected LoginPage() throws RemoteException {
		super();
		registry = null;
		// TODO Auto-generated constructor stub
	}

	JLabel userLabel, passwordLabel, userLabel1, userName;
	JButton loginButton, registerButton;
	JTextField userText;
	JPasswordField passwordtext;
	JFrame frame = new JFrame("Welcome to Collab Text");
	JPanel panel = new JPanel();
	String username, password;

	ArrayList<String> loginNAmes = new ArrayList<String>();

	public void start() {
		LoginPage myObj = this;
		frame.setSize(500, 500);
		panel.setLayout(null);

		userLabel = new JLabel("User name");
		userLabel.setBounds(10, 100, 100, 30);

		userText = new JTextField(20);
		userText.setBounds(100, 100, 200, 30);

		userName = new JLabel(
				"Enter an alphanumeric username of min 6 characters.");
		userName.setBounds(10, 150, 300, 30);

		passwordLabel = new JLabel("Password");
		passwordLabel.setBounds(10, 200, 100, 30);

		JPasswordField passwordText = new JPasswordField(20);
		passwordText.setBounds(100, 200, 200, 30);
		PlainDocument document = (PlainDocument) passwordText.getDocument();
		document.setDocumentFilter(new DocumentFilter() {

			@Override
			public void replace(DocumentFilter.FilterBypass fb, int offset,
					int length, String text, AttributeSet attrs)
					throws BadLocationException {
				String string = fb.getDocument().getText(0,
						fb.getDocument().getLength())
						+ text;

				if (string.length() <= 10) {
					super.replace(fb, offset, length, text, attrs);
				}
			}

		});

		userLabel1 = new JLabel("Enter upto 10 characters for password");
		userLabel1.setBounds(10, 250, 500, 30);

		loginButton = new JButton("Login");
		loginButton.setBounds(10, 300, 100, 30);
		loginButton.addActionListener(new ActionListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {

				if (e.getActionCommand().equals("Login")) {
					username = userText.getText();
					password = passwordText.getText();
					try {
						RMI rmi = (RMI) registry.lookup("server");
						boolean registered = rmi.checkUser(username, password); 
						if (!registered) {
							JOptionPane.showMessageDialog(null,
									"Login info wrong", "Error",
									JOptionPane.ERROR_MESSAGE);
						} else {
							new Create_Share(username, password, registry)
									.view();
							frame.setVisible(false);
							frame.dispose();
						}

					} catch (RemoteException | NotBoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});

		registerButton = new JButton("Register");
		registerButton.setBounds(180, 300, 100, 30);
		registerButton.addActionListener(new ActionListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("Register")) {
					username = userText.getText();
					password = passwordText.getText();
					if (userText.getText() == null
							|| userText.getText().length() < 5) {
						JOptionPane
								.showMessageDialog(
										null,
										"Enter username and password within constraints",
										"Error", JOptionPane.ERROR_MESSAGE);
					} else {
						try {
							RMI rmi = (RMI) registry.lookup("server");
							rmi.proxyRebind(username, myObj);
							rmi.registerUser(username, password);
							new Create_Share(username, password, registry)
									.view();
							frame.setVisible(false);
							frame.dispose();

						} catch (AccessException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (RemoteException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NotBoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		});
		panel.add(userText);
		panel.add(userName);
		panel.add(passwordLabel);
		panel.add(passwordText);
		panel.add(userLabel1);
		panel.add(loginButton);
		panel.add(userLabel);
		panel.add(registerButton);
		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

/*
 * This is the main method
 * 
 * 	
 */
	
	public static void main(String args[]) throws RemoteException {

		LoginPage login = new LoginPage();
		login.registry = LocateRegistry.getRegistry(args[0],
				Integer.parseInt(args[1]));
		login.start();
	}

	@Override
	public void proxyRebind(String id, RMI rmi) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerUser(String username, String password)
			throws RemoteException {
		// TODO Auto-generated method stub

	}

	/*
	 * @Override public void addUsers(String doc_id, ArrayList<String> list) {
	 * // TODO Auto-generated method stub
	 * 
	 * }
	 */

	@Override
	public void updateDoc(String docName, String content, String username )
			throws RemoteException {
		// TODO Auto-generated method stub

	}

	/*
	 * @Override public void addDocUsers(String username, Document d) throws
	 * RemoteException { // TODO Auto-generated method stub
	 * 
	 * }
	 */

	@Override
	public ArrayList<Document> getList(String username) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkUser(String username, String password)
			throws RemoteException {
		return false;
		// TODO Auto-generated method stub

	}

	@Override
	public void addDoc(String doc_id, Document d) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean check(String username) throws RemoteException {
		return false;
		// TODO Auto-generated method stub
		
	}

	@Override
	public String content(String doc_name) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
}
