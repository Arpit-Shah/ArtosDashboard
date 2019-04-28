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

public class DB_Users {

	public final String tableName = "users";

	// Unique ID
	private int idUsers = -1;
	private String name = "";
	private String job_title = "";
	private String username = "";
	private String passwordHash = "";
	private int disable = 0;
	private Timestamp timestamp;
	private String updatedBy = "";

	public DB_Users(int idUsers, String name, String job_title, String username, String passwordHash, int disable, Timestamp timestamp,
			String updatedBy) {
		super();
		this.idUsers = idUsers;
		this.name = name;
		this.job_title = job_title;
		this.username = username;
		this.passwordHash = passwordHash;
		this.disable = disable;
		this.timestamp = timestamp;
		this.updatedBy = updatedBy;
	}

	public DB_Users(ResultSet rs) throws SQLException {
		this.idUsers = rs.getInt("idUsers");
		this.name = rs.getString("name");
		this.job_title = rs.getString("job_title");
		this.username = rs.getString("username");
		this.passwordHash = rs.getString("passwordHash");
		this.disable = rs.getInt("disable");
		this.timestamp = rs.getTimestamp("timestamp");
		this.updatedBy = rs.getString("updatedBy");
	}

	public int getIdUsers() {
		return idUsers;
	}

	public void setIdUsers(int idUsers) {
		this.idUsers = idUsers;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJob_title() {
		return job_title;
	}

	public void setJob_title(String job_title) {
		this.job_title = job_title;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
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

}
