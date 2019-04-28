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

import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

public class ClientProgressWrapper {

	private String clientName;

	private ObservableList<PieChart.Data> pieChartData_TestCase;
	private ObservableList<PieChart.Data> pieChartData_UnitCase;
	private ObservableList<ProgressTable> observableProgressTableList = FXCollections.observableArrayList();

	private IntegerProperty testCasePASSCount;
	private IntegerProperty testCaseFAILCount;
	private IntegerProperty testCaseSKIPCount;
	private IntegerProperty testCaseKTFCount;
	
	private IntegerProperty testUnitPASSCount;
	private IntegerProperty testUnitFAILCount;
	private IntegerProperty testUnitSKIPCount;
	private IntegerProperty testUnitKTFCount;

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public IntegerProperty getTestCasePASSCount() {
		return testCasePASSCount;
	}

	public void setTestCasePASSCount(IntegerProperty testCasePASSCount) {
		this.testCasePASSCount = testCasePASSCount;
	}

	public IntegerProperty getTestCaseFAILCount() {
		return testCaseFAILCount;
	}

	public void setTestCaseFAILCount(IntegerProperty testCaseFAILCount) {
		this.testCaseFAILCount = testCaseFAILCount;
	}

	public IntegerProperty getTestCaseSKIPCount() {
		return testCaseSKIPCount;
	}

	public void setTestCaseSKIPCount(IntegerProperty testCaseSKIPCount) {
		this.testCaseSKIPCount = testCaseSKIPCount;
	}

	public IntegerProperty getTestCaseKTFCount() {
		return testCaseKTFCount;
	}

	public void setTestCaseKTFCount(IntegerProperty testCaseKTFCount) {
		this.testCaseKTFCount = testCaseKTFCount;
	}

	public IntegerProperty getTestUnitPASSCount() {
		return testUnitPASSCount;
	}

	public void setTestUnitPASSCount(IntegerProperty testUnitPASSCount) {
		this.testUnitPASSCount = testUnitPASSCount;
	}

	public IntegerProperty getTestUnitFAILCount() {
		return testUnitFAILCount;
	}

	public void setTestUnitFAILCount(IntegerProperty testUnitFAILCount) {
		this.testUnitFAILCount = testUnitFAILCount;
	}

	public IntegerProperty getTestUnitSKIPCount() {
		return testUnitSKIPCount;
	}

	public void setTestUnitSKIPCount(IntegerProperty testUnitSKIPCount) {
		this.testUnitSKIPCount = testUnitSKIPCount;
	}

	public IntegerProperty getTestUnitKTFCount() {
		return testUnitKTFCount;
	}

	public void setTestUnitKTFCount(IntegerProperty testUnitKTFCount) {
		this.testUnitKTFCount = testUnitKTFCount;
	}

	public ObservableList<PieChart.Data> getPieChartData_TestCase() {
		return pieChartData_TestCase;
	}

	public void setPieChartData_TestCase(ObservableList<PieChart.Data> pieChartData_TestCase) {
		this.pieChartData_TestCase = pieChartData_TestCase;
	}

	public ObservableList<PieChart.Data> getPieChartData_UnitCase() {
		return pieChartData_UnitCase;
	}

	public void setPieChartData_UnitCase(ObservableList<PieChart.Data> pieChartData_UnitCase) {
		this.pieChartData_UnitCase = pieChartData_UnitCase;
	}

	public ObservableList<ProgressTable> getObservableProgressTableList() {
		return observableProgressTableList;
	}

	public void setObservableProgressTableList(ObservableList<ProgressTable> observableProgressTableList) {
		this.observableProgressTableList = observableProgressTableList;
	}

}
