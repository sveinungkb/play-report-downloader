package no.sveinub.play.sales.builder;

import no.sveinub.play.download.PlayCredentials;
import no.sveinub.play.sales.prepare.SalesReportContext;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author georgilambov
 * 
 */
public abstract class PlayReportBuilder {

	@Getter
	@Setter
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
	 * Creates report content.
	 */
	public abstract void buildPlayReportContent();

	/**
	 * Creates new report.
	 * 
	 * @param credentials
	 */
	public void createNewReport(PlayCredentials credentials) {
		playReportEntity = new PlayReportEntity();
		playReportEntity.setCredentials(credentials);
		playReportEntity.setSalesReportContext(new SalesReportContext());
	}

}
