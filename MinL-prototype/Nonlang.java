// Made by: Piotr Woloszyn 2015
// Build: 4

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class Nonlang extends JFrame implements ActionListener{
	
	JPanel jp = new JPanel();
	JPanel ansp = new JPanel();
	JTextArea texta;
	JTextArea textb;
	JScrollPane scrollp;
	JScrollPane scrollpb;
	Color bc = Color.BLACK;
	Color fc = Color.WHITE;
	
	ImageIcon background;
	JLabel backgroundm;
	
	String res;
	
	Map<String,Integer> int_variables = new HashMap<String, Integer>();
	Map<String,String> string_variables = new HashMap<String, String>();
	
	public Nonlang() {
		super("_nonLanG beta");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		
		//ImageIcon img0 = new ImageIcon(this.getClass().getResource("NLicon.png"));		
		//this.setIconImage(img0.getImage());		
		int_variables.put("test", 0);
		
		draw();
		add(jp);
		
		setSize(400,660);
		this.setVisible(true);
	}
	
	public void draw() {
		jp.setLayout(null);	
		//background = new ImageIcon(this.getClass().getResource("backNL.png"));
		//backgroundm = new JLabel(background);
		//backgroundm.setBounds(0,0,400,660);
		
		texta = new JTextArea();
		texta.setFont(new Font("Consolas", Font.PLAIN, 16));
		texta.setBackground(bc);
		texta.setForeground(fc);
		texta.setCaretColor(fc);
		texta.setTabSize(2);;
		texta.setEditable(true);
		scrollp = new JScrollPane(texta);	
		scrollp.setBounds(14, 14, 365, 485);
		jp.add(scrollp);	
		textb = new JTextArea();
		textb.setFont(new Font("Consolas", Font.PLAIN, 16));
		textb.setBackground(bc);
		textb.setForeground(fc);
		textb.setText("");
		textb.setEditable(false);	
		scrollpb = new JScrollPane(textb);
		scrollpb.setBounds(14, 520, 280, 95);
		jp.add(scrollpb);	
		JButton btn = new JButton("Run");	
		btn.setBounds(323, 545, btn.getPreferredSize().width, 40);
		btn.addActionListener(this);	
		btn.setActionCommand("Run");	
		jp.add(btn);
		
		//jp.add(backgroundm);
	}
	
	public void IterateCode(String code) {
		
		String definition = "def";
		String incrementation = "inc";
		String decrementation = "dec";
		String loop = "loop";
		String return_statment = "ret";
		String condition = "if";
		
		boolean brackets_active = false;
		boolean brackets_open = false;
			
		String variable_name = "";
		String variable_value = "";
		String function_name = "";
		String bracket_contents = "";
		
		String confitional_statment;
		String lhs_cond_varaible;
		String rhs_cond_varaible;
		
		int bracket_depth = 0;
		
		int loops = 1;
		
		Scanner scr = new Scanner(code);
		
		String str_line;
		int line_len;
		
		while(scr.hasNextLine()) {
			
			str_line = scr.nextLine();
			
			str_line = str_line.replaceAll("\t","");			
			line_len = str_line.length();
		
			function_name = "";
			
			for(int i=0; i<line_len; i++) {		
				
				
				if(str_line.charAt(i) == '(' && !brackets_active) {
					brackets_open = true;
				} else if(str_line.charAt(i) == ')' && !brackets_active) {
					brackets_open = false;
				} else if(str_line.charAt(i) == '(' && brackets_active) {
					if(bracket_depth >= 1) {
						bracket_contents += str_line += '\n';
						i = line_len;
						bracket_depth++;
					} else {
						brackets_open = true;
						bracket_depth++;
					}
				} else if(brackets_active && str_line.charAt(i) != ')') {
					bracket_contents += str_line += '\n';
					i = line_len;
				} else if(str_line.charAt(i) != '.' && str_line.charAt(i) != '(' && str_line.charAt(i) != ')' && str_line.charAt(i) != '\t' && !brackets_open) {
					function_name += str_line.charAt(i);
				} else if(str_line.charAt(i) == ')' && brackets_active) {
					if(bracket_depth == 1) {				
						bracket_depth--;	
						brackets_open = false;
						brackets_active = false;	
						for(int z=0; z<loops; z++) {
							IterateCode(bracket_contents);
						}
						bracket_contents = "";
						loops = 1;
					} else {
						bracket_depth--;
						bracket_contents += str_line += '\n';
						i = line_len;
					}
				}
				
				// definition handler
				if(function_name.equals(definition)) {
					i+=2;
					while(i<line_len && str_line.charAt(i) != '.'){
						variable_name += str_line.charAt(i);
						i++;
					}
					if(i<line_len && str_line.charAt(i) == '.') {
						i++;
						while(i<line_len && str_line.charAt(i) != '.'){
							variable_value += str_line.charAt(i);
							i++;
						}
						if(i == line_len || (i<line_len && str_line.charAt(i) == ',' )) {
							if(isNumber(variable_value)) {
								if(string_variables.containsKey(variable_name)) {
									string_variables.remove(variable_name);
								}
								int_variables.put(variable_name, Integer.parseInt(variable_value));
							} else {
								if(int_variables.containsKey(variable_name)) {
									int_variables.remove(variable_name);
								}
								string_variables.put(variable_name, variable_value);
							}
						} else {
							//error
						}		
					} else {
						//error
					}
					variable_name = "";
					variable_value = "";
					function_name = "";
					
					
				// increment handler
				} else if (function_name.equals(incrementation)) {
					i+=2;
					while(i<line_len && str_line.charAt(i) != '.' && str_line.charAt(i) != '(' && str_line.charAt(i) != ')' && str_line.charAt(i) != ','){
						variable_name += str_line.charAt(i);
						i++;
					}
					if(i == line_len || (i<line_len && str_line.charAt(i) == ',')) {
						if(int_variables.containsKey(variable_name)) {
							increment_variable(variable_name);
						} else {
							//error
						}
					} else {
						//error
					}
					variable_name = "";
					function_name = "";
					
				// decrement handler
				} else if (function_name.equals(decrementation)) {
					i+=2;
					while(i<line_len && str_line.charAt(i) != '.' && str_line.charAt(i) != '(' && str_line.charAt(i) != ')' && str_line.charAt(i) != ','){
						variable_name += str_line.charAt(i);
						i++;
					}
					if(i == line_len || (i<line_len && str_line.charAt(i) == ',')) {
						if(int_variables.containsKey(variable_name)) {
							decrement_variable(variable_name);
						} else {
							//error
						}
					} else {
						//error
					}		
					variable_name = "";
					function_name = "";
					
				
				// conditional statement handler
				} else if (function_name.equals(condition)) {	
					i+=2;
					confitional_statment = "";
					lhs_cond_varaible = "";
					rhs_cond_varaible = "";
					while(i<line_len && str_line.charAt(i) != '.' && str_line.charAt(i) != '(' && str_line.charAt(i) != ')' && str_line.charAt(i) != ','){
						confitional_statment += str_line.charAt(i);
						i++;
					}				
					if(i==line_len) {
						//error
					}				
					i++;
					while(i<line_len && str_line.charAt(i) != '.' && str_line.charAt(i) != '(' && str_line.charAt(i) != ')' && str_line.charAt(i) != ','){
						lhs_cond_varaible += str_line.charAt(i);
						i++;
					}		
					if(i==line_len) {
						//error
					}	
					i++;
					while(i<line_len && str_line.charAt(i) != '.' && str_line.charAt(i) != '(' && str_line.charAt(i) != ')' && str_line.charAt(i) != ','){
						rhs_cond_varaible += str_line.charAt(i);
						i++;
					}		
					if(i<line_len) {
						//error
					}
					
					if(confitional_statment.equals("eq")) {	
						if(isNumber(lhs_cond_varaible) && isNumber(rhs_cond_varaible)) {			
							if(Integer.parseInt(lhs_cond_varaible) == Integer.parseInt(rhs_cond_varaible)) {
								brackets_active = true;
							} else {
								brackets_active = false;
							}
						} else if(!isNumber(lhs_cond_varaible) && !isNumber(rhs_cond_varaible)) {
							if(int_variables.containsKey(lhs_cond_varaible) && int_variables.containsKey(rhs_cond_varaible)) {
								if(int_variables.get(lhs_cond_varaible) == int_variables.get(rhs_cond_varaible)) {
									brackets_active = true;
								} else {
									brackets_active = false;
								}
							} else if(string_variables.containsKey(lhs_cond_varaible) && string_variables.containsKey(rhs_cond_varaible)) {
								if(string_variables.get(lhs_cond_varaible).equals(string_variables.get(rhs_cond_varaible))) {
									brackets_active = true;
								} else {
									brackets_active = false;
								}
							} else {
								//error
							}
							
						} else if(isNumber(lhs_cond_varaible) && !isNumber(rhs_cond_varaible)) {
							//error
						} else {
							//error
						}
												
					} else if(confitional_statment.equals("neq")) {
						if(isNumber(lhs_cond_varaible) && isNumber(rhs_cond_varaible)) {			
							if(Integer.parseInt(lhs_cond_varaible) != Integer.parseInt(rhs_cond_varaible)) {
								brackets_active = true;
							} else {
								brackets_active = false;
							}
						} else if(!isNumber(lhs_cond_varaible) && !isNumber(rhs_cond_varaible)) {
							if(int_variables.containsKey(lhs_cond_varaible) && int_variables.containsKey(rhs_cond_varaible)) {
								if(int_variables.get(lhs_cond_varaible) != int_variables.get(rhs_cond_varaible)) {
									brackets_active = true;
								} else {
									brackets_active = false;
								}
							} else if(string_variables.containsKey(lhs_cond_varaible) && string_variables.containsKey(rhs_cond_varaible)) {
								if(!string_variables.get(lhs_cond_varaible).equals(string_variables.get(rhs_cond_varaible))) {
									brackets_active = true;
								} else {
									brackets_active = false;
								}
							} else {
								//error
							}
							
						} else if(isNumber(lhs_cond_varaible) && !isNumber(rhs_cond_varaible)) {
							//error
						} else {
							//error
						}										
						
					} else if(confitional_statment.equals("gt")) {				
						if(isNumber(lhs_cond_varaible) && isNumber(rhs_cond_varaible)) {			
							if(Integer.parseInt(lhs_cond_varaible) > Integer.parseInt(rhs_cond_varaible)) {
								brackets_active = true;
							} else {
								brackets_active = false;
							}
						} else if(!isNumber(lhs_cond_varaible) && !isNumber(rhs_cond_varaible)) {
							if(int_variables.containsKey(lhs_cond_varaible) && int_variables.containsKey(rhs_cond_varaible)) {
								if(int_variables.get(lhs_cond_varaible) > int_variables.get(rhs_cond_varaible)) {
									brackets_active = true;
								} else {
									brackets_active = false;
								}
							} else if(string_variables.containsKey(lhs_cond_varaible) && string_variables.containsKey(rhs_cond_varaible)) {
								if(string_variables.get(lhs_cond_varaible).length() > (string_variables.get(rhs_cond_varaible)).length()) {
									brackets_active = true;
								} else {
									brackets_active = false;
								}
							} else {
								//error
							}
							
						} else if(isNumber(lhs_cond_varaible) && !isNumber(rhs_cond_varaible)) {
							//error
						} else {
							//error
						}		
						
						
					} else if(confitional_statment.equals("lt")) {
						if(isNumber(lhs_cond_varaible) && isNumber(rhs_cond_varaible)) {			
							if(Integer.parseInt(lhs_cond_varaible) < Integer.parseInt(rhs_cond_varaible)) {
								brackets_active = true;
							} else {
								brackets_active = false;
							}
						} else if(!isNumber(lhs_cond_varaible) && !isNumber(rhs_cond_varaible)) {
							if(int_variables.containsKey(lhs_cond_varaible) && int_variables.containsKey(rhs_cond_varaible)) {
								if(int_variables.get(lhs_cond_varaible) < int_variables.get(rhs_cond_varaible)) {
									brackets_active = true;
								} else {
									brackets_active = false;
								}
							} else if(string_variables.containsKey(lhs_cond_varaible) && string_variables.containsKey(rhs_cond_varaible)) {
								if(string_variables.get(lhs_cond_varaible).length() < (string_variables.get(rhs_cond_varaible)).length()) {
									brackets_active = true;
								} else {
									brackets_active = false;
								}
							} else {
								//error
							}
							
						} else if(isNumber(lhs_cond_varaible) && !isNumber(rhs_cond_varaible)) {
							//error
						} else {
							//error
						}		
						
						
					} else {
						//error
					}
					
					
					
				// loop handler
				} else if (function_name.equals(loop)) {
					i+=2;
					while(i<line_len && str_line.charAt(i) != '.' && str_line.charAt(i) != '(' && str_line.charAt(i) != ')' && str_line.charAt(i) != ','){
						variable_name += str_line.charAt(i);
						i++;
					}
					brackets_active = true;
					
					if(isNumber(variable_name)) {
						loops = Integer.parseInt(variable_name);
					} else {
						if(int_variables.containsKey(variable_name)) {
							loops = int_variables.get(variable_name);
						} else {
							//error
						}
					}
					
					variable_name = "";	
					function_name = "";
					
				// return handler
				} else if (function_name.equals(return_statment)) {
					
					i+=2;
					while(i<line_len &&  str_line.charAt(i) != '.' && str_line.charAt(i) != '(' && str_line.charAt(i) != ')' && str_line.charAt(i) != ','){
						variable_name += str_line.charAt(i);
						i++;
					}
					if(i == line_len) {
						
						if(isNumber(variable_name)) {
							printoutval(variable_name);
						} else {
							if(int_variables.containsKey(variable_name)) {
								printoutval(int_variables.get(variable_name)+"");
							} else if (string_variables.containsKey(variable_name)){
								printoutval(string_variables.get(variable_name));
							} else {
								//error
							}
						}		
					} else {
						//error
					}
					variable_name = "";		
					function_name = "";
				} 	
				
				
				
				
			}
		}
	}

	
	public boolean increment_variable(String var_name) {
		if(!int_variables.containsKey(var_name))
			return false;
		
		int_variables.put(var_name, int_variables.get(var_name) + 1);
		return true;
	}
	
	public boolean decrement_variable(String var_name) {
		if(!int_variables.containsKey(var_name))
			return false;
		
		int_variables.put(var_name, int_variables.get(var_name) - 1);
		return true;
	}
	
	public boolean isNumber(String str) {
		return str.matches("-?\\d+(\\.\\d+)?");
	}
	
	public void printoutval(String val) {
		res = val;
		textb.append(res+"\n");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String name = e.getActionCommand();
		textb.setText("");
		if(name == "Run") {
			int_variables.clear();
			IterateCode(texta.getText());
			int_variables.put("test", 0);
		}
	}
	

	public static void main(String[] args) {
		Nonlang nlg = new Nonlang();
	}
}
