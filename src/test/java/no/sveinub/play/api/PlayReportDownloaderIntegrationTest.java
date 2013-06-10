package no.sveinub.play.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import no.sveinub.play.bean.PlayReportRequestBean;
import no.sveinub.play.download.PlayCredentials;

import org.dozer.MappingException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author georgilambov
 * 
 */
public class PlayReportDownloaderIntegrationTest {

	private PlayReportDownloader playReportDownloader;
	private static final Properties prop = new Properties();

	@BeforeClass
	public static void init() throws IOException {
		InputStream in = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("play.properties");
		prop.load(in);
		in.close();
	}

	@Before
	public void setUp() {
		PlayCredentials credentials = new PlayCredentials();
		credentials.setEmail(prop.getProperty("play.email"));
		credentials.setPassword(prop.getProperty("play.password"));

		PlayReportRequestBean requestBean = new PlayReportRequestBean();
		requestBean.setCredentials(credentials);

		playReportDownloader = new PlayReportDownloader();
		playReportDownloader.setRequestBean(requestBean);
	}

	@Test
	public void retrieveEstimatedSalesReport() throws MappingException,
			IOException {
		Assert.assertNotNull(playReportDownloader
				.retrieveEstimatedSalesReport());
	}

}
