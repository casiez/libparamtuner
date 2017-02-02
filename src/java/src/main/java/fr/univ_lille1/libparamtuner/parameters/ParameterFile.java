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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ParameterFile {
	
	private final Map<String, Parameter> parameters = new TreeMap<>();
	
	public final File file;
	

	public ParameterFile(String fileName, boolean loadFromFile) throws Exception {
		this(new File(fileName), loadFromFile);
	}
	
	public ParameterFile(File f, boolean loadFromFile) throws Exception {
		this(f, loadFromFile, false);
	}
	
	public ParameterFile(String fileName, boolean loadFromFile, boolean retryLoading) throws Exception {
		this(new File(fileName), loadFromFile, retryLoading);
	}
	
	public ParameterFile(File f, boolean loadFromFile, boolean retryLoading) throws Exception {
		file = f;
		
		if (loadFromFile) {
			DocumentBuilder builder = getXMLBuilder();
			Document doc = null;
			int i = 0;
			do {
				/* Parfois, le fichier n'est pas lisible au moment de recevoir la
				 * notification depuis le système de fichier (sûrement car le fichier
				 * est encore en cours d'écriture)
				 */
				try {
					doc = builder.parse(file);
				} catch(IOException e) {
					i++; // itérateur si jamais le fichier n'existe vraiment plus, on ne boucle pas à l'infini
					if (i >= 10 || !retryLoading)
						throw e;
					Thread.sleep(50); // attendre avant de réessayer de lire
				}
			} while(doc == null);
			readDocument(doc);
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
		
		for (Parameter s : parameters.values()) {
			parentEl.appendChild(s.toXMLElement(doc));
		}
		
		
		return doc;
	}
	
	
	
	public void save() {
		try {
		Transformer tf = TransformerFactory.newInstance().newTransformer();
		tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		tf.setOutputProperty(OutputKeys.INDENT, "yes");
		tf.transform(new DOMSource(createXMLDocument()), new StreamResult(file));
		} catch(TransformerException | ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void addParameter(Parameter s) {
		parameters.put(s.name, s);
	}
	
	public Parameter getParameter(String name) {
		return parameters.get(name);
	}
	
	public void removeSetting(String name) {
		parameters.remove(name);
	}
	
	public List<Parameter> getAll() {
		return new ArrayList<>(parameters.values());
	}
	
	
}
