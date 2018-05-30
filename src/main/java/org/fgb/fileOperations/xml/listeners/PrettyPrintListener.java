package org.fgb.fileOperations.xml.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.logging.Logger;

public class PrettyPrintListener implements ActionListener {
    /**
     * The name of this class.
     */
    private static final String _className = PrettyPrintListener.class.getName();
    /**
     * JDK logger.
     */
    private static final Logger _logger = Logger.getLogger(_className);
    private final DefaultActionListener defaultListener;

    /**
     * 
     */
    public PrettyPrintListener() {
	this.defaultListener = new DefaultActionListener();
    }


    /**
     * 
     */
    public void actionPerformed(ActionEvent event) {
	this.defaultListener.actionPerformed(event);
	for (File file : this.defaultListener.getSelectedFiles()) {
	    System.out.println(_className + ".actionPerformed(): Selected file = " + file.getAbsolutePath());
	}
    }

}
