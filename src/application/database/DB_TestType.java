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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DB_TestType {

	public final String tableName = "test_type";

	// Unique ID
	private int idTest_Type = -1;
	private String testType = "";
	private int disable = 0;
	private Timestamp timestamp;
	private String updatedBy = "";

	public DB_TestType(int idTest_Type, String testType, int disable, Timestamp timestamp, String updatedBy) {
		super();
		this.idTest_Type = idTest_Type;
		this.testType = testType;
		this.disable = disable;
		this.timestamp = timestamp;
		this.updatedBy = updatedBy;
	}

	public DB_TestType(ResultSet rs) throws SQLException {
		this.idTest_Type = rs.getInt("idTest_Type");
		this.testType = rs.getString("testType");
		this.disable = rs.getInt("disable");
		this.timestamp = rs.getTimestamp("timestamp");
		this.updatedBy = rs.getString("updatedBy");
	}

	public int getIdTest_Type() {
		return idTest_Type;
	}

	public void setIdTest_Type(int idTest_Type) {
		this.idTest_Type = idTest_Type;
	}

	public String getTestType() {
		return testType;
	}

	public void setTestType(String testType) {
		this.testType = testType;
	}

	public int getDisable() {
		return disable;
	}

	public void setDisable(int disable) {
		this.disable = disable;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getTableName() {
		return tableName;
	}


}
