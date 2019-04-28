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

import java.util.ArrayList;
import java.util.List;

import artos.dashboard.controllers.ProgressTable;

public class TestCaseTracker {

	List<String> TestCaseStatusEvent = new ArrayList<>();
	List<String> TestUnitStatusEvent = new ArrayList<>();
	ProgressTable progressTable;
	private String testcaseFQCN = "";
	private String testcaseStatus = "UNKNOWN";
	private String testUnitStatus = "UNKNOWN";
	private String userName;
	private String suiteName;
	private String passCount;
	private String failCount;
	private String skipCount;
	private String ktfCount;
	private String testImportance;
	private String testDuration;

	public String getTestcaseFQCN() {
		return testcaseFQCN;
	}

	public void setTestcaseFQCN(String testcaseFQCN) {
		this.testcaseFQCN = testcaseFQCN;
	}

	public String getTestcaseStatus() {
		return testcaseStatus;
	}

	public void setTestcaseStatus(String testcaseStatus) {
		this.testcaseStatus = testcaseStatus;
	}

	public String getTestUnitStatus() {
		return testUnitStatus;
	}

	public void setTestUnitStatus(String testUnitStatus) {
		this.testUnitStatus = testUnitStatus;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSuiteName() {
		return suiteName;
	}

	public void setSuiteName(String suiteName) {
		this.suiteName = suiteName;
	}

	public String getPassCount() {
		return passCount;
	}

	public void setPassCount(String passCount) {
		this.passCount = passCount;
	}

	public String getFailCount() {
		return failCount;
	}

	public void setFailCount(String failCount) {
		this.failCount = failCount;
	}

	public String getSkipCount() {
		return skipCount;
	}

	public void setSkipCount(String skipCount) {
		this.skipCount = skipCount;
	}

	public String getKtfCount() {
		return ktfCount;
	}

	public void setKtfCount(String ktfCount) {
		this.ktfCount = ktfCount;
	}

	public String getTestImportance() {
		return testImportance;
	}

	public void setTestImportance(String testImportance) {
		this.testImportance = testImportance;
	}

	public String getTestDuration() {
		return testDuration;
	}

	public void setTestDuration(String testDuration) {
		this.testDuration = testDuration;
	}

	public List<String> getTestCaseStatusEvent() {
		return TestCaseStatusEvent;
	}

	public void setTestCaseStatusEvent(String strTestCaseStatusEvent) {
		this.TestCaseStatusEvent.add(strTestCaseStatusEvent);
		// Every new test case event , we must ensure that related unit events are cleared.
		TestUnitStatusEvent.clear();
	}

	public List<String> getTestUnitStatusEvent() {
		return TestUnitStatusEvent;
	}

	public void setTestUnitStatusEvent(String testUnitStatusEvent) {
		this.TestUnitStatusEvent.add(testUnitStatusEvent);
	}

	public ProgressTable getProgressTable() {
		return progressTable;
	}

	public void setProgressTable(ProgressTable progressTable) {
		this.progressTable = progressTable;
	}

}
