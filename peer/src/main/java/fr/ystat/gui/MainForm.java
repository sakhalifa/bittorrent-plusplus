package fr.ystat.gui;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class MainForm {
	public MainForm() {
		this.contentPane.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
	}

	@Getter
	private JPanel contentPane;
}
