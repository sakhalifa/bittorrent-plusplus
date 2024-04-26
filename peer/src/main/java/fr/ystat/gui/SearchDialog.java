package fr.ystat.gui;

import fr.ystat.tracker.criterions.ComparisonType;
import fr.ystat.util.Pair;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;


// TODO Make it so you can append multiple filesize criterions...
public class SearchDialog extends JDialog {
	@FunctionalInterface
	public interface OKCallback{
		void run(String fileName, String fileKey, List<Pair<ComparisonType, Long>> fileSizes);
	}

	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JTextField fileNameField;
	private JTextField fileKeyField;
	private JComboBox<ComparisonType> fileSizeComparisonList;
	private JSpinner fileSizeSpinner;
	private JCheckBox fileKeyEnabled;
	private JCheckBox fileNameEnabled;
	private JCheckBox fileSizeEnabled;
	private JLabel fileKeyLabel;
	private JLabel fileNameLabel;
	private JLabel fileSizeLabel;
	private final OKCallback okCallback;

	public SearchDialog(OKCallback callback) {
		this.okCallback = callback;
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

		fileKeyEnabled.addActionListener(actionEvent -> {
			fileKeyLabel.setEnabled(fileKeyEnabled.isSelected());
			fileKeyField.setEnabled(fileKeyEnabled.isSelected());
		});
		fileNameEnabled.addActionListener(actionEvent -> {
			fileNameLabel.setEnabled(fileNameEnabled.isSelected());
			fileNameField.setEnabled(fileNameEnabled.isSelected());
		});
		fileSizeEnabled.addActionListener(actionEvent -> {
			fileSizeLabel.setEnabled(fileSizeEnabled.isSelected());
			fileSizeComparisonList.setEnabled(fileSizeEnabled.isSelected());
			fileSizeSpinner.setEnabled(fileSizeEnabled.isSelected());
		});
	}

	private void onOK() {
		String fileName = fileNameEnabled.isSelected() ? fileNameField.getText() : null;
		String fileKey = fileKeyEnabled.isSelected() ? fileKeyField.getText() : null;
		ComparisonType fileSizeComparisonType = fileSizeEnabled.isSelected() ? (ComparisonType) fileSizeComparisonList.getSelectedItem() : null;
		Long fileSize = fileSizeEnabled.isSelected() ? (Long) fileSizeSpinner.getValue() : null;
		okCallback.run(fileName, fileKey, List.of(Pair.of(fileSizeComparisonType, fileSize)));
		dispose();
	}

	private void onCancel() {
		dispose();
	}

	private void createUIComponents() {
		fileSizeComparisonList = new JComboBox<>(ComparisonType.values());

		// Use intermediate variables to coerce the type to Long and not use the "double" constructor. Java is dump.
		Long val = 1L;
		Long min = 1L;
		Long max = Long.MAX_VALUE;
		Long step = 1L;

		fileSizeSpinner = new JSpinner(new SpinnerNumberModel(val, min, max, step));
	}
}
