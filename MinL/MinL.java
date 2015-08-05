// Made by: Piotr Woloszyn 2015
// Build: 8

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
	
	//----------------- Interpreter variables -----------------//
	
	// global variables are stored in these maps
	Map<String,Double> double_variables = new HashMap<String, Double>();
	Map<String,String> string_variables = new HashMap<String, String>();
	Map<String,String> functions = new HashMap<String, String>();
	
	// Local variables are stored in these maps
	Map<String,Double> local_double_variables = new HashMap<String, Double>();
	Map<String,String> local_string_variables = new HashMap<String, String>();
	
	//---------------------------------------------------------//
	
	
	// Constructor
	public MinL() {
		super("MinL Editor");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		// Do not uncomment this
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
		
		addTab("new_file.minl");
		
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
	// the mode variable designates if the code is a part of 
	// a function method.
	// 0 - not a function
	// 1 - function
	public void interpretorMainMethod(String code, int mode) {
		
		//TODO comments
		
		// Used to count the bracket balance
		int bracket_depth = 0;	
		
		// Loop variables, loop_active designates whether the current line
		// is a part of a loop, loop_length designates how many times the loop
		// will run
		boolean loop_active = false;
		int loop_length = 0;	
		
		// If statement variable, designates if the if condition
		// allows for the code that follows it to execute
		boolean conditional_state = true;	
		
		// A flag the determines whether the current line should
		// be interpreted
		boolean skip_line = false;
		
		// Function variables, function_name contains the name
		// of the function currently being ran or written, function_is_being_written
		// indicates that the current lien of code is part of the function being
		// written/saved, function_arguments contains the arguments that the
		// function comes with
		String function_name = "";
		boolean function_is_being_written = false;
		LinkedList<String> function_arguments = new LinkedList<>();
		
		// The current line number being interpreted
		// Note: lines in loops, if statements and functions
		// are counted separately
		int line_count = 0;
		
		// Scanner for the code String
		Scanner line_scanner = new Scanner(code);
		
		// This String contains bracketed code which will be passed to
		// either a new function or to be ran via loop
		String passed_code = "";
		
		// Outer interpreter
		while(line_scanner.hasNextLine()) {
			line_count++;
			String line_temp = line_scanner.nextLine();
			String line = "";
			
			// This code removes the leading spaces from the line
			for(int i = 0; i<line_temp.length(); i++) {
				if(line_temp.charAt(i) != ' ') {
					line = line_temp.substring(i);
					i = 999;
				}
			}
			
			// Line length after clearing the leading spaces
			int line_length = line.length();
			
			// Bracket depth control
			// Counts bracket depth and makes the inner interpreter skip
			// the first and last bracket
			if(line.contains("(")) {
				bracket_depth++;
				if(bracket_depth == 1)
					skip_line = true;
			} else if(line.contains(")")) {
				bracket_depth--;
				if(bracket_depth == 0)
					skip_line = true;
			} else if(line.isEmpty()) {
				skip_line = true;
			}
			
			// Comments skip
			// Makes the inner interpreter skip lines beginning with "//"
			if(line_length > 2) {
				if(line.substring(0, 2).equals("//"))
					skip_line = true;
			}
			
			// if statement lock/skip
			// Makes the inner interpreter skip 'if' statement blocks if
			// the 'if' statement is false
			if(!conditional_state && bracket_depth != 0) {
				skip_line = true;
			} else if(!conditional_state && bracket_depth == 0) {
				conditional_state = true;
			}
				
			// Inner interpreter
			// Handles the syntax interpretation and execution
			if(!skip_line) {
				
				// This checks if we have reached the end of the function declaration,
				// should we do the function is saved in the function map
				if(bracket_depth == 0 && function_is_being_written) {								
					function_is_being_written = false;
					functions.put(function_name, functionHandler(function_arguments, passed_code, "write"));
					function_arguments.clear();
					passed_code = "";
					
				// This checks if we have reached the end of the code block contained in the loop,
				// should we do the code contained in the loop (the passed_code variable)
				// gets passed along in a recursive call to the interpreter main method.
				} else if(bracket_depth == 0 && loop_active){
					if(mode == 1) {
						for(int i=0; i<loop_length; i++)
							interpretorMainMethod(passed_code, 1);
					} else {
						for(int i=0; i<loop_length; i++)
							interpretorMainMethod(passed_code, 0);
					}
					loop_active = false;
					loop_length = 0;
					passed_code = "";
					
				// This checks if we have reached the end of code block contained in the 'if'
				// Statement, should we do: the conditional_state variable is set to true
				} else if(bracket_depth == 0 && !conditional_state) {		
					conditional_state = true;
				} 
				
				// If either the function_is_being_written or loop_active variables are true
				// we simply pass the current line to a temporary string without
				// interpreting it
				if(function_is_being_written || loop_active) {
					passed_code += line + "\n";
					
				// Secondary bracket skips, this is here only on account of having to
				// check the function, loop, 'if' statement endings
				} else if(line.contains("(") || line.contains(")")) {
					
				// The key words are checked
				} else {				
					
					// This loop determines the location of the first non-space character.
					// However as the code above indicates, this will always be 0, because
					// we remove the empty spaces beforehand in the Outer interpreter.
					// The loop here is simply for debugging purposes.
					int empty_space = 0;
					boolean space_error = false;				
					while(line.charAt(empty_space) == ' ' || space_error) {
						empty_space++;
						if(empty_space == 50) {
							space_error = true;
						}
					}
					
					// stop statement
					// Syntax: stop
					// Corresponds to: no java counterpart
					// Indicates the end of a code block
					if (line.contains("stop") && line.length() < 7) {
					}
					
					// def statement
					// Syntax: def x : y
					// Corresponds to: int x = y;  OR  Double x = y;  OR  String x = y;
					else if(line_length > 3 && line.substring(empty_space, empty_space+4).equals("def ")) {
						int end_of_word_offset;
						String var_name = "";
						String var_definition = "";
						boolean comma_present = false;
						int offset = empty_space+4;
						
						// This loop finds and assigns the definition components:
						// the var_name - the name of the variable we are defining
						// the var_definition - the contents of the variable
						while(offset < line_length) {
							if(line.charAt(offset) == ' ')
								offset++;
							else {
								if(var_name.isEmpty()) {
									end_of_word_offset = nextWordEndIndex(line, offset);
									var_name = line.substring(offset, end_of_word_offset);
									offset = end_of_word_offset;
								} else if(!comma_present && line.charAt(offset) == ':') {
									comma_present = true;
									offset++;
								} else if(var_definition.isEmpty() && comma_present) {
									var_definition = line.substring(offset, line_length);
									offset = line_length;
								} else {
									offset = line_length;
								}
							}
						}
						
						// This checks if the definition syntax is correct
						if(var_name.isEmpty() || var_definition.isEmpty() || !comma_present) {
							minc.consolearea.append("Error: bad definition syntax. line "+line_count+"\n");
						} else {
							// This checks if we are using a reserved word as the name of the variable
							if(!(var_name.equals("def") && var_name.equals("inc") && var_name.equals("dec") && var_name.equals("output") && var_name.equals("loop") && var_name.equals("if") && var_name.equals("loadfile"))) {
								if(isNumber(var_definition)) {
									if(mode == 0)
										double_variables.put(var_name, 0.0 + Integer.parseInt(var_definition));
									else
										local_double_variables.put(var_name, 0.0 + Integer.parseInt(var_definition));
								} else {
									if(mode == 0)
										string_variables.put(var_name, var_definition);
									else
										local_string_variables.put(var_name, var_definition);
								}
							} else {
								minc.consolearea.append("Error: prohibited reserved word use. line "+line_count+"\n");
							}
						}
					}		
					
					// inc statement
					// Syntax: inc x
					// Corresponds to: x++;
					else if(line_length > 3 && line.substring(empty_space, empty_space+4).equals("inc ")) {
						String var_name;
						int offset = empty_space+4;
						
						while(offset < line_length) {
							if(line.charAt(offset) == ' ') {
								offset++;
							} else {
								var_name = line.substring(offset, line_length).replaceAll("\\s+","");
								if(!var_name.isEmpty()) {
									if(mode == 1) {
										if(local_double_variables.containsKey(var_name))
											local_double_variables.put(var_name, local_double_variables.get(var_name)+1.0);
										else if(double_variables.containsKey(var_name))
											double_variables.put(var_name, double_variables.get(var_name)+1.0);
										else
											minc.consolearea.append("Error: bad increment syntax. line "+line_count+"\n");
									} else {
										if(double_variables.containsKey(var_name))
											double_variables.put(var_name, double_variables.get(var_name)+1.0);
										else
											minc.consolearea.append("Error: bad increment syntax. line "+line_count+"\n");
									}
								} else {
									minc.consolearea.append("Error: bad increment syntax. line "+line_count+"\n");
								}
								offset = line_length;
							}
						}
					}
					
					// dec statement
					// Syntax: dec x
					// Corresponds to: x--;
					else if(line_length > 3 && line.substring(empty_space, empty_space+4).equals("dec ")) {
						String var_name;
						int offset = empty_space+4;
						
						while(offset < line_length) {
							if(line.charAt(offset) == ' ') {
								offset++;
							} else {
								var_name = line.substring(offset, line_length).replaceAll("\\s+","");
								if(!var_name.isEmpty()) {
									if(mode == 1) {
										if(local_double_variables.containsKey(var_name))
											local_double_variables.put(var_name, local_double_variables.get(var_name)-1.0);
										else if(double_variables.containsKey(var_name))
											double_variables.put(var_name, double_variables.get(var_name)-1.0);
										else
											minc.consolearea.append("Error: bad increment syntax. line "+line_count+"\n");
									} else {
										if(double_variables.containsKey(var_name))
											double_variables.put(var_name, double_variables.get(var_name)-1.0);
										else
											minc.consolearea.append("Error: bad increment syntax. line "+line_count+"\n");
									}
								} else {
									minc.consolearea.append("Error: bad decrement syntax. line "+line_count+"\n");
								}
								offset = line_length;
							}
						}
					}
					
					// output statement
					// Syntax: output : x
					// Corresponds to: System.out.println(x);				
					else if(line_length > 8 && line.substring(empty_space, empty_space+9).equals("output : ")) {
						int end_of_word_offset;
						String output_string = "";
						String temp_word;
						int offset = empty_space+9;
						
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
										if(mode == 1) {
											if(local_double_variables.containsKey(key))
												output_string += local_double_variables.get(key);
											else if(local_string_variables.containsKey(key))
												output_string += local_string_variables.get(key);
											else if(double_variables.containsKey(key))
												output_string += double_variables.get(key);
											else if(string_variables.containsKey(key))
												output_string += string_variables.get(key);
											else
												minc.consolearea.append("Error: bad variable call in output. line "+line_count+"\n");
										} else {
											if(double_variables.containsKey(key))
												output_string += double_variables.get(key);
											else if(string_variables.containsKey(key))
												output_string += string_variables.get(key);
											else
												minc.consolearea.append("Error: bad variable call in output. line "+line_count+"\n");
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
					// Syntax: loop x
					// Corresponds to: for(int i=0; i<x; i++)
					else if(line_length > 4 && line.substring(empty_space, empty_space+5).equals("loop ")) {
						try {
							String next_element = line.substring(empty_space+5, nextWordEndIndex(line, empty_space+5));
							// Flags the loop as active for the interpreter
							loop_active = true;
						
							// These conditions attempt to retrieve the loop variable, or
							// the number of times the loop will execute
							if(isNumber(next_element)) {
								loop_length = Integer.parseInt(next_element);
							} else if(mode == 1) {
								if(local_double_variables.containsKey(next_element))
									loop_length += local_double_variables.get(next_element);
								else if(double_variables.containsKey(next_element))
									loop_length += double_variables.get(next_element);
								else
									minc.consolearea.append("Error: bad loop syntax. line "+line_count+"\n");
							} else {
								if(double_variables.containsKey(next_element))
									loop_length += double_variables.get(next_element);
								else
									minc.consolearea.append("Error: bad loop syntax. line "+line_count+"\n");				
							}
							
						} catch (IndexOutOfBoundsException e) {
							minc.consolearea.append("Error: bad loop syntax. line "+line_count+"\n");
						}					
					}
					
					// if statement
					// Syntax and correspondent expressions explained below
					// This is a rather length segment of the interpreter, however the code logic
					// is very simple: the code checks for proper syntax and whether the called
					// variables are defined. The length is a product of the possible combinations of
					// variable types.
					else if(line_length > 2 && line.substring(empty_space, empty_space+3).equals("if ")) {
						try {
							String next_element = line.substring(empty_space+3, nextWordEndIndex(line, empty_space+3));	
							String lhs;
							String rhs;		
							
							// Equals condition
							// Syntax: if eq x y
							// Corresponds to: if(x == y)
							if(next_element.equals("eq")) {
								lhs = line.substring(empty_space+6, nextWordEndIndex(line, empty_space+6));
								rhs = line.substring(empty_space+6+lhs.length()+1, nextWordEndIndex(line, empty_space+6+lhs.length()+1));
								if(isNumber(lhs) && isNumber(rhs)) {
									if(Integer.parseInt(lhs) != Integer.parseInt(rhs)) {
										conditional_state = false;
									}
								} else if(isNumber(lhs)) {							
									if(mode == 1) {
										if(local_double_variables.containsKey(rhs)) {
											if(Integer.parseInt(lhs) != local_double_variables.get(rhs)) {
												conditional_state = false;
											}
										} else if(double_variables.containsKey(rhs)) {
											if(Integer.parseInt(lhs) != double_variables.get(rhs)) {
												conditional_state = false;
											}
										} else {
											minc.consolearea.append("Error: bad conditional syntax. line "+line_count+". Unknown variable: "+rhs+"\n");
										}
									} else {
										if(double_variables.containsKey(rhs)) {
											if(Integer.parseInt(lhs) != double_variables.get(rhs)) {
												conditional_state = false;
											}
										} else {
											minc.consolearea.append("Error: bad conditional syntax. line "+line_count+". Unknown variable: "+rhs+"\n");
										}		
									}	
								} else if(isNumber(rhs)) {
									if(mode == 1) {
										if(local_double_variables.containsKey(lhs)) {
											if(local_double_variables.get(lhs) != Integer.parseInt(rhs)) {
												conditional_state = false;
											}
										} else if(double_variables.containsKey(lhs)) {
											if(double_variables.get(lhs) != Integer.parseInt(rhs)) {
												conditional_state = false;
											}
										} else {
											minc.consolearea.append("Error: bad conditional syntax. line "+line_count+". Unknown variable: "+lhs+"\n");
										}	
									} else {
										if(double_variables.containsKey(lhs)) {
											if(double_variables.get(lhs) != Integer.parseInt(rhs)) {
												conditional_state = false;
											}
										} else {
											minc.consolearea.append("Error: bad conditional syntax. line "+line_count+". Unknown variable: "+lhs+"\n");
										}	
									}
								} else {
									if(mode == 1) {
										if(local_double_variables.containsKey(rhs) && local_double_variables.containsKey(lhs)) {
											if(local_double_variables.get(lhs) != local_double_variables.get(rhs)) {
												conditional_state = false;
											}
										} else if(local_double_variables.containsKey(rhs) && double_variables.containsKey(lhs)) {
											if(local_double_variables.get(lhs) != double_variables.get(rhs)) {
												conditional_state = false;
											}
										} else if(double_variables.containsKey(rhs) && local_double_variables.containsKey(lhs)) {
											if(double_variables.get(lhs) != local_double_variables.get(rhs)) {
												conditional_state = false;
											}
										} else if(double_variables.containsKey(rhs) && double_variables.containsKey(lhs)) {
											if(double_variables.get(lhs) != double_variables.get(rhs)) {
												conditional_state = false;
											}
										} else if(local_string_variables.containsKey(rhs) && local_string_variables.containsKey(lhs)) {
											if(!local_string_variables.get(lhs).equals(local_string_variables.get(rhs))) {
												conditional_state = false;
											}
										} else if(local_string_variables.containsKey(rhs) && string_variables.containsKey(lhs)) {
											if(!local_string_variables.get(lhs).equals(string_variables.get(rhs))) {
												conditional_state = false;
											}
										} else if(string_variables.containsKey(rhs) && local_string_variables.containsKey(lhs)) {
											if(!string_variables.get(lhs).equals(local_string_variables.get(rhs))) {
												conditional_state = false;
											}
										} else if(string_variables.containsKey(rhs) && string_variables.containsKey(lhs)) {
											if(!string_variables.get(lhs).equals(string_variables.get(rhs))) {
												conditional_state = false;
											}
										} else {
											minc.consolearea.append("Error: bad conditional syntax. line "+line_count+". Unknown variable\n");
										}
									} else {
										if(double_variables.containsKey(rhs) && double_variables.containsKey(lhs)) {
											if(double_variables.get(lhs) != double_variables.get(rhs)) {
												conditional_state = false;
											}
										} else if(string_variables.containsKey(rhs) && string_variables.containsKey(lhs)) {
											if(!string_variables.get(lhs).equals(string_variables.get(rhs))) {
												conditional_state = false;
											}
										} else {
											minc.consolearea.append("Error: bad conditional syntax. line "+line_count+". Unknown variable\n");
										}	
									}
								}	
							
							// Not equals condition
							// Syntax: if neq x y
							// Corresponds to: if(x != y)
							} else if(next_element.equals("neq")) {
								lhs = line.substring(empty_space+7, nextWordEndIndex(line, empty_space+7));
								rhs = line.substring(empty_space+7+lhs.length()+1, nextWordEndIndex(line, empty_space+7+lhs.length()+1));
								
								if(isNumber(lhs) && isNumber(rhs)) {
									if(Integer.parseInt(lhs) == Integer.parseInt(rhs)) {
										conditional_state = false;
									}
								} else if(isNumber(lhs)) {							
									if(mode == 1) {
										if(local_double_variables.containsKey(rhs)) {
											if(Integer.parseInt(lhs) == local_double_variables.get(rhs)) {
												conditional_state = false;
											}
										} else if(double_variables.containsKey(rhs)) {
											if(Integer.parseInt(lhs) == double_variables.get(rhs)) {
												conditional_state = false;
											}
										} else {
											minc.consolearea.append("Error: bad conditional syntax. line "+line_count+". Unknown variable: "+rhs+"\n");
										}
									} else {
										if(double_variables.containsKey(rhs)) {
											if(Integer.parseInt(lhs) == double_variables.get(rhs)) {
												conditional_state = false;
											}
										} else {
											minc.consolearea.append("Error: bad conditional syntax. line "+line_count+". Unknown variable: "+rhs+"\n");
										}		
									}	
								} else if(isNumber(rhs)) {
									if(mode == 1) {
										if(local_double_variables.containsKey(lhs)) {
											if(local_double_variables.get(lhs) == Integer.parseInt(rhs)) {
												conditional_state = false;
											}
										} else if(double_variables.containsKey(lhs)) {
											if(double_variables.get(lhs) == Integer.parseInt(rhs)) {
												conditional_state = false;
											}
										} else {
											minc.consolearea.append("Error: bad conditional syntax. line "+line_count+". Unknown variable: "+lhs+"\n");
										}	
									} else {
										if(double_variables.containsKey(lhs)) {
											if(double_variables.get(lhs) == Integer.parseInt(rhs)) {
												conditional_state = false;
											}
										} else {
											minc.consolearea.append("Error: bad conditional syntax. line "+line_count+". Unknown variable: "+lhs+"\n");
										}	
									}
								} else {
									if(mode == 1) {
										if(local_double_variables.containsKey(rhs) && local_double_variables.containsKey(lhs)) {
											if(local_double_variables.get(lhs) == local_double_variables.get(rhs)) {
												conditional_state = false;
											}
										} else if(local_double_variables.containsKey(rhs) && double_variables.containsKey(lhs)) {
											if(local_double_variables.get(lhs) == double_variables.get(rhs)) {
												conditional_state = false;
											}
										} else if(double_variables.containsKey(rhs) && local_double_variables.containsKey(lhs)) {
											if(double_variables.get(lhs) == local_double_variables.get(rhs)) {
												conditional_state = false;
											}
										} else if(double_variables.containsKey(rhs) && double_variables.containsKey(lhs)) {
											if(double_variables.get(lhs) == double_variables.get(rhs)) {
												conditional_state = false;
											}
										} else if(local_string_variables.containsKey(rhs) && local_string_variables.containsKey(lhs)) {
											if(local_string_variables.get(lhs).equals(local_string_variables.get(rhs))) {
												conditional_state = false;
											}
										} else if(local_string_variables.containsKey(rhs) && string_variables.containsKey(lhs)) {
											if(local_string_variables.get(lhs).equals(string_variables.get(rhs))) {
												conditional_state = false;
											}
										} else if(string_variables.containsKey(rhs) && local_string_variables.containsKey(lhs)) {
											if(string_variables.get(lhs).equals(local_string_variables.get(rhs))) {
												conditional_state = false;
											}
										} else if(string_variables.containsKey(rhs) && string_variables.containsKey(lhs)) {
											if(string_variables.get(lhs).equals(string_variables.get(rhs))) {
												conditional_state = false;
											}
										} else {
											minc.consolearea.append("Error: bad conditional syntax. line "+line_count+". Unknown variable\n");
										}
									} else {
										if(double_variables.containsKey(rhs) && double_variables.containsKey(lhs)) {
											if(double_variables.get(lhs) == double_variables.get(rhs)) {
												conditional_state = false;
											}
										} else if(string_variables.containsKey(rhs) && string_variables.containsKey(lhs)) {
											if(string_variables.get(lhs).equals(string_variables.get(rhs))) {
												conditional_state = false;
											}
										} else {
											minc.consolearea.append("Error: bad conditional syntax. line "+line_count+". Unknown variable\n");
										}	
									}
								}	
								
							// Less than condition
							// Syntax: if lt x y
							// Corresponds to: if(x < y)
							} else if(next_element.equals("lt")) {
								lhs = line.substring(empty_space+6, nextWordEndIndex(line, empty_space+6));
								rhs = line.substring(empty_space+6+lhs.length()+1, nextWordEndIndex(line, empty_space+6+lhs.length()+1));
								if(isNumber(lhs) && isNumber(rhs)) {
									if(Integer.parseInt(lhs) >= Integer.parseInt(rhs)) {
										conditional_state = false;
									}
								} else if(isNumber(lhs)) {
									if(mode == 1) {
										if(local_double_variables.containsKey(rhs)) {
											if(Integer.parseInt(lhs) >= local_double_variables.get(rhs)) {
												conditional_state = false;
											}
										} else if(double_variables.containsKey(rhs)) {
											if(Integer.parseInt(lhs) >= double_variables.get(rhs)) {
												conditional_state = false;
											}
										} else {
											minc.consolearea.append("Error: bad conditional syntax. line "+line_count+". Unknown variable: "+rhs+"\n");
										}	
									} else {
										if(double_variables.containsKey(rhs)) {
											if(Integer.parseInt(lhs) >= double_variables.get(rhs)) {
												conditional_state = false;
											}
										} else {
											minc.consolearea.append("Error: bad conditional syntax. line "+line_count+". Unknown variable: "+rhs+"\n");
										}	
									}	
								} else if(isNumber(rhs)) {
									if(mode == 1) {
										if(local_double_variables.containsKey(lhs)) {
											if(local_double_variables.get(lhs) >= Integer.parseInt(rhs)) {
												conditional_state = false;
											}
										} else if(double_variables.containsKey(lhs)) {
											if(double_variables.get(lhs) >= Integer.parseInt(rhs)) {
												conditional_state = false;
											}
										} else {
											minc.consolearea.append("Error: bad conditional syntax. line "+line_count+". Unknown variable: "+lhs+"\n");
										}
									} else {
										if(double_variables.containsKey(lhs)) {
											if(double_variables.get(lhs) >= Integer.parseInt(rhs)) {
												conditional_state = false;
											}
										} else {
											minc.consolearea.append("Error: bad conditional syntax. line "+line_count+". Unknown variable: "+lhs+"\n");
										}	
									}
								} else {
									if(double_variables.containsKey(rhs) && double_variables.containsKey(lhs)) {
										if(double_variables.get(lhs) >= double_variables.get(rhs)) {
											conditional_state = false;
										}
									} else {
										minc.consolearea.append("Error: bad conditional syntax. line "+line_count+". Unknown variable\n");
									}
								}
								
							// Greater than condition
							// Syntax: if gt x y
							// Corresponds to: if(x > y)
							} else if(next_element.equals("gt")) {
								lhs = line.substring(empty_space+6, nextWordEndIndex(line, empty_space+6));
								rhs = line.substring(empty_space+6+lhs.length()+1, nextWordEndIndex(line, empty_space+6+lhs.length()+1));
								
								if(isNumber(lhs) && isNumber(rhs)) {
									if(Integer.parseInt(lhs) <= Integer.parseInt(rhs)) {
										conditional_state = false;
									}
								} else if(isNumber(lhs)) {
									if(mode == 1) {
										if(local_double_variables.containsKey(rhs)) {
											if(Integer.parseInt(lhs) <= local_double_variables.get(rhs)) {
												conditional_state = false;
											}
										} else if(double_variables.containsKey(rhs)) {
											if(Integer.parseInt(lhs) <= double_variables.get(rhs)) {
												conditional_state = false;
											}
										} else {
											minc.consolearea.append("Error: bad conditional syntax. line "+line_count+". Unknown variable: "+rhs+"\n");
										}	
									} else {
										if(double_variables.containsKey(rhs)) {
											if(Integer.parseInt(lhs) <= double_variables.get(rhs)) {
												conditional_state = false;
											}
										} else {
											minc.consolearea.append("Error: bad conditional syntax. line "+line_count+". Unknown variable: "+rhs+"\n");
										}	
									}	
								} else if(isNumber(rhs)) {
									if(mode == 1) {
										if(local_double_variables.containsKey(lhs)) {
											if(local_double_variables.get(lhs) <= Integer.parseInt(rhs)) {
												conditional_state = false;
											}
										} else if(double_variables.containsKey(lhs)) {
											if(double_variables.get(lhs) <= Integer.parseInt(rhs)) {
												conditional_state = false;
											}
										} else {
											minc.consolearea.append("Error: bad conditional syntax. line "+line_count+". Unknown variable: "+lhs+"\n");
										}
									} else {
										if(double_variables.containsKey(lhs)) {
											if(double_variables.get(lhs) <= Integer.parseInt(rhs)) {
												conditional_state = false;
											}
										} else {
											minc.consolearea.append("Error: bad conditional syntax. line "+line_count+". Unknown variable: "+lhs+"\n");
										}	
									}
								} else {
									if(double_variables.containsKey(rhs) && double_variables.containsKey(lhs)) {
										if(double_variables.get(lhs) <= double_variables.get(rhs)) {
											conditional_state = false;
										}
									} else {
										minc.consolearea.append("Error: bad conditional syntax. line "+line_count+". Unknown variable\n");
									}
								}
							} else {
								minc.consolearea.append("Error: bad conditional syntax. line "+line_count+"\n");
							}
						} catch (IndexOutOfBoundsException e) {
							minc.consolearea.append("Error: bad conditional syntax. line "+line_count+"\n");
						}
					}
					
					// loadfile statement
					// Syntax: loadfile filename.minl
					// Corresponds to: no java analog
					// This code simply executes the MinL code written on another file.
					else if(line_length > 8 && line.substring(empty_space, empty_space+9).equals("loadfile ")) {
						
						try {
							String next_element = line.substring(empty_space+9, nextWordEndIndex(line, empty_space+9));
							fileLoaderMethod(next_element);	
						} catch (IndexOutOfBoundsException e) {
							//TODO error test
							minc.consolearea.append("Error: bad loadfile syntax. line "+line_count+"\n");
						}
					}			
					
					// function declaration/call
					// Syntax: f.function_name x y z
					// Corresponds to: public void function_name(int/Double/String x, int/Double/String y, int/Double/String z)
					else if(line_length > 1 && line.substring(empty_space, empty_space+2).equals("f.")) {
						
						try {
							String next_element = line.substring(empty_space+2, nextWordEndIndex(line, empty_space+2));					
							function_name = "f."+next_element;	
							// These two conditions identify whether the function called is defined or not
							// if it is the function is simply called, if not it is declared. The syntax for call is simple:
							// f.function_name argument1 argument2 etc...
							// The syntax for declare requires a code block:
							// f.function_name argument_name1 argument_name2 etc...
							// {
							//   loop argument_name1
							//   {
							//     inc argument_name2
							//   }
							// }
							if(functions.containsKey(function_name)) {
								// Function call code
								if(function_name.length() == line_length) {
									interpretorMainMethod(functions.get(function_name), 1);
								} else {
									
									int offset = function_name.length()+1;	
									// This loop retrieves and saves the function arguments in a list
									while(offset < line.length()) {
										if(line.charAt(offset) == ' ') {
											offset++;
										} else {
											next_element = line.substring(offset, nextWordEndIndex(line, offset));
											offset = nextWordEndIndex(line, offset);
											function_arguments.add(next_element);
										}
									}	
									// The function is called via recursive call to the interpreterMainMethod
									interpretorMainMethod(functionHandler(function_arguments, functions.get(function_name), "read"), 1);
									local_double_variables.clear();
									local_string_variables.clear();
									function_arguments.clear();
								}							
							} else {
								// function declare code
								// take note the function isn't actually saved here, but in the code far above
								function_is_being_written = true;							
								if(function_name.length() != line_length) {								
									int offset = function_name.length()+1;
									// This loop retrieves and saves the function arguments in a list
									while(offset < line.length()) {
										if(line.charAt(offset) == ' ') {
											offset++;
										} else {
											next_element = line.substring(offset, nextWordEndIndex(line, offset));
											offset = nextWordEndIndex(line, offset);
											function_arguments.add(next_element);
										}
									}
								}
							}
						} catch (IndexOutOfBoundsException e) {
							//TODO error test
							minc.consolearea.append("Error: bad function syntax. line "+line_count+"\n"+line);
						}			
					}
					// report error
					else {
						minc.consolearea.append("Error: Unknown Operation. line "+line_count+"\n"+line);
					}
				}
			} else {
				skip_line = false;
			}	
		}
	}
	
	//------------------ Helper methods ------------------------
	
	//----------------------------------    functionHandler    ----------------------------------
	// This method changes the variable names in the declared function from
	// the ones specified by the coder to ones whose names match the positions
	// of the variables that follow the function name on call.
	// Example:
	//
	// code can be:
	// f.add x y    <--- first line, the variable names are not saved, only the name f.add is
	// (
	//   loop y
	//   (
	//     inc x
	//   )
	// )
	//
	// The variables x and y will be renamed to $p0 and $p1 so that when the function is called:
	// f.add 2 3, the code will know that the first variable '2' will go to locations specified
	// by $p0 and '3' to locations specified by $p1.
	//
	// on mode: write
	// variables are as mentioned above replaced with place-holders
	// on mode : read
	// the process is reversed where the place-holders are replaced with the input
	// arguments provided on function call
	//--------------------------------------------------------------------------------------------
	public String functionHandler(LinkedList<String> argumentList, String code, String mode) {
		
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
					if(mode.equals("write")) {
						if(argumentList.contains(code_word)) {
							code_word = "$p" + argumentList.indexOf(code_word);
						}
					} else if(mode.equals("read")) {
						
						if(code_word.length() > 2 && code_word.substring(0, 2).equals("$p")) {
							code_word = argumentList.get(Integer.parseInt(code_word.substring(2, 3)));
						}
					}
					final_code += code_word;
					final_code += " ";
					code_word = "";
				} else {
					
					if(mode.equals("write")) {
						if(argumentList.contains(code_word)) {
							code_word = "$p" + argumentList.indexOf(code_word);
						}
					} else if(mode.equals("read")) {
						
						if(code_word.length() > 2 && code_word.substring(0, 2).equals("$p")) {
							code_word = argumentList.get(Integer.parseInt(code_word.substring(2, 3)));		
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
	
	// Returns the string index value for the last character
	// of a word starting at location 'offset' in the passed string
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
	
	// Returns true if the passed string is a number
	public boolean isNumber(String str) {
		return str.matches("-?\\d+(\\.\\d+)?");
	}
	
	// File loader method, loadfile command will grab the defined variables
	// and functions from a specified file and put them in the interpreter variable
	// maps.
	public void fileLoaderMethod(String filename) {
		
		String fileloc = "";
		if(!prf.foldername.getText().equals(""))
			fileloc += prf.foldername.getText()+"/";
		
		fileloc += filename;
		File fl = new File(fileloc);
		
		String code = "";
		
		if(!fl.exists()) {
			JOptionPane.showMessageDialog(null, "The specified file doesnt exist at the given location.");
		} else {	
			try {
				FileReader flrd = new FileReader(fileloc);
				BufferedReader bufread = new BufferedReader(flrd);		
				String str;
				while((str = bufread.readLine()) != null) {
					code += str + "\n";
				}		
				bufread.close();
				flrd.close();			
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
		interpretorMainMethod(code, 0);
	}
	
	//===========================================================================================//
	
	// Creates a new tab
	public void newFile() {
		String filename = "untitled";
		filename = JOptionPane.showInputDialog(null,
				"Enter the new file name");
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
						bw.write(((MinLFile) tabbedPane.getSelectedComponent()).CodeArea.getText());
						bw.close();
						fw.close();
						((MinLFile) tabbedPane.getSelectedComponent()).filechanged = false;
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			} else {
				try {
					FileWriter fw = new FileWriter(fileloc);
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(((MinLFile) tabbedPane.getSelectedComponent()).CodeArea.getText());
					bw.close();
					fw.close();
					((MinLFile) tabbedPane.getSelectedComponent()).filechanged = false;
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
		
		String filename = JOptionPane.showInputDialog(null,"Enter the filename.");
		fileloc += filename;
		File fl = new File(fileloc);
		
		if(!fl.exists()) {
			JOptionPane.showMessageDialog(null, "The specified file doesnt exist at the given location.");
		} else {
			MinLFile nminl = new MinLFile(filename);
			
			try {
				FileReader flrd = new FileReader(fileloc);
				BufferedReader bufread = new BufferedReader(flrd);
				
				nminl.CodeArea.setText("");
			
				String str;
				while((str = bufread.readLine()) != null) {
					Document doc = nminl.CodeArea.getDocument();
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
		JTextPane code = ((MinLFile) tabbedPane.getSelectedComponent()).CodeArea;
		minc.consolearea.setText("");
		interpretorMainMethod(code.getText(), 0);
		double_variables.clear();
		string_variables.clear();
		functions.clear();
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
