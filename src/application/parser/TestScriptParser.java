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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import application.exception.InvalidDataException;
import application.infra.FWStaticStore;
import application.infra.TestSuite;

public class TestScriptParser {

	/**
	 * Reads test script and provides testSuite list back to user.
	 * 
	 * @param testScriptFile testScript formatted with XML
	 * @return list of test cases name
	 * @throws ParserConfigurationException if a DocumentBuildercannot be created which satisfies the configuration requested.
	 * @throws IOException If any IO errors occur.
	 * @throws SAXException If any parse errors occur.
	 * @throws InvalidDataException If user provides invalid data
	 */
	public List<TestSuite> readTestScript(File testScriptFile) throws ParserConfigurationException, SAXException, IOException, InvalidDataException {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(testScriptFile);

			doc.getDocumentElement().normalize();

			return readTestScript(doc);
		} catch (FileNotFoundException fe) {
			System.out.println(fe.getMessage() + "\n" + "Fall back to Default Organisation values");
		}
		return null;
	}

	/**
	 * Reads test script
	 * 
	 * @param doc Document object of XML file
	 * @throws InvalidDataException thrown when invalid data is provided by user
	 */
	private List<TestSuite> readTestScript(Document doc) throws InvalidDataException {

		String version = "0";
		List<TestSuite> testDataBaseList = new ArrayList<>();

		Element rootElement = doc.getDocumentElement();
		if (!"".equals(rootElement.getAttribute("version"))) {
			version = rootElement.getAttribute("version");
		}
		NodeList suiteNodeList = doc.getElementsByTagName("suite");

		for (int temp = 0; temp < suiteNodeList.getLength(); temp++) {
			TestSuite _suite = new TestSuite();
			// This is true if object is constructed using test script
			_suite.setVersion(version);

			Node suiteNode = suiteNodeList.item(temp);
			parseSuite(_suite, suiteNode);

			// add test suite in the list even no test list is provided
			if (null != _suite.getTestFQCNList()) {
				testDataBaseList.add(_suite);
			}
		}

		return testDataBaseList;
	}

	private void parseSuite(TestSuite _suite, Node suiteNode) throws InvalidDataException {
		if (suiteNode.getNodeType() == Node.ELEMENT_NODE) {
			Element eElement = (Element) suiteNode;
			if ("suite".equals(eElement.getNodeName())) {

				// Only allow A-Z or a-Z or 0-9 or - to be part of the name
				String suiteName = eElement.getAttribute("name").trim().replaceAll("[^A-Za-z0-9-_]", "");
				// Only allow maximum of 10 digit
				if (suiteName.length() > 10) {
					System.err.println("Warning: TestSuite name >10 char. It will be trimmed");
					suiteName = suiteName.substring(0, 10);
				}
				_suite.setSuiteName(suiteName);
			}

			NodeList testsNodeList = eElement.getElementsByTagName("tests");
			if (testsNodeList.getLength() > 0) {
				for (int temp = 0; temp < testsNodeList.getLength(); temp++) {
					Node testsNode = testsNodeList.item(temp);
					parseTests(_suite, testsNode);
				}
			} else {
				// create empty list so null pointer exception can be avoided
				_suite.setTestFQCNList(new ArrayList<>());
			}
		}
	}

	private void parseTests(TestSuite _suite, Node testsNode) {
		List<String> testFQCNList = new ArrayList<>();
		NodeList nChildList = testsNode.getChildNodes();
		for (int i = 0; i < nChildList.getLength(); i++) {
			Node nChildNode = nChildList.item(i);
			if (nChildNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nChildNode;
				if ("test".equals(eElement.getNodeName())) {
					testFQCNList.add(eElement.getAttribute("name").trim());
				}
			}
		}
		_suite.setTestFQCNList(testFQCNList);
	}

	public void createExecScriptFromObjWrapper() throws Exception {

		File scriptFile;
		String packageName = "ProjectRoot";
		scriptFile = new File(FWStaticStore.DATABSE_BASE_DIR + packageName + ".xml");

		if (scriptFile.exists() && scriptFile.isFile()) {
			// If file is present then do not overwrite
			return;
		}

		if (!scriptFile.getParentFile().exists()) {
			scriptFile.getParentFile().mkdirs();
		}

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("configuration");
		doc.appendChild(rootElement);

		Attr verAttr = doc.createAttribute("version");
		verAttr.setValue("1");
		rootElement.setAttributeNode(verAttr);

		// Organisation Info elements
		Element suite = doc.createElement("suite");
		rootElement.appendChild(suite);

		Attr attr2 = doc.createAttribute("name");
		attr2.setValue("UniqueName");
		suite.setAttributeNode(attr2);

		createTestList(doc, suite);

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(scriptFile);

		transformer.transform(source, result);

	}

	private void createTestList(Document doc, Element suite) {
		// Organisation Info elements
		Element tests = doc.createElement("tests");
		suite.appendChild(tests);

		for (int i = 0; i < 2; i++) {
			// add test cases
			Element property = doc.createElement("test");
			property.appendChild(doc.createTextNode(""));
			tests.appendChild(property);

			Attr attr1 = doc.createAttribute("name");
			attr1.setValue("com.testproject.testclass.testName" + i + 1);
			property.setAttributeNode(attr1);
		}
	}
}
