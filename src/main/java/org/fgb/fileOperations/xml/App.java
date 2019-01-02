package org.fgb.fileOperations.xml;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EventListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.fgb.fileOperations.xml.listeners.FileSelectionPropertyChangeListener;
import org.fgb.fileOperations.xml.utilities.Configuration;

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
public class App extends JFrame {
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
	 * made available to the various {@linkplain java.awt.event.ActionListener}s
	 * that invoke the major work flows of this application.
	 */
	private FileSelectionPropertyChangeListener fileSelectionPCL;

	/**
	 * There is a single <i>results panel</i> for the application.  This is the panel
	 * where results of the various <i>work flows</i> will be published.  The reference
	 * to this panel is passed as an argument to the listeners that control invocation
	 * of the work flows.
	 */
	private final JComponent resultsPanel = new JPanel();

	/**
	 * Default Constructor.
	 */
	public App() {
		super();

		// add shutdown hook
		final ShutdownHook shutdownHook = new ShutdownHook();
		final Runtime runtime = Runtime.getRuntime();
		runtime.addShutdownHook(shutdownHook);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(final WindowEvent e) {
				quit();
			}
		});
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

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setName("TabbedPane");

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
		this.fileSelectionPCL = new FileSelectionPropertyChangeListener();
		fileChooser.addPropertyChangeListener(this.fileSelectionPCL);
		MouseListener[] mls = (MouseListener[]) (fileChooser.getListeners(MouseListener.class));
		for (int i = 0; i < mls.length; i++) {
			System.out.println(_className + ".buildFrame(): " + mls[i].getClass().getName());
		}
		tabbedPane.addTab("FileChoser", fileChooser);
//	this.disableNewFolderButton(fileChooser);

		this.resultsPanel.setLayout(new BorderLayout());
		this.resultsPanel.setName("Results");
		this.resultsPanel.setOpaque(true); // content panes must be opaque
		tabbedPane.addTab("Results", this.resultsPanel);

		JTextArea textArea = new JTextArea();
		textArea.setName("ResultsTextArea");
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setName("ScrollPane");
		scrollPane.setSize(400, 400);
		this.resultsPanel.add(scrollPane);

		this.getContentPane().add(this.buildToolBar(configuration), BorderLayout.NORTH);
		this.getContentPane().add(tabbedPane, BorderLayout.CENTER);
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
			EventListener listener = null;
			JButton b = new JButton(operationName);
			b.setToolTipText(configuration.getOperationDescription(operationName));

			try {
				listener = this.invokeListener(configuration.getOperationListener(operationName));
				b.setName(operationName);
				b.addActionListener((ActionListener) listener);
				tb.add(b);
				tb.addSeparator();
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e1) {
				// TODO Check all these Exceptions.  Are some subclasses of others?  Can These Exceptions be consolidated?
				// If keep use of reflection, might be nice to have some type of application alert manager.
				e1.printStackTrace();
			}
		}
		JButton cancel = new JButton("Cancel");
		cancel.setEnabled(false);
		tb.add(cancel);

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
//				listener = el.getConstructor(JPanel.class).newInstance(this.resultsPanel);
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


	private EventListener invokeListener(final String operationListener) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class<?> clazz = Class.forName(operationListener);
		Constructor<?> c = clazz.getConstructor(JPanel.class);
		EventListener listener = (EventListener) c.newInstance(this.resultsPanel);
		return listener;
	}


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
		this.removePropertyChangeListener(this.fileSelectionPCL);
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
