package no.sveinub.play.sales.builder;

import lombok.Setter;
import no.sveinub.play.domain.DeveloperAccount;
import no.sveinub.play.domain.PlayLogin;
import no.sveinub.play.domain.SecurityCheck;
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

	@Setter
	private SalesReportContext salesReportContext;

	private SecurityCheck securityCheck;

	private PlayLogin playLogin;

	public EstimatedSalesReportBuilder() {
		salesReportContext = playReportEntity.getSalesReportContext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see no.sveinub.play.sales.builder.PlayReportBuilder#buildPlayLogin()
	 */
	@Override
	public void buildPlayLogin() {
		GooglePlayLogin googlePlayLogin = new GooglePlayLogin();
		googlePlayLogin.setCredentials(playReportEntity.getCredentials());

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
		GooglePlaySecurityCheck devAccOpen = new GooglePlaySecurityCheck();
		devAccOpen.setSecurityCheck(securityCheck);
		salesReportContext.setReportConnector(devAccOpen);
		DeveloperAccount developerAccount = salesReportContext
				.createStep(DeveloperAccount.class);

		playReportEntity.setDeveloperAccount(developerAccount);
	}

}
