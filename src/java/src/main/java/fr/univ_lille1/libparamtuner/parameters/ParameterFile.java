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
package fr.univ_lille1.libparamtuner.parameters;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory;

public class ParameterFile {
	
	private final Map<String, Parameter> parameters = new TreeMap<>();
	private final Vector<String> paramOrder = new Vector<String>(); 
	
	public final File file;
	
	public ParameterFile(String fileName, boolean loadFromFile) throws Exception {
		this(new File(fileName), loadFromFile);
	}
	
	public ParameterFile(File f, boolean loadFromFile) throws Exception {
		file = f;
		
		if (loadFromFile) {
			load();
		}
	}
	
	
	private DocumentBuilder getXMLBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringComments(true);
		return factory.newDocumentBuilder();
	}
	
	private void readDocument(Document doc) throws Exception {
		Element parentEl = doc.getDocumentElement();
		
		if (!parentEl.getTagName().equalsIgnoreCase("ParamList")) {
			throw new Exception("Settings file does not contains ParamList root node");
		}
		
		NodeList paramNodes = parentEl.getChildNodes();
		
		for (int i = 0; i < paramNodes.getLength(); i++) {
			Node node = paramNodes.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Parameter s = Parameter.fromXMLElement((Element) node);
			addParameter(s);
		}
	}
	
	private Document createXMLDocument() throws ParserConfigurationException {
		Document doc = getXMLBuilder().newDocument();
		
		Element parentEl = doc.createElement("ParamList");
		doc.appendChild(parentEl);
		
		for (String s : paramOrder) {
			parentEl.appendChild(parameters.get(s).toXMLElement(doc));
		}
		
		
		return doc;
	}
	
	private String createXMLstring() {
		String res = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n";
		res += "<ParamList>\n";
		
		for (String s : paramOrder) {
			res += "\t" + parameters.get(s).toXMLstring() + "\n";	
		}
		res += "</ParamList>\n";
		
		return res;
	}
	
	public void save() throws Exception {
		
		// converting content to XML String
		Transformer tf = TransformerFactory.newInstance().newTransformer();
		tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		tf.setOutputProperty(OutputKeys.INDENT, "yes");
		tf.setOutputProperty(OutputPropertiesFactory.S_BUILTIN_OLD_EXTENSIONS_UNIVERSAL +"indent-amount", "4");
		tf.setOutputProperty(OutputKeys.VERSION, "1.0");
		System.out.println(tf.getOutputProperties().keySet());
		String content2;
		try (StringWriter swr = new StringWriter()) {
			tf.transform(new DOMSource(createXMLDocument()), new StreamResult(swr));
			content2 = swr.toString();
		}
		
		String content = createXMLstring();
		
		System.out.println(content);
		System.out.println(content2);
		
		// saving XML to file (multiple try if needed)
		boolean ok = false;
		for(int i = 0; !ok; i++) {
			/*
			 * Sometimes, the file is not writable, surely because an other
			 * software is still reading/writing in the file. We try multiple
			 * times to write the file until it succeeds.
			 */
			try (FileWriter wr = new FileWriter(file, false)) {
				wr.write(content);
				ok = true;
			} catch (IOException e) {
				/* In case of the file will not writable anymore (access right ?)
				 * we count and stop if we've done to many trials
				 */
				if (i >= 10)
					throw e;
				Thread.sleep(50); // Wait before retry
				continue;
			}
			
		}
	}
	
	
	public void load() throws Exception {
		DocumentBuilder builder = getXMLBuilder();
		Document doc = null;
		int i = 0;
		do {
			/*
			 * Sometimes, the file is not readable when we receive the notification
			 * from the watcher (surely because an other software is still writing
			 * in the file). We try multiple times to read the file until it
			 * succeeds.
			 */
			try {
				doc = builder.parse(file);
			} catch (Exception e) {
				/* In case of the file will not readable anymore (deleted ? )
				 * we count and stop if we've done to many trials
				 */
				i++;
				if (i >= 10)
					throw e;
				Thread.sleep(50); // Wait before retry
			}
		} while (doc == null);
		readDocument(doc);
	}
	
	
	
	public void addParameter(Parameter s) {
		parameters.put(s.name, s);
		paramOrder.add(s.name);
	}
	
	public Parameter getParameter(String name) {
		return parameters.get(name);
	}
	
	public void removeParameter(String name) {
		parameters.remove(name);
	}
	
	public List<Parameter> getAll() {
		ArrayList<Parameter> res = new ArrayList<Parameter>();
		
		for (String s : paramOrder) {
			res.add(parameters.get(s));
		}
		return res;
	}
	
	
}
