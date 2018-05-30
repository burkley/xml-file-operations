package org.fgb.fileOperations.xml.listeners;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * Class <code>DefaultActionListener</code> implements the {@linkplain java.awt.event.ActionListener} interface.  The
 * {@linkplain #actionPerformed(ActionEvent)} method of this class implements behaviors and or functionality required
 * by the major work flows of this application.
 * <p>
 * Those major work flows are
 * <ul>
 *   <li>Pretty Print XML Instance Documents</li>
 *   <li>Regression test XML Instance Documents</li>
 *   <li>Validate XML Instance Documents</li>
 * </ul>
 * 
 * @see org.fgb.fileOperations.xml.listeners.PrettyPrintListener
 * @see org.fgb.fileOperations.xml.listeners.RegressionTestListener
 * @see org.fgb.fileOperations.xml.listeners.ValidateListener
 * @author FrederickBurkley
 *
 */
public class DefaultActionListener implements ActionListener {
    /**
     * The name of this class.
     */
    private static final String _className = DefaultActionListener.class.getName();
    /**
     * JDK logger.
     */
    private static final Logger _logger = Logger.getLogger(_className);
    /**
     * Keep a list of the currently selected files from the <code>JFileChooser</code>.
     */
    private List<File> selectedFiles;


    /**
     * Default Constructor.
     */
    public DefaultActionListener() {
	this.selectedFiles = new ArrayList<File>();
    }


    /**
     * This method implements behaviors and or functionality required by the major work flows of this application as follows:
     * <ul>
     *   <li>Gets the list of files that are currently selected via the <code>JFileChooser</code>.  This list of files is subsequently available via the {@linkplain #getSelectedFiles()} method.</li>
     *   <li>Switches the focus of the user interface to the "Results" JPanel.</li>
     * </ul>
     */
    public void actionPerformed(ActionEvent event) {
	JButton button;
	System.out.println(_className + ".actionPerformed(): event = " + event);
	System.out.println(_className + ".actionPerformed(): event.getSource().getClass().getName() = " + event.getSource().getClass().getName());
	if (event.getSource() instanceof JButton) {
	    button = (JButton)event.getSource();
	    Container topLevel = button.getTopLevelAncestor();
	    System.out.println(_className + ".actionPerformed(): topLevel.getName() = " + topLevel.getName() + "  getClass().getName() = " + topLevel.getClass().getName());
	    System.out.println(_className + ".actionPerformed(): topLevel.getName() = " + topLevel.getName());
	    System.out.println(_className + ".actionPerformed(): (topLevel instanceof JFrame) = " + (topLevel instanceof JFrame ? true : false));
	    if (topLevel instanceof JFrame) {
		JFrame jframe = (JFrame)topLevel;
		for (Component c1 : jframe.getContentPane().getComponents()) {
//		    System.out.println(_className + ".actionPerformed(): component.getName() = " + c1.getName() + "  getClass().getName() = " + c1.getClass().getName());
		    if (c1 instanceof JTabbedPane) {
			JTabbedPane jtabbedPane = (JTabbedPane)c1;
			for (Component c2 : jtabbedPane.getComponents()) {
//			    System.out.println(_className + ".actionPerformed():   component.getName() = " + c2.getName() + "  getClass().getName() = " + c2.getClass().getName());
			    if (c2 instanceof JFileChooser) {
				JFileChooser chooser = (JFileChooser)c2;
				for (PropertyChangeListener l : chooser.getPropertyChangeListeners()) {
//				    System.out.println(_className + ".actionPerformed(): PCL = " + l.getClass().getName());
				    if (l instanceof FileSelectionPropertyChangeListener) {
					FileSelectionPropertyChangeListener fspcl = (FileSelectionPropertyChangeListener)l;
					this.selectedFiles = fspcl.getSelectedFiles();
//					for (File file : this.selectedFiles) {
//					    System.out.println(_className + ".actionPerformed(): Selected File = " + file.getAbsolutePath());
//					}
				    }
				}
			    } else if (c2 instanceof JPanel) {
				JPanel panel = (JPanel)c2;
				jtabbedPane.setSelectedComponent(panel);
			    }
			}
		    }
		}
	    } else {
		
	    }
	} else {
	    
	}
    }


    /**
     * 
     * @return
     */
    public List<File> getSelectedFiles() {
	return new ArrayList<File>(this.selectedFiles);
    }
}
