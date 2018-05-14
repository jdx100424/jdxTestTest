package com.maoshen.component.dao.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class DBUtil {
	// 定义一个用于放置数据库连接的局部线程变量（使每个线程都拥有自己的连接）
	private static ThreadLocal<Connection> connContainer = new ThreadLocal<Connection>();

	// 获取连接
	public static Connection getConnection() {
		Connection conn = connContainer.get();
		try {
			if (conn == null) {
				SqlInfo sqlInfo = SqlInfo.getDefaultSqlInfo();
				Class.forName(sqlInfo.getDriver());
				conn = DriverManager.getConnection(sqlInfo.getUrl(), sqlInfo.getUsername(), sqlInfo.getPassword());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connContainer.set(conn);
		}
		return conn;
	}

	public static void closeConnection() {
		closeConnection(null);
	}
	// 关闭连接
	public static void closeConnection(PreparedStatement preparedStatement) {
		try {
			if(preparedStatement!=null) {
				preparedStatement.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		Connection conn = connContainer.get();
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connContainer.remove();
		}
	}
}
