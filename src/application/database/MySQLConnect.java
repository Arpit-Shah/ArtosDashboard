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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import application.infra.FWStaticStore;

public class MySQLConnect {
	// init database constants
	private static final String DATABASE_DRIVER = "com.mysql.cj.jdbc.Driver";
	private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/artos";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "admin";
	private static final String MAX_POOL = "250";

	// init connection object
	private Connection connection;
	// init properties object
	private Properties properties;

	// create properties
	private Properties getProperties() {
		if (properties == null) {
			properties = new Properties();
			properties.setProperty("user", USERNAME);
			properties.setProperty("password", PASSWORD);
			properties.setProperty("MaxPooledStatements", MAX_POOL);
		}
		return properties;
	}

	// connect database
	public Connection connect() {
		if (connection == null) {
			try {
				Class.forName(DATABASE_DRIVER);
				connection = DriverManager.getConnection(DATABASE_URL, getProperties());
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
		}
		return connection;
	}

	// disconnect database
	public void disconnect() {
		if (connection != null) {
			try {
				connection.close();
				connection = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		MySQLConnect mysqlConnect = new MySQLConnect();
		FWStaticStore.connection = mysqlConnect.connect();

		DB_Project_Utils.clearTable();
		DB_TestType_Utils.clearTable();
		DB_TestImportance_Utils.clearTable();
		DB_TestStatus_Utils.clearTable();
		DB_Users_Utils.clearTable();

		DB_Project_Utils.addProject("P1-Regression", "Product 1 Regression");
		DB_Project_Utils.addProject("P2-Regression", "Product 2 Regression");

		DB_TestType_Utils.addTestType("Regression");
		DB_TestType_Utils.addTestType("Functional");
		DB_TestType_Utils.addTestType("Manual");

		DB_TestImportance_Utils.addTestImportance("FATAL");
		DB_TestImportance_Utils.addTestImportance("CRITICAL");
		DB_TestImportance_Utils.addTestImportance("HIGH");
		DB_TestImportance_Utils.addTestImportance("MEDIUM");
		DB_TestImportance_Utils.addTestImportance("LOW");
		DB_TestImportance_Utils.addTestImportance("UNDEFINED");
		
		DB_TestStatus_Utils.addTestStatus("PASS");
		DB_TestStatus_Utils.addTestStatus("FAIL");
		DB_TestStatus_Utils.addTestStatus("SKIP");
		DB_TestStatus_Utils.addTestStatus("KTF");
		DB_TestStatus_Utils.addTestStatus("UNKNOWN");
		
		DB_Users_Utils.addUser("Arpit Shah", "Team Lead", "arpits", Utils_DB.calculateHash256("123456"));
		DB_Users_Utils.addUser("Shobhit Bhatnagar", "Feature Team Lead", "ShobhitB", Utils_DB.calculateHash256("234567"));
		DB_Users_Utils.addUser("Swapna Soni", "Test Engineer", "SwapnaS", Utils_DB.calculateHash256("345678"));

		mysqlConnect.disconnect();
	}
}