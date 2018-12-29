package org.fgb.fileOperations.xml.utilities;

import java.awt.Component;
import java.awt.Container;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;

import org.fgb.fileOperations.xml.listeners.PrettyPrintListener;

/**
 * Class <code>SwingUtils</code> is a convenience class with some useful swing utilities.
 * 
 * @author Burkley
 */
public class SwingUtils {
	/**
	 * The name of this class.
	 */
	private static final String _className = PrettyPrintListener.class.getName();
	/**
	 * JDK logger.
	 */
	private static final Logger _logger = Logger.getLogger(_className);


	/**
	 * Prevent instances.
	 */
	private SwingUtils() {}


	/**
	 * Find a Swing <code>Component</code> by its name.
	 * <p>
	 * This method performs a hierarchical search of a <code>Container</code>, searching for a
	 * <code>Component</code> whose name matches the input argument <code>name</code>.	 In order
	 * for a <code>Component</code> to be found by this method, its name must be set via its
	 * {@linkplain Component#setName(String)} method.
	 * <p>
	 * If no match is found, <code>null</code> is returned.
	 * 
	 * @param container The <code>Container</code> to search.
	 * @param name The name of <code>Component</code> the to find.
	 * @return The <code>Component</code> whose name matches the input argument <code>name</code>, else <code>null</code>.
	 */
	public static Component findComponentByName(Container container, String name) {
		System.out.println(_className + ".findComponentByName(): ENTER...");
		System.out.println(_className + ".findComponentByName(): container.getName() = " + container.getName() + " container.getClass().getName() = " + container.getClass().getName());

		for (Component component : container.getComponents()) {
			System.out.println(_className + ".findComponentByName(): component.getName() = " + component.getName() + " component.getClass().getName() = " + component.getClass().getName());
			if (name.equals(component.getName())) {
				return component;
			}
			if (component instanceof JRootPane) {
				// When a JRootPane is found, recurse into it and continue searching.
				JRootPane nestedJRootPane = (JRootPane) component;
				return findComponentByName(nestedJRootPane.getContentPane(), name);
			}
			if (component instanceof JPanel) {
				// JPanel found. Recursing into this panel.
				JPanel nestedJPanel = (JPanel) component;
				return findComponentByName(nestedJPanel, name);
			}
			if (component instanceof JScrollPane) {
				// JPanel found. Recursing into this panel.
				JScrollPane nestedJScrollPane = (JScrollPane) component;
				return findComponentByName(nestedJScrollPane.getViewport(), name);
			}
			
		}
		return null;
	}
}
