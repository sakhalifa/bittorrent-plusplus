package fr.ystat.gui;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class MainForm {
	private CardLayout cardLayout;
	private FilesForm filesForm;
	private SearchForm searchForm;


	public MainForm() {
		this.contentPane.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
		this.searchButton.addActionListener(e -> {
			cardLayout.show(subPane, e.getActionCommand().toUpperCase());
		});
		this.filesButton.addActionListener(e -> {
			cardLayout.show(subPane, e.getActionCommand().toUpperCase());
		});
	}

	@Getter
	private JPanel contentPane;
	private JButton searchButton;
	private JButton settingsButton;
	private JButton filesButton;
	private JPanel subPane;

	private void createUIComponents() {
		filesForm = new FilesForm();
		searchForm = new SearchForm();
		cardLayout = new CardLayout();
		subPane = new JPanel(cardLayout);
		subPane.add(filesForm.getContentPane(), "FILES");
		subPane.add(searchForm.getContentPane(), "SEARCH");
	}
}
