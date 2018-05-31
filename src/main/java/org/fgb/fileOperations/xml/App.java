package org.fgb.fileOperations.xml;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.EventListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

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
 * Application "XML File Operations".  This application renders a Graphical User Interface that orchestrates several
 * useful <i>work flows</i> on an XML file or set of XML file(s).
 * <p>
 * Those work flows are:
 * <ul>
 *   <li>Pretty Print XML Instance Documents</li>
 *   <li>Regression test XML Instance Documents</li>
 *   <li>Validate XML Instance Documents</li>
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
//    private static final float _WINDOW_HEIGHT = 0.4f;
//    private static final float _WINDOW_WIDTH = 0.5f;

    /**
     * There is a single file selection property change listener for the application.  This listener will keep track
     * of the files (i.e. a list of files) that are currently selected via the JFileCooser.  This list of files is
     * made available to the various {@linkplain java.awt.event.ActionListener}s that invoke the major work flows of
     * this application.
     */
    private FileSelectionPropertyChangeListener fileSelectionPCL;

    /**
     * Default Constructor.
     * 
     * @param configuration The configuration for the application.
     */
    public App(final Configuration configuration) {
	super();
	// build the gui
	this.buildFrame(configuration);

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
     * Build the window.
     * 
     * @param configuration The configuration for the application.
     */
    private void buildFrame(final Configuration configuration) {
	this.setTitle("XML Utilities");
	this.setName("UtilitiesJFrame");
	final StringBuilder msg = new StringBuilder();
//	final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
//	final Dimension screenSize = defaultToolkit.getScreenSize();

//	final int screenWidth = screenSize.width;
//	final int screenHeight = screenSize.height;
//	final int windowWidth = (int) (screenWidth * _WINDOW_WIDTH);
//	final int windowHeight = (int) (screenHeight * _WINDOW_HEIGHT);
//	final int screenX = (screenWidth - windowWidth) / 3;
//	final int screenY = (screenHeight - windowHeight) / 3;
//	setBounds(screenX, screenY, windowWidth, windowHeight);

//	this.setJMenuBar(this.buildMenus(configuration));
	this.getContentPane().setName("UtilitiesContentFrame");
	this.getContentPane().setLayout(new BorderLayout());
	this.getContentPane().add(this.buildToolBar(configuration), BorderLayout.NORTH);
	// i_iFrameListener = new IFrameListener();
	
	JTabbedPane tabbedPane = new JTabbedPane();
	tabbedPane.setName("TabbedPane");

//	tabbedPane.addTab("Panel1", panel1);
	JFileChooser fileChooser = new JFileChooser();
	fileChooser.setName("UtilitiesFileChooser");
	fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//	FileNameExtensionFilter filter = new FileNameExtensionFilter("ACDM XML File", "xml");
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
	tabbedPane.addTab("FileChoser", fileChooser);

	JComponent resultsPanel = new JPanel();
	resultsPanel.setName("Results");
	tabbedPane.addTab("Results", resultsPanel);
	
	this.getContentPane().add(tabbedPane, BorderLayout.CENTER);
	this.pack();
    }


    /**
     * Build the toolbar.
     * 
     * @param configuration The configuration for the application.
     */
    private JToolBar buildToolBar(final Configuration configuration) {
	JToolBar tb = new JToolBar ();
	tb.setName("UtilitiesToolBar");
//      tb.setLayout(new BorderLayout());

	for (String operationName : configuration.getOperationNames()) {
	    EventListener listener = null;
	    JButton b = new JButton(operationName);
	    b.setToolTipText(configuration.getOperationDescription(operationName));

	    try {
		listener = this.invokeListener(configuration.getOperationListener(operationName));
		b.setName(operationName);
		b.addActionListener((ActionListener) listener);
	    } catch (ClassNotFoundException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
		b.setEnabled(false);
	    } catch (InstantiationException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
		b.setEnabled(false);
	    } catch (IllegalAccessException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
		b.setEnabled(false);
	    } catch (IllegalArgumentException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
		b.setEnabled(false);
	    }

	    tb.add(b);
	    tb.addSeparator();
	}
      JButton cancel = new JButton("Cancel");
      tb.add(cancel);

      tb.add(Box.createHorizontalGlue());
      JButton quit = new JButton("Quit");
      quit.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          quit();
      }});
      tb.add(quit, BorderLayout.EAST);
      return tb;
    }


    private EventListener invokeListener(String operationListener) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
	Class<?> clazz = Class.forName(operationListener);
	EventListener listener = (EventListener) clazz.newInstance();
	return listener;
    }

    /**
     * Build the menus.
     */
    private JMenuBar buildMenus(final Configuration configuration) {
      JMenuBar mb = new JMenuBar();
      mb.setOpaque(true);
//      JMenu help = buildHelpMenu();
      mb.add(this.buildFileMenu(configuration));
//      mb.add(help);
      return mb;	
    }


    /**
     * Build the File menu.
     */
    private JMenu buildFileMenu(final Configuration configuration) {
      JMenu     file     = new JMenu("File");
      JMenuItem quitItem = new JMenuItem("Quit");
      quitItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          quit();
      }});
      file.add(quitItem);
      return file;
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
     * @param args The command line arguments.
     */
    public static void main(final String[] args) {
	// Command line options
	final Option helpOption = new Option("h", "help", false, "Recursively copy a file system, excluding all hidden files.");
	final Option configurationFileOption = new Option("c", "configuration", true, "The path to the configuration file.  This argument is mandatory.");
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
	Configuration configuration = null;
	try {
	    configuration = new Configuration(configFilePath);	    
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	final JFrame frame = new App(configuration);
	frame.setVisible(true);
    }
}
