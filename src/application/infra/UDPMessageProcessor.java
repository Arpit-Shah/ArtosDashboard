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

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import artos.dashboard.utils.Transform;

public class UDPMessageProcessor {

	Thread serverThread;

	/**
	 * Closes this datagram socket.
	 * 
	 * Any thread currently blocked in receive upon this socket will throw a SocketException.
	 * 
	 */
	public void stopProcessing() {
		try {
			serverThread.interrupt();
			System.out.println("Connection Closed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startProcessing() {
		final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);
		final Runnable task = new Runnable() {
			@Override
			public void run() {
				try {
					clientProcessingPool.submit(new UDPProcessorTask());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		serverThread = new Thread(task);
		serverThread.start();
	}

}

class UDPProcessorTask implements Runnable {
	Transform _transform = new Transform();
	UDP udpListener;
//	boolean testUnitReceivingMode = false;

	UDPProcessorTask() {
		this.udpListener = FWStaticStore.udpListener;
	}

	@Override
	public void run() {
		try {
			while (true) {
				byte[] buffer = null;
				boolean isTestUnit = false;
				if (null != (buffer = udpListener.getNextMsg())) {

					String bufferData = _transform.bytesToAscii(buffer);

					// Ensure message is a valid summary event
					if (!(bufferData.contains("PASS") || bufferData.contains("FAIL") || bufferData.contains("SKIP") || bufferData.contains("KTF"))) {
						if (!bufferData.contains("duration:")) {
							continue;
						}
					}

					// Breakdown of summary event
					byte[] testStatus = new byte[4];
					byte[] testName = new byte[100];
					byte[] PassCount = new byte[4];
					byte[] FailCount = new byte[4];
					byte[] SkipCount = new byte[4];
					byte[] KTFCount = new byte[4];
					byte[] TestImportance = new byte[10];
					byte[] testTime = new byte[21];
					byte[] jiraRef = new byte[20];

					// DashBoard Metadata
					byte[] userName = new byte[10];
					byte[] suiteName = new byte[10];
					byte[] FQCN = new byte[100];

					byte[] seperator1 = new byte[1];
					byte[] seperator2 = new byte[2];
					byte[] seperator3 = new byte[3];
					byte[] seperator5 = new byte[5];

					ByteBuffer bbuf = ByteBuffer.wrap(buffer);

					if (bufferData.contains("  |--")) {
						isTestUnit = true;

						// remove 5 bytes
						bbuf.get(seperator5);
						bbuf.get(testStatus);
						bbuf.get(seperator3);

						// less 5 bytes for units
						testName = new byte[100 - 5];
						bbuf.get(testName);
						bbuf.get(seperator3);
					} else {
						bbuf.get(testStatus);
						bbuf.get(seperator3);

						bbuf.get(testName);
						bbuf.get(seperator3);
					}
					// Remove ..... after test name and only get FQCN
					String processedTestName = _transform.bytesToAscii(testName).replaceAll("\\.\\.", "");
					processedTestName = processedTestName.endsWith(".") ? processedTestName.substring(0, processedTestName.length() - 1)
							: processedTestName;

					bbuf.get(PassCount);
					bbuf.get(seperator3);

					bbuf.get(FailCount);
					bbuf.get(seperator3);

					bbuf.get(SkipCount);
					bbuf.get(seperator3);

					bbuf.get(KTFCount);
					bbuf.get(seperator2);

					bbuf.get(TestImportance);
					bbuf.get(seperator2);

					bbuf.get(testTime);
					bbuf.get(seperator1);

					bbuf.get(jiraRef);
					bbuf.get(seperator1);

					bbuf.get(userName);
					bbuf.get(seperator1);

					bbuf.get(suiteName);
					bbuf.get(seperator1);

					bbuf.get(FQCN);

					// System.err.println("Event: " + bufferData);
					// System.err.println("userName: " + _transform.bytesToAscii(userName));
					// System.err.println("suiteName: " + _transform.bytesToAscii(suiteName));
					// System.err.println("testStatus: " + _transform.bytesToAscii(testStatus));
					// System.err.println("testName: " + _transform.bytesToAscii(testName));
					// System.err.println("testNameProcessed: " + processedTestName);
					// System.err.println("PassCount: " + _transform.bytesToAscii(PassCount));
					// System.err.println("FailCount: " + _transform.bytesToAscii(FailCount));
					// System.err.println("SkipCount: " + _transform.bytesToAscii(SkipCount));
					// System.err.println("KTFCount: " + _transform.bytesToAscii(KTFCount));
					// System.err.println("TestImportance: " + _transform.bytesToAscii(TestImportance));
					// System.err.println("testTime: " + _transform.bytesToAscii(testTime));

					// Find suite map related to the event
					HashMap<String, TestCaseTracker> suiteMap = FWStaticStore.testSuitesMap.get(_transform.bytesToAscii(suiteName).trim());
					// Find test tracker object for given test event
					TestCaseTracker testTrackerObj = suiteMap.get(_transform.bytesToAscii(FQCN).trim());

					// TestCase Found in DataBase
					if (null != testTrackerObj && !isTestUnit) {
//						testUnitReceivingMode = false;

						// Update test tracker object with latest information
						testTrackerObj.setTestCaseStatusEvent(bufferData.trim());
						testTrackerObj.setUserName(_transform.bytesToAscii(userName).trim());
						testTrackerObj.setSuiteName(_transform.bytesToAscii(suiteName).trim());
						testTrackerObj.setTestcaseStatus(_transform.bytesToAscii(testStatus).trim());
						testTrackerObj.setTestcaseFQCN(processedTestName.trim());
						testTrackerObj.setPassCount(_transform.bytesToAscii(PassCount).trim());
						testTrackerObj.setFailCount(_transform.bytesToAscii(FailCount).trim());
						testTrackerObj.setSkipCount(_transform.bytesToAscii(SkipCount).trim());
						testTrackerObj.setKtfCount(_transform.bytesToAscii(KTFCount).trim());
						testTrackerObj.setTestDuration(_transform.bytesToAscii(testTime).trim());

						// TestUnit Found in DataBase
					} else if (null != testTrackerObj && isTestUnit) {

//						if (!testUnitReceivingMode) {
//							testTrackerObj.getTestUnitStatusEvent().clear();
//						}
//						testUnitReceivingMode = true;

						// Update test tracker object with latest information
						testTrackerObj.setTestUnitStatusEvent(bufferData.trim());
						testTrackerObj.setUserName(_transform.bytesToAscii(userName).trim());
						testTrackerObj.setSuiteName(_transform.bytesToAscii(suiteName).trim());
						// testTrackerObj.setTestcaseStatus(_transform.bytesToAscii(testStatus).trim());
						testTrackerObj.setTestcaseFQCN(_transform.bytesToAscii(FQCN).trim());
						// testTrackerObj.setPassCount(_transform.bytesToAscii(PassCount).trim());
						// testTrackerObj.setFailCount(_transform.bytesToAscii(FailCount).trim());
						// testTrackerObj.setSkipCount(_transform.bytesToAscii(SkipCount).trim());
						// testTrackerObj.setKtfCount(_transform.bytesToAscii(KTFCount).trim());
						testTrackerObj.setTestDuration(_transform.bytesToAscii(testTime).trim());
					} else {
						System.err.println("Unknown Test Case :" + (null == testTrackerObj ? "" : _transform.bytesToAscii(buffer)));
					}
				}
				Thread.sleep(10);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Terminating thread");
	}

}