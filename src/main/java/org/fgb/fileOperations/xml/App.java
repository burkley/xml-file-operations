package org.fgb.fileOperations.xml;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.fgb.fileOperations.xml.listeners.FileSelectionPropertyChangeListener;
import org.fgb.fileOperations.xml.utilities.Configuration;
import org.fgb.fileOperations.xml.workers.PrettyPrintWorker;
import org.fgb.fileOperations.xml.workers.RegressionTestWorker;
import org.fgb.fileOperations.xml.workers.ValidateWorker;

/**
 * Application "XML File Operations". This application renders a Graphical User
 * Interface that orchestrates several useful <i>work flows</i> on an XML file
 * or set of XML file(s).
 * <p>
 * Those work flows are:
 * <ul>
 * <li>Pretty Print XML Instance Documents</li>
 * <li>Regression test XML Instance Documents</li>
 * <li>Validate XML Instance Documents</li>
 * </ul>
 *
 * @author FrederickBurkley
 */
public class App extends JFrame implements ActionListener, PropertyChangeListener {
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The name of this class.
	 */
	private static final String _className = App.class.getName();
	/**
	 * JDK logger.
	 */
	private static final Logger _logger = Logger.getLogger(_className);
	/**
	 * There is a single file selection property change listener for the
	 * application. This listener will keep track of the files (i.e. a list of
	 * files) that are currently selected via the JFileCooser. This list of files is
	 * processed by the various {@linkplain javax.swing.SwingWorker}s that invoke
	 * the major work flows of this application.
	 */
	private FileSelectionPropertyChangeListener fileSelectionListener;
	/**
	 * There is a single <i>results panel</i> for the application.  This is the panel
	 * where results of the various <i>work flows</i> will be published.  The reference
	 * to this panel is passed as an argument to the various {@linkplain javax.swing.SwingWorker}s
	 * that invoke the major work flows of this application.
	 */
	private final JPanel workflowResultsPanel = new JPanel();
	/**
	 * There is a single tabbed pane that has two tabs:
	 * <ul>
	 * <li>A tab that holds a <code>JFileChooser</code></li>
	 * <li>A tab that holds a <code>JPanel</code> (the <i>workflowResultsPanel</i> as above)</li>
 	 * </ul>
 	 * <p>
 	 * The <i>workflowResultsPanel</i> of the tabbed pane is <i>selected</i> when one of the major workflows
 	 * is invoked.
	 */
	private JTabbedPane tabbedPane;
	/**
	 * The <i>cancelButton</i> cancels a workflow when the workflow is in progress.
	 */
	private final JButton cancelButton;
	/**
	 * References to the buttons that invoke the <i>work flows</i>.  A list of buttons is maintained
	 * so the buttons can be enabled or disabled depending on depending on whether a work flow is in
	 * progress (or not).
	 */
	private final List<JButton> workflowInvocationButtons;
	/**
	 * The {@linkplain javax.swing.SwingWorker} is initialized to a particular <i>work flow</i> when the
	 * user invokes one of the application <i>work flow</i>s.
	 */
	SwingWorker<String, String> workflowWorker;


	/**
	 * Default Constructor.
	 */
	public App() {
		super();

		_logger.entering(_className, "App");

		this.cancelButton = new JButton("Cancel");
		this.workflowInvocationButtons = new ArrayList<JButton>();

		// add shutdown hook
		final ShutdownHook shutdownHook = new ShutdownHook();
		final Runtime runtime = Runtime.getRuntime();
		runtime.addShutdownHook(shutdownHook);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(final WindowEvent e) {
				quit();
			}
		});
		_logger.exiting(_className, "App");
	}

	/**
	 * Build and show the window.
	 * 
	 * @param configuration The configuration for the application.
	 */
	private void createAndShowGUI(final Configuration configuration) {
		this.setTitle("XML Utilities");
		this.setName("UtilitiesJFrame");
		final StringBuilder msg = new StringBuilder();
//	final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
//	final Dimension screenSize = defaultToolkit.getScreenSize();

		this.getContentPane().setName("UtilitiesContentFrame");
		this.getContentPane().setLayout(new BorderLayout());
//		this.getContentPane().add(this.buildToolBar(configuration), BorderLayout.NORTH);

		this.tabbedPane = new JTabbedPane();
		this.tabbedPane.setName("TabbedPane");

		Boolean origReadOnlyValue = UIManager.getBoolean("FileChooser.readOnly");
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);
		JFileChooser fileChooser = new JFileChooser();
		UIManager.put("FileChooser.readOnly", origReadOnlyValue);

		fileChooser.setName("UtilitiesFileChooser");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File file) {
				boolean ret = false;
//		System.out.println(this.getClass().getName() + ".accept(): file.getName() = " + file.getName());
				if (!file.isHidden()) {
					if (file.isDirectory()) {
						ret = true;
					} else if (file.getName().matches("^Acdm.*\\.xml$")) {
						ret = true;
					}
				}
				return ret;
			}

			@Override
			public String getDescription() {
				return "File filter that accepts XML files and Directories.";
			}
		});
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.setControlButtonsAreShown(false);
		File file = new File(configuration.getStartingDirectory());
		fileChooser.setCurrentDirectory(file);
		if (!file.exists()) {
			if (_logger.isLoggable(Level.INFO)) {
				msg.append("The configuration file (");
				msg.append(configuration.getConfigurationFilePath());
				msg.append(") specifies a starting directory for this application.  This starting directory \"");
				msg.append(file.getAbsolutePath());
				msg.append("\" does not exist.  Defaulting the starting directory to \"");
				msg.append(fileChooser.getCurrentDirectory());
				msg.append("\".");
				_logger.info(msg.toString());
				msg.delete(0, msg.length());
			}
		}
		this.fileSelectionListener = new FileSelectionPropertyChangeListener();
		fileChooser.addPropertyChangeListener(this.fileSelectionListener);
		MouseListener[] mls = (MouseListener[]) (fileChooser.getListeners(MouseListener.class));
		for (int i = 0; i < mls.length; i++) {
			System.out.println(_className + ".buildFrame(): " + mls[i].getClass().getName());
		}
		this.tabbedPane.addTab("FileChoser", fileChooser);
//	this.disableNewFolderButton(fileChooser);

		this.workflowResultsPanel.setLayout(new BorderLayout());
		this.workflowResultsPanel.setName("Results");
		this.workflowResultsPanel.setOpaque(true); // content panes must be opaque
		this.tabbedPane.addTab("Results", this.workflowResultsPanel);

		JTextArea textArea = new JTextArea();
		textArea.setName("ResultsTextArea");
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setName("ScrollPane");
		scrollPane.setSize(400, 400);
		this.workflowResultsPanel.add(scrollPane);

		this.getContentPane().add(this.buildToolBar(configuration), BorderLayout.NORTH);
		this.getContentPane().add(this.tabbedPane, BorderLayout.CENTER);
		this.pack();
		this.setVisible(true);
	}


	/**
	 * Build the toolbar.
	 * 
	 * @param configuration The configuration for the application.
	 */
	private JToolBar buildToolBar(final Configuration configuration) {
		JToolBar tb = new JToolBar();
		tb.setName("UtilitiesToolBar");

		for (String operationName : configuration.getOperationNames()) {
//			EventListener listener = null;
			JButton b = new JButton(operationName);
			b.setToolTipText(configuration.getOperationDescription(operationName));

//			try {
//				listener = this.invokeListener(configuration.getOperationListener(operationName));
//				b.setName(operationName);
//				b.addActionListener((ActionListener) listener);
				tb.add(b);
				tb.addSeparator();
				this.workflowInvocationButtons.add(b);
//			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e1) {
//				// TODO Check all these Exceptions.  Are some subclasses of others?  Can These Exceptions be consolidated?
//				// If keep use of reflection, might be nice to have some type of application alert manager.
//				e1.printStackTrace();
//			}
			b.addActionListener(this);
		}
		this.cancelButton.setEnabled(false);
		tb.add(this.cancelButton);

		tb.add(Box.createHorizontalGlue());
		JButton quit = new JButton("Quit");
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		});
		tb.add(quit, BorderLayout.EAST);
		return tb;
	}

//	private EventListener invokeListener(final String operationListener)
//			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
////		Class<?> clazz = Class.forName(operationListener);
////		EventListener listener = (EventListener) clazz.newInstance();
//		Class<?> clazz;
//		EventListener listener = null;
//
//		if (operationListener.contains("PrettyPrintListener")) {
//			Class<org.fgb.fileOperations.xml.listeners.PrettyPrintListener> el = org.fgb.fileOperations.xml.listeners.PrettyPrintListener.class;
//			try {
//				listener = el.getConstructor(JPanel.class).newInstance(this.workflowResultsPanel);
//			} catch (IllegalArgumentException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (InvocationTargetException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (NoSuchMethodException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (SecurityException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else {
//			clazz = Class.forName(operationListener);
//			listener = (EventListener) clazz.newInstance();
//		}
//		return listener;
//	}


//	private EventListener invokeListener(final String operationListener) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		Class<?> clazz = Class.forName(operationListener);
//		Constructor<?> c = clazz.getConstructor(JPanel.class);
//		EventListener listener = (EventListener) c.newInstance(this.workflowResultsPanel);
//		return listener;
//	}


	/**
	 * Disable the new folder button. This is done to prevent the user from
	 * modifying the file system.
	 * <p>
	 * This method will recursively check the <code>JButton</code>s of the
	 * <code>Container</code> argument. The button is disabled and is not rendered
	 * if it is configured as a "Create New Folder" button.
	 * 
	 * @param c The <code>Container</code> to check.
	 */
	private void disableNewFolderButton(Container c) {
		int len = c.getComponentCount();
		for (int i = 0; i < len; i++) {
			Component comp = c.getComponent(i);
			if (comp instanceof JButton) {
				JButton b = (JButton) comp;
				Icon icon = b.getIcon();
				if (icon != null && icon == UIManager.getIcon("FileChooser.newFolderIcon")) {
					b.setEnabled(false);
					b.setVisible(false);
				}
			} else if (comp instanceof Container) {
				this.disableNewFolderButton((Container) comp);
			}
		}
	}


	/**
	 * Prompt user to confirm exit and do so.
	 */
	private void quit() {
		this.removePropertyChangeListener(this.fileSelectionListener);
		System.runFinalization();
		setVisible(false);
		dispose();
		System.exit(0);
	}


	/**
	 * Shutdown hook called by the Virtual Machine at shutdown time.
	 */
	private class ShutdownHook extends Thread {
		public void run() {
			System.out.println("In shutdown hook...");

		}
	}


	private void toggleButtons(final boolean enabled) {
		for (JButton button: App.this.workflowInvocationButtons) {
			button.setEnabled(enabled);
		}
		App.this.cancelButton.setEnabled(!enabled);
	}


	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		StringBuilder msg = new StringBuilder();
		
		if (_logger.isLoggable(Level.FINEST)) {
			msg.append("SwingUtilities.isEventDispatchThread() = ").append(SwingUtilities.isEventDispatchThread());
			_logger.finest(msg.toString());
			msg.delete(0, msg.length());

			msg.append("PropertyName = ").append(evt.getPropertyName());
			_logger.finest(msg.toString());
			msg.delete(0, msg.length());

			msg.append(".propertyChange(): evt.getOldValue() = ").append(evt.getOldValue());
			msg.append("  evt.getNewValue() = ").append(evt.getNewValue());
			_logger.finest(msg.toString());
			msg.delete(0, msg.length());
			
			msg.append("evt.getOldValue().getClass().getName() = ").append(evt.getOldValue().getClass().getName());
			msg.append("  evt.getNewValue().getClass().getName() = ").append(evt.getNewValue().getClass().getName());
			_logger.finest(msg.toString());
			msg.delete(0, msg.length());
		}

		if ("state" == evt.getPropertyName()) {
			switch ((SwingWorker.StateValue) evt.getNewValue()) {
			case STARTED:
				_logger.fine("New state value is STARTED");
				this.cancelButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						App.this.workflowWorker.cancel(false);
					}
				});
				this.toggleButtons(false);
				break;
			case PENDING:
				_logger.fine("New state value is PENDING");
				break;
			case DONE:
				if (_logger.isLoggable(Level.FINE)) {
					msg.append("New state value is DONE");
					_logger.fine(msg.toString());
					msg.delete(0, msg.length());
				}
				for (ActionListener l : App.this.cancelButton.getActionListeners()) {
					App.this.cancelButton.removeActionListener(l);
				}
				this.toggleButtons(true);
				this.workflowWorker.removePropertyChangeListener(this);
				this.workflowWorker = null;
				break;
			default:
				// We should not be here
				break;
			}
		} else if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
		}
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		StringBuilder msg = new StringBuilder();

		if (_logger.isLoggable(Level.FINER)) {
			msg.append("Action = ").append(e.getActionCommand());
			_logger.finer(msg.toString());
			msg.delete(0, msg.length());
		}

		switch (e.getActionCommand()) {
		case "Validate":
			this.workflowWorker = new ValidateWorker(this.workflowResultsPanel, this.fileSelectionListener.getSelectedFiles());
			break;
		case "PrettyPrint":
			this.workflowWorker = new PrettyPrintWorker(this.workflowResultsPanel, this.fileSelectionListener.getSelectedFiles());
			break;
		case "RegressionTest":
			this.workflowWorker = new RegressionTestWorker(this.workflowResultsPanel, this.fileSelectionListener.getSelectedFiles());
			break;
		default:
			break;
		}
		
		this.tabbedPane.setSelectedComponent(this.workflowResultsPanel);
		
		this.workflowWorker.addPropertyChangeListener(this);
		this.workflowWorker.execute();
	}


	/**
	 * Bootstrap the application.
	 * 
	 * @param args The command line arguments.
	 */
	public static void main(final String[] args) {
		// Command line options
		final Option helpOption = new Option("h", "help", false,
				"Recursively copy a file system, excluding all hidden files.");
		final Option configurationFileOption = new Option("c", "configuration", true,
				"The path to the configuration file.  This argument is mandatory.");
		String configFilePath = null;
		final StringBuilder msg = new StringBuilder();

		final Options options = new Options();
		options.addOption(helpOption);
		options.addOption(configurationFileOption);

		final HelpFormatter formatter = new HelpFormatter();
		final CommandLineParser parser = new DefaultParser();

		try {
			final CommandLine commandLine = parser.parse(options, args);
			if (commandLine.hasOption("h")) {
				formatter.printHelp(_className, options);
				System.exit(0);
			}
			if (commandLine.hasOption("c")) {
				configFilePath = commandLine.getOptionValue("c");
			}
		} catch (final ParseException pe) {
			_logger.log(Level.SEVERE, null, pe);
			System.exit(1);
		}

		// Check for necessary command line args
		if (configFilePath == null) {
			formatter.printHelp(_className, options);
			System.exit(1);
		}
		try {
			final Configuration configuration = new Configuration(configFilePath);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					App app = new App();
					app.createAndShowGUI(configuration);
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(_className + ".main(): Exit main()...");
	}
}
