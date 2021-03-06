package com.redhat.xpaas.jdv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class App {

	private static final String JDV_USERNAME = "teiidUser";
	private static final String JDV_PASSWORD = "JBoss.123";

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.err.println("No arguments found. Need one argument - service URL");
			System.exit(1);
		}

		final String sql = "select * from mytable";
		final String url = String.format("jdbc:teiid:Test@mms://%s:443", args[0]);
		final Connection connection = DriverManager.getConnection(url, JDV_USERNAME, JDV_PASSWORD);
		final Statement statement = connection.createStatement();
		final ResultSet results = statement.executeQuery(sql);

		System.out.println("Query URL " + url);
		while (results.next()) {
			System.out.println(results.getInt(1) + " : " + results.getString(2));
		}

		results.close();
		statement.close();
		connection.close();
	}
}
