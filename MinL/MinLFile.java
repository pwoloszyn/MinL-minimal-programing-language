// Made by: Piotr Woloszyn 2015
// Build: 6
// Documentation in separate file

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;


public class MinLFile extends JPanel{
	
	public JTextPane textaA;
	public JTextPane textaB;
	
	Document doc;
	
	public JPanel temparea;
	
	String filename;
	
	public JScrollPane scrollp;
	Color background_color = new Color(90,56,37);
	Color main_text_color = new Color(185,122,87);
	Color line_number_color = new Color(255,201,14);
	Color numbers_color = new Color(255,201,14);
	Color core_function_color = new Color(242,234,191);
	Color aux_color = new Color(255,255,255);
	Color funcion_declaration_color = new Color(195,195,195);
	
	boolean filechanged = false;
	
	public MinLFile(String filename) {

		GridLayout experimentLayout = new GridLayout(1,1);
		setLayout(experimentLayout);
		
		this.filename = filename;
		
		temparea = new JPanel();
		scrollp = new JScrollPane();
		
		temparea.setLayout(new GridBagLayout());
		GridBagConstraints constr = new GridBagConstraints();
		
		StyleContext stylecont = StyleContext.getDefaultStyleContext();
		final AttributeSet atrib_MainText = stylecont.addAttribute(stylecont.getEmptySet(), StyleConstants.Foreground, main_text_color);
		final AttributeSet atrib_CoreFunc = stylecont.addAttribute(stylecont.getEmptySet(), StyleConstants.Foreground, core_function_color);
		final AttributeSet atrib_Numbers = stylecont.addAttribute(stylecont.getEmptySet(), StyleConstants.Foreground, numbers_color);
		final AttributeSet atrib_Aux = stylecont.addAttribute(stylecont.getEmptySet(), StyleConstants.Foreground, aux_color);
		final AttributeSet atrib_Func = stylecont.addAttribute(stylecont.getEmptySet(), StyleConstants.Foreground, funcion_declaration_color);
		
		DefaultStyledDocument stldoc = new DefaultStyledDocument() {
			// Offset is the length of the string in the text
			public void insertString(int offset, String str, AttributeSet attrbset) throws BadLocationException {
				str = str.replaceAll("\t", "  ");
				super.insertString(offset, str, attrbset);
				
				String text = getText(0, getLength());	
				int before = findLastNonWordChar(text, offset);
				if (before < 0) before = 0;
				int after = findFirstNonWordChar(text, offset + str.length());
				int wordL = before;
                int wordR = before;
				
				while (wordR <= after) {
                    if (wordR == after || String.valueOf(text.charAt(wordR)).matches("\\W")) {
                        if (text.substring(wordL, wordR).matches("(\\W)*(def|loop|output|inc|dec|cond|var|loadfile)"))
                            setCharacterAttributes(wordL, wordR - wordL, atrib_CoreFunc, false);
                        else if (text.substring(wordL, wordR).matches("(\\W)*(f|ret)"))
                        	setCharacterAttributes(wordL, wordR - wordL, atrib_Func, false);
                        else if (text.substring(wordL, wordR).matches("(\\W)*(-?\\d+(\\.\\d+)?)"))
                        	setCharacterAttributes(wordL, wordR - wordL, atrib_Numbers, false);
                        else if (text.substring(wordL, wordR).matches("(\\W)*(:|minl|\\(|\\))"))
                        	setCharacterAttributes(wordL, wordR - wordL, atrib_Aux, false);
                        else
                            setCharacterAttributes(wordL, wordR - wordL, atrib_MainText, false);
                        wordL = wordR;
                    }
                    wordR++;
                }	
			}		
			public void remove (int offs, int len) throws BadLocationException {
                super.remove(offs, len);

                String text = getText(0, getLength());
                int before = findLastNonWordChar(text, offs);
                if (before < 0) before = 0;
                int after = findFirstNonWordChar(text, offs);

                if (text.substring(before, after).matches("(\\W)*(def|loop|output|inc|dec|cond|var|loadfile)"))
                    setCharacterAttributes(before, after - before, atrib_CoreFunc, false);
                else if (text.substring(before, after).matches("(\\W)*(f|ret)"))
                	setCharacterAttributes(before, after - before, atrib_Func, false);
                else if (text.substring(before, after).matches("(\\W)*(-?\\d+(\\.\\d+)?)"))
                	setCharacterAttributes(before, after - before, atrib_Numbers, false);
                else if (text.substring(before, after).matches("(\\W)*(:|minl|\\(|\\))"))
                	setCharacterAttributes(before, after - before, atrib_Aux, false);
                else
                    setCharacterAttributes(before, after - before, atrib_MainText, false);
            }			
		};
		
		textaA = new JTextPane(stldoc);
		
		textaA.setFont(new Font("Consolas", Font.PLAIN, 16));
		textaA.setMargin(new Insets(0,5,0,0));
		textaA.setBackground(background_color);
		textaA.setForeground(aux_color);
		textaA.setCaretColor(main_text_color);	
		doc = textaA.getDocument();
		if(doc instanceof PlainDocument) {
			doc.putProperty(PlainDocument.tabSizeAttribute, 2);
		}
		textaA.setEditable(true);
		
		textaB = new JTextPane();
		//textaB.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		textaB.setMargin(new Insets(0,3,0,1));
		Document doc = textaB.getDocument();
		try {
		doc.insertString(doc.getLength(), "1", null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		textaB.setFont(new Font("Consolas", Font.PLAIN, 16));
		textaB.setBackground(background_color);
		textaB.setForeground(line_number_color);
		textaB.setCaretColor(line_number_color);
		textaB.setEditable(false);
		
		constr.fill = GridBagConstraints.BOTH;
		constr.weightx = 40.0;
		constr.weighty = 1.0;
		constr.gridx = 1;
		constr.gridy = 0;
		temparea.add(textaA,constr);
		
		constr.fill = GridBagConstraints.BOTH;
		constr.weightx = 0.3;
		constr.weighty = 1.0;
		constr.insets = new Insets(0,1,0,1);
		constr.gridx = 0;
		constr.gridy = 0;
		temparea.add(textaB,constr);	
	
		textaA.getDocument().addDocumentListener(new DocumentListener() {
			public String getText() {
				int caretPosition = textaA.getDocument().getLength();
				Element root = textaA.getDocument().getDefaultRootElement();
				String text = "1" + System.getProperty("line.separator");
				for(int i=2; i<root.getElementIndex(caretPosition)+2; i++) {
					text += i + System.getProperty("line.separator");
				}
				return text;
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				textaB.setText(getText());
				filechanged = true;
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				textaB.setText(getText());
				filechanged = true;
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				textaB.setText(getText());
				filechanged = true;
			}
		});
		
		scrollp.getViewport().add(temparea);
		scrollp.getVerticalScrollBar().setUnitIncrement(16);	
		add(scrollp);
	}
	
	private int findLastNonWordChar (String text, int index) {
        while (--index >= 0) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
        }
        return index;
    }
	
	private int findFirstNonWordChar (String text, int index) {
        while (index < text.length()) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
            index++;
        }
        return index;
    }
	
	public void setFileChangedToFalse() {
		filechanged = false;
	}
	
}
