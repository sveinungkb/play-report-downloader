package no.sveinub.play.download;

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

public class ReportDownloaderException extends Exception {

	private static final long serialVersionUID = -7851478431690106362L;

	public ReportDownloaderException(Throwable throwable) {
		super(throwable);
	}

	public ReportDownloaderException(String message) {
		super(message);
	}

}
