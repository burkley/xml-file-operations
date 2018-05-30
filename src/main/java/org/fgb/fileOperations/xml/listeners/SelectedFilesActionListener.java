package org.fgb.fileOperations.xml.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JFileChooser;

import org.fgb.fileOperations.xml.App;

/**
 * @deprecated
 * @author FrederickBurkley
 *
 */
public class SelectedFilesActionListener implements ActionListener, PropertyChangeListener {
    /** The name of this class. */
    private static final String _className = App.class.getName();
    /** JDK logger. */
    private static final Logger _logger = Logger.getLogger(_className);
    /**
     * Keep a list of the currently selected files from the <code>JFileChooser</code>.
     */
    private List<File> selectedFiles = new ArrayList<File>();

    
    public void actionPerformed(ActionEvent event) {
	JFileChooser fileChooser;
	System.out.println(_className + ".actionPerformed(): event.getClass().getName() = " + event.getClass().getName());
	System.out.println(_className + ".actionPerformed(): event.getSource().getClass().getName() = " + event.getSource().getClass().getName());
	System.out.println(_className + ".actionPerformed(): event.getActionCommand() = " + event.getActionCommand());
	System.out.println(_className + ".actionPerformed(): event.toString() = " + event.toString());
	fileChooser = (JFileChooser)event.getSource();
	File selectedFiles[] = fileChooser.getSelectedFiles();
	System.out.println(_className + ".actionPerformed(): selectedFiles.length = " + selectedFiles.length);
	for (File file : selectedFiles) {
	    System.out.println(_className + ".actionPerformed(): file.getName() = " + file.getName());
	}
    }

    /**
     * 
     */
    public void propertyChange(PropertyChangeEvent event) {
	this.selectedFiles.clear();
	Object oldValue = event.getOldValue();
	Object newValue = event.getNewValue();
	System.out.println(_className + "\n\n");
	System.out.println(_className + ".propertyChange(): event.getPropertyName() = " + event.getPropertyName());
	System.out.println(_className + ".propertyChange(): event.getOldValue() = " + oldValue);
	if (oldValue != null) {
//	    System.out.println(_className + ".propertyChange(): event.getOldValue().getClass().getTypeName() = " + event.getOldValue().getClass().getTypeName());
	    if (oldValue.getClass().isArray()) {
		System.out.println(_className + ".propertyChange(): oldValue is an Array.  Length = " + Array.getLength(oldValue));
		for(int i=0; i<Array.getLength(oldValue); i++) {
		    System.out.println(Array.get(oldValue, i).toString());
		}
	    }
	}
	System.out.println(_className + ".propertyChange(): event.getNewValue() = " + newValue);
	if (newValue != null) {
//	    System.out.println(_className + ".propertyChange(): event.getNewValue().getClass().getTypeName() = " + newValue.getClass().getTypeName());
	    if (newValue.getClass().isArray()) {
		System.out.println(_className + ".propertyChange(): newValue is an Array.  Length = " + Array.getLength(newValue));
		for(int i=0; i<Array.getLength(newValue); i++) {
		    System.out.println(Array.get(newValue, i).toString());
		    if (Array.get(newValue, i) instanceof File) {
			this.selectedFiles.add((File)Array.get(newValue, i));
		    }
		}
	    }
	}
    }
}
