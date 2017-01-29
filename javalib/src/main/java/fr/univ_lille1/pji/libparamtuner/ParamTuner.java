package fr.univ_lille1.pji.libparamtuner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class allowing developers to set up internal software settings in real-time.
 * It avoid repetitively compile a whole software every time we need to change
 * a constant value inside the source code.
 * <p>
 * <b>How to use this library ?</b>
 * 
 * <ol>
 * 	<li>Create an XML file. It must contain a parent node <code>&lt;ParamNode&gt;</code>,
 * 		which contains itseft some node that respect theses rules :
 * 		<ul>
 * 			<li>The name of the node is the name of the parameter</li>
 * 			<li>The node have a <code>type</code> attribute wich describe the type of the parameter</li>
 * 			<li>The node have a <code>value</code> attribe that contains the value of the parameter</li>
 * 		</ul>
 * 		For example :
 * 		<pre>
 *&lt;ParamNode&gt;
 *	&lt;myString type="string" value="foo"/&gt;
 *	&lt;myInteger type="int" value="183"/&gt;
 *&lt;/ParamNode&gt;</pre>
 *  </li>
 *  <li>
 *  	Call the method {@link #load(String)} to monitor modifications of the XML file.
 *  </li>
 *  <li>Then you must say to the library which variable will be updated when the file is modified.<br>
 *  	The library can only update static variables or member variable (due to Java limitations).<br>
 *  	Example code :
 *  <pre>
 *  class Foo {
 *  	// values to update
 *  	public static int intValue;
 *  	public static String stringValue;
 *  
 *  	// setter for intValue;
 *  	public static void setInt(int newValue) { intValue = newValue; }
 *  	
 *  	public static void main(String[] args) throws Exception {
 *  		
 *  		ParamTuner.load("settings.xml"); // relative path are allowed
 *  
 *  		// we use lambda expression here. v is the new value read in file.
 *  		ParamTuner.bindString("myString", v -> stringValue = v);
 *  		
 *  		// we use method reference here, if you have a setter for your variable
 *  		ParamTuner.bindInt("myInteger", Foo::setInt); 
 *  		
 *  		while(true) { // your main loop
 *  			Thread.sleep(500);
 *  			System.out.println(intValue + " " + stringValue);
 *  		}
 *  	}
 *  }</pre>
 *  	For this example, when the file is saved with the XML example above, <code>intValue</code>
 *  	will contain 123 and <code>stringValue</code> will contain "foo".
 *  	
 *  </li>
 * </ol>
 * 
 * <b>List of supported types :</b>
 * <table border="1">
 * 	<tr>
 * 		<td>Bind method</td>
 * 		<td>Java type</td>
 * 		<td><code>type</code> attribute</td>
 * 		<td><code>value</code> attribute interpretation</td>
 * 	</tr>
 * 	<tr>
 * 		<td>{@link #bindBoolean(String, Consumer)}</td>
 * 		<td><code>boolean</code>, {@link Boolean}</td>
 * 		<td><code>bool</code></td>
 * 		<td>See {@link Boolean#parseBoolean(String)}</td>
 * 	</tr>
 * 	<tr>
 * 		<td>{@link #bindString(String, Consumer)}</td>
 * 		<td>{@link String}</td>
 * 		<td><code>string</code></td>
 * 		<td>As is</td>
 * 	</tr>
 * 	<tr>
 * 		<td>{@link #bindInt(String, IntConsumer)}</td>
 * 		<td><code>int</code>, {@link Integer}</td>
 * 		<td><code>int</code></td>
 * 		<td>See {@link Integer#parseInt(String)}</td>
 * 	</tr>
 * 	<tr>
 * 		<td>{@link #bindLong(String, LongConsumer)}</td>
 * 		<td><code>long</code>, {@link Long}</td>
 * 		<td><code>int</code></td>
 * 		<td>See {@link Long#parseLong(String)}</td>
 * 	</tr>
 * 	<tr>
 * 		<td>{@link #bindFloat(String, Consumer)}</td>
 * 		<td><code>float</code>, {@link Float}</td>
 * 		<td><code>double</code></td>
 * 		<td>See {@link Float#parseFloat(String)}</td>
 * 	</tr>
 * 	<tr>
 * 		<td>{@link #bindDouble(String, DoubleConsumer)}</td>
 * 		<td><code>double</code>, {@link Double}</td>
 * 		<td><code>double</code></td>
 * 		<td>See {@link Double#parseDouble(String)}</td>
 * 	</tr>
 * </table>
 * 
 * 
 */
public class ParamTuner extends Thread {
	
	private final File confFile;
	private final Path parentPath;
	
	private final WatchService watcher;
	private final WatchKey key;
	
	private ParamTuner(File configFile) throws IOException {
		super("libParamTuner Thread");
		
		confFile = configFile.getAbsoluteFile();
		if (!confFile.isFile())
			throw new IllegalArgumentException("configFile is not a regular file");
		if (confFile.getParentFile() == null)
			throw new IllegalArgumentException("configFile hasn't got a parent directory");
		
		parentPath = confFile.getParentFile().toPath();
		
		// initilaise a new watcher service
		watcher = FileSystems.getDefault().newWatchService();
		
		key = parentPath.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
	}
	
	private static void printError(String message) {
		System.err.println("libParamTuner: " + message);
	}
	
	@Override
	public void run() {
		try {
			for (;;) {
				
				// wait for key to be signalled
				WatchKey key;
				try {
					key = watcher.take();
				} catch (InterruptedException e) {
					printError("thread interrupted");
					watcher.close();
					return;
				}
				
				
				if (key == null || !key.equals(this.key))
					continue;
				
				for (WatchEvent<?> event : key.pollEvents()) {
					WatchEvent.Kind<?> kind = event.kind();
					
					// TBD - provide example of how OVERFLOW event is handled
					if (kind == StandardWatchEventKinds.OVERFLOW)
						continue;
					
					@SuppressWarnings("unchecked")
					Path child = parentPath.resolve(((WatchEvent<Path>) event).context());
					if (!child.toFile().equals(confFile))
						continue;
					
					loadFile(true);
					
				}
				
				// reset key and remove from set if directory no longer accessible
				boolean valid = key.reset();
				if (!valid) {
					printError("WatchKey no longer valid");
					watcher.close();
					return;
				}
			}
		} catch (Exception e) {
			printError("Exception in watcher thread:");
			e.printStackTrace();
		}
	}
	
	/*
	 * Static data structure
	 */
	private static ParamTuner paramWatcherInstance;
	
	private static Map<String, LongConsumer> longBinding = Collections.synchronizedMap(new HashMap<>());
	private static Map<String, IntConsumer> intBinding = Collections.synchronizedMap(new HashMap<>());
	private static Map<String, Consumer<Float>> floatBinding = Collections.synchronizedMap(new HashMap<>());
	private static Map<String, DoubleConsumer> doubleBinding = Collections.synchronizedMap(new HashMap<>());
	private static Map<String, Consumer<Boolean>> booleanBinding = Collections.synchronizedMap(new HashMap<>());
	private static Map<String, Consumer<String>> stringBinding = Collections.synchronizedMap(new HashMap<>());
	
	
	/*
	 * Private static method
	 */
	private static synchronized void loadFile(boolean verbose) {
		if (paramWatcherInstance == null)
			return;
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = null;
			
			do {
				/* Parfois, le fichier n'est pas lisible au moment de recevoir la
				 * notification depuis le système de fichier (sûrement car le fichier
				 * est encore en cours d'écriture)
				 */
				try {
					doc = builder.parse(paramWatcherInstance.confFile);
				} catch(FileNotFoundException e) {
					Thread.sleep(50); // attendre avant de réessayer de lire
				}
			} while(doc == null);
			
			Element parentEl = doc.getDocumentElement();
			
			if (!parentEl.getTagName().equalsIgnoreCase("ParamList")) {
				if (verbose)
					printError("Settings file does not contains ParamList root node");
				return;
			}
			
			NodeList paramNodes = parentEl.getChildNodes();
			
			for (int i = 0; i < paramNodes.getLength(); i++) {
				Node node = paramNodes.item(i);
				if (node.getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element el = (Element) node;
				
				String name = el.getTagName();
				String value = el.getAttribute("value");
				String type = el.getAttribute("type").toLowerCase();
				
				// apply value to corresponding variable
				try {
					if (type.equals("int")) {
						if (intBinding.containsKey(name)) {
							intBinding.get(name).accept(Integer.parseInt(value));
						}
						else if (longBinding.containsKey(name)) {
							longBinding.get(name).accept(Long.parseLong(value));
						}
						else if (verbose) {
							printError("Setting '" + name + "' is not binded to a int or long variable.");
						}
					}
					else if (type.equals("double")) {
						if (floatBinding.containsKey(name)) {
							floatBinding.get(name).accept(Float.parseFloat(value));
						}
						else if (doubleBinding.containsKey(name)) {
							doubleBinding.get(name).accept(Double.parseDouble(value));
						}
						else if (verbose) {
							printError("Setting '" + name + "' is not binded to a float or double variable.");
						}
					}
					else if (type.equals("bool")) {
						if (booleanBinding.containsKey(name)) {
							booleanBinding.get(name).accept(Boolean.parseBoolean(value));
						}
						else if (verbose) {
							printError("Setting '" + name + "' is not binded to a boolean variable.");
						}
					}
					else if (type.equals("string")) {
						if (stringBinding.containsKey(name)) {
							stringBinding.get(name).accept(value);
						}
						else if (verbose) {
							printError("Setting '" + name + "' is not binded to a String variable.");
						}
					}
					else if (verbose) {
						printError("Setting '" + name + "' has an unsupported 'type' attribut");
					}
				} catch (NumberFormatException e) {
					if (verbose)
						printError("Value for setting '" + name + "' is not a valid " + type + ".");
				}
				
				
			}
			
			
		} catch (FileNotFoundException e) {
			printError(e.getMessage());
		}
		catch (Exception e) {
			printError("Error while parsing XML file:");
			e.printStackTrace();
		}
	}
	
	
	private static void clearBind(String settingName) {
		intBinding.remove(settingName);
		longBinding.remove(settingName);
		floatBinding.remove(settingName);
		doubleBinding.remove(settingName);
		booleanBinding.remove(settingName);
		stringBinding.remove(settingName);
	}
	
	
	
	
	
	/*
	 * Public static methods (public API)
	 */
	
	/**
	 * Start listening modifications of the specified file. <br>
	 * 
	 * Calling this method is equivalent to :
	 * 
	 * <pre>ParamTuner.load(new File(configFile));</pre>
	 * 
	 * @param configFile The absolute or relative path to the file.
	 * 
	 * @see #load(File)
	 */
	public static void load(String configFile) {
		load(new File(configFile));
	}
	

	/**
	 * Start listening modifications of the specified file. <br>
	<br>
	After this method call, when the specified file is modified
	by another program, the variables binded with one of the bind...() method
	are updated to their new values from file.<br>
	<br>
	The specified file's content must be an XML file with a root node
	"ParamList". Direct child node of the ParamList node represent a
	parameter in your program, for example :
		<pre>&lt;paramName value="foo" type="string"/&gt;</pre>
	
	
	If a node is not binded to a variable, a message will be sent to standard
	error stream. If a node has not a valid type or value, an error will be
	displayed too, and the variable will not be updated.<br>
	<br>
	When this method is called multiple times, the current call disable
	listener of the previous call.
	
	@param configFile the {@link File} to listen to
	
	 */
	public static synchronized void load(File configFile) {
		if (paramWatcherInstance != null) {
			paramWatcherInstance.interrupt();
		}
		
		try {
			paramWatcherInstance = new ParamTuner(configFile);
		} catch (Exception e) {
			printError("Can't start Watcher thread because of Exception:");
			e.printStackTrace();
			paramWatcherInstance = null;
			return;
		}
		paramWatcherInstance.start();
	}
	
	/**
		Bind a long variable with a parameter in the XML file.<br>
		<br>
		The parameter in XML file must be of <code>type="int"</code>, otherwise
		an error is displayed in terminal.<br>
		<br>
		This method may be called before or after calling lptLoad().
		The internal storage of binded values is always preserved.<br>
		<br>
		If a variable is already binded with the specified name, the old
		binding will be erased.
		
		@param settingName the parameter name, that is equal to the node name
		containing the parameter value.
		
		@param setter a {@link Consumer} that take the new value on first
		parameter and may apply this new value to the variable.
	*/
	public static void bindLong(String settingName, LongConsumer setter) {
		clearBind(settingName);
		longBinding.put(settingName, setter);
	}
	

	/**
		Bind a int variable with a parameter in the XML file.<br>
		<br>
		The parameter in XML file must be of <code>type="int"</code>, otherwise
		an error is displayed in terminal.<br>
		<br>
		This method may be called before or after calling lptLoad().
		The internal storage of binded values is always preserved.<br>
		<br>
		If a variable is already binded with the specified name, the old
		binding will be erased.
		
		@param settingName the parameter name, that is equal to the node name
		containing the parameter value.
		
		@param setter a {@link Consumer} that take the new value on first
		parameter and may apply this new value to the variable.
	*/
	public static void bindInt(String settingName, IntConsumer setter) {
		clearBind(settingName);
		intBinding.put(settingName, setter);
	}
	
	/**
		Bind a float variable with a parameter in the XML file.<br>
		<br>
		The parameter in XML file must be of <code>type="double"</code>, otherwise
		an error is displayed in terminal.<br>
		<br>
		This method may be called before or after calling lptLoad().
		The internal storage of binded values is always preserved.<br>
		<br>
		If a variable is already binded with the specified name, the old
		binding will be erased.
		
		@param settingName the parameter name, that is equal to the node name
		containing the parameter value.
		
		@param setter a {@link Consumer} that take the new value on first
		parameter and may apply this new value to the variable.
	*/
	public static void bindFloat(String settingName, Consumer<Float> setter) {
		clearBind(settingName);
		floatBinding.put(settingName, setter);
	}

	
	/**
		Bind a double variable with a parameter in the XML file.<br>
		<br>
		The parameter in XML file must be of <code>type="double"</code>, otherwise
		an error is displayed in terminal.<br>
		<br>
		This method may be called before or after calling lptLoad().
		The internal storage of binded values is always preserved.<br>
		<br>
		If a variable is already binded with the specified name, the old
		binding will be erased.
		
		@param settingName the parameter name, that is equal to the node name
		containing the parameter value.
		
		@param setter a {@link Consumer} that take the new value on first
		parameter and may apply this new value to the variable.
	*/
	public static void bindDouble(String settingName, DoubleConsumer setter) {
		clearBind(settingName);
		doubleBinding.put(settingName, setter);
	}

	
	/**
		Bind a boolean variable with a parameter in the XML file.<br>
		<br>
		The parameter in XML file must be of <code>type="bool"</code>, otherwise
		an error is displayed in terminal.<br>
		<br>
		This method may be called before or after calling lptLoad().
		The internal storage of binded values is always preserved.<br>
		<br>
		If a variable is already binded with the specified name, the old
		binding will be erased.
		
		@param settingName the parameter name, that is equal to the node name
		containing the parameter value.
		
		@param setter a {@link Consumer} that take the new value on first
		parameter and may apply this new value to the variable.
	*/
	public static void bindBoolean(String settingName, Consumer<Boolean> setter) {
		clearBind(settingName);
		booleanBinding.put(settingName, setter);
	}

	
	/**
		Bind a {@link String} variable with a parameter in the XML file.<br>
		<br>
		The parameter in XML file must be of <code>type="string"</code>, otherwise
		an error is displayed in terminal.<br>
		<br>
		This method may be called before or after calling lptLoad().
		The internal storage of binded values is always preserved.<br>
		<br>
		If a variable is already binded with the specified name, the old
		binding will be erased.
		
		@param settingName the parameter name, that is equal to the node name
		containing the parameter value.
		
		@param setter a {@link Consumer} that take the new value on first
		parameter and may apply this new value to the variable.
	*/
	public static void bindString(String settingName, Consumer<String> setter) {
		clearBind(settingName);
		stringBinding.put(settingName, setter);
	}
	
	
	
}
