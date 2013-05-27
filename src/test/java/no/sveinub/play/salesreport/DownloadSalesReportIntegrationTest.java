package no.sveinub.play.salesreport;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import no.sveinub.play.download.ReportDownloader;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author georgilambov
 * 
 */
public class DownloadSalesReportIntegrationTest {

	private static final Properties prop = new Properties();

	private ReportDownloader reportDownload;

	@BeforeClass
	public static void init() throws IOException {
		InputStream in = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("play.properties");
		prop.load(in);
		in.close();
	}

	@Before
	public void setUp() {

	}

	@Test
	public void propertiesFileLoaded() {
		Assert.assertNotNull(prop);
		Assert.assertNotNull(prop.get("play.email"));
		Assert.assertNotNull(prop.get("play.password"));
	}

	@Test
	public void loginToPlayAndGetDevAccInfo() {

	}

}
