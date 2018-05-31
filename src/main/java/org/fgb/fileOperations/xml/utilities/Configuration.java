package org.fgb.fileOperations.xml.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;


/**
 * 
 * @author FrederickBurkley
 *
 */
public class Configuration {
    /** The name of this class. */
    private static final String _className = Configuration.class.getName();
    /** JDK logger. */
    private static final Logger _logger = Logger.getLogger(_className);
    /**
     * 
     */
    private final LinkedHashMap<String,HashMap<String,String>> operations;
    /**
     * A copy of the configuration file path for debugging purposes.
     */
    private final String configurationFilePath;
    /**
     * The starting directory for this application.
     */
    private final String startingDirectory;


    /**
     * Default Constructor.
     * 
     * @param path The path to the configuration file.
     * @throws IOException 
     */
    public Configuration(final String path) throws IOException {
	this.operations = new LinkedHashMap<String, HashMap<String, String>>();
	File file = new File(path);
	this.configurationFilePath = file.getAbsolutePath();
	FileInputStream fis = new FileInputStream(file);

	JsonReader jsonReader = Json.createReader(fis);
	JsonObject jsonObject = jsonReader.readObject();
	JsonArray operations = jsonObject.getJsonArray("operations");
	for (int i=0; i<operations.size(); i++) {
	    JsonObject op = operations.getJsonObject(i);
	    this.operations.put(op.getString("name"), new HashMap<String, String>());
	    this.operations.get(op.getString("name")).put("description", op.getString("description"));
	    this.operations.get(op.getString("name")).put("listener", op.getString("listener"));
//	    System.out.println(_className + ".Configuration(): this.xOperations = " + this.operations);
	}
	this.startingDirectory = jsonObject.getString("startingDirectory");
	try {
	    jsonReader.close();
	} catch (JsonException je) {
	    throw new IOException(je);
	}
//	System.out.println("jsonObject = " + jsonObject);
//	System.out.println("operations = " + this.operations);
    }


    /**
     * 
     * @return
     */
    public Set<String> getOperationNames() {
	return this.operations.keySet();
    }
    
    /**
     * 
     * @param operationName
     * @return
     * @throws IllegalArgumentException
     */
    public String getOperationDescription(final String operationName) throws IllegalArgumentException {
	String ret = null;

	if (this.operations.containsKey(operationName)) {
	    ret = this.operations.get(operationName).get("description");
	} else {
	    StringBuilder msg = new StringBuilder("The operationName \"");
	    msg.append(operationName);
	    msg.append("\" is not valid.  Valid operationNames are ");
	    msg.append(this.operations.keySet().toString());
	    msg.append(".");
	    throw new IllegalArgumentException(msg.toString());
	}
	return ret;
    }

    /**
     * 
     * @param operationName
     * @return
     * @throws IllegalArgumentException
     */
    public String getOperationListener(final String operationName) throws IllegalArgumentException {
	String ret = null;

	if (this.operations.containsKey(operationName)) {
	    ret = this.operations.get(operationName).get("listener");
	} else {
	    StringBuilder msg = new StringBuilder("The operationName \"");
	    msg.append(operationName);
	    msg.append("\" is not valid.  Valid operationNames are ");
	    msg.append(this.operations.keySet().toString());
	    msg.append(".");
	    throw new IllegalArgumentException(msg.toString());
	}
	return ret;
    }

    public String getStartingDirectory() {
        return this.startingDirectory;
    }
    
    public String getConfigurationFilePath() {
	return this.configurationFilePath;
    }
}
