/*
 *  libParamTuner
 *  Copyright (C) 2017 Marc Baloup, Veïs Oudjail
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.univ_lille1.libparamtuner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import fr.univ_lille1.libparamtuner.parameters.Parameter;
import fr.univ_lille1.libparamtuner.parameters.ParameterFile;
import fr.univ_lille1.libparamtuner.parameters.Type;

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
 *  		ParamTuner.bind("myString", String.class, v -> stringValue = v);
 *  		
 *  		// we use method reference here, if you have a setter for your variable
 *  		ParamTuner.bind("myInteger", Integer.class, Foo::setInt); 
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
 * 		<td>Java type</td>
 * 		<td><code>type</code> attribute</td>
 * 		<td><code>value</code> attribute interpretation</td>
 * 	</tr>
 * 	<tr>
 * 		<td><code>boolean</code>, {@link Boolean}</td>
 * 		<td><code>bool</code> or <code>boolean</code></td>
 * 		<td>See {@link Boolean#parseBoolean(String)}</td>
 * 	</tr>
 * 	<tr>
 * 		<td>{@link String}</td>
 * 		<td><code>string</code></td>
 * 		<td>As is</td>
 * 	</tr>
 * 	<tr>
 * 		<td><code>int</code>, {@link Integer}, <code>long</code>, {@link Long}</td>
 * 		<td><code>int</code>, <code>integer</code> or <code>long</code></td>
 * 		<td>See {@link Long#parseLong(String)}</td>
 * 	</tr>
 * 	<tr>
 * 		<td><code>double</code>, {@link Double}, <code>float</code>, {@link Float}</td>
 * 		<td><code>double</code> or <code>float</code></td>
 * 		<td>See {@link Double#parseDouble(String)}</td>
 * 	</tr>
 * </table>
 * 
 * 
 */
public class ParamTuner {

	/* package */ static void printError(String message) {
		System.err.println("libParamTuner: " + message);
	}
	
	/* package */ static void printInfo(String message) {
		System.out.println("libParamTuner: " + message);
	}
	
	/*
	 * Static data structure
	 */
	private static FileWatcher fileWatcherInstance;
	
	private static Map<String, Bind<?>> binding = Collections.synchronizedMap(new HashMap<>());
	
	private static class Bind<T> {
		public final Class<T> javaType;
		public final Consumer<T> setter;
		
		private Bind(Class<T> jType, Consumer<T> s) {
			javaType = jType;
			setter = s;
		}
	}
	
	/*
	 * Private static method
	 */
	@SuppressWarnings("unchecked")
	private static synchronized void loadFile() {
		if (fileWatcherInstance == null)
			return;
		
		try {
			ParameterFile parameterFile = new ParameterFile(fileWatcherInstance.confFile, true);
			
			for (Parameter param : parameterFile.getAll()) {
				
				try {
					if (binding.containsKey(param.name)) {
						@SuppressWarnings("rawtypes")
						Bind bind = binding.get(param.name);
						Type t = Type.getTypeFromJavaType(bind.javaType);
						if (t.parameterClass.isInstance(param)) {
							bind.setter.accept(Type.getFunctionGetterFromJavaType(bind.javaType, t.parameterClass)
									.apply(t.parameterClass.cast(param)));
						}
						else {
							printError("Setting '" + param.name + "' has type '" + param.getType().name().toLowerCase()
									+ "' in XML file but is binded to Java type " + bind.javaType.getSimpleName()
									+ ".");
						}
					}
					else {
						printError("Setting '" + param.name + "' is not binded to a variable.");
					}
					
				} catch (NumberFormatException e) {
					printError("Value for setting '" + param.name + "' is not a valid.");
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			printError(e.getMessage());
		} catch (Exception e) {
			printError("Error while parsing XML file:");
			e.printStackTrace();
		}
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
		if (fileWatcherInstance != null) {
			fileWatcherInstance.interrupt();
		}
		
		try {
			fileWatcherInstance = new FileWatcher(configFile, fw -> loadFile());
		} catch (Exception e) {
			printError("Can't start Watcher thread because of Exception:");
			e.printStackTrace();
			fileWatcherInstance = null;
			return;
		}
		fileWatcherInstance.start();
	}
	
	
	/**
		Bind a variable with a parameter in the XML file.<br>
		<br>
		This method may be called before or after calling one of <code>load()</code> methods.
		The internal storage of binded values is always preserved.<br>
		<br>
		If a variable is already binded with the specified name, the old
		binding will be erased.
		
		@param parameterName the parameter name, that is equal to the node name
		containing the parameter value.
		
		@param javaType the javaType of the variable that has to be updated.
				Primitive {@link Class} object are not supported. Please specify wrapper
				Class object instead (even if your variable is primitive)
		
		@param setter a {@link Consumer} that take the new value as parameter
				and should apply this new value to the variable.
	*/
	public static <T> void bind(String parameterName, Class<T> javaType, Consumer<T> setter) {
		if (javaType.isPrimitive()) {
			throw new IllegalArgumentException(
					"Please specify wrapper Class object instead of primitive Class object, on parameter 'javaType'");
		}
		if (Type.getTypeFromJavaType(javaType) == null) {
			throw new IllegalArgumentException(
					"Java type " + javaType.getSimpleName() + " is not supported for binding.");
		}
		binding.put(parameterName, new Bind<>(javaType, setter));
	}
	
	
	public static void unbind(String parameterName) {
		binding.remove(parameterName);
	}
	
	
}
