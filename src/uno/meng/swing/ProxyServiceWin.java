package uno.meng.swing;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.SpringLayout;

import uno.meng.Client;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class ProxyServiceWin {

	private JFrame frame;
	private JTextField port;
	public static int PORT = 12345;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ProxyServiceWin window = new ProxyServiceWin();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ProxyServiceWin() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setTitle("匡盟盟代理服务器");
		frame.setBounds(100, 100, 300, 100);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SpringLayout springLayout = new SpringLayout();
		frame.getContentPane().setLayout(springLayout);
		
		JLabel lblNewLabel = new JLabel("代理端口：");
		frame.getContentPane().add(lblNewLabel);
		
		port = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, port, 10, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, port, 105, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, lblNewLabel, 5, SpringLayout.NORTH, port);
		springLayout.putConstraint(SpringLayout.EAST, lblNewLabel, -6, SpringLayout.WEST, port);
		frame.getContentPane().add(port);
		port.setColumns(10);
		
		JButton edit = new JButton("编辑规则");
		edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Runtime.getRuntime().exec("/Applications/Atom.app/Contents/MacOS/Atom  /Users/kuangmeng/Documents/Eclipse/ProxyService/src/uno/meng/filter/filter.json");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, edit, 6, SpringLayout.SOUTH, lblNewLabel);
		springLayout.putConstraint(SpringLayout.WEST, edit, 28, SpringLayout.WEST, frame.getContentPane());
		frame.getContentPane().add(edit);
		
		JButton action = new JButton("启动代理");
		springLayout.putConstraint(SpringLayout.NORTH, action, 0, SpringLayout.NORTH, edit);
		springLayout.putConstraint(SpringLayout.WEST, action, 6, SpringLayout.EAST, edit);
		action.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				PORT = Integer.parseInt(port.getText());
				Client.main(null);
			}
		});
		frame.getContentPane().add(action);
	}
}
