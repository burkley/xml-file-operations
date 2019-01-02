package org.fgb.fileOperations.xml.listeners;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class ValidateListener implements ActionListener {
	/**
	 * The name of this class.
	 */
	private static final String _className = ValidateListener.class.getName();
	/**
	 * JDK logger.
	 */
	private static final Logger _logger = Logger.getLogger(_className);
	/**
	 * Keep a list of the currently selected files from the
	 * <code>JFileChooser</code>.
	 */
	private List<File> selectedFiles = new ArrayList<File>();
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
	public ValidateListener() {
		System.out.println(_className + ".ValidateListener()...");
	}

	/**
	 * 
	 */
	public ValidateListener(final JPanel panel) {
		this();
		System.out.println(_className + ".ValidateListener(JPanel j)...");
		this.resultsPanel = panel;
	}

	public void actionPerformed(ActionEvent event) {
		JButton button;
		System.out.println(_className + ".actionPerformed(): event = " + event);
		System.out.println(_className + ".actionPerformed(): event.getSource().getClass().getName() = "
				+ event.getSource().getClass().getName());
		if (event.getSource() instanceof JButton) {
			button = (JButton) event.getSource();
			Container topLevel = button.getTopLevelAncestor();
			System.out.println(_className + ".actionPerformed(): topLevel.getName() = " + topLevel.getName()
					+ "  getClass().getName() = " + topLevel.getClass().getName());
			System.out.println(_className + ".actionPerformed(): topLevel.getName() = " + topLevel.getName());
			System.out.println(_className + ".actionPerformed(): (topLevel instanceof JFrame) = "
					+ (topLevel instanceof JFrame ? true : false));
			if (topLevel instanceof JFrame) {
				JFrame jframe = (JFrame) topLevel;
//		jframe.getContentPane().getComponents();
				for (Component c1 : jframe.getContentPane().getComponents()) {
					System.out.println(_className + ".actionPerformed(): component.getName() = " + c1.getName()
							+ "  getClass().getName() = " + c1.getClass().getName());
					if (c1 instanceof JTabbedPane) {
						JTabbedPane jtabbedPane = (JTabbedPane) c1;
						for (Component c2 : jtabbedPane.getComponents()) {
							System.out.println(_className + ".actionPerformed():   component.getName() = "
									+ c2.getName() + "  getClass().getName() = " + c2.getClass().getName());
							if (c2 instanceof JFileChooser) {
								JFileChooser chooser = (JFileChooser) c2;
							} else if (c2 instanceof JPanel) {
								JPanel panel = (JPanel) c2;
								jtabbedPane.setSelectedComponent(panel);
							}

						}
					}
				}
			}
		}

	}

//    public void propertyChange(PropertyChangeEvent event) {
//	this.selectedFiles.clear();
//	Object oldValue = event.getOldValue();
//	Object newValue = event.getNewValue();
//	System.out.println(_className + "\n\n");
//	System.out.println(_className + ".propertyChange(): event.getPropertyName() = " + event.getPropertyName());
//	System.out.println(_className + ".propertyChange(): event.getOldValue() = " + oldValue);
//	if (oldValue != null) {
////	    System.out.println(_className + ".propertyChange(): event.getOldValue().getClass().getTypeName() = " + event.getOldValue().getClass().getTypeName());
//	    if (oldValue.getClass().isArray()) {
//		System.out.println(_className + ".propertyChange(): oldValue is an Array.  Length = " + Array.getLength(oldValue));
//		for(int i=0; i<Array.getLength(oldValue); i++) {
//		    System.out.println(Array.get(oldValue, i).toString());
//		}
//	    }
//	}
//	System.out.println(_className + ".propertyChange(): event.getNewValue() = " + newValue);
//	if (newValue != null) {
////	    System.out.println(_className + ".propertyChange(): event.getNewValue().getClass().getTypeName() = " + newValue.getClass().getTypeName());
//	    if (newValue.getClass().isArray()) {
//		System.out.println(_className + ".propertyChange(): newValue is an Array.  Length = " + Array.getLength(newValue));
//		for(int i=0; i<Array.getLength(newValue); i++) {
//		    System.out.println(Array.get(newValue, i).toString());
//		    if (Array.get(newValue, i) instanceof File) {
//			this.selectedFiles.add((File)Array.get(newValue, i));
//		    }
//		}
//	    }
//	}
//    }

}
