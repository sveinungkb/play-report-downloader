package no.sveinub.play.download;

import lombok.Data;

/*
 * 
 * Play Report Downloader
 * https://github.com/sveinungkb/play-report-downloader
 * 
 * Georgi Lambov, georgi.lambov@gmail.com
 * Copyright (2012) Sveinung Kval Bakken
 * sveinung.bakken@gmail.com
 * 
 * Use this code however you like, but please keep this notice if you modify the file.
 * If you want to contribute, add your name and email above and request a merge.
 * 
 */
@Data
public class Credentials {
	private String email;
	private String password;
	private String devNumber;
}
