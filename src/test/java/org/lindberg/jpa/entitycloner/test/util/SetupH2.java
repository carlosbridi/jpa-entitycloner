package org.lindberg.jpa.entitycloner.test.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SetupH2 {
	

	private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_CONNECTION = "jdbc:h2:mem:test";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

	public static void setupH2Database() {
		Connection connection = getDBConnection();
		PreparedStatement createPreparedStatement = null;

		try {
			connection.setAutoCommit(false);

			createPreparedStatement = connection
					.prepareStatement("CREATE TABLE PRODUTO (ID INT, CODIGO INT, DESCRICAO VARCHAR);");
			createPreparedStatement.executeUpdate();
			createPreparedStatement.close();
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static Connection getDBConnection() {
		Connection dbConnection = null;
		try {
			Class.forName(DB_DRIVER);
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
		try {
			dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
			return dbConnection;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return dbConnection;
	}
}
