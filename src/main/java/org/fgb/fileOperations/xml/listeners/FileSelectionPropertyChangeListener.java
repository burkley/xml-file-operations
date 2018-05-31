package org.fgb.fileOperations.xml.listeners;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;


public class FileSelectionPropertyChangeListener implements PropertyChangeListener {
    /** The name of this class. */
    private static final String _className = FileSelectionPropertyChangeListener.class.getName();
    /** JDK logger. */
    private static final Logger _logger = Logger.getLogger(_className);
    /**
     * Keep a list of the currently selected files from the <code>JFileChooser</code>.
     */
    private List<File> selectedFiles = new ArrayList<File>();

    /**
     * 
     */
    public void propertyChange(PropertyChangeEvent event) {
	this.selectedFiles.clear();
	Object oldValue = event.getOldValue();
	Object newValue = event.getNewValue();
	System.out.println(_className + ".propertyChange(): event.getPropertyName() = " + event.getPropertyName());
//	System.out.println(_className + ".propertyChange(): event.getOldValue() = " + oldValue);
	if (oldValue != null) {
//	    System.out.println(_className + ".propertyChange(): event.getOldValue().getClass().getTypeName() = " + event.getOldValue().getClass().getTypeName());
	    if (oldValue.getClass().isArray()) {
//		System.out.println(_className + ".propertyChange(): oldValue is an Array.  Length = " + Array.getLength(oldValue));
		for(int i=0; i<Array.getLength(oldValue); i++) {
		    System.out.println(Array.get(oldValue, i).toString());
		}
	    }
	}
	System.out.println(_className + ".propertyChange(): event.getNewValue() = " + newValue);
	if (newValue != null) {
//	    System.out.println(_className + ".propertyChange(): event.getNewValue().getClass().getTypeName() = " + newValue.getClass().getTypeName());
	    if (newValue.getClass().isArray()) {
//		System.out.println(_className + ".propertyChange(): newValue is an Array.  Length = " + Array.getLength(newValue));
		for(int i=0; i<Array.getLength(newValue); i++) {
//		    System.out.println(Array.get(newValue, i).toString());
		    if (Array.get(newValue, i) instanceof File) {
			this.selectedFiles.add((File)Array.get(newValue, i));
		    }
		}
	    }
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
