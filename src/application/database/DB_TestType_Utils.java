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
package application.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import application.infra.FWStaticStore;

public class DB_TestType_Utils {

	public static String projectColumn = "`artos`.`test_type`";

	public static void addTestType(String testType) {
		try {

			if (null == testType) {
				return;
			}

			if (isTestTypePresent(testType)) {
				System.out.println("Test Type: " + testType + " already present");
				return;
			}

			Connection conn = FWStaticStore.connection;
			String query = " insert into " + projectColumn + " (testType, disable, updatedBy)" + " values (?, ?, ?)";

			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, testType);
			preparedStmt.setInt(2, 0);
			preparedStmt.setString(3, FWStaticStore.systemProperties.getUserAccountName());

			// execute the prepared statement
			preparedStmt.execute();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<DB_TestType> getTestType(String testType) {

		List<DB_TestType> testTypeList = new ArrayList<>();

		try {
			if (null == testType) {
				return testTypeList;
			}

			Connection conn = FWStaticStore.connection;
			String query = " select * from " + projectColumn + " where `testType` Like '" + testType + "'";
			System.out.println(query);
			// create the java statement
			Statement st = conn.createStatement();

			// execute the query, and get a java result set
			ResultSet rs = st.executeQuery(query);

			// iterate through the java result set
			while (rs.next()) {
				testTypeList.add(new DB_TestType(rs));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return testTypeList;
	}

	public static boolean isTestTypePresent(String testType) {
		return (getTestType(testType).size() > 0 ? true : false);
	}

	public static void clearTable() {
		try {

			Connection conn = FWStaticStore.connection;
			String query = " truncate " + projectColumn;
			System.out.println(query);
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			// execute the prepared statement
			preparedStmt.execute();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
