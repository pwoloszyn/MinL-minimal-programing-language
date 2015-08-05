// Made by: Piotr Woloszyn 2015
// Build: 8

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JTextArea;

// A GUI class, handles the small folder location window
public class ProgramRunFolder extends JPanel {
	
	public JPanel temparea;
	JTextArea label;
	JTextArea foldername;
	Color bc = new Color(90,56,37);
	Color fc = new Color(185,122,87);
	Color lc = new Color(255,201,14);

	public ProgramRunFolder() {
		
		GridLayout experimentLayout = new GridLayout(1,1);
		setLayout(experimentLayout);
		
		temparea = new JPanel();
		
		temparea.setLayout(new GridBagLayout());
		GridBagConstraints constr = new GridBagConstraints();
		
		foldername = new JTextArea();
		foldername.setFont(new Font("Consolas", Font.PLAIN, 14));
		foldername.setMargin(new Insets(0,1,0,0));
		foldername.setBackground(bc);
		foldername.setForeground(fc);
		foldername.setCaretColor(fc);
		foldername.setTabSize(2);;
		foldername.setEditable(true);
		
		label = new JTextArea();
		label.setMargin(new Insets(0,3,0,0));
		label.append("Folder name:");
		label.setFont(new Font("Consolas", Font.PLAIN, 14));
		label.setBackground(bc);
		label.setForeground(lc);
		label.setCaretColor(lc);
		label.setTabSize(4);;
		label.setEditable(false);
		
		constr.fill = GridBagConstraints.BOTH;
		constr.weightx = 40.0;
		constr.weighty = 1.0;
		constr.gridx = 1;
		constr.gridy = 0;
		temparea.add(foldername,constr);
		
		constr.fill = GridBagConstraints.BOTH;
		constr.weightx = 0.3;
		constr.weighty = 1.0;
		constr.insets = new Insets(0,1,0,0);
		constr.gridx = 0;
		constr.gridy = 0;
		temparea.add(label,constr);	
		
		add(temparea);
	}
}
