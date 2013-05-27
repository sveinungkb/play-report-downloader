package no.sveinub.play;

/*
 * 
 * Play Report Downloader
 * https://github.com/sveinungkb/play-report-downloader
 * 
 * Copyright (2012) Sveinung Kval Bakken
 * sveinung.bakken@gmail.com
 * 
 * Use this code however you like, but please keep this notice if you modify the file.
 * If you want to contribute, add your name and email above and request a merge.
 * 
 */

import java.io.File;

import no.sveinub.play.download.Credentials;
import no.sveinub.play.download.ReportDownloader;
import no.sveinub.play.report.Report;
import no.sveinub.play.report.ReportReader;

public class Main {
	private static final String USERNAME = "your.email@gmail.com";
	private static final String PASSWORD = "password";

	/*
	 * You'll find this one by logging in to your dev account:
	 * https://play.google.com/apps/publish/Home?dev_acc=092462466935067nnnnn
	 * <-- copy everything after dev_acc=
	 */
	private static final String DEV_ACCOUNT = "1771.....72194";
	private static final String PACKAGE = "com.your.package";
	private static final int N_DAYS = 100;

	public static void main(String[] args) throws Exception {
		ReportDownloader downloader = new ReportDownloader(getCredentials());
		downloader.login();
		File reportFile = downloader.downloadReportToDirectory(reportsDir(),
				PACKAGE, N_DAYS);
		Report report = ReportReader.read(reportFile);
		System.out.println("Read: " + report.getLines().size()
				+ " lines for application: " + report.packageName);
	}

	private static File reportsDir() {
		return new File("c:\\users\\username\\desktop\\reports");
	}

	private static Credentials getCredentials() {
		Credentials whois = new Credentials();
		whois.devNumber = DEV_ACCOUNT;
		whois.password = PASSWORD;
		whois.email = USERNAME;
		return whois;
	}
	
}
