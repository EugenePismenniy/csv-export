package ua.privatbank.vkfm.csv.export;

import java.util.Properties;

import static org.apache.commons.lang3.exception.ExceptionUtils.wrapAndThrow;

/**
 * @author evgeniy.pismenny on 10.11.17 11:13.
 */
public class Main {

	static {
		try {
			Class.forName("com.sybase.jdbc4.jdbc.SybDriver");
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			wrapAndThrow(e);
		}
	}


	public static void main(String[] args) throws Exception {

		Properties properties = new Properties();
		properties.setProperty("user", args[1]);
		properties.setProperty("password", args[2]);

		new JdbcCsvWriter(args[0], properties)
				.writeCsv(args[3], args[4] + ".csv");
	}

}
