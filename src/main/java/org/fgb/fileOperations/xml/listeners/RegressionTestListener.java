package org.fgb.fileOperations.xml.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class RegressionTestListener implements ActionListener {
	/**
	 * The name of this class.
	 */
	private static final String _className = RegressionTestListener.class.getName();
	/**
	 * JDK logger.
	 */
	private static final Logger _logger = Logger.getLogger(_className);
	private final DefaultActionListener defaultListener;
	/**
	 * There is a single <i>results panel</i> for the application. This is the panel
	 * where results of the various <i>work flows</i> will be published. The
	 * reference to this panel is passed to this class as an argument to the
	 * Constructor.
	 */
	private JComponent resultsPanel = new JPanel();

	/**
	 * 
	 */
	public RegressionTestListener() {
		this.defaultListener = new DefaultActionListener();
		System.out.println(_className + ".RegressionTestListener()...");
	}

	/**
	 * 
	 */
	public RegressionTestListener(final JPanel panel) {
		this();
		System.out.println(_className + ".RegressionTestListener(JPanel j)...");
		this.resultsPanel = panel;
	}

	/**
	 * 
	 */
	public void actionPerformed(ActionEvent event) {
		this.defaultListener.actionPerformed(event);
		for (File file : this.defaultListener.getSelectedFiles()) {
			System.out.println(_className + ".actionPerformed(): Selected file = " + file.getAbsolutePath());
		}
	}

}
