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

		final String url = String.format("jdbc:teiid:Portfolio@mms://%s:443", args[0]);
		final Connection connection = DriverManager.getConnection(url, JDV_USERNAME, JDV_PASSWORD);
		final Statement statement = connection.createStatement();
		ResultSet results = statement.executeQuery("select * from Account");

		System.out.println("Querying model: Account");
		while (results.next()) {
			System.out.println(results.getInt(1) + ", " +
					results.getString(2) + ", " +
					results.getString(3) + ", " +
					results.getString(4));
		}
		results.close();

		System.out.println("----------------------------------------");
		System.out.println("Querying model: StockPrices");
		results = statement.executeQuery("select * from StockPrices");
		while (results.next()) {
			System.out.println(results.getString(1) + ", " +
					results.getBigDecimal(2));
		}
		results.close();

		System.out.println("----------------------------------------");
		System.out.println("Querying model: Stock");
		results = statement.executeQuery("select * from Stock");
		while (results.next()) {
			System.out.println(results.getInt(1) + ", " +
					results.getString(2) + ", " +
					results.getBigDecimal(3) + ", " +
					results.getString(4));
		}
		results.close();

		System.out.println("----------------------------------------");
		System.out.println("Querying model: PersonalHoldings");
		results = statement.executeQuery("select * from PersonalHoldings");
		while (results.next()) {
			System.out.println(results.getInt(1) + ", " +
					results.getString(2) + ", " +
					results.getString(3) + ", " +
					results.getString(4) + ", " +
					results.getString(5) + ", " +
					results.getBigDecimal(6));
		}

		results.close();
		statement.close();
		connection.close();
	}
}
