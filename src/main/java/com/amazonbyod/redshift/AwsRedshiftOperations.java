package com.amazonbyod.redshift;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.amazonbyod.awsprop.AWSProjectProperties;
import com.amazonbyod.listclass.StockData;
import com.amazonbyod.listclass.WeatherData;

public class AwsRedshiftOperations {

	static AWSProjectProperties awscredentials = new AWSProjectProperties();
	static final String dbURL = "jdbc:redshift://immersion-project.cziozxqpyojq.us-west-2.redshift.amazonaws.com:5439/immersion";
	static final String MasterUsername = "immersion";
	static final String MasterUserPassword = "Immersion!2016";

	public Connection redShiftConnect() {
		Connection connect = null;
		try {
			Class.forName("com.amazon.redshift.jdbc4.Driver");
			Properties props = new Properties();
			props.setProperty("user", MasterUsername);
			props.setProperty("password", MasterUserPassword);
			connect = DriverManager.getConnection(dbURL, props);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connect;

	}

	public int redShiftDisconnect(Connection connect) {
		int flag = 0;
		if (connect != null) {
			try {
				connect.close();
				flag = 1;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			flag = 0;
		}
		return flag;

	}
	
	public java.sql.Date convertJavaDateToSqlDate(java.util.Date date) {
	    return new java.sql.Date(date.getTime());
	}
	
	public java.sql.Timestamp convertJavaDateToSqlTimeStamp(java.util.Date date) {
		Date date1 = new Date();
	    return new java.sql.Timestamp(date1.getTime());
	}


	public void insertStockData(Connection conn, List<StockData> row) {
		String insertTableSQL = "INSERT INTO stock_datademo (stock_symbol,stock_data,stock_time ,stock_open ,stock_high,stock_low ,stock_close ,stock_vol ,stock_div ,stock_split ,stock_adjopen ,stock_adjhigh ,stock_adjlow ,stock_adjclose ,stock_adjvol )"
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(insertTableSQL);
			preparedStatement.setString(1, row.get(0).getCompanySymbol());
			preparedStatement.setDate(2, convertJavaDateToSqlDate(row.get(0).getStockDate()));
			preparedStatement.setTimestamp(3, convertJavaDateToSqlTimeStamp(row.get(0).getStockTime()));
			preparedStatement.setFloat(4, row.get(0).getStockopen());
			preparedStatement.setFloat(5, row.get(0).getStockhigh());
			preparedStatement.setFloat(6, row.get(0).getStockadjlow());
			preparedStatement.setFloat(7, row.get(0).getStockclose());
			preparedStatement.setInt(8, row.get(0).getStockvol());
			preparedStatement.setFloat(9, row.get(0).getStockdiv());
			preparedStatement.setInt(10, row.get(0).getStocksplit());
			preparedStatement.setFloat(11, row.get(0).getStockadjopen());
			preparedStatement.setFloat(12, row.get(0).getStockadjhigh());
			preparedStatement.setFloat(13, row.get(0).getStockadjlow());
			preparedStatement.setFloat(14, row.get(0).getStockadjclose());
			preparedStatement.setInt(15, row.get(0).getStockadjvol());
			
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void insertWeatherData(Connection conn, List<WeatherData> row) {
		String insertTableSQL = "INSERT INTO weather_storm_datademo (station_code,station_name,lat ,lng ,wdate,tmax ,tmin ,windspeed ,rainfall ,snowfall ,storm)"
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(insertTableSQL);
			preparedStatement.setString(1, row.get(0).getStationCode());
			preparedStatement.setString(2, row.get(0).getStationName());
			preparedStatement.setFloat(3, row.get(0).getLat());
			preparedStatement.setFloat(4, row.get(0).getLng());
			preparedStatement.setDate(5, convertJavaDateToSqlDate(row.get(0).getDate()));
			preparedStatement.setInt(6, row.get(0).getTmax());
			preparedStatement.setInt(7, row.get(0).getTmin());
			preparedStatement.setFloat(8, row.get(0).getWind());
			preparedStatement.setFloat(9, row.get(0).getRain());
			preparedStatement.setInt(10, row.get(0).getSnowfall());
			preparedStatement.setInt(11, row.get(0).getStorm());
			
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void truncateTable(Connection conn,String tableName){
		String SQLQuery="truncate "+tableName;
		
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(SQLQuery);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void loadDatafromS3(Connection conn,String tablename,String bucketStructure,String key){
		String loadSQL = "copy stock_data from 's3://amazon-immersion-project/WIKI-AAPL (copy)1.csv' "
				+ "credentials 'aws_access_key_id=AKIAJFETOLAADYA37PTQ;aws_secret_access_key=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' "
				+ "delimiter ',' region 'us-west-2'";
		
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(loadSQL);
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
