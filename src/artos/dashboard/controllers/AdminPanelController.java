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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import application.infra.FWStaticStore;
import application.infra.TestCaseTracker;
import application.infra.TestSuite;
import application.infra.UDP;
import application.infra.UDPMessageProcessor;
import application.interfaces.CrossTalk;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class AdminPanelController {

	List<CrossTalk> listenerList = new ArrayList<CrossTalk>();

	@FXML
	private ComboBox<String> SuiteCombobox;

	@FXML
	void initialize() throws InterruptedException {

		SuiteCombobox.setOnAction((event) -> {
			String client = SuiteCombobox.getSelectionModel().getSelectedItem();
			System.out.println("ComboBox Action (selected: " + client + ")");
			notifyUserSelectedClient(client);
		});

		ObservableList<String> suiteNames = FXCollections.observableArrayList();
		FWStaticStore.testSuitesMap = new LinkedHashMap<String, LinkedHashMap<String, TestCaseTracker>>();

		for (TestSuite suite : FWStaticStore.SuiteList) {

			// populate combo-box
			String suiteName = suite.getSuiteName();
			System.out.println(suiteName);
			suiteNames.add(suiteName);

			// Get all FQCN from the test suite
			List<String> FQCNList = suite.getTestFQCNList();

			// Create test case tracker object per FQCN within a suite
			LinkedHashMap<String, TestCaseTracker> suiteTestFQCNMap = new LinkedHashMap<>();
			for (String fqcn : FQCNList) {
				System.out.println(fqcn);
				TestCaseTracker tct = new TestCaseTracker();
				tct.setTestcaseFQCN(fqcn);
				suiteTestFQCNMap.put(fqcn, tct);
			}

			// Add suite FQCN map against suite name for later use
			FWStaticStore.testSuitesMap.put(suiteName, suiteTestFQCNMap);
		}

		SuiteCombobox.getItems().clear();
		SuiteCombobox.setItems(suiteNames);
		SuiteCombobox.getSelectionModel().select(0);

		// attempt to bind UDP port
		if (null == FWStaticStore.udpListener) {

			// Start UDP listener
			FWStaticStore.udpListener = new UDP("127.0.0.1", 11111, "127.0.0.1", 22222);
			FWStaticStore.udpListener.connect();

			// Start MSG processor
			FWStaticStore.msgprocessor = new UDPMessageProcessor();
			FWStaticStore.msgprocessor.startProcessing();
		}
	}

	// ==================================================================================
	// Register, deRegister and Notify Event Listeners
	// ==================================================================================

	protected void registerListener(CrossTalk listener) {
		listenerList.add(listener);
	}

	protected void deRegisterListener(CrossTalk listener) {
		listenerList.remove(listener);
	}

	protected void deRegisterAllListener() {
		listenerList.clear();
	}

	void notifyUserSelectedClient(String client) {
		for (CrossTalk listener : listenerList) {
			listener.userSelection(client);
		}
	}
}
