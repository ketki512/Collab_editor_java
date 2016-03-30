/**
 * @author Shikha Soni, 
 * 		   Ketki Trimukhe  
 * 
 * This program is used to create a UI for editing a document or creating a new document
 * 
 * 
 */


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;


/*
 * This class created a UI for editor and different menus like file, edit and previous window
 * 
 */


@SuppressWarnings("serial")
public class EditorGUI extends JFrame implements ActionListener/*, RMI*/ {

	String doc_name, username, password, content;
	RMI rmi=null;
	Registry registry= null;
	public static void main(String[] args) {
		//new EditorGUI("","","","");
	}

	// ============================================
	// FIELDS
	// ============================================

	// Menus
	private JMenu fileMenu;
	private JMenu editMenu;
	private JMenu prevPage;
	private JMenuItem newFile, openFile, saveFile, saveAsFile, pageSetup,
			printFile, exit;
	private JMenuItem undoEdit, redoEdit, selectAll, copy, paste, cut;
	private JMenuItem addDoc, documentList;

	// Window
	private JFrame editorWindow;

	// Text Area
	private Border textBorder;
	private JScrollPane scroll;
	private JTextArea textArea;
	private Font textFont;

	// Window
	private JFrame window;

	// Printing
	private PrinterJob job;
	public PageFormat format;

	// Is File Saved/Opened
	private boolean opened = false;
	private boolean saved = false;

	// Record Open File for quick saving
	private File openedFile;

	// Undo manager for managing the storage of the undos
	// so that the can be redone if requested
	private UndoManager undo;

	// ============================================
	// CONSTRUCTOR
	// ============================================

/*
 * Constructor which takes username, password ,Rmi registry, content, document name as parameters
 * 	
 */		
	
	public EditorGUI(String username, String password, String content,Registry registry,String doc_name){
		super("Shared Document");
		this.username= username;
		this.password= password;
		this.registry= registry;
		this.content= content;
		this.doc_name = doc_name;
		// Create Menus
		fileMenu();
		editMenu();
		prevPage();

		// Create Text Area
		createTextArea();

		// Create Undo Manager for managing undo/redo commands
		undoMan();

		// Create Window
		createEditorWindow();

	}
	
/*
 * Method to create editor window
 * 
 */
	
	private JFrame createEditorWindow() {
		editorWindow = new JFrame(doc_name);
		editorWindow.setVisible(true);
		editorWindow.setExtendedState(Frame.MAXIMIZED_BOTH);
		editorWindow.getDefaultCloseOperation();
		// Create Menu Bar
		editorWindow.setJMenuBar(createMenuBar());
		editorWindow.add(scroll, BorderLayout.CENTER);
		editorWindow.pack();
		// Centers application on screen
		editorWindow.setLocationRelativeTo(null);
		return editorWindow;
	}

	
/*
 * Method to define text area properties
 * 	
 */
	
	private JTextArea createTextArea() {
		textBorder = BorderFactory.createBevelBorder(0, Color.RED, Color.RED);
		textArea = new JTextArea(30, 50);
		textArea.setEditable(true);
		textArea.setBorder(BorderFactory.createCompoundBorder(textBorder,
				BorderFactory.createEmptyBorder(2, 5, 0, 0)));

		textFont = new Font("Verdana", 0, 14);
		textArea.setFont(textFont);

		scroll = new JScrollPane(textArea,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		/*textArea.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				System.out.println("Click position (X, Y):  " + e.getX() + ", "
						+ e.getY());
			}
		});*/
		textArea.setText(content);
		
		textArea.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				updateDoc(e);
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateDoc(e);
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				updateDoc(e);	
			}
		});
		return textArea;
	}
	
	
/*
 * This method is used to update the document whenever the user modifies the document
 * 	
 */
	
	 public void updateDoc(DocumentEvent e) {
     	Document docm = (Document)e.getDocument();
     	try {
				 String content=docm.getText(0, docm.getLength());
				// System.out.println(content);
				try {
						try {
							rmi=(RMI)registry.lookup("server");
						} catch (NotBoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					rmi.updateDoc(doc_name, content, username);
					//textArea.replaceRange(rmi.getContent(doc_name), 0, docm.getLength());
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	 }
	
/*
 * Method to create UI for menu bar
 * 
 */
	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(prevPage);

		return menuBar;
	}

/*
 * Method for undo and redo functions in the document
 * 	
 */
	
	private UndoManager undoMan() {
		// Listener for undo and redo functions to document
		undo = new UndoManager();
		textArea.getDocument().addUndoableEditListener(
				new UndoableEditListener() {

					@Override
					public void undoableEditHappened(UndoableEditEvent e) {
						undo.addEdit(e.getEdit());
					}
				});

		return undo;
	}

/*
 * Method to display UI for File menu
 * 	
 */
	
	private void fileMenu() {
		// Create File Menu
		fileMenu = new JMenu("File");
		fileMenu.setPreferredSize(new Dimension(40, 20));

		// Add file menu items
		newFile = new JMenuItem("New");
		newFile.addActionListener(this);
		newFile.setPreferredSize(new Dimension(100, 20));
		newFile.setEnabled(true);

		openFile = new JMenuItem("Open...");
		openFile.addActionListener(this);
		openFile.setPreferredSize(new Dimension(100, 20));
		openFile.setEnabled(true);

		saveFile = new JMenuItem("Save");
		saveFile.addActionListener(this);
		saveFile.setPreferredSize(new Dimension(100, 20));
		saveFile.setEnabled(true);

		saveAsFile = new JMenuItem("Save As...");
		saveAsFile.addActionListener(this);
		saveAsFile.setPreferredSize(new Dimension(100, 20));
		saveAsFile.setEnabled(true);

		pageSetup = new JMenuItem("Page Setup...");
		pageSetup.addActionListener(this);
		pageSetup.setPreferredSize(new Dimension(100, 20));
		pageSetup.setEnabled(true);

		printFile = new JMenuItem("Print...");
		printFile.addActionListener(this);
		printFile.setPreferredSize(new Dimension(100, 20));
		printFile.setEnabled(true);

		exit = new JMenuItem("Exit");
		exit.addActionListener(this);
		exit.setPreferredSize(new Dimension(100, 20));
		exit.setEnabled(true);

		// Add items to menu
		fileMenu.add(newFile);
		fileMenu.add(openFile);
		fileMenu.add(saveFile);
		fileMenu.add(saveAsFile);
		fileMenu.add(pageSetup);
		fileMenu.add(printFile);
		fileMenu.add(exit);
	}

/*
 * Method to display UI for edit menu
 * 	
 */
	
	private void editMenu() {
		editMenu = new JMenu("Edit");
		editMenu.setPreferredSize(new Dimension(40, 20));

		// Add file menu items
		undoEdit = new JMenuItem("Undo");
		undoEdit.addActionListener(this);
		undoEdit.setPreferredSize(new Dimension(100, 20));
		undoEdit.setEnabled(true);

		redoEdit = new JMenuItem("Redo");
		redoEdit.addActionListener(this);
		redoEdit.setPreferredSize(new Dimension(100, 20));
		redoEdit.setEnabled(true);

		selectAll = new JMenuItem("Select All");
		selectAll.addActionListener(this);
		selectAll.setPreferredSize(new Dimension(100, 20));
		selectAll.setEnabled(true);

		copy = new JMenuItem("Copy");
		copy.addActionListener(this);
		copy.setPreferredSize(new Dimension(100, 20));
		copy.setEnabled(true);

		paste = new JMenuItem("Paste");
		paste.addActionListener(this);
		paste.setPreferredSize(new Dimension(100, 20));
		paste.setEnabled(true);

		cut = new JMenuItem("Cut");
		cut.addActionListener(this);
		cut.setPreferredSize(new Dimension(100, 20));
		cut.setEnabled(true);

		// Add items to menu
		editMenu.add(undoEdit);
		editMenu.add(redoEdit);
		editMenu.add(selectAll);
		editMenu.add(copy);
		editMenu.add(paste);
		editMenu.add(cut);
	}

/*
 * Method to display previous window
 * 	
 */	
	private void prevPage() {
		prevPage = new JMenu("PrevWindows");
		prevPage.setPreferredSize(new Dimension(150, 20));

		// add prevPage items
		addDoc = new JMenuItem("addDoc");
		addDoc.addActionListener(this);
		addDoc.setPreferredSize(new Dimension(150, 20));
		addDoc.setEnabled(true);

		documentList = new JMenuItem("Document List");
		documentList.addActionListener(this);
		documentList.setPreferredSize(new Dimension(150, 20));
		documentList.setEnabled(true);

		// Adding items to menu
		prevPage.add(addDoc);
		prevPage.add(documentList);
	}

/*
 * Method for saving files 
 */
	private void saveFile(File filename) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			writer.write(textArea.getText());
			writer.close();
			saved = true;
			window.setTitle("JavaText - " + filename.getName());
		} catch (IOException err) {
			err.printStackTrace();
		}
	}

/*
 * Method for quick saving files
 */
	private void quickSave(File filename) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			writer.write(textArea.getText());
			writer.close();
		} catch (IOException err) {
			err.printStackTrace();
		}
	}

/*
 * 
 * Method for opening files
 */
	private void openingFiles(File filename) {
		try {
			openedFile = filename;
			FileReader reader = new FileReader(filename);
			textArea.read(reader, null);
			opened = true;
			window.setTitle("JavaEdit - " + filename.getName());
		} catch (IOException err) {
			err.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == newFile) {
			//new addDoc(1, username, password, registry).view();
		} else if (event.getSource() == openFile) {
			JFileChooser open = new JFileChooser();
			open.showOpenDialog(null);
			File file = open.getSelectedFile();
			openingFiles(file);
		} else if (event.getSource() == saveFile) {
			JFileChooser save = new JFileChooser();
			File filename = save.getSelectedFile();
			if (opened == false && saved == false) {
				save.showSaveDialog(null);
				int confirmationResult;
				if (filename.exists()) {
					confirmationResult = JOptionPane.showConfirmDialog(
							saveFile, "Replace existing file?");
					if (confirmationResult == JOptionPane.YES_OPTION) {
						saveFile(filename);
					}
				} else {
					saveFile(filename);
				}
			} else {
				quickSave(openedFile);
			}
		} else if (event.getSource() == saveAsFile) {
			JFileChooser saveAs = new JFileChooser();
			saveAs.showSaveDialog(null);
			File filename = saveAs.getSelectedFile();
			int confirmationResult;
			if (filename.exists()) {
				confirmationResult = JOptionPane.showConfirmDialog(saveAsFile,
						"Replace existing file?");
				if (confirmationResult == JOptionPane.YES_OPTION) {
					saveFile(filename);
				}
			} else {
				saveFile(filename);
			}
		} else if (event.getSource() == pageSetup) {
			job = PrinterJob.getPrinterJob();
			format = job.pageDialog(job.defaultPage());
		} else if (event.getSource() == printFile) {
			job = PrinterJob.getPrinterJob();
			if (job.printDialog()) {
				try {
					job.print();
				} catch (PrinterException err) {
					err.printStackTrace();
				}
			}
		} else if (event.getSource() == exit) {
			System.exit(0);
		} else if (event.getSource() == undoEdit) {
			try {
				undo.undo();
			} catch (CannotUndoException cu) {
				cu.printStackTrace();
			}
		} else if (event.getSource() == redoEdit) {
			try {
				undo.redo();
			} catch (CannotUndoException cur) {
				cur.printStackTrace();
			}
		} else if (event.getSource() == selectAll) {
			textArea.selectAll();
		} else if (event.getSource() == copy) {
			textArea.copy();
		} else if (event.getSource() == paste) {
			textArea.paste();
		} else if (event.getSource() == cut) {
			textArea.cut();
		} else if (event.getSource() == addDoc) {
			new addDoc(username,password,registry);
		} else if (event.getSource() == documentList) {
			try {
				new DocumentList(username, password, registry).displayDocument();
			} catch (RemoteException | NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// ============================================
	// GETTERS AND SETTERS
	// ============================================

	public JTextArea getTextArea() {
		return textArea;
	}

	public void setTextArea(JTextArea text) {
		textArea = text;
	}
}