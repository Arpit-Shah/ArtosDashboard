/*******************************************************************************
 * Copyright (C) 2018-2019 Arpit Shah and Artos Contributors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package application.parser;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import application.infra.FWStaticStore;

/**
 * This class is responsible for storing framework Configuration. During test suit execution XML file will be searched at location ./conf
 */
public class FrameworkConfigParser {

	final File fXmlFile = new File(FWStaticStore.CONFIG_BASE_DIR + "framework_configuration.xml");

	// Email Settings Info
	private String udpLocalPort = "11111";

	/**
	 * Constructor
	 * 
	 * @param createIfNotPresent enables creation of default configuration file if not present
	 */
	public FrameworkConfigParser(boolean createIfNotPresent) {
		readXMLConfig(createIfNotPresent);
	}

	/**
	 * Reads Framework configuration file and set global values so framework configurations is available to everyone
	 * 
	 * @param createIfNotPresent enables creation of default configuration file if not present
	 */
	public void readXMLConfig(boolean createIfNotPresent) {

		try {
			if (!fXmlFile.exists() || !fXmlFile.isFile()) {
				if (createIfNotPresent) {
					fXmlFile.getParentFile().mkdirs();
					writeDefaultConfig(fXmlFile);
				}
			}

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			readListenerSettings(doc);
		} catch (FileNotFoundException fe) {
			System.out.println(fe.getMessage() + "\n" + "Fall back to Default Organisation values");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ===============================================================
	// Write
	// ===============================================================

	/**
	 * Writes default framework configuration file
	 * 
	 * @param fXmlFile Destination file object
	 * @throws Exception
	 */
	private void writeDefaultConfig(File fXmlFile) throws Exception {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		docFactory.setNamespaceAware(true);
		docFactory.setValidating(true);
		Schema schema = sf.newSchema(new StreamSource(FWStaticStore.CONFIG_BASE_DIR + File.separator + "framework_configuration.xsd"));
		docFactory.setSchema(schema);

		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("configuration");
		rootElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:noNamespaceSchemaLocation", "framework_configuration.xsd");
		doc.appendChild(rootElement);

		addListenerConfig(doc, rootElement);

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(fXmlFile);

		transformer.transform(source, result);

	}

	private void addListenerConfig(Document doc, Element rootElement) {
		// SMTP Settings
		Element smtp_settings = doc.createElement("listener_config");
		smtp_settings.setAttribute("profile", "dev");
		rootElement.appendChild(smtp_settings);

		// Properties of Organisation Info
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getUdpLocalPort()));
			smtp_settings.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("LocalPort");
			property.setAttributeNode(attr);
		}
	}

	// ===============================================================
	// Read
	// ===============================================================

	/**
	 * Reads Listener Settings from config file
	 * 
	 * @param doc Document object of an XML file
	 */
	private void readListenerSettings(Document doc) {
		NodeList nList = doc.getElementsByTagName("listener_config");

		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);

			Element element = (Element) nNode;

			NodeList nChildList = nNode.getChildNodes();
			for (int i = 0; i < nChildList.getLength(); i++) {
				Node nChildNode = nChildList.item(i);
				if (nChildNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nChildNode;
					if ("LocalPort".equals(eElement.getAttribute("name"))) {
						setUdpLocalPort(eElement.getTextContent());
					}
				}
			}

			break;
		}
	}

	public String getUdpLocalPort() {
		return udpLocalPort;
	}

	public void setUdpLocalPort(String udpLocalPort) {
		this.udpLocalPort = udpLocalPort;
	}
}
