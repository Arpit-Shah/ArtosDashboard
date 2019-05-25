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

import org.apache.logging.log4j.Level;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
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
	String profileName = "dev";

	// UDP Connector
	private String udpLocalPort = "11111";

	// Organisation Info
	private String Organisation_Name = "<Organisation> PTY LTD";
	private String Organisation_Address = "XX, Test Street, Test address";
	private String Organisation_Country = "NewZealand";
	private String Organisation_Contact_Number = "+64 1234567";
	private String Organisation_Email = "artos.framework@gmail.com";
	private String Organisation_Website = "www.theartos.com";

	// Logger
	private String logLevel = "debug";
	private String logRootDir = FWStaticStore.LOG_BASE_DIR;
	private boolean enableLogDecoration = false;
	private boolean enableTextLog = true;
	private boolean enableHTMLLog = false;
	private boolean enableExtentReport = true;

	/**
	 * Constructor
	 * 
	 * @param createIfNotPresent enables creation of default configuration file if not present
	 * @param profileName profile name for choosing correct framework configuration
	 */
	public FrameworkConfigParser(boolean createIfNotPresent, String profileName) {
		this.profileName = profileName;
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

			readOrganisationInfo(doc);
			readLoggerConfig(doc);
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

		addOrganisatioInfo(doc, rootElement);
		addLoggerConfig(doc, rootElement);
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

	private void addOrganisatioInfo(Document doc, Element rootElement) {
		// Organisation Info elements
		Element orgnization_info = doc.createElement("organization_info");
		orgnization_info.setAttribute("profile", "dev");
		rootElement.appendChild(orgnization_info);

		// Properties of Organisation Info
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getOrganisation_Name()));
			orgnization_info.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("Name");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getOrganisation_Address()));
			orgnization_info.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("Address");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getOrganisation_Country()));
			orgnization_info.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("Country");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getOrganisation_Contact_Number()));
			orgnization_info.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("Contact_Number");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getOrganisation_Email()));
			orgnization_info.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("Email");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getOrganisation_Website()));
			orgnization_info.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("Website");
			property.setAttributeNode(attr);
		}
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

	private void addLoggerConfig(Document doc, Element rootElement) {
		// Logger config elements
		Element logger = doc.createElement("logger");
		logger.setAttribute("profile", "dev");
		rootElement.appendChild(logger);
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getLogLevel()));
			logger.appendChild(property);

			Comment comment = doc.createComment("LogLevel Options : info:debug:trace:fatal:warn:all");
			property.getParentNode().insertBefore(comment, property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("logLevel");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(getLogRootDir()));
			logger.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("logRootDir");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(Boolean.toString(isEnableLogDecoration())));
			logger.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("enableLogDecoration");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(Boolean.toString(isEnableTextLog())));
			logger.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("enableTextLog");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(Boolean.toString(isEnableHTMLLog())));
			logger.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("enableHTMLLog");
			property.setAttributeNode(attr);
		}
		{
			Element property = doc.createElement("property");
			property.appendChild(doc.createTextNode(Boolean.toString(isEnableExtentReport())));
			logger.appendChild(property);

			Attr attr = doc.createAttribute("name");
			attr.setValue("enableExtentReport");
			property.setAttributeNode(attr);
		}
	}

	// ===============================================================
	// Read
	// ===============================================================

	/**
	 * Reads logger info from config file
	 * 
	 * @param doc Document object of XML file
	 */
	private void readLoggerConfig(Document doc) {
		// System.out.println("Root element :" +
		// doc.getDocumentElement().getNodeName());
		NodeList nList = doc.getElementsByTagName("logger");

		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);

			Element element = (Element) nNode;

			// If profile is provided then look for configuration with given profile or else take default
			if (element.hasAttributes()) {
				if (profileName == null || !profileName.equals(element.getAttribute("profile").toString().trim())) {
					if (profileName == null || temp == nList.getLength() - 1) {
						System.err.println("[WARNING]: logger profile with name " + profileName + " does not exist. Applying default");
					}
					continue;
				}
			}

			NodeList nChildList = nNode.getChildNodes();
			for (int i = 0; i < nChildList.getLength(); i++) {
				Node nChildNode = nChildList.item(i);
				if (nChildNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nChildNode;

					if ("logLevel".equals(eElement.getAttribute("name"))) {
						setLogLevel(eElement.getTextContent());
					} else if ("logRootDir".equals(eElement.getAttribute("name"))) {
						String rootDir = eElement.getTextContent();
						if (rootDir.endsWith("/") || rootDir.endsWith("\\")) {
							setLogRootDir(rootDir);
						} else {
							setLogRootDir(rootDir + File.separator);
						}
					} else if ("enableLogDecoration".equals(eElement.getAttribute("name"))) {
						setEnableLogDecoration(Boolean.parseBoolean(eElement.getTextContent()));
					} else if ("enableTextLog".equals(eElement.getAttribute("name"))) {
						setEnableTextLog(Boolean.parseBoolean(eElement.getTextContent()));
					} else if ("enableHTMLLog".equals(eElement.getAttribute("name"))) {
						setEnableHTMLLog(Boolean.parseBoolean(eElement.getTextContent()));
					} else if ("enableExtentReport".equals(eElement.getAttribute("name"))) {
						setEnableExtentReport(Boolean.parseBoolean(eElement.getTextContent()));
					}
				}
			}

			break;
		}
	}

	/**
	 * Reads organisationInfo from config file
	 * 
	 * @param doc Document object of an XML file
	 */
	private void readOrganisationInfo(Document doc) {
		// System.out.println("Root element :" +
		// doc.getDocumentElement().getNodeName());
		NodeList nList = doc.getElementsByTagName("organization_info");

		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);

			Element element = (Element) nNode;

			// If profile is provided then look for configuration with given profile or else take default
			if (element.hasAttributes()) {
				if (profileName == null || !profileName.equals(element.getAttribute("profile").toString().trim())) {
					if (profileName == null || temp == nList.getLength() - 1) {
						System.err.println("[WARNING]: organization_info profile with name " + profileName + " does not exist. Applying default");
					}
					continue;
				}
			}

			NodeList nChildList = nNode.getChildNodes();
			for (int i = 0; i < nChildList.getLength(); i++) {
				Node nChildNode = nChildList.item(i);
				if (nChildNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nChildNode;
					// System.out.println(eElement.getNodeName());
					// System.out.println(eElement.getAttribute("name"));
					// System.out.println(eElement.getAttribute("name") +
					// ":" +
					// eElement.getTextContent());
					if ("Name".equals(eElement.getAttribute("name"))) {
						setOrganisation_Name(eElement.getTextContent());
					} else if ("Address".equals(eElement.getAttribute("name"))) {
						setOrganisation_Address(eElement.getTextContent());
					} else if ("Country".equals(eElement.getAttribute("name"))) {
						setOrganisation_Country(eElement.getTextContent());
					} else if ("Contact_Number".equals(eElement.getAttribute("name"))) {
						setOrganisation_Contact_Number(eElement.getTextContent());
					} else if ("Email".equals(eElement.getAttribute("name"))) {
						setOrganisation_Email(eElement.getTextContent());
					} else if ("Website".equals(eElement.getAttribute("name"))) {
						setOrganisation_Website(eElement.getTextContent());
					}
				}
			}

			break;
		}
	}

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

			// If profile is provided then look for configuration with given profile or else take default
			if (element.hasAttributes()) {
				if (profileName == null || !profileName.equals(element.getAttribute("profile").toString().trim())) {
					if (profileName == null || temp == nList.getLength() - 1) {
						System.err.println("[WARNING]: listener_config profile with name " + profileName + " does not exist. Applying default");
					}
					continue;
				}
			}

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

	/**
	 * Returns Log Level Enum value based on Framework configuration set in XML file
	 * 
	 * @see Level
	 * 
	 * @return LogLevel
	 * @see Level
	 */
	public Level getLoglevelFromXML() {
		String logLevel = getLogLevel();
		if (logLevel.equals("info")) {
			return Level.INFO;
		}
		if (logLevel.equals("all")) {
			return Level.ALL;
		}
		if (logLevel.equals("fatal")) {
			return Level.FATAL;
		}
		if (logLevel.equals("trace")) {
			return Level.TRACE;
		}
		if (logLevel.equals("warn")) {
			return Level.WARN;
		}
		if (logLevel.equals("debug")) {
			return Level.DEBUG;
		}
		return Level.DEBUG;
	}

	public String getUdpLocalPort() {
		return udpLocalPort;
	}

	public void setUdpLocalPort(String udpLocalPort) {
		this.udpLocalPort = udpLocalPort;
	}

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	public String getLogRootDir() {
		return logRootDir;
	}

	public void setLogRootDir(String logRootDir) {
		this.logRootDir = logRootDir;
	}

	public boolean isEnableLogDecoration() {
		return enableLogDecoration;
	}

	public void setEnableLogDecoration(boolean enableLogDecoration) {
		this.enableLogDecoration = enableLogDecoration;
	}

	public boolean isEnableTextLog() {
		return enableTextLog;
	}

	public void setEnableTextLog(boolean enableTextLog) {
		this.enableTextLog = enableTextLog;
	}

	public boolean isEnableHTMLLog() {
		return enableHTMLLog;
	}

	public void setEnableHTMLLog(boolean enableHTMLLog) {
		this.enableHTMLLog = enableHTMLLog;
	}

	public boolean isEnableExtentReport() {
		return enableExtentReport;
	}

	public void setEnableExtentReport(boolean enableExtentReport) {
		this.enableExtentReport = enableExtentReport;
	}

	public String getOrganisation_Name() {
		return Organisation_Name;
	}

	public void setOrganisation_Name(String organisation_Name) {
		Organisation_Name = organisation_Name;
	}

	public String getOrganisation_Address() {
		return Organisation_Address;
	}

	public void setOrganisation_Address(String organisation_Address) {
		Organisation_Address = organisation_Address;
	}

	public String getOrganisation_Country() {
		return Organisation_Country;
	}

	public void setOrganisation_Country(String organisation_Country) {
		Organisation_Country = organisation_Country;
	}

	public String getOrganisation_Contact_Number() {
		return Organisation_Contact_Number;
	}

	public void setOrganisation_Contact_Number(String organisation_Contact_Number) {
		Organisation_Contact_Number = organisation_Contact_Number;
	}

	public String getOrganisation_Email() {
		return Organisation_Email;
	}

	public void setOrganisation_Email(String organisation_Email) {
		Organisation_Email = organisation_Email;
	}

	public String getOrganisation_Website() {
		return Organisation_Website;
	}

	public void setOrganisation_Website(String organisation_Website) {
		Organisation_Website = organisation_Website;
	}
}
