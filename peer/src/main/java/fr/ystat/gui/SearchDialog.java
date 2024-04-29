package fr.ystat.gui;

import fr.ystat.tracker.criterions.ComparisonType;
import fr.ystat.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;


// TODO Make it so you can append multiple filesize criterions...
public class SearchDialog extends JDialog {
	@FunctionalInterface
	public interface OKCallback {
		void run(String fileName, String fileKey, List<Pair<Long, ComparisonType>> fileSizes, List<Pair<Long, ComparisonType>> pieceSizes);
	}

	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JTextField fileNameField;
	private JTextField fileKeyField;
	private JSpinner fileSizeSpinner;
	private JCheckBox fileKeyEnabled;
	private JCheckBox fileNameEnabled;
	private JCheckBox fileSizeEnabled;
	private JLabel fileKeyLabel;
	private JLabel fileNameLabel;
	private JLabel fileSizeLabel;
	private JCheckBox pieceSizeEnabled;
	private JPanel fileSizeListPanel;
	private JPanel pieceSizeListPanel;
	private JComboBox<ComparisonType> fileSizeComparisonComboBox;
	private JComboBox<ComparisonType> pieceSizeComparisonComboBox;
	private JSpinner pieceSizeSpinner;
	private JLabel pieceSizeLabel;
	private final OKCallback okCallback;

	public SearchDialog(OKCallback callback) {
		this.okCallback = callback;

		setTitle("Search file");
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
			fileSizeComparisonComboBox.setEnabled(fileSizeEnabled.isSelected());
			fileSizeSpinner.setEnabled(fileSizeEnabled.isSelected());
		});
		pieceSizeEnabled.addActionListener(actionEvent -> {
			pieceSizeSpinner.setEnabled(pieceSizeEnabled.isSelected());
			pieceSizeComparisonComboBox.setEnabled(pieceSizeEnabled.isSelected());
			pieceSizeLabel.setEnabled(pieceSizeEnabled.isSelected());
		});
	}

	private void onOK() {
		String fileName = fileNameEnabled.isSelected() ? fileNameField.getText() : null;
		String fileKey = fileKeyEnabled.isSelected() ? fileKeyField.getText() : null;
		List<Pair<Long, ComparisonType>> fileSizesCriterions = null;
		if(fileSizeEnabled.isSelected()) {
			fileSizesCriterions = List.of(Pair.of((Long)fileSizeSpinner.getValue(), (ComparisonType)fileSizeComparisonComboBox.getSelectedItem()));
		}
		List<Pair<Long, ComparisonType>> pieceSizesCriterions = null;
		if(pieceSizeEnabled.isSelected()) {
			pieceSizesCriterions = List.of(Pair.of((Long)pieceSizeSpinner.getValue(), (ComparisonType)pieceSizeComparisonComboBox.getSelectedItem()));
		}

		okCallback.run(fileName, fileKey, fileSizesCriterions, pieceSizesCriterions);
		dispose();
	}

	private void onCancel() {
		dispose();
	}

	private void createUIComponents() {

		fileSizeComparisonComboBox = new JComboBox<>(ComparisonType.values());
		pieceSizeComparisonComboBox = new JComboBox<>(ComparisonType.values());


		// Use intermediate variables to coerce the type to Long and not use the "double" constructor. Java is dump.
		Long val = 1L;
		Long min = 1L;
		Long max = Long.MAX_VALUE;
		Long step = 1L;

		fileSizeSpinner = new JSpinner(new SpinnerNumberModel(val, min, max, step));
		pieceSizeSpinner = new JSpinner(new SpinnerNumberModel(val, min, max, step));
	}

	private void createInnerLayout(){
		// TODO actually do it
		var layout = new GridBagLayout();
		//noinspection BoundFieldAssignment
		fileSizeListPanel = new JPanel(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.BASELINE_LEADING;
		gbc.gridx = gbc.gridy = 0;
		JButton plusButton = new JButton("+");
		plusButton.addActionListener(actionEvent -> {
			JButton source = (JButton) actionEvent.getSource();
			GridBagConstraints gbc2 = new GridBagConstraints();
			gbc2.anchor = GridBagConstraints.BASELINE_LEADING;
			gbc2.gridx = 0;
			gbc2.gridy = 1;
			layout.setConstraints(source, gbc2);

			gbc2.gridy = 0;
			fileSizeListPanel.add(new JButton("-"), gbc2);
			fileSizeListPanel.revalidate();
		});
		fileSizeListPanel.add(plusButton, gbc);
	}
}
