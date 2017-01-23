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
	public static void load(String configFile) {
		load(new File(configFile));
	}
	
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
	
	public static void bindLong(String settingName, LongConsumer setter) {
		clearBind(settingName);
		longBinding.put(settingName, setter);
	}
	
	public static void bindInt(String settingName, IntConsumer setter) {
		clearBind(settingName);
		intBinding.put(settingName, setter);
	}
	
	public static void bindFloat(String settingName, Consumer<Float> setter) {
		clearBind(settingName);
		floatBinding.put(settingName, setter);
	}
	
	public static void bindDouble(String settingName, DoubleConsumer setter) {
		clearBind(settingName);
		doubleBinding.put(settingName, setter);
	}
	
	public static void bindBoolean(String settingName, Consumer<Boolean> setter) {
		clearBind(settingName);
		booleanBinding.put(settingName, setter);
	}
	
	public static void bindString(String settingName, Consumer<String> setter) {
		clearBind(settingName);
		stringBinding.put(settingName, setter);
	}
	
	
	
}
