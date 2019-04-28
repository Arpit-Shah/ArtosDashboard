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
package application.infra;

import java.io.File;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.List;

import application.parser.FrameworkConfigParser;
import javafx.collections.ObservableList;

public class FWStaticStore {

	public static String CURRENT_REVISION = "01.00.0000";
	public static String TOOL_NAME = "Test DashBoard (ARTOS)";
	public static String TEST_DATABASE_FILENAME = "TestSuite.xml";

	// default paths
	public static final String DATABSE_BASE_DIR = "." + File.separator + "database" + File.separator;
	public static final String CONFIG_BASE_DIR = "." + File.separator + "conf" + File.separator;
	public static final String DATABSE_FILE = DATABSE_BASE_DIR + File.separator + TEST_DATABASE_FILENAME;

	// Must be kept after default paths initialised
	public static FrameworkConfigParser frameworkConfig = null;
	public static SystemProperties systemProperties = new SystemProperties();
	public static final String ARTOS_DASHBOARD_BUILD_VERSION = new Version().getBuildVersion();
	public static final String ARTOS_DASHBOARD_BUILD_DATE = new Version().getBuildDate();
	public static List<TestSuite> SuiteList = null;

	// Global variables
	public static UDP udpListener = null;
	public static UDPMessageProcessor msgprocessor = null;
	public static LinkedHashMap<String, LinkedHashMap<String, TestCaseTracker>> testSuitesMap = null;
	public static LinkedHashMap<String, ObservableList<TestCaseTracker>> testSuiteObservableListMap = null;

	// DataBase Connection
	public static Connection connection = null;

}
