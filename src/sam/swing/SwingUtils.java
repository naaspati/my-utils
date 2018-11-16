package sam.swing;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

public final class SwingUtils {
	// VERSION = 1.2;


	/**
	 * calls {@link #showErrorDialog(Component, CharSequence, Exception)}
	 * with Component = null <br>
	 * @param msg
	 * @param e
	 * @return
	 */
	public static  String showErrorDialog(CharSequence msg, Exception e){
		return showErrorDialog(null, msg, e);
	}

	/**
	 * open a JOptionPane with a textarea displaying the error 
	 * @param msg
	 * @param e
	 * @return the string shown in dialog 
	 */
	public static  String showErrorDialog(Component parent, CharSequence msg, Exception e){
		String str = null;
		if(e != null){
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			if(msg != null)
				pw.println(msg.toString());
			e.printStackTrace(pw);

			str = sw.toString();
			pw.close();
		}
		else if(msg != null)
			str = msg.toString();
		else
			str = null;

		JTextArea ta;

		if(str == null){
			ta = new JTextArea("Null");
			ta.setFont(new Font("Consolas", 1, 50));
			ta.setBorder(new EmptyBorder(50, 50, 50, 50));
		}
		else {
			Matcher m = Pattern.compile("\r?\n").matcher(str);

			int maxWidth = 0;
			int lines = 0;
			int previousStart = 0;
			boolean b = m.find();

			if(b){
				while(b){
					int start = m.start();
					if(start - previousStart > maxWidth)
						maxWidth = start - previousStart;
					previousStart = start;
					lines++;
					b = m.find();
				}
			}
			else{ //this solve single line issue
				maxWidth = str.length();
				lines = 1;	
			}

			ta = new JTextArea(str, 
					lines + 1 < 20 ? lines + 1 : 20, 
							maxWidth + 1 < 80 ? maxWidth + 1 : 80);
			ta.setFont(new Font("Consolas", 1, 18));
			ta.setBorder(new EmptyBorder(10, 10, 10, 10));
		}
		ta.setEditable(false);

		boolean emptyString = str == null || str.trim().isEmpty();

		int option =  JOptionPane.showOptionDialog(parent, new JScrollPane(ta), "Error", emptyString ? JOptionPane.OK_OPTION : JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, emptyString ? new String[]{"OK"} : new String[]{"OK", "Save To File"}, null);

		if(emptyString)
			return str;

		if(option == JOptionPane.NO_OPTION){
			JFileChooser chooser = new JFileChooser();
			int returnValue = chooser.showSaveDialog(parent);

			if(returnValue == JFileChooser.APPROVE_OPTION){
				File file  = chooser.getSelectedFile();

				Path p;
				if(!file.getName().matches(".+\\.\\w+"))
					p = Paths.get(file.toString()+".txt");
				else
					p = file.toPath();

				try {
					Files.write(p, str.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
				} catch (IOException e1) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					pw.println("Failed write errors to file");
					e1.printStackTrace(pw);

					ta = new JTextArea(sw.toString(),20,80);
					ta.setFont(new Font("Consolas", 1, 18));
					ta.setBorder(new EmptyBorder(10, 10, 10, 10));
					pw.close();
					JOptionPane.showMessageDialog(null, new JScrollPane(ta), "Error", JOptionPane.PLAIN_MESSAGE, null);
				}
			}
		}

		return str;
	}

	public static boolean showDialog(String title, Component content) {
		JDialog fm = new JDialog(null, title, ModalityType.APPLICATION_MODAL);
		fm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JButton ok = new JButton("OK");
		JButton cancel = new JButton("CANCEL");

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5), false);
		buttons.add(cancel);
		buttons.add(ok);

		boolean b[] = {false};

		cancel.addActionListener(e -> fm.dispose());
		ok.addActionListener(e -> {
			b[0] = true;
			fm.dispose();
		});

		fm.add(content, BorderLayout.CENTER);
		fm.add(buttons, BorderLayout.SOUTH);

		fm.setSize(500, 500);
		fm.setLocationRelativeTo(null);
		fm.setVisible(true);
		return b[0];
	} 

	public static  String inputDialog(String title) {
		JTextArea a = new JTextArea();
		return  showDialog(title, a) ? a.getText() : null;
	}

	public static  File filePathInputOptionPane(String msg, String initialValue){
		File file = getFile(msg, initialValue);
		return file == null || !file.isFile() ? null : file;
	}
	public static  File dirPathInputOptionPane(String msg, String initialValue){
		File file = getFile(msg, initialValue);
		return file == null || !file.isDirectory() ? null : file;
	}

	public static  File filePathInputOptionPane(String msg){
		return filePathInputOptionPane(msg, null);
	}
	public static  File dirPathInputOptionPane(String msg){
		return dirPathInputOptionPane(msg, null);
	}
	public static  Window mainWindow = null;

	private  static File getFile(String msg, String initialValue) {
		String str = JOptionPane.showInputDialog(mainWindow, msg, initialValue);

		if(str == null || (str = str.replace('"', ' ').trim()).isEmpty())
			return null;

		File file = new File(str);
		return file.exists() ? file : null;
	}

	public static  void addCloseActionToDialog(JDialog dialog) {
		dialog.getRootPane().getActionMap().put("close_dialoag", new AbstractAction() {
			private static final long serialVersionUID = 77650767569011187L;

			@Override
			public void actionPerformed(ActionEvent e) { dialog.dispose(); }
		});
		dialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true), "close_dialoag");
	}
}
