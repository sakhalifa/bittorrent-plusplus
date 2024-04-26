package fr.ystat.gui;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class MainForm {
	private CardLayout cardLayout;
	private FilesForm filesForm;


	public MainForm() {
		this.contentPane.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
	}

	@Getter
	private JPanel contentPane;
	private JButton searchButton;
	private JButton settingsButton;
	private JButton filesButton;
	private JPanel subPane;

	private void createUIComponents() {
		filesForm = new FilesForm();
		subPane = filesForm.getContentPane();
	}
}
