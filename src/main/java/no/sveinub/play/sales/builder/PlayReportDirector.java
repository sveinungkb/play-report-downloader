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

	public PlayReportEntity getReport() {
		return playReportBuilder.getPlayReportEntity();
	}

	public void constructReport(Credentials credentials) {
		playReportBuilder.createNewReport(credentials);
		playReportBuilder.buildPlayLogin();
		playReportBuilder.buildSecurityCheck();
		playReportBuilder.buildDeveloperAccount();

		salesReportContext = playReportBuilder.getPlayReportEntity()
				.getSalesReportContext();

		GooglePlayEstimatedSalesReport googlePlayEstimatedSalesReport = new GooglePlayEstimatedSalesReport();
		googlePlayEstimatedSalesReport.setCredentials(credentials);
		googlePlayEstimatedSalesReport.setDeveloperAccount(playReportBuilder
				.getPlayReportEntity().getDeveloperAccount());

		salesReportContext.setReportConnector(googlePlayEstimatedSalesReport);

	}

	/**
	 * Retrieves Google play report content.
	 * 
	 * @return
	 */
	public String getReportContent() {
		return salesReportContext.createStep(String.class);
	}

}
