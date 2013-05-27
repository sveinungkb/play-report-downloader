package no.sveinub.play.report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ReportReader {
	private static final String LINE_HEADER_PREFIX = "# App: ";
	private static final String LINE_REPORT_REGEXP = "^(\\d{8}),.*";
	private static final int NUMBER_OF_SUPPORTED_COLUMNS = 8;

	public static Report read(File report) throws ReportReaderException {
		ReportReader reader = new ReportReader(report);
		return reader.read();
	}

	private final File file;
	private final Report report = new Report();

	public ReportReader(File report) throws ReportReaderException {
		if(!report.exists()) {
			throw new ReportReaderException(report + " does not exists, can't read");
		}
		file = report;
	}

	private Report read() throws ReportReaderException {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			while((line = reader.readLine()) != null) {
				readLine(line);
			}
			reader.close();
		} catch (Exception e) {
			throw new ReportReaderException(e);
		}
		if(!report.hasValues()) throw new ReportReaderException("Report not read properly.");
		return report;
	}

	private void readLine(String line) throws ReportReaderException {
		if(line.startsWith(LINE_HEADER_PREFIX)) {
			readHeader(line);
		}
		else if(line.matches(LINE_REPORT_REGEXP)) {
			readReportLine(line);
		}
	}


	// # App: no.sveinub.autorecorder, date: 20120512-20120612
	private void readHeader(String header) throws ReportReaderException {
		if(report.hasHeader()) throw new ReportReaderException("Header values already read, won't do it again!");
		
		String[] components = header.split(",");
		if(components.length != 2) throw new ReportReaderException("Could not read header: " + header);
		
		readPackageName(components);
		readDates(components);
	}

	private void readPackageName(String[] components) {
		String packageName = components[0].substring(LINE_HEADER_PREFIX.length());
		report.packageName = packageName;
	}

	private void readDates(String[] components) throws ReportReaderException {
		String dateRange = components[1].substring(" date: ".length());
		String[] dates = dateRange.split("-");
		if(dates.length != 2) throw new ReportReaderException("Could not read header.");
		report.startDate = DateHelper.toDate(dates[0]);
		report.endDate = DateHelper.toDate(dates[1]);
	}

	/*
	 * 0 date						20120612
	 * 1 country					unknown
	 * 2 daily_device_installs		0
	 * 3 daily_device_uninstalls	0
	 * 4 daily_device_upgrades		0
	 * 5 active_user_installs		0
	 * 6 total_user_installs		663
	 * 7 daily_user_installs		0
	 * 8 daily_user_uninstalls		0
	 */
	private void readReportLine(String line) throws ReportReaderException {
		String[] components = line.split(",");
		if(components.length != NUMBER_OF_SUPPORTED_COLUMNS) {
			ReportLine reportLine = ReportLine.builder()
				.date(components[0])
				.country(components[1])
				.dailyDeviceInstalls(components[2])
				.dailyDeviceUninstalls(components[3])
				.dailyDeviceUpgrades(components[4])
				.activeUserInstalls(components[5])
				.totalUserInstalls(components[6])
				.dailyUserInstalls(components[7])
				.dailyUserUninstalls(components[7])
				.build();
			report.addLine(reportLine);
		}
		else throw new ReportReaderException("Could not read: " + line + " unknown number of columns.");
	}
}
