package org.fgb.fileOperations.xml.workers;

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

import org.fgb.fileOperations.xml.listeners.DefaultActionListener;
import org.fgb.fileOperations.xml.listeners.PrettyPrintListener;
import org.fgb.fileOperations.xml.utilities.SwingUtils;

/**
 * SwingWorker is a generic class, with two type parameters.  The first type parameter specifies a return type for
 * doInBackground, and also for the get method, which is invoked by other threads to retrieve the object returned
 * by doInBackground.
 * SwingWorker's second type parameter specifies a type for interim results returned while the background task is
 * still active.  If the SwingWorker doesn't return interim results, Void can be used as a placeholder.
 * 
 * @author Burkley
 *
 */
public class PrettyPrintWorker extends SwingWorker<String, String> implements ActionListener {
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

	public PrettyPrintWorker(final JComponent panel) {
		this.defaultListener = new DefaultActionListener();
		System.out.println(_className + ".PrettyPrintListener(JPanel j): panel.getName() = " + panel.getName());
		this.resultsPanel = panel;
		Component c;
		if ((c = SwingUtils.findComponentByName(panel, "ResultsTextArea")) != null) {
			this.resultsTextArea = (JTextArea) c;
			System.out.println(_className + ".PrettyPrintListener(): c.getName() = " + c.getName());
		}
	}


	@Override
	protected String doInBackground() throws Exception {
		StringBuilder msg = new StringBuilder();
		int totalSleepTime = 15000;
		int sleptSoFar = 0;
		int sleepTime = 1000;
		System.out.println(this.getClass().getName() + ".doInBackground(): isEventDispatchThread = "
				+ SwingUtilities.isEventDispatchThread());
		try {
			while (sleptSoFar < totalSleepTime) {
				msg.append("Going to sleep for ").append(sleepTime).append(" milliseconds.");
//				System.out.println(this.getClass().getName() + ".doInBackground(): " + msg.toString());
				publish(msg.toString());
				msg.delete(0, msg.length());
				Thread.sleep(sleepTime);
				sleptSoFar += sleepTime;
				msg.append("  Wake up.  Have slept for a total of ").append(sleptSoFar).append(" milliseconds.\n");
//				System.out.println(this.getClass().getName() + ".doInBackground(): " + msg.toString());
				publish(msg.toString());
				msg.delete(0, msg.length());
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable th) {
			th.printStackTrace();
		}
		return "All Done!";
	}


	@Override
	protected void process(List<String> chunks) {
		super.process(chunks);
		System.out.println(this.getClass().getName() + ".process(): isEventDispatchThread = " + SwingUtilities.isEventDispatchThread());
		for (String chunk : chunks) {
			this.resultsTextArea.append(chunk);
			this.resultsTextArea.setCaretPosition(this.resultsTextArea.getDocument().getLength());				
		}
	}


	@Override
	protected void done() {
		super.done();
		System.out.println(this.getClass().getName() + ".done(): isEventDispatchThread = "
				+ SwingUtilities.isEventDispatchThread());
		System.out.println(this.getClass().getName() + ".done(): Done...");
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
