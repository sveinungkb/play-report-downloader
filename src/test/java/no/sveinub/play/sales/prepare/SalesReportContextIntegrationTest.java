package no.sveinub.play.sales.prepare;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import no.sveinub.play.domain.DeveloperAccount;
import no.sveinub.play.domain.PlayLogin;
import no.sveinub.play.domain.SecurityCheck;
import no.sveinub.play.download.Credentials;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author georgilambov
 * 
 */
public class SalesReportContextIntegrationTest {

	private SalesReportContext salesReportContext;
	private static final Properties prop = new Properties();
	private Credentials credentials;

	@BeforeClass
	public static void init() throws IOException {
		InputStream in = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("play.properties");
		prop.load(in);
		in.close();
	}

	@Before
	public void setUp() {
		salesReportContext = new SalesReportContext();

		credentials = new Credentials();
		credentials.setEmail(prop.getProperty("play.email"));
		credentials.setPassword(prop.getProperty("play.password"));
	}

	@Test
	public void retrieveEstimatedSalesReportSequence() {
		GooglePlayLogin googlePlayLogin = new GooglePlayLogin();
		googlePlayLogin.setCredentials(credentials);

		salesReportContext.setReportConnector(googlePlayLogin);
		PlayLogin playLogin = salesReportContext.createStep(PlayLogin.class);

		GooglePlayOpen googlePlayOpen = new GooglePlayOpen();
		googlePlayOpen.setPlayLogin(playLogin);

		salesReportContext.setReportConnector(googlePlayOpen);
		SecurityCheck securityCheck = salesReportContext
				.createStep(SecurityCheck.class);

		GooglePlaySecurityCheck devAccOpen = new GooglePlaySecurityCheck();
		devAccOpen.setSecurityCheck(securityCheck);

		salesReportContext.setReportConnector(devAccOpen);
		DeveloperAccount developerAccount = salesReportContext
				.createStep(DeveloperAccount.class);
		Assert.assertNotNull(developerAccount.getDevAcc());

		GooglePlayEstimatedSalesReport googlePlayEstimatedSalesReport = new GooglePlayEstimatedSalesReport();
		googlePlayEstimatedSalesReport.setCredentials(credentials);
		googlePlayEstimatedSalesReport.setDeveloperAccount(developerAccount);
		salesReportContext.setReportConnector(googlePlayEstimatedSalesReport);

		String salesReportContent = salesReportContext.createStep(String.class);
		Assert.assertNotNull(salesReportContent);
	}

}
