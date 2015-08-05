// Made by: Piotr Woloszyn 2015
// Build: 8

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;

//The console object, this is where the output will be displayed. This is
// a GUI class so there is not much to elaborate on
public class MinLConsole extends JPanel{
	
	
	public JPanel temparea;
	public JTextArea consolearea;
	public JTextArea indicator;
	public JScrollPane scrollp;
	Color bc = new Color(90,56,37);
	Color fc = new Color(185,122,87);
	Color lc = new Color(255,201,14);
	boolean ctrl_held = false;
	
	public MinLConsole(final JButton brun, final JButton bexit, final MinL main_Object) {
		
		GridLayout experimentLayout = new GridLayout(1,1);
		setLayout(experimentLayout);
		
		temparea = new JPanel();
		scrollp = new JScrollPane();
		
		temparea.setLayout(new GridBagLayout());
		GridBagConstraints constr = new GridBagConstraints();
		
		consolearea = new JTextArea();
		consolearea.setFont(new Font("Consolas", Font.PLAIN, 16));
		consolearea.setMargin(new Insets(0,1,0,0));
		consolearea.setBackground(bc);
		consolearea.setForeground(fc);
		consolearea.setCaretColor(fc);
		consolearea.setTabSize(2);;
		consolearea.setEditable(true);
		
		indicator = new JTextArea();
		indicator.setMargin(new Insets(0,3,0,0));
		indicator.append(">>");
		indicator.setFont(new Font("Consolas", Font.PLAIN, 16));
		indicator.setBackground(bc);
		indicator.setForeground(lc);
		indicator.setCaretColor(lc);
		indicator.setTabSize(4);;
		indicator.setEditable(false);
		
		constr.fill = GridBagConstraints.BOTH;
		constr.weightx = 40.0;
		constr.weighty = 1.0;
		constr.gridx = 1;
		constr.gridy = 0;
		temparea.add(consolearea,constr);
		
		constr.fill = GridBagConstraints.BOTH;
		constr.weightx = 0.3;
		constr.weighty = 1.0;
		constr.insets = new Insets(0,1,0,0);
		constr.gridx = 0;
		constr.gridy = 0;
		temparea.add(indicator,constr);	
		
		// This handles some minor input functionality in the console area,
		// as in: the console will identify the commands 'run' which will run the code
		// and 'exit' which will exit the program.
		consolearea.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				
			}
			@Override
			public void keyPressed(KeyEvent e) {
				int keyid = e.getKeyCode();
				if(!e.isControlDown() && keyid == KeyEvent.VK_ENTER) {
					String contents = consolearea.getText().trim();
					if(!contents.equals("")) {
						// TODO error and data check
						if(contents.equals("*run")) {
							consolearea.setText("");
							brun.doClick();
						} else if(contents.equals("*exit")) {
							bexit.doClick();
						} else {
							consolearea.setText("");
						} 
					}
				} else if(e.isControlDown() && keyid == KeyEvent.VK_ENTER) {
					consolearea.append("\n");
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {

			}
		});
		
		// This inserts the '>>' characters at the start of each line
		consolearea.getDocument().addDocumentListener(new DocumentListener() {
			public String getText() {
				int caretPosition = consolearea.getDocument().getLength();
				Element root = consolearea.getDocument().getDefaultRootElement();
				String text = ">>" + System.getProperty("line.separator");
				for(int i=2; i<root.getElementIndex(caretPosition)+2; i++) {
					text += ">>"+ System.getProperty("line.separator");
				}
				return text;
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				indicator.setText(getText());
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				indicator.setText(getText());			
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				indicator.setText(getText());
			}
		});		
		
		scrollp.getViewport().add(temparea);
		add(scrollp);
	}	
}
