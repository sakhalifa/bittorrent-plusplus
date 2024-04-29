package fr.ystat.gui;

import fr.ystat.Main;
import fr.ystat.tracker.commands.server.LookCommand;
import fr.ystat.tracker.criterions.*;
import lombok.Getter;
import org.tinylog.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainForm {
	private CardLayout cardLayout;


	public MainForm() {
		this.contentPane.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
		searchButton.addActionListener(e -> {
			SearchDialog dialog = new SearchDialog((fileName, fileKey, fileSizeCriterions, pieceSizeCriterions) -> {
				List<ICriterion> criterions = new ArrayList<>();
				if (fileName != null)
					criterions.add(new FilenameCriterion(fileName));
				if (fileKey != null)
					criterions.add(new KeyCriterion(fileKey));
				if (fileSizeCriterions != null)
					for (var pair : fileSizeCriterions)
						criterions.add(new FilesizeCriterion(pair.getFirst(), pair.getSecond()));
				if (pieceSizeCriterions != null)
					for (var pair : pieceSizeCriterions)
						criterions.add(new PiecesizeCriterion(pair.getFirst(), pair.getSecond()));

				Main.getTrackerConnection().sendLook(new LookCommand(criterions),
						list -> {
							SwingUtilities.invokeLater(() -> {
								var resultDialog = new SearchResultDialog(list.getFileProperties());
								resultDialog.pack();
								resultDialog.setVisible(true);
							});
						},
						throwable -> {
							if(throwable instanceof IOException)
								Logger.error("Error fetching files :(");
							else
								Logger.error(throwable);
						});
			});
			dialog.pack();
			dialog.setVisible(true);
		});
	}

	@Getter
	private JPanel contentPane;
	private JButton searchButton;
	private JButton settingsButton;
	private JPanel subPane;

	private void createUIComponents() {
		FilesForm filesForm = new FilesForm();
		subPane = filesForm.getContentPane();
	}
}
