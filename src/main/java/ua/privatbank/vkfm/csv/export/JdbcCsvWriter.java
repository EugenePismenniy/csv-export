package ua.privatbank.vkfm.csv.export;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.*;
import java.util.Properties;

import static org.apache.commons.lang3.exception.ExceptionUtils.wrapAndThrow;

/**
 * @author evgeniy.pismenny on 26.10.17 11:13.
 */
public class JdbcCsvWriter {

	private final String url;
	private final Properties connectionProperties;
	private final EscapeCharReplacer charReplacer;

	public JdbcCsvWriter(String url, Properties connectionProperties) {
		this.url = url;
		this.connectionProperties = connectionProperties;
		this.charReplacer = new EscapeCharReplacer(new char[]{'\0', '\f', '`'}, ' ');
	}

	public void writeCsv(String query, String fileName) {
		try(Connection connection = DriverManager.getConnection(url, connectionProperties)) {
			writeCsv(connection, query, fileName);
		} catch (SQLException e) {
			wrapAndThrow(e);
		}
	}

	public void writeCsv(Connection con, String query, String fileName) {
		try(Statement statement = con.createStatement()) {
			writeCsv(statement, query, fileName);
		} catch (SQLException e) {
			wrapAndThrow(e);
		}
	}

	public void writeCsv(Statement statement, String query, String fileName) {
		try(ResultSet resultSet = statement.executeQuery(query)) {
			resultSet.setFetchSize(50);
			writeCsv(resultSet, fileName);
		} catch (SQLException e) {
			wrapAndThrow(e);
		}
	}

	public void writeCsv(ResultSet resultSet, String fileName) {
		try(CSVPrinter print = CSVFormat.POSTGRESQL_CSV
				.withDelimiter('\f')
				.withRecordSeparator('\n')
				.withNullString("")
				.withTrim()
				.withQuoteMode(QuoteMode.MINIMAL)
				.withQuote('`')
				.withHeader(resultSet)
				.withEscape(null)
				.print(new OutputStreamWriter(new FileOutputStream(fileName), "utf8"))) {

			print(print, resultSet);

			print.flush();
		} catch (SQLException|IOException e) {
			wrapAndThrow(e);
		}
	}


	private void print(CSVPrinter csvPrinter, ResultSet resultSet) throws SQLException, IOException {

		final int columnCount = resultSet.getMetaData().getColumnCount();

		long row = 0;
		while (resultSet.next()) {

			row ++;

			for (int i = 1; i <= columnCount; i++) {

				Object object = resultSet.getObject(i);

				if (object instanceof String) {
					csvPrinter.print(charReplacer.replaceAll((String) object));
				} else {
					csvPrinter.print(object);
				}
			}
			csvPrinter.println();

			if (row % 1000 == 0) {
				System.out.println(row);
			}
		}


		System.out.printf("total rows = %s %n", row);
	}
}
