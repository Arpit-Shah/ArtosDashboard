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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import application.infra.FWStaticStore;
import application.infra.TestCaseTracker;
import application.interfaces.CrossTalk;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class ProgressPanelController implements CrossTalk {

	TestProgress clientTask = null;
	Thread clientTaskThread = null;
	List<CrossTalk> listenerList = new ArrayList<CrossTalk>();
	Map<String, ClientProgressWrapper> clientProgressMap = new HashMap<>();

	@FXML
	private TableView<ProgressTable> progress_tableview;

	@FXML
	private PieChart Pie_chart_1;

	@FXML
	private PieChart Pie_chart_2;

	@FXML
	private ProgressIndicator Progress_Indicator;

	@FXML
	void initialize() throws Exception {

		prepareTableColumn();
		configureCommonPieChartPropertis();
		prepareClientObjects();
		prepareLandingPage();

	}

	private void prepareTableColumn() {

		TableColumn<ProgressTable, String> column1 = new TableColumn<>("Test Case");
		column1.setCellValueFactory(new PropertyValueFactory<>("testcaseFQCN"));
		column1.setSortable(false);

		TableColumn<ProgressTable, String> column2 = new TableColumn<>("TestCase");

		TableColumn<ProgressTable, String> column3 = new TableColumn<>("Updated #");
		column3.setCellValueFactory(new PropertyValueFactory<>("testcaseEventCount"));
		column3.setStyle("-fx-alignment: CENTER;");
		// column3.getStyleClass().add("-fx-alignment: CENTER-RIGHT;");
		column3.setSortable(false);

		TableColumn<ProgressTable, String> column4 = new TableColumn<>("Status");
		column4.setCellValueFactory(new PropertyValueFactory<>("testcaseStatus"));
		column4.setStyle("-fx-alignment: CENTER;");
		column4.setSortable(false);

		// Add sub columns to the UpdateCount
		column2.getColumns().addAll(column3, column4);

		TableColumn<ProgressTable, String> column5 = new TableColumn<>("TestUnit");

		// Same as Event Count
		// TableColumn<ProgressTable, String> column6 = new TableColumn<>("Updated #");
		// column6.setCellValueFactory(new PropertyValueFactory<>("testcaseEventCount"));
		// column6.setStyle( "-fx-alignment: CENTER;");
		// column6.setSortable(false);

		TableColumn<ProgressTable, String> column7 = new TableColumn<>("Unit Count");
		column7.setCellValueFactory(new PropertyValueFactory<>("testcaseUnitCount"));
		column7.setStyle("-fx-alignment: CENTER;");
		column7.setSortable(false);

		// TableColumn<ProgressTable, String> column7 = new TableColumn<>("TestUnit");
		// column7.setCellValueFactory(new PropertyValueFactory<>("testUnitStatus"));
		// column7.setSortable(false);
		//
		// Add sub columns to the Status
		column5.getColumns().addAll(column7);

		column1.prefWidthProperty().bind(progress_tableview.widthProperty().multiply(0.68));

		progress_tableview.getColumns().clear();
		progress_tableview.getColumns().add(column1);
		progress_tableview.getColumns().add(column2);
		progress_tableview.getColumns().add(column5);

		column4.setCellFactory(new Callback<TableColumn<ProgressTable, String>, TableCell<ProgressTable, String>>() {

			@Override
			public TableCell<ProgressTable, String> call(TableColumn<ProgressTable, String> param) {
				return new TableCell<ProgressTable, String>() {

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (!isEmpty()) {
							if (item.contains("UNKNOWN")) {
								this.setTextFill(Color.BLACK);
							} else if (item.contains("PASS")) {
								this.setTextFill(Color.GREEN);
							} else if (item.contains("KTF")) {
								this.setTextFill(Color.ORANGE);
							} else if (item.contains("FAIL")) {
								this.setTextFill(Color.RED);
							} else if (item.contains("SKIP")) {
								this.setTextFill(Color.BLUEVIOLET);
							}
						}
						setText(item);
					}
				};
			}
		});

	}

	/**
	 * Create separate properties per client so during client switch, data is not wrongly represented. It would be cheaper to create same properties
	 * but if user have too many test cases to parse then momentarily next client will show old clients data which can confuse user, so we create and
	 * maintain separate properties per client to give it more real feel.
	 */
	private void prepareClientObjects() {
		LinkedHashMap<String, LinkedHashMap<String, TestCaseTracker>> testSuitesMap = FWStaticStore.testSuitesMap;

		for (Map.Entry<String, LinkedHashMap<String, TestCaseTracker>> entry : testSuitesMap.entrySet()) {
			String client = entry.getKey();

			ClientProgressWrapper cpw = new ClientProgressWrapper();
			cpw.setClientName(client);

			ObservableList<ProgressTable> observableProgressTableList = FXCollections.observableArrayList();
			cpw.setObservableProgressTableList(observableProgressTableList);

			// Set TestCase PieChart Data Objects
			cpw.setPieChartData_TestCase(FXCollections.observableArrayList(new PieChart.Data("PASS", 0), new PieChart.Data("FAIL", 0),
					new PieChart.Data("SKIP", 0), new PieChart.Data(" KTF", 0)));
			// Set TestUnit PieChart Data Objects
			cpw.setPieChartData_UnitCase(FXCollections.observableArrayList(new PieChart.Data("PASS", 0), new PieChart.Data("FAIL", 0),
					new PieChart.Data("SKIP", 0), new PieChart.Data(" KTF", 0)));

			// Set Test Case Data Object property value
			cpw.setTestCasePASSCount(new SimpleIntegerProperty(0));
			cpw.setTestCaseFAILCount(new SimpleIntegerProperty(0));
			cpw.setTestCaseSKIPCount(new SimpleIntegerProperty(0));
			cpw.setTestCaseKTFCount(new SimpleIntegerProperty(0));

			// Set Test Unit Data Object property value
			cpw.setTestUnitPASSCount(new SimpleIntegerProperty(0));
			cpw.setTestUnitFAILCount(new SimpleIntegerProperty(0));
			cpw.setTestUnitSKIPCount(new SimpleIntegerProperty(0));
			cpw.setTestUnitKTFCount(new SimpleIntegerProperty(0));

			// Add clientProgressObject to map with key as a clientName
			clientProgressMap.put(client, cpw);
		}

	}

	/**
	 * Landing page will prepare and display data relating to very first client in the testSuite list
	 */
	private void prepareLandingPage() {
		// When Application is launched for the first time, display items related to first client
		String firstClient = FWStaticStore.testSuitesMap.keySet().iterator().next();
		userSelection(firstClient);
	}

	/**
	 * method unbinds previously selected client properties from pie chart and bind current client properties, so pie chart shows selected client
	 * specific information
	 * 
	 * @param cpw = Wrapper class which holds client specific properties
	 */
	private void setPieChartProperties(ClientProgressWrapper cpw) {

		// Bind Integer property to Test Case Pie Chart Data object
		{
			cpw.getPieChartData_TestCase().forEach(data -> {
				if (null != data.pieValueProperty()) {
					data.pieValueProperty().unbind();
				}
			});

			cpw.getPieChartData_TestCase().get(0).pieValueProperty().bind(cpw.getTestCasePASSCount());
			cpw.getPieChartData_TestCase().get(1).pieValueProperty().bind(cpw.getTestCaseFAILCount());
			cpw.getPieChartData_TestCase().get(2).pieValueProperty().bind(cpw.getTestCaseSKIPCount());
			cpw.getPieChartData_TestCase().get(3).pieValueProperty().bind(cpw.getTestCaseKTFCount());

			// Modify pieChart Data to add actual number
			cpw.getPieChartData_TestCase().get(0).nameProperty().bind(Bindings.concat("PASS", "-", cpw.getTestCasePASSCount()));
			cpw.getPieChartData_TestCase().get(1).nameProperty().bind(Bindings.concat("FAIL", "-", cpw.getTestCaseFAILCount()));
			cpw.getPieChartData_TestCase().get(2).nameProperty().bind(Bindings.concat("SKIP", "-", cpw.getTestCaseSKIPCount()));
			cpw.getPieChartData_TestCase().get(3).nameProperty().bind(Bindings.concat("KTF", "-", cpw.getTestCaseKTFCount()));

			// Add data object to pie chart
			Pie_chart_1.setData(cpw.getPieChartData_TestCase());
		}

		// Bind Integer property to Test Unit Pie Chart Data object
		{
			cpw.getPieChartData_UnitCase().forEach(data -> {
				if (null != data.pieValueProperty()) {
					data.pieValueProperty().unbind();
				}
			});

			cpw.getPieChartData_UnitCase().get(0).pieValueProperty().bind(cpw.getTestUnitPASSCount());
			cpw.getPieChartData_UnitCase().get(1).pieValueProperty().bind(cpw.getTestUnitFAILCount());
			cpw.getPieChartData_UnitCase().get(2).pieValueProperty().bind(cpw.getTestUnitSKIPCount());
			cpw.getPieChartData_UnitCase().get(3).pieValueProperty().bind(cpw.getTestUnitKTFCount());

			// Modify pieChart Data to add actual number
			cpw.getPieChartData_UnitCase().get(0).nameProperty().bind(Bindings.concat("PASS", "-", cpw.getTestUnitPASSCount()));
			cpw.getPieChartData_UnitCase().get(1).nameProperty().bind(Bindings.concat("FAIL", "-", cpw.getTestUnitFAILCount()));
			cpw.getPieChartData_UnitCase().get(2).nameProperty().bind(Bindings.concat("SKIP", "-", cpw.getTestUnitSKIPCount()));
			cpw.getPieChartData_UnitCase().get(3).nameProperty().bind(Bindings.concat("KTF", "-", cpw.getTestUnitKTFCount()));

			// Add data object to pie chart
			Pie_chart_2.setData(cpw.getPieChartData_UnitCase());
		}

	}

	private void configureCommonPieChartPropertis() {

		// // Setting the title of the Pie chart
		// Pie_chart.setTitle("Test Status");

		// setting the direction to arrange the data
		Pie_chart_1.setClockwise(true);

		// // Setting the length of the label line
		Pie_chart_1.setLabelLineLength(5);

		// Setting the labels of the pie chart visible
		Pie_chart_1.setLabelsVisible(true);

		// Setting the start angle of the pie chart
		Pie_chart_1.setStartAngle(180);

		Pie_chart_1.setLegendVisible(true);
		Pie_chart_1.setLegendSide(Side.BOTTOM);
		// Pie_chart_1.forEach(data -> data.nameProperty().bind(Bindings.concat(data.getName(), " ", data.pieValueProperty(), " Tons")));

		// // Setting the title of the Pie chart
		// Pie_chart.setTitle("Test Status");

		// setting the direction to arrange the data
		Pie_chart_2.setClockwise(true);

		// // Setting the length of the label line
		Pie_chart_2.setLabelLineLength(5);

		// Setting the labels of the pie chart visible
		Pie_chart_2.setLabelsVisible(true);

		// Setting the start angle of the pie chart
		Pie_chart_2.setStartAngle(180);

		// Pie_chart_2.setPrefSize(30, 30);
		Pie_chart_2.setLegendVisible(true);
		Pie_chart_2.setLegendSide(Side.BOTTOM);

	}

	/**
	 * Each TestCaseName (Client) has their own task thread and value properties, If user selects different client from drop down then PrgressBar and
	 * Pie charts should be refreshed with properties related to selected client. This can be achieved by un-binding old property and attach new
	 * property.
	 */
	@Override
	public void userSelection(String client) {

		System.out.println("User Selected Client : " + client);
		ClientProgressWrapper cpw = clientProgressMap.get(client);

		// printThreadSet();
		if (null != clientTask) {
			clientTask.terminate();
		}
		if (null != clientTaskThread && clientTaskThread.isAlive()) {
			clientTaskThread.interrupt();
		}
		// printThreadSet();

		// Create TestProgress Task per Client and run in its own thread
		clientTask = new TestProgress(client, clientProgressMap);
		clientTaskThread = new Thread(clientTask, client);
		clientTaskThread.setDaemon(true);

		// Clear previously set client objects and set current client objects
		progress_tableview.getItems().clear();
		progress_tableview.setItems(cpw.getObservableProgressTableList());

		// Un-bind old client and bind selected client
		Progress_Indicator.progressProperty().unbind();
		Progress_Indicator.progressProperty().bind(clientTask.progressProperty());

		// Un-bind old client and bind selected client properties
		setPieChartProperties(cpw);

		// Give some time before processing thread launch.
		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		clientTaskThread.start();

	}

	public void printThreadSet() {
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		for (Thread t : threadSet) {
			System.out.println("Thread :" + t + ":" + "state:" + t.getState());
		}
	}

	@Override
	public void updateFindings(long MaxValue, long currentProgressCount) {

	}

}

/**
 * Task<Void> is JavaFx way to concurrently update progress indicator. Each Task should be run in its separate thread. In this particular example,
 * Task will read through its particular client DataBase and find out the current status of all test cases. Based on the status, it will update
 * progress on ProgressIndicator by using method updateProgress(long workDone, long max).
 *
 */
class TestProgress extends Task<Void> {

	Map<String, LinkedHashMap<String, TestCaseTracker>> testSuitesMap = FWStaticStore.testSuitesMap;
	// this is the client name for the thread
	volatile String clientName = null;
	private volatile boolean running = true;
	Map<String, ClientProgressWrapper> clientProgressMap;

	public void terminate() {
		running = false;
	}

	public TestProgress(String clientName, Map<String, ClientProgressWrapper> clientProgressMap) {
		this.clientName = clientName;
		this.clientProgressMap = clientProgressMap;
	}

	@Override
	protected Void call() throws Exception {
		HashMap<String, TestCaseTracker> clientTestDataBase = testSuitesMap.get(clientName);
		ClientProgressWrapper cpw = clientProgressMap.get(clientName);
		ObservableList<ProgressTable> observableTestList = cpw.getObservableProgressTableList();

		while (running) {

			observableTestList.clear();

			// RunLater is important as we are updating GUI from different thread
			Platform.runLater(() -> {

				int testCaseUnknownCount = 0;
				int testCasePassCount = 0;
				int testCaseFailCount = 0;
				int testCaseSkipCount = 0;
				int testCaseKTFCount = 0;

				int testUnitPassCount = 0;
				int testUnitFailCount = 0;
				int testUnitSkipCount = 0;
				int testUnitKTFCount = 0;

				if (null != clientName) {

					int progress = 0;

					for (Map.Entry<String, TestCaseTracker> entry : clientTestDataBase.entrySet()) {
						TestCaseTracker testCase = entry.getValue();
						String testCaseStatus = testCase.getTestcaseStatus();

						ProgressTable pt = new ProgressTable();
						pt.setTestcaseFQCN(entry.getKey());
						pt.setTestcaseEventCount(Integer.toString(testCase.getTestCaseStatusEvent().size()));
						pt.setTestcaseStatus(testCaseStatus);
						pt.setTestcaseUnitCount(Integer.toString(testCase.getTestUnitStatusEvent().size()));
						// pt.setTestUnitStatus();
						observableTestList.add(pt);

						if (testCaseStatus.equals("PASS")) {
							progress++;
							testCasePassCount++;
						} else if (testCaseStatus.equals("FAIL")) {
							progress++;
							testCaseFailCount++;
						} else if (testCaseStatus.equals("SKIP")) {
							progress++;
							testCaseSkipCount++;
						} else if (testCaseStatus.equals("KTF")) {
							progress++;
							testCaseKTFCount++;
						} else {
							testCaseUnknownCount++;
						}

						if (!testCase.getTestUnitStatusEvent().isEmpty()) {
							for (String unitEvent : testCase.getTestUnitStatusEvent()) {
								if (unitEvent.startsWith("|--PASS")) {
									testUnitPassCount++;
								} else if (unitEvent.startsWith("|--FAIL")) {
									testUnitFailCount++;
								} else if (unitEvent.startsWith("|--SKIP")) {
									testUnitSkipCount++;
								} else if (unitEvent.startsWith("|--KTF")) {
									testUnitKTFCount++;
								} else {
									System.err.println(unitEvent);
								}
							}
						}
					}
					updateProgress(progress, clientTestDataBase.size());
					System.out.println(clientName + "->" + testCasePassCount + " " + testCaseFailCount + " " + testCaseSkipCount + " "
							+ testCaseKTFCount + " " + testCaseUnknownCount + "->" + testUnitPassCount + " " + testUnitFailCount + " "
							+ testUnitSkipCount + " " + testUnitKTFCount);

					cpw.getTestCasePASSCount().set(testCasePassCount);
					cpw.getTestCaseFAILCount().set(testCaseFailCount);
					cpw.getTestCaseSKIPCount().set(testCaseSkipCount);
					cpw.getTestCaseKTFCount().set(testCaseKTFCount);

					cpw.getTestUnitPASSCount().set(testUnitPassCount);
					cpw.getTestUnitFAILCount().set(testUnitFailCount);
					cpw.getTestUnitSKIPCount().set(testUnitSkipCount);
					cpw.getTestUnitKTFCount().set(testUnitKTFCount);

				}
			});
			Thread.sleep(500);

		}

		System.out.println("Client " + clientName + " Thread Terminated");
		return null;
	}
}