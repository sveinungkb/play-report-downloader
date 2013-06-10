package no.sveinub.play.sales.builder;

import lombok.Getter;
import lombok.Setter;
import no.sveinub.play.download.PlayCredentials;
import no.sveinub.play.sales.prepare.SalesReportContext;

/**
 * Report creator.
 * 
 * @author georgilambov
 * 
 */
public class PlayReportDirector {

	@Setter
	@Getter
	private SalesReportContext salesReportContext;

	@Getter
	@Setter
	private PlayReportBuilder playReportBuilder;

	public PlayReportEntity getReport() {
		return playReportBuilder.getPlayReportEntity();
	}

	/**
	 * Constructs specific play report.
	 * 
	 * @param credentials
	 */
	public void constructReport(PlayCredentials credentials) {
		playReportBuilder.createNewReport(credentials);
		playReportBuilder.buildPlayLogin();
		playReportBuilder.buildSecurityCheck();
		playReportBuilder.buildDeveloperAccount();
		playReportBuilder.buildPlayReportContent();
	}

}
