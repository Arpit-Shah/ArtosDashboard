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

public class DB_Project_Utils {

	public static String projectColumn = "`artos`.`project`";

	public static void addProject(String projectName, String projectDescription) {
		try {

			if (null == projectName || null == projectDescription) {
				return;
			}

			if (isProjectPresent(projectName)) {
				System.out.println("Project name: " + projectName + " already present");
				return;
			}

			Connection conn = FWStaticStore.connection;
			String query = " insert into " + projectColumn + " (projectName, description, disable, updatedBy)" + " values (?, ?, ?, ?)";

			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, projectName);
			preparedStmt.setString(2, projectDescription);
			preparedStmt.setInt(3, 0);
			preparedStmt.setString(4, FWStaticStore.systemProperties.getUserAccountName());

			// execute the prepared statement
			preparedStmt.execute();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<DB_Project> getProject(String projectName) {

		List<DB_Project> projectList = new ArrayList<>();

		try {
			if (null == projectName) {
				return projectList;
			}

			Connection conn = FWStaticStore.connection;
			String query = " select * from " + projectColumn + " where `projectName` Like '" + projectName + "'";
			System.out.println(query);
			// create the java statement
			Statement st = conn.createStatement();

			// execute the query, and get a java result set
			ResultSet rs = st.executeQuery(query);

			// iterate through the java result set
			while (rs.next()) {
				projectList.add(new DB_Project(rs));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return projectList;
	}

	public static boolean isProjectPresent(String projectName) {
		return (getProject(projectName).size() > 0 ? true : false);
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
