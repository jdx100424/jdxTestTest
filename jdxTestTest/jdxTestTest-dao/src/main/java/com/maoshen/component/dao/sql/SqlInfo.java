package com.maoshen.component.dao.sql;

public class SqlInfo {
	private String driver;
	private String url;
	private String username;
	private String password;

	private static final SqlInfo DEFAULT_SQL_INFO = new SqlInfo();

	public static SqlInfo getDefaultSqlInfo() {
		return DEFAULT_SQL_INFO;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setInitProperties(String url, String username, String password, String driver) {
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.password = password;
	}
}
