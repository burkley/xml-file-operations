package org.fgb.fileOperations.xml.listeners;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;

public class FileSelectionPropertyChangeListener implements PropertyChangeListener {
	/** The name of this class. */
	private static final String _className = FileSelectionPropertyChangeListener.class.getName();
	/** JDK logger. */
	private static final Logger _logger = Logger.getLogger(_className);
	/**
	 * Keep a list of the currently selected files from the
	 * <code>JFileChooser</code>.
	 */
	private List<File> selectedFiles = new ArrayList<File>();

	/**
	 * 
	 */
	public void propertyChange(PropertyChangeEvent event) {

		if (JFileChooser.SELECTED_FILES_CHANGED_PROPERTY.equals(event.getPropertyName())) {
			this.selectedFiles.clear();
			Object newValue = event.getNewValue();
//	    System.out.println(_className + ".propertyChange(): event.getPropertyName() = " + event.getPropertyName());
//	    System.out.println(_className + ".propertyChange(): event.getNewValue() = " + newValue);
			if (newValue != null) {
				if (newValue.getClass().isArray()) {
					for (int i = 0; i < Array.getLength(newValue); i++) {
						_logger.logp(Level.FINE, _className, "propertyChange", Array.get(newValue, i).toString());
						if (Array.get(newValue, i) instanceof File) {
							this.selectedFiles.add((File) Array.get(newValue, i));
						}
					}
				}
			}
//	    System.out.println(_className + ".propertyChange():");
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
