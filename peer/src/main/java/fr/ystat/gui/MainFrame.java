package fr.ystat.gui;

import javax.swing.*;

public class MainFrame extends JFrame{
	public MainFrame(){

		setContentPane(contentPanel);
		setTitle("Simple GUI");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300, 300);
		setLocationRelativeTo(null);
		setVisible(true);
		this.showDialogButton.addActionListener((e) -> {
			JOptionPane.showMessageDialog(this, this.typeHereTextField.getText());
		});
	}
	private JPanel contentPanel;
	private JTextField typeHereTextField;
	private JButton showDialogButton;
}
