package fr.ystat.gui;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class LoadingForm {
	public LoadingForm(){
		contentPanel.setSize(300, 300);
	}

	@Getter
	private JPanel contentPanel;
	private JLabel loadingImgLabel;
	private JLabel loadingTxtLabel;

	private void createUIComponents() {
		// TODO: place custom component creation code here
		loadingImgLabel	= new JLabel();
		var imgIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/assets/loading.gif")));
		imgIcon = new ImageIcon(imgIcon.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));
		loadingImgLabel.setIcon(imgIcon);
	}
}
