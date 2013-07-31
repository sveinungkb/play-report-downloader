package no.sveinub.play.sales.builder;

import lombok.Getter;
import lombok.Setter;
import no.sveinub.play.bean.PlayReportRequestBean;
import no.sveinub.play.sales.prepare.SalesReportContext;

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
	public void createNewReport(PlayReportRequestBean requestBean) {
		playReportEntity = new PlayReportEntity();
		playReportEntity.setRequestBean(requestBean);
		playReportEntity.setSalesReportContext(new SalesReportContext());
	}

}
