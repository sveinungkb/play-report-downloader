package no.sveinub.play.sales.prepare;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import no.sveinub.play.bean.PlayReportRequestBean;
import no.sveinub.play.domain.DeveloperAccount;
import no.sveinub.play.domain.PlayLogin;
import no.sveinub.play.domain.SecurityCheck;
import no.sveinub.play.download.PlayCredentials;

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
	private PlayCredentials credentials;
	private PlayReportRequestBean requestBean;

	@BeforeClass
	public static void init() throws IOException {
		InputStream in = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("play.properties");
		prop.load(in);
		in.close();
	}

	@Before
	public void setUp() throws ParseException {
		salesReportContext = new SalesReportContext();

		credentials = new PlayCredentials();
		credentials.setEmail(prop.getProperty("play.email"));
		credentials.setPassword(prop.getProperty("play.password"));

		requestBean = new PlayReportRequestBean();
		requestBean.setCredentials(credentials);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM");
		Date d = simpleDateFormat.parse("2013_05");
		requestBean.setReportDate(d);
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
		googlePlayEstimatedSalesReport.setRequestBean(requestBean);
		googlePlayEstimatedSalesReport.setDeveloperAccount(developerAccount);
		salesReportContext.setReportConnector(googlePlayEstimatedSalesReport);

		String salesReportContent = salesReportContext.createStep(String.class);
		Assert.assertNotNull(salesReportContent);
	}

}
