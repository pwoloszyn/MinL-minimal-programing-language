// Made by: Piotr Woloszyn 2015
// Build: 6
// Documentation in separate file

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;


public class MinL extends JFrame implements ActionListener{
	
	//--------------------- GUI variables ---------------------//
	
	JPanel mainpanel = new JPanel();
	JTabbedPane tabbedPane = new JTabbedPane();
	MinLConsole minc;
	ProgramRunFolder prf;
	LinkedList<MinLFile> MinLFile = new LinkedList<MinLFile>();
	
	//----------------- Interpreter variables -----------------//
	
	Map<String,Integer> int_variables = new HashMap<String, Integer>();
	Map<String,Double> double_variables = new HashMap<String, Double>();
	Map<String,String> string_variables = new HashMap<String, String>();
	Map<String,String> std_functions = new HashMap<String, String>(); // void return type
	Map<String,String> ret_functions = new HashMap<String, String>(); // return something
	
	//---------------------------------------------------------//
	
	
	// Constructor
	public MinL() {
		super("MinL Editor");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		// Do not uncomment this, unless there is a 20x20 .png file in the img folder
		// called MLicon.png
		//ImageIcon img0 = new ImageIcon(this.getClass().getResource("MLicon.png"));	
		//this.setIconImage(img0.getImage());		
		//addTab("Untitled.minl");

		JButton btn_new = new JButton("New file");
		JButton btn_save = new JButton("Save file");
		JButton btn_load = new JButton("Load file");
		JButton btn_rename = new JButton("Rename");
		JButton btn_close = new JButton("Close file");
		JButton btn_run = new JButton("Run");
		JButton btn_exit = new JButton("Exit");
		
		JPanel btnpane = new JPanel();
		
		btn_new.addActionListener(this);	
		btn_new.setActionCommand("New");
		btn_save.addActionListener(this);	
		btn_save.setActionCommand("Save");
		btn_load.addActionListener(this);	
		btn_load.setActionCommand("Load");
		btn_rename.addActionListener(this);	
		btn_rename.setActionCommand("Rename");
		btn_close.addActionListener(this);	
		btn_close.setActionCommand("Close");
		btn_run.addActionListener(this);	
		btn_run.setActionCommand("Run");
		btn_exit.addActionListener(this);	
		btn_exit.setActionCommand("Exit");
		
		minc = new MinLConsole(btn_run, btn_exit, this);	
		
		btnpane.add(btn_new);
		btnpane.add(btn_save);
		btnpane.add(btn_load);
		btnpane.add(btn_rename);
		btnpane.add(btn_close);
		btnpane.add(btn_run);
		btnpane.add(btn_exit);
		
		prf = new ProgramRunFolder();
		
		mainpanel.setLayout(new GridBagLayout());
		GridBagConstraints constr = new GridBagConstraints();	
		
		constr.fill = GridBagConstraints.BOTH;
		constr.weightx = 1.0;
		constr.weighty = 3.0;
		constr.gridwidth = 3;
		constr.gridx = 0;
		constr.gridy = 1;
		mainpanel.add(tabbedPane,constr);
		
		constr.fill = GridBagConstraints.BOTH;
		constr.weightx = 1.0;
		constr.weighty = 1.0;
		constr.insets = new Insets(3,3,3,3);
		constr.gridwidth = 3;
		constr.gridx = 0;
		constr.gridy = 2;
		mainpanel.add(minc,constr);	
		
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.weightx = 0.1;
		constr.weighty = 0.1;
		constr.gridwidth = 1;
		constr.gridx = 0;
		constr.gridy = 0;
		mainpanel.add(prf,constr);
			
		constr.fill = GridBagConstraints.NONE;
		constr.weightx = 0.0;
		constr.weighty = 0.0;
		constr.anchor = GridBagConstraints.LINE_END;
		constr.gridwidth = 2;
		constr.gridx = 1;
		constr.gridy = 0;
		mainpanel.add(btnpane,constr);
		
		Container contentPane = getContentPane();
		contentPane.add(mainpanel, BorderLayout.CENTER);
		
		setSize(900,600);
		
		this.setVisible(true);	
	}
	
	// This method adds new tabs to the tab pane
	public void addTab(String filename) {
		
		MinLFile nminl = new MinLFile(filename);
		JComponent panel = nminl;
		
		tabbedPane.addTab(filename, null, panel,
				filename);		
		tabbedPane.setMnemonicAt(0, 0);
	}
	
	//===========================================================================================//
	// THIS IS WHERE THE MAGIC HAPPENS
	// The interpreter method
	public void interpretorMainMethod(String code) {
		//TODO
		
		boolean read_function = false;
		int bracket_depth = 0;
		
		boolean loop_active = false;
		int loop_length = 0;
		
		boolean conditional_state = true;
		
		boolean skip_line = false;
		
		//Key words:
		String definition = "def";
		String output = "output";
		String loop = "loop";
		String increment = "inc";
		String decrement = "dec";
		String condition = "cond";
		String variable = "var";
		String loadfile = "loadfile";
		
		Scanner line_scanner = new Scanner(code);
		
		// This String contains bracketed code
		// which will be passed to a 
		String passed_code = "";
		
		while(line_scanner.hasNextLine()) {
			String line_temp = line_scanner.nextLine();
			String line = "";
			
			for(int i = 0; i<line_temp.length(); i++) {
				if(line_temp.charAt(i) != ' ') {
					line = line_temp.substring(i);
					i = 9999;
				}
			}
			
			int line_length = line.length();
			
			if(line.equals("(")) {
				bracket_depth++;
				if(bracket_depth == 1)
					skip_line = true;
			} else if(line.equals(")")) {
				bracket_depth--;
			} else if(line.isEmpty()) {
				skip_line = true;
			}
			
			if(line_length > 2) {
				if(line.substring(0, 2).equals("//"))
					skip_line = true;
			}
				
			if(!skip_line) {
				if(bracket_depth == 0 && read_function) {
					
					
					read_function = false;
					passed_code = "";
				} else if(bracket_depth == 0 && loop_active){				
					for(int i=0; i<loop_length; i++)
						interpretorMainMethod(passed_code);				
					loop_active = false;
					passed_code = "";
				} else if(bracket_depth == 0 && !conditional_state) {		
					conditional_state = true;;
				} else if(read_function || loop_active) {
					passed_code += line + "\n";
				} else {
					
					int empty_space = 0;
					boolean space_error = false;
					
					while(line.charAt(empty_space) == ' ' || space_error) {
						empty_space++;
						if(empty_space == 50) {
							System.out.println("Error: Spaced line");
							space_error = true;
						}
					}
					
					// def statement
					if(line.substring(empty_space, empty_space+4).equals("def ")) {
						int end_of_word_offset;
						String var_name = "@@@";
						String var_definition = "@@@";
						boolean comma_present = false;
						int offset = empty_space+4;
						while(offset < line_length) {
							if(line.charAt(offset) == ' ')
								offset++;
							else {
								if(var_name.equals("@@@")) {
									end_of_word_offset = nextWordEndIndex(line, offset);
									var_name = line.substring(offset, end_of_word_offset);
									offset = end_of_word_offset;
								} else if(!comma_present && line.charAt(offset) == ':') {
									comma_present = true;
									offset++;
								} else if(var_definition.equals("@@@") && comma_present) {
									var_definition = line.substring(offset, line_length);
									offset = line_length;
								} else {
									offset = line_length;
								}
							}
						}
						if(var_name.equals("@@@") || var_definition.equals("@@@") || !comma_present) {
							System.out.println("Error: bad definition syntax");
						} else {
							if(isNumber(var_definition)) {
								double_variables.put(var_name, 0.0 + Integer.parseInt(var_definition));
							} else {
								string_variables.put(var_name, var_definition);
							}
						}
					}				
					// inc statement
					else if(line.substring(empty_space, empty_space+4).equals("inc ")) {
						String var_name;
						int offset = empty_space+4;
						
						while(offset < line_length) {
							if(line.charAt(offset) == ' ') {
								offset++;
							} else {
								var_name = line.substring(offset, line_length);
								if(!var_name.contains(" ")) {
									if(double_variables.containsKey(var_name))
										double_variables.put(var_name, double_variables.get(var_name)+1.0);
									else
										System.out.println("Error: bad increment syntax");
								} else {
									System.out.println("Error: bad increment syntax");
								}
								offset = line_length;
							}
						}
					}
					// dec statement
					else if(line.substring(empty_space, empty_space+4).equals("dec ")) {
						String var_name;
						int offset = empty_space+4;
						
						while(offset < line_length) {
							if(line.charAt(offset) == ' ') {
								offset++;
							} else {
								var_name = line.substring(offset, line_length);
								if(!var_name.contains(" ")) {
									if(double_variables.containsKey(var_name))
										double_variables.put(var_name, double_variables.get(var_name)-1.0);
									else
										System.out.println("Error: bad increment syntax");
								} else {
									System.out.println("Error: bad increment syntax");
								}
								offset = line_length;
							}
						}
						
					}
					// output statement
					else if(line.substring(empty_space, empty_space+9).equals("output : ")) {
						int end_of_word_offset;
						String output_string = "";
						String temp_word;
						int offset = 9;
						while(offset < line_length) {
							if(line.charAt(offset) == ' ') {
								output_string += line.charAt(offset);
								offset++;
							} else {
								end_of_word_offset = nextWordEndIndex(line, offset);
								temp_word = line.substring(offset, end_of_word_offset);
								offset = end_of_word_offset;
								if(temp_word.length() > 4) {
									if(temp_word.substring(0, 4).equals("var.")) {
										String key = temp_word.substring(4, temp_word.length());
										if(double_variables.containsKey(key)) {
											output_string += double_variables.get(key);
										} else if(string_variables.containsKey(key)) {
											output_string += string_variables.get(key);
										} else {
											System.out.println("Error: bad variable call in output.");
										}
									} else {
										output_string += temp_word;
									}
								} else {
									output_string += temp_word;
								}
							}
						}
						minc.consolearea.append(output_string+"\n");
					}
					// loop statement
					else if(line.substring(empty_space, empty_space+5).equals("loop ")) {
						String next_element = line.substring(empty_space+5, nextWordEndIndex(line, empty_space+5));
						
					}
					// if statement
					else if(line.substring(empty_space, empty_space+3).equals("if ")) {
						String next_element = line.substring(empty_space+3, nextWordEndIndex(line, empty_space+3));
						
					}
					// loadfile statement
					else if(line.substring(empty_space, empty_space+9).equals("loadfile ")) {
						String next_element = line.substring(empty_space+9, nextWordEndIndex(line, empty_space+9));
						
					}										
					// function declaration/call
					else if(line.substring(empty_space, empty_space+2).equals("f.")) {
						String next_element = line.substring(empty_space+2, nextWordEndIndex(line, empty_space+2));
						
					}
					// report error
					else {
						System.out.println("Unknown operation: "+line);
					}
					
				}	
				
				
			} else {
				skip_line = false;
			}
			
		}
	
	}
	
	public String returningFunctionHandler(String code) {
		String ret = "";
			
		return ret;
	}
	
	
	//-------------------------------------------------------------------------------------------
	// This method changes the variable names in the declared function from
	// the ones specified by the coder to ones whose names match the positions
	// of the variables that follow the function name on call.
	// Example:
	//
	// code can be:
	// f.add x y ret    <--- first line, the variable names are not saved, only the name f.add is
	// (
	//   def ret x
	//   lup y
	//   (
	//     inc ret
	//   )
	// )
	//
	// The variables x and y will be renamed to $p0 and $p1 so that when the function is called:
	// f.add 2 3, the code will know that the first variable '2' will go to locations specified
	// by $p0 and '3' to locations specified by $p1.
	//--------------------------------------------------------------------------------------------
	public String modFunction(String first_line, String code) {
		int var_count = 0;
		String var_names[] = new String[10];	
		
		String current_word = "";
		boolean function_name_found = false;
		for(int i=0; i<first_line.length(); i++) {
			if(first_line.charAt(i) != ' ' && i<first_line.length()-1) {
				current_word += first_line.charAt(i);
			} else if(!function_name_found) {
				function_name_found = true;
				current_word = "";
			} else if(i == first_line.length()-1) {
				current_word += first_line.charAt(i);
				if(!current_word.equals("ret")) {
					var_names[var_count] = current_word;
					current_word = "";
					var_count++;
				}
			} else {
				var_names[var_count] = current_word;
				current_word = "";
				var_count++;
			}			
		}	
		Scanner code_scanner = new Scanner(code);
		String code_line;
		String final_code = "";
		
		while(code_scanner.hasNextLine()) {
			code_line = code_scanner.nextLine();
			
			String code_word = "";
			
			for(int i=0; i<code_line.length(); i++) {			
				if(code_line.charAt(i) != ' ' && i<code_line.length()-1) {
					code_word += code_line.charAt(i);
				} else if(i == code_line.length()-1) {
					code_word += code_line.charAt(i);
					for(int j=0; j<var_count; j++) {
						if(code_word.equals(var_names[j])) {
							code_word = "$p" + j;
						}
					}
					final_code += code_word;
					final_code += " ";
					code_word = "";
				} else {
					for(int j=0; j<var_count; j++) {
						if(code_word.equals(var_names[j])) {
							code_word = "$p" + j;
						}
					}
					final_code += code_word;
					final_code += " ";
					code_word = "";
				}				
			}	
			final_code += "\n";
		}
		return final_code;
	}
	
	//------------------ Helper methods ------------------------
	
	public static int nextWordEndIndex(String line, int offset) {
		int word_end_index = offset;
		for(int i=offset; i<line.length(); i++) {
			if(line.charAt(i) != ' ')
				word_end_index++;
			else
				i = line.length();
		}
		return word_end_index;
	}
	
	public boolean isNumber(String str) {
		return str.matches("-?\\d+(\\.\\d+)?");
	}
	
	//===========================================================================================//
	
	// Creates a new tab
	public void newFile() {
		String filename = "untitled"+".minl";
		filename = JOptionPane.showInputDialog(null,
				"Enter the new file name (without extension)");
		addTab(filename+".minl");
	}

	// Save the contents of the selected tab into a file
	public void saveFile() {	
		String fileloc = "";
		if(!prf.foldername.getText().equals(""))
			fileloc += prf.foldername.getText()+"/";
		fileloc += ((MinLFile) tabbedPane.getSelectedComponent()).filename;;		
		File fl = new File(fileloc);
		
		try {
			if(fl.exists()) {
				int option = JOptionPane.showConfirmDialog(null, "The file with this name already exists, do you want to overwrite it?", "Save File", JOptionPane.YES_NO_OPTION);
				if(option == 0) {
					Files.delete(fl.toPath());
					try {
						
						FileWriter fw = new FileWriter(fileloc);
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write(((MinLFile) tabbedPane.getSelectedComponent()).textaA.getText());
						bw.close();
						fw.close();				
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			} else {
				try {
					FileWriter fw = new FileWriter(fileloc+".minl");
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(((MinLFile) tabbedPane.getSelectedComponent()).textaA.getText());
					bw.close();
					fw.close();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "The specified folder doesnt exist.");
					e1.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Loads a file into a tab
	public void loadFile() {
		
		String fileloc = "";
		if(!prf.foldername.getText().equals(""))
			fileloc += prf.foldername.getText()+"/";
		
		String filename = JOptionPane.showInputDialog(null,"Enter the name of the person tracked.");
		fileloc += filename += ".minl";
		File fl = new File(fileloc);
		
		if(!fl.exists()) {
			JOptionPane.showMessageDialog(null, "The specified file doesnt exist at the given location.");
		} else {
			MinLFile nminl = new MinLFile(filename);
			
			try {
				FileReader flrd = new FileReader(fileloc);
				BufferedReader bufread = new BufferedReader(flrd);
				
				nminl.textaA.setText("");
			
				String str;
				while((str = bufread.readLine()) != null) {
					Document doc = nminl.textaA.getDocument();
					try {
					doc.insertString(doc.getLength(), str, null);
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				}
				
				bufread.close();
				flrd.close();
			
			} catch (IOException e) {
				e.printStackTrace();
			}		
			
			JComponent panel = nminl;
			
			tabbedPane.addTab(filename, null, panel,
					filename);		
			tabbedPane.setMnemonicAt(0, 0);
		}	
	}
	
	// Renames the tab
	public void renameTab() {
		String newtabname = JOptionPane.showInputDialog(null,"Enter the new name for the tab.");
		tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), newtabname+".minl");
	}
	
	// Closes the tab
	public void closeFile() {
		boolean filechanged = ((MinLFile) tabbedPane.getSelectedComponent()).filechanged;
		if(filechanged) {
			int option = JOptionPane.showConfirmDialog(null,"The file has been edited, do you wish to save the changes?", "Close File", JOptionPane.YES_NO_CANCEL_OPTION);
			if(option == 0) {
				saveFile();
			} else if(option == 1) {
				tabbedPane.remove(tabbedPane.getSelectedComponent());
			}
		} else {
			tabbedPane.remove(tabbedPane.getSelectedComponent());
		}
		tabbedPane.remove(tabbedPane.getSelectedComponent());
	}
	
	// Runs the code in the selected tab
	public void runProgram() {
		JTextPane code = ((MinLFile) tabbedPane.getSelectedComponent()).textaA;
		minc.consolearea.setText("");
		interpretorMainMethod(code.getText());
		int_variables.clear();
		double_variables.clear();
		string_variables.clear();
		std_functions.clear();
		ret_functions.clear();
	}
	
	// Exits the application
	public void exit() {
		boolean filechanged = false;
		for(int i = 0; i<tabbedPane.getTabCount(); i++) {
			if(((MinLFile) tabbedPane.getComponent(i)).filechanged)
				filechanged = true;		
		}
		if(filechanged) {
			int option = JOptionPane.showConfirmDialog(null, "There are unsaved files, do you still want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
			if(option == 0) {
				System.exit(0);
			}
		} else {
			System.exit(0);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String name = e.getActionCommand();
		if(name == "New") {
			newFile();
		} else if(name == "Save") {
			saveFile();
		} else if(name == "Load") {
			loadFile();
		} else if(name == "Rename") {
			renameTab();
		} else if(name == "Close") {
			closeFile();
		} else if(name == "Run") {
			runProgram();
		} else if(name == "Exit") {
			exit();
		} 
	}
	
	public static void main(String[] args) {
		MinL minl = new MinL();
	}
}
