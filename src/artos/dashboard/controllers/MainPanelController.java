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
package artos.dashboard.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.core.LoggerContext;
import org.xml.sax.SAXException;

import application.exception.InvalidDataException;
import application.infra.FWStaticStore;
import application.infra.LogWrapper;
import application.infra.OrganisedLog;
import application.infra.TestContext;
import application.interfaces.CrossTalk;
import application.parser.FrameworkConfigParser;
import application.parser.TestScriptParser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainPanelController {

	private double x = 0, y = 0;

	@FXML
	private BorderPane parent;

	@FXML
	void initialize() throws Exception {

		provideSchema();
		readFrameworkConfig();
		generateRequiredDir();
		generateRequiredFiles();
		getTestDataBaseList();

		// Create new context for each thread
		TestContext context = new TestContext();
		// generate logger context
		LoggerContext loggerContext = createGlobalLoggerContext();
		// Get logger for particular thread and set to context object
		LogWrapper logWrapper = new LogWrapper(loggerContext, 0);
		// store logger
		context.setLogWrapper(logWrapper);

		try {
			// Admin Panel
			FXMLLoader adminPanelLoader = new FXMLLoader(getClass().getResource("/artos/dashboard/views/AdminPanel.fxml"));
			Parent adminPanel = adminPanelLoader.load();

			// Menu Bar
			FXMLLoader menubarLoader = new FXMLLoader(getClass().getResource("/artos/dashboard/views/Menubar.fxml"));
			Parent menubar = menubarLoader.load();

			// Progress Panel
			FXMLLoader progressPanelLoader = new FXMLLoader(getClass().getResource("/artos/dashboard/views/ProgressPanel.fxml"));
			Parent contentPanel = progressPanelLoader.load();

			// Load each Parent to Boarder Layout
			parent.setLeft(adminPanel);
			parent.setTop(menubar);
			// parent.setCenter(logPanel);
			parent.setRight(contentPanel);

			// Register Listener
			// CrossTalkListener listener = new CrossTalkListener();
			AdminPanelController adminController = adminPanelLoader.getController();
			// ProgressPanelController is implemented with CrossTalk interface
			CrossTalk progressPanelController = progressPanelLoader.getController();
			adminController.registerListener(progressPanelController);

		} catch (BindException e) {
			Parent menubar = FXMLLoader.load(getClass().getResource("/artos/dashboard/views/Menubar.fxml"));
			Parent contentPanel = FXMLLoader.load(getClass().getResource("/artos/dashboard/views/WarningPanel.fxml"));
			parent.setTop(menubar);
			parent.setCenter(contentPanel);
		}

		makeDragable();
	}

	private void makeDragable() {

		parent.setOnMousePressed(((event) -> {
			x = event.getSceneX();
			y = event.getSceneY();
		}));

		parent.setOnMouseDragged(((event) -> {
			// Get Stage
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

			// Set X, Y
			stage.setX(event.getScreenX() - x);
			stage.setY(event.getScreenY() - y);

			// Make it little transparent
			stage.setOpacity(0.6f);
		}));

		parent.setOnDragDone(((event) -> {

			// Set Opacity back to normal
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setOpacity(1.0f);
		}));

		parent.setOnMouseReleased(((event) -> {
			// Set Opacity back to normal
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setOpacity(1.0f);
		}));
	}

	private void getTestDataBaseList() throws ParserConfigurationException, SAXException, IOException, InvalidDataException {
		TestScriptParser scriptParser = new TestScriptParser();
		FWStaticStore.SuiteList = scriptParser.readTestScript(new File(FWStaticStore.DATABSE_FILE));
	}

	public static void generateRequiredDir() {
		File dataBaseDir = new File(FWStaticStore.DATABSE_BASE_DIR);
		if (!dataBaseDir.exists() || !dataBaseDir.isDirectory()) {
			// create directory if not present
			dataBaseDir.mkdirs();
		}
	}

	private void readFrameworkConfig() {
		FWStaticStore.frameworkConfig = new FrameworkConfigParser(true, "dev");
	}

	private void provideSchema() throws IOException {
		// transfer XML validator
		boolean transferXSD = true;
		if (transferXSD) {
			// only create xsd file if not present already
			File targetFile = new File(FWStaticStore.CONFIG_BASE_DIR + File.separator + "framework_configuration.xsd");
			if (!targetFile.exists() || !targetFile.isFile()) {

				// create dir if not present
				File file = new File(FWStaticStore.CONFIG_BASE_DIR);
				if (!file.exists() || !file.isDirectory()) {
					file.mkdirs();
				}

				InputStream ins = getClass().getResourceAsStream("/application/template/framework_configuration.xsd");
				byte[] buffer = new byte[ins.available()];
				ins.read(buffer);

				OutputStream outStream = new FileOutputStream(targetFile);
				outStream.write(buffer);
				outStream.flush();
				outStream.close();
				ins.close();
			}
		}
	}

	private void generateRequiredFiles() throws IOException {
		if (FWStaticStore.frameworkConfig.isEnableExtentReport()) {
			// only create Extent configuration file if not present already
			File targetFile = new File(FWStaticStore.CONFIG_BASE_DIR + File.separator + "extent_configuration.xml");

			if (!targetFile.exists() || !targetFile.isFile()) {

				// create dir if not present
				File file = new File(FWStaticStore.CONFIG_BASE_DIR);
				if (!file.exists() || !file.isDirectory()) {
					file.mkdirs();
				}

				InputStream ins = getClass().getResourceAsStream("/application/template/extent_configuration.xml");
				byte[] buffer = new byte[ins.available()];
				ins.read(buffer);

				OutputStream outStream = new FileOutputStream(targetFile);
				outStream.write(buffer);
				outStream.flush();
				outStream.close();
				ins.close();
			}
		}
	}

	/**
	 * Creates appenders for number of suites provided incase parallel execution is required, if test script is not provided then appenders are
	 * created for one thread
	 * 
	 * @param testSuiteList list of testSuites
	 * @return LoggetContext
	 */
	private LoggerContext createGlobalLoggerContext() {
		// Create loggerContext with all possible thread appenders
		/**
		 * Package name can not be used for log sub-directory name in case where test cases are launched from project root directory, thus log will
		 * come out in logging base directory.
		 */
		String logSubDir = "artos.dashboard";

		// Get Framework configuration set by user
		String logDirPath = FWStaticStore.frameworkConfig.getLogRootDir();
		boolean enableLogDecoration = FWStaticStore.frameworkConfig.isEnableLogDecoration();
		boolean enableTextLog = FWStaticStore.frameworkConfig.isEnableTextLog();
		boolean enableHTMLLog = FWStaticStore.frameworkConfig.isEnableHTMLLog();

		// Create loggerContext
		application.infra.OrganisedLog organisedLog = new OrganisedLog(logDirPath, logSubDir, enableLogDecoration, enableTextLog, enableHTMLLog);
		return organisedLog.getLoggerContext();
	}

}
