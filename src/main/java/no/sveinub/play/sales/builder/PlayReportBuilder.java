package no.sveinub.play.sales.builder;

import no.sveinub.play.download.Credentials;
import no.sveinub.play.sales.prepare.SalesReportContext;
import lombok.Getter;

/**
 * 
 * @author georgilambov
 * 
 */
public abstract class PlayReportBuilder {

	@Getter
	protected PlayReportEntity playReportEntity;

	/**
	 * Builds login object
	 */
	public abstract void buildPlayLogin();

	/**
	 * Creates security check
	 */
	public abstract void buildSecurityCheck();

	/**
	 * Retrieves developer account info
	 */
	public abstract void buildDeveloperAccount();

	/**
	 * Creates new report.
	 * 
	 * @param credentials
	 */
	public void createNewReport(Credentials credentials) {
		playReportEntity = new PlayReportEntity();
		playReportEntity.setCredentials(credentials);
		playReportEntity.setSalesReportContext(new SalesReportContext());
	}

}