package fr.ystat.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import fr.ystat.files.DownloadedFile;
import fr.ystat.files.FileInventory;
import fr.ystat.files.FileProperties;
import fr.ystat.files.StockedFile;
import fr.ystat.peer.leecher.downloader.FileDownloader;
import fr.ystat.peer.leecher.downloader.GreedyDownloader;
import fr.ystat.peer.leecher.exceptions.DownloadException;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;

public class SearchResultDialog extends JDialog {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JPanel innerPane;

	private final List<FileProperties> files;

	public SearchResultDialog(List<FileProperties> files) {
		this.files = files;

		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);

		buttonOK.addActionListener(e -> onOK());

		buttonCancel.addActionListener(e -> onCancel());

		// call onCancel() when cross is clicked
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});

		// call onCancel() on ESCAPE
		contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}

	private void onOK() {
		// add your code here
		dispose();
	}

	private void onCancel() {
		// add your code here if necessary
		dispose();
	}

	private void createUIComponents() {

		// TODO: place custom component creation code here
		var layout = new GridLayoutManager(this.files.size() + 1, 5);
		innerPane = new JPanel(layout);

		var constraints = new GridConstraints();
		constraints.setRow(0);
		constraints.setColumn(0);
		constraints.setAnchor(SwingConstants.CENTER);
		innerPane.add(new JLabel("File Hash", SwingConstants.CENTER), constraints);
		constraints.setColumn(1);
		innerPane.add(new JLabel("File Name", SwingConstants.CENTER), constraints);
		constraints.setColumn(2);
		innerPane.add(new JLabel("File Size", SwingConstants.CENTER), constraints);
		constraints.setColumn(3);
		innerPane.add(new JLabel("Piece Size", SwingConstants.CENTER), constraints);

		int row = 1;
		for(FileProperties properties : files) {
			constraints.setRow(row++);
			constraints.setColumn(0);
			innerPane.add(new JLabel(properties.getHash(), SwingConstants.CENTER), constraints);
			constraints.setColumn(1);
			innerPane.add(new JLabel(properties.getName(), SwingConstants.CENTER), constraints);
			constraints.setColumn(2);
			innerPane.add(new JLabel(properties.getSize() + "", SwingConstants.CENTER), constraints);
			constraints.setColumn(3);
			innerPane.add(new JLabel(properties.getPieceSize() + "", SwingConstants.CENTER), constraints);
			constraints.setColumn(4);
			JButton button = new JButton("Download");
			var file = FileInventory.getInstance().getStockedFile(properties.getHash());
			if(file != null){
				button.setEnabled(false);
				button.setText((file instanceof DownloadedFile) ? "Downloading" : "Downloaded");
			}else{
				button.addActionListener(e -> {
					button.setText("Downloading");
					button.setEnabled(false);
					this.pack();
					System.out.println(properties);

                    try {
                        FileDownloader downloader = FileDownloader.create(properties, GreedyDownloader.class);
						downloader.startDownload();
                    } catch (DownloadException ex) {
                        throw new RuntimeException(ex);
                    }

                });
			}

			innerPane.add(button, constraints);
		}
	}
}
