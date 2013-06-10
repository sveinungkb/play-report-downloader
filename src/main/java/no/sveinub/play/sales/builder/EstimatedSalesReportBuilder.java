package no.sveinub.play.sales.builder;

import lombok.Getter;
import no.sveinub.play.domain.DeveloperAccount;
import no.sveinub.play.domain.PlayLogin;
import no.sveinub.play.domain.SecurityCheck;
import no.sveinub.play.sales.prepare.GooglePlayEstimatedSalesReport;
import no.sveinub.play.sales.prepare.GooglePlayLogin;
import no.sveinub.play.sales.prepare.GooglePlayOpen;
import no.sveinub.play.sales.prepare.GooglePlaySecurityCheck;
import no.sveinub.play.sales.prepare.SalesReportContext;

/**
 * Builder for Estimad sales report.
 * 
 * @author georgilambov
 * 
 */
public class EstimatedSalesReportBuilder extends PlayReportBuilder {

	@Getter
	private SecurityCheck securityCheck;
	@Getter
	private PlayLogin playLogin;
	private DeveloperAccount developerAccount;

	/*
	 * (non-Javadoc)
	 * 
	 * @see no.sveinub.play.sales.builder.PlayReportBuilder#buildPlayLogin()
	 */
	@Override
	public void buildPlayLogin() {
		SalesReportContext salesReportContext = playReportEntity
				.getSalesReportContext();

		GooglePlayLogin googlePlayLogin = new GooglePlayLogin();
		googlePlayLogin.setCredentials(playReportEntity.getRequestBean()
				.getCredentials());

		salesReportContext.setReportConnector(googlePlayLogin);
		playLogin = salesReportContext.createStep(PlayLogin.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see no.sveinub.play.sales.builder.PlayReportBuilder#buildSecurityCheck()
	 */
	@Override
	public void buildSecurityCheck() {
		SalesReportContext salesReportContext = playReportEntity
				.getSalesReportContext();

		GooglePlayOpen googlePlayOpen = new GooglePlayOpen();
		googlePlayOpen.setPlayLogin(playLogin);
		salesReportContext.setReportConnector(googlePlayOpen);
		securityCheck = salesReportContext.createStep(SecurityCheck.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * no.sveinub.play.sales.builder.PlayReportBuilder#buildDeveloperAccount()
	 */
	@Override
	public void buildDeveloperAccount() {
		SalesReportContext salesReportContext = playReportEntity
				.getSalesReportContext();

		GooglePlaySecurityCheck devAccOpen = new GooglePlaySecurityCheck();
		devAccOpen.setSecurityCheck(securityCheck);
		salesReportContext.setReportConnector(devAccOpen);
		developerAccount = salesReportContext
				.createStep(DeveloperAccount.class);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * no.sveinub.play.sales.builder.PlayReportBuilder#buildPlayReportContent()
	 */
	@Override
	public void buildPlayReportContent() {
		GooglePlayEstimatedSalesReport googlePlayEstimatedSalesReport = new GooglePlayEstimatedSalesReport();
		googlePlayEstimatedSalesReport.setRequestBean(playReportEntity
				.getRequestBean());
		googlePlayEstimatedSalesReport.setDeveloperAccount(developerAccount);

		SalesReportContext salesReportContext = playReportEntity
				.getSalesReportContext();
		salesReportContext.setReportConnector(googlePlayEstimatedSalesReport);

		String content = salesReportContext.createStep(String.class);

		playReportEntity.setReportContent(content);

	}

}
