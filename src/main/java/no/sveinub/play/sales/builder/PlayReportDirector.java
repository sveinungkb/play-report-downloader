package no.sveinub.play.sales.builder;

import no.sveinub.play.download.Credentials;
import no.sveinub.play.sales.prepare.GooglePlayEstimatedSalesReport;
import no.sveinub.play.sales.prepare.SalesReportContext;
import lombok.Getter;
import lombok.Setter;

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

	@Getter
	@Setter
	private GooglePlayEstimatedSalesReport googlePlayEstimatedSalesReport = new GooglePlayEstimatedSalesReport();

	public PlayReportEntity getReport() {
		return playReportBuilder.getPlayReportEntity();
	}

	/**
	 * Constructs specific play report.
	 * 
	 * @param credentials
	 */
	public void constructReport(Credentials credentials) {
		playReportBuilder.createNewReport(credentials);
		playReportBuilder.buildPlayLogin();
		playReportBuilder.buildSecurityCheck();
		playReportBuilder.buildDeveloperAccount();
		playReportBuilder.buildPlayReportContent();
	}

}
