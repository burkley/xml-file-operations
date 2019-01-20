package org.fgb.fileOperations.xml.listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.fgb.fileOperations.xml.utilities.SwingUtils;

public class PrettyPrintListener implements ActionListener {
	/**
	 * The name of this class.
	 */
	private static final String _className = PrettyPrintListener.class.getName();
	/**
	 * JDK logger.
	 */
	private static final Logger _logger = Logger.getLogger(_className);
	/**
	 * 
	 */
	private final DefaultActionListener defaultListener;
	/**
	 * There is a single <i>results panel</i> for the application. This is the panel
	 * where results of the various <i>work flows</i> will be published. The
	 * reference to this panel is passed to this class as an argument to the
	 * Constructor.
	 */
	private JComponent resultsPanel = new JPanel();
	private JTextArea resultsTextArea = null;

	/**
	 * 
	 */
	public PrettyPrintListener() {
		this.defaultListener = new DefaultActionListener();
		System.out.println(_className + ".PrettyPrintListener()...");
	}

	/**
	 * 
	 */
	public PrettyPrintListener(final JPanel panel) {
		this();
		System.out.println(_className + ".PrettyPrintListener(JPanel j): panel.getName() = " + panel.getName());
		this.resultsPanel = panel;
		Component c;
		if ((c = SwingUtils.findComponentByName(panel, "ResultsTextArea")) != null) {
			this.resultsTextArea = (JTextArea) c;
			System.out.println(_className + ".PrettyPrintListener(): c.getName() = " + c.getName());
		}
	}

	/**
	 * 
	 */
	public void actionPerformed(ActionEvent event) {
		_logger.entering(_className, "actionPerformed");
		this.defaultListener.actionPerformed(event);
		for (File file : this.defaultListener.getSelectedFiles()) {
			System.out.println(_className + ".actionPerformed(): Selected file = " + file.getAbsolutePath());
		}
		System.out.println(
				_className + ".actionPerformed(): isEventDispatchThread = " + SwingUtilities.isEventDispatchThread());
		_logger.exiting(_className, "actionPerformed");
	}
}
