package no.sveinub.play;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipInputStream;

import no.sveinub.play.download.GameStatsReportDownloader;
import no.sveinub.play.download.PlayCredentials;
import no.sveinub.play.download.ReportDownloaderException;
import no.sveinub.play.http.RawCookieBuilder;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
	private DefaultHttpClient httpclient;
	private HttpContext localContext;

	private GameStatsReportDownloader reportDownload;

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
		credentials.setCdsSecurityToken(prop
				.getProperty("play.cds_security_token"));

		reportDownload = new GameStatsReportDownloader(credentials);

		PoolingClientConnectionManager cxMgr = new PoolingClientConnectionManager(
				SchemeRegistryFactory.createDefault());
		cxMgr.setMaxTotal(3);
		cxMgr.setDefaultMaxPerRoute(20);

		httpclient = new DefaultHttpClient();
		httpclient.setRedirectStrategy(new RedirectStrategy() {
			@Override
			public boolean isRedirected(HttpRequest httpRequest,
					HttpResponse httpResponse, HttpContext httpContext)
					throws ProtocolException {
				return false;
			}

			@Override
			public HttpUriRequest getRedirect(HttpRequest httpRequest,
					HttpResponse httpResponse, HttpContext httpContext)
					throws ProtocolException {
				return null;
			}
		});
		httpclient.getParams().setParameter(
				CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		httpclient.getParams().setParameter(
				CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");
		httpclient
				.getParams()
				.setParameter(
						CoreProtocolPNames.USER_AGENT,
						"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.7; rv:20.0) Gecko/20100101 Firefox/20.0");

		localContext = new BasicHttpContext();
	}

	@Test
	public void propertiesFileLoaded() {
		Assert.assertNotNull(prop);
		Assert.assertNotNull(prop.get("play.email"));
		Assert.assertNotNull(prop.get("play.password"));
	}

	@Test
	public void loginViaReportDownloader() throws ReportDownloaderException {
		reportDownload.login();
	}

	@Test
	public void retrieveEstimatedSalesReport() throws URISyntaxException,
			ClientProtocolException, IOException {
		// https://accounts.google.com/ServiceLogin?service=androiddeveloper&passive=true&nui=1&continue=https://play.google.com/apps/publish&followup=https://play.google.com/apps/publish
		URIBuilder builder = new URIBuilder();
		builder.setScheme("https")
				.setHost("accounts.google.com")
				.setPath("/ServiceLogin")
				.setQuery(
						"?service=androiddeveloper&passive=true&nui=1&continue=https://play.google.com/apps/publish&followup=https://play.google.com/apps/publish");
		URI uri = builder.build();

		HttpGet httpget = new HttpGet(uri);
		HttpResponse response = httpclient.execute(httpget, localContext);
		Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine()
				.getStatusCode());

		RawCookieBuilder loginCookieBuilder = new RawCookieBuilder();
		BasicHeaderElementIterator its = new BasicHeaderElementIterator(
				response.headerIterator("Set-Cookie"));
		String gapsFirst = null;
		while (its.hasNext()) {
			HeaderElement elem = its.nextElement();
			if (elem.getName().equals("GoogleAccountsLocale_session")
					|| elem.getName().equals("GAPS")
					|| elem.getName().equals("GALX")) {
				loginCookieBuilder
						.addParameter(elem.getName(), elem.getValue());
			}

			if (elem.getName().equals("GAPS")) {
				gapsFirst = elem.getValue();
			}
		}
		Assert.assertNotNull(gapsFirst);

		loginCookieBuilder.addParameter("GoogleAccountsLocale_session", "bg");

		// parse url to get only /ServiceLoginAuth
		Document loginSite = Jsoup.parse(EntityUtils.toString(response
				.getEntity()));

		Element loginForm = loginSite.getElementById("gaia_loginform");
		Assert.assertNotNull(loginForm);
		EntityUtils.consume(response.getEntity());

		String loginFormAction = loginForm.attributes().get("action");
		Assert.assertEquals("https://accounts.google.com/ServiceLoginAuth",
				loginFormAction);

		builder.setScheme("https").setHost("accounts.google.com")
				.setPath("/ServiceLoginAuth");
		uri = builder.build();
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();

		Elements inputElements = loginForm.getElementsByTag("input");
		for (int i = 0; i < inputElements.size(); i++) {
			Element element = inputElements.get(i);
			String name = element.attr("name");
			String value = element.attr("value");

			if ("Passwd".equals(name)) {
				value = prop.getProperty("play.password");
			} else if ("Email".equals(name)) {
				value = prop.getProperty("play.email");
			} else if ("PersistentCookie".equals(name)) {
				value = "no";
			}

			formparams.add(new BasicNameValuePair(name, value));
		}

		UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(formparams,
				"UTF-8");

		HttpPost httpPost = new HttpPost(uri);
		httpPost.setHeader(HTTP.CONTENT_TYPE,
				"application/x-www-form-urlencoded");
		httpPost.setHeader("Cookie", loginCookieBuilder.toString());
		httpPost.setEntity(formEntity);

		response = httpclient.execute(httpPost, localContext);
		Assert.assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response
				.getStatusLine().getStatusCode());

		EntityUtils.consume(response.getEntity());

		// check cookie security to https://accounts.google.com/CheckCookie
		String checkCookieLocation = null;
		for (Header h : response.getAllHeaders()) {
			if (h.getName().equals("Location")) {
				checkCookieLocation = h.getValue()
						+ "&service=androiddeveloper";
			}
		}

		Assert.assertNotNull(checkCookieLocation);

		RawCookieBuilder checkCookieBuilder = new RawCookieBuilder();
		its = new BasicHeaderElementIterator(
				response.headerIterator("Set-Cookie"));
		String gapsSecond = null;
		while (its.hasNext()) {
			HeaderElement elem = its.nextElement();
			if (elem.getName().equals("GAPS")) {
				gapsSecond = elem.getValue();
			}

			if (elem.getName().equals("GAPS") || elem.getName().equals("RMME")
					|| elem.getName().equals("NID")
					|| elem.getName().equals("SID")
					|| elem.getName().equals("LSID")
					|| elem.getName().equals("HSID")
					|| elem.getName().equals("SSID")
					|| elem.getName().equals("APISID")
					|| elem.getName().equals("SAPISID")) {
				checkCookieBuilder
						.addParameter(elem.getName(), elem.getValue());
			}
		}

		Assert.assertTrue(!gapsFirst.equals(gapsSecond));

		for (NameValuePair param : loginCookieBuilder.getQueryParams()) {
			if (param.getName().equals("GALX")) {
				checkCookieBuilder.addParameter(param.getName(),
						param.getValue());
			}
		}

		httpget = new HttpGet(checkCookieLocation);
		// httpget.setHeader("Cookie", checkCookieBuilder.toString());
		response = httpclient.execute(httpget, localContext);
		Assert.assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response
				.getStatusLine().getStatusCode());

		EntityUtils.consume(response.getEntity());

		// call SetSID
		String accountsSetSIDLocation = null;
		for (Header h : response.getAllHeaders()) {
			if (h.getName().equals("Location")) {
				accountsSetSIDLocation = h.getValue();
			}
		}

		Assert.assertNotNull(accountsSetSIDLocation);
		httpget = new HttpGet(accountsSetSIDLocation);
		response = httpclient.execute(httpget, localContext);
		Assert.assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response
				.getStatusLine().getStatusCode());

		EntityUtils.consume(response.getEntity());

		String publishAuthLocation = null;
		for (Header h : response.getAllHeaders()) {
			if (h.getName().equals("Location")) {
				publishAuthLocation = h.getValue();
			}
		}
		Assert.assertNotNull(publishAuthLocation);

		// publish auth
		httpget = new HttpGet(publishAuthLocation);
		response = httpclient.execute(httpget, localContext);
		Assert.assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response
				.getStatusLine().getStatusCode());
		EntityUtils.consume(response.getEntity());

		String adCookie = null;
		its = new BasicHeaderElementIterator(
				response.headerIterator("Set-Cookie"));
		while (its.hasNext()) {
			HeaderElement elem = its.nextElement();
			if (elem.getName().equals("AD")) {
				adCookie = elem.getValue();
				Assert.assertNotNull(adCookie);
			}
		}

		// app/publish for dev_acc
		httpget = new HttpGet("https://play.google.com/apps/publish/");
		response = httpclient.execute(httpget, localContext);
		Assert.assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response
				.getStatusLine().getStatusCode());
		EntityUtils.consume(response.getEntity());

		String devAccLocation = null;
		for (Header h : response.getAllHeaders()) {
			if (h.getName().equals("Location")) {
				devAccLocation = h.getValue();
			}
		}

		Assert.assertNotNull(devAccLocation);
		List<NameValuePair> devAccValues = URLEncodedUtils.parse(
				URI.create(devAccLocation), "UTF-8");
		BigInteger devAcc = BigInteger.ZERO;
		for (NameValuePair p : devAccValues) {
			if (p.getName().equals("dev_acc")) {
				devAcc = new BigInteger(p.getValue());
			}
		}
		Assert.assertTrue(devAcc != BigInteger.ZERO);

		// download sales report from google cloud storage
		// https://storage.cloud.google.com/pubsite_prod_rev_10794437684402093811/sales/salesreport_201307.zip
		builder.setScheme("https")
				.setHost("storage.cloud.google.com")
				.setPath(
						"/pubsite_prod_rev_10794437684402093811/sales/salesreport_201307.zip");
		uri = builder.build();

		httpget = new HttpGet(uri);
		response = httpclient.execute(httpget, localContext);
		Assert.assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response
				.getStatusLine().getStatusCode());

		String cdsLoginLocation = null;
		for (Header h : response.getAllHeaders()) {
			if (h.getName().equals("Location")) {
				cdsLoginLocation = h.getValue();
			}
		}

		Assert.assertNotNull(cdsLoginLocation);
		Assert.assertTrue(cdsLoginLocation
				.startsWith("https://www.google.com/accounts/ServiceLogin?service=cds&passive"));
		EntityUtils.consume(response.getEntity());

		// cloud storage service
		httpget = new HttpGet(cdsLoginLocation);
		// httpget.setHeader(
		// "Cookie",
		// "GoogleAccountsLocale_session=bg; GAPS=1:3Vg9_9RXo_1bhMk9Jr2yKDNNt1D-dg:a7q5pw4oLhM7lOR3; GALX=uYrpBz3Zlps; RMME=false; NID=67=mvvhf5g7xafY6X_9YB1_Vccw1puY929lkxfClOs7BvL4Rn9xEvTgqwgKdZnvvJKNZyQCwP8nfTHX-ET-ye1q3BDFESMDFoxauGcyvHX3hco8XSbJKB5XCmcDJwamG0l_cnhNYPi2WVBAyjFVdg; SID=DQAAAAQBAAC0IzDwwXGTILwRExDJCN16bCsj6gZNqizTcZtCC1idDFKIy31c4cxqiazHTmEdpo6VMoTDuNwdVtAsZIKFIA6GAPiy7HAijoR5xX2ZwVp2BKxl2IWJlBhcEW6QZ4lwVYgmHVJWS_ttQO1zhXQZDwSuQl4mbKNkgM4_qJKJTh17lEC2KRUfWqyTxVOzxiJIybrgTMQvXxwMM7Si_jwmz7Fwep73ygf6rHccztaj1aqntogUFfWikRQz_3bx0haKDQyVYuWA4YnJfCeSLtw4LMpAh_OmUwzFFBevWUfKrKCuCEAK7hRbFfrFIKvyGEsbQPUDvqW1oBLUe_emiKtFnRqlYrZcLjeAeMCsf3WvfNPNeA; LSID=s.BG:DQAAAAcBAADc_Uw1dBMIh3W_w0OSq-ubE40HixS1uDSZJm770BMqJj-svBwhFcShTw1HQ0YX1vHxgsXp4o1DVb_zA4gw391rmH8ZR9MjzlRPIsFkJSg_Z6DHE4SKl5Jm98rZyumLuDlL6yr6lPKPxHJOBx-3jeIpq_NBm6V0SPiXkqsNMS_95eVovnovcWvUimlkljeqtinl2BVaZ70IxAQ9AnBoyBhIKVDAJZKErdqLmBcrYFegnNzq0A3ZgG86Ss21hiYJsmvltCwO9ZD52mUtwtvAAZKEHoeSvc6qDu424SfnOfLjEm37ZVeF2-ru415jg7f7l-CoFuOa-6dlBnnfNXTAkFvh34AKl1rAnLtRCXcf7SekfA; HSID=AuCBV44QF5H8XIgVx; SSID=AFTkAPVhqxkPvkonw; APISID=JuZ9GjqaLibPkGh3/A6BGwRmPLLzcgLm57; SAPISID=ctsHxMol2UoD7K2b/AJRGiSTG8rnVyU3hO");
		response = httpclient.execute(httpget, localContext);
		Assert.assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response
				.getStatusLine().getStatusCode());
		for (Header h : response.getAllHeaders()) {
			if (h.getName().equals("Location")) {
				cdsLoginLocation = h.getValue();
			}
		}

		Assert.assertNotNull(cdsLoginLocation);
		Assert.assertTrue(cdsLoginLocation
				.startsWith("https://accounts.google.com/ServiceLogin?service=cds&passive"));
		EntityUtils.consume(response.getEntity());

		httpget = new HttpGet(cdsLoginLocation);
		response = httpclient.execute(httpget, localContext);
		Assert.assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response
				.getStatusLine().getStatusCode());

		for (Header h : response.getAllHeaders()) {
			if (h.getName().equals("Location")) {
				cdsLoginLocation = h.getValue();
			}
		}
		EntityUtils.consume(response.getEntity());

		httpget = new HttpGet(cdsLoginLocation);
		response = httpclient.execute(httpget, localContext);
		for (Header h : response.getAllHeaders()) {
			if (h.getName().equals("Location")) {
				cdsLoginLocation = h.getValue();
			}
		}

		RawCookieBuilder cdsCookieBuilder = new RawCookieBuilder();
		its = new BasicHeaderElementIterator(
				response.headerIterator("Set-Cookie"));
		while (its.hasNext()) {
			HeaderElement elem = its.nextElement();
			if (elem.getName().equals("cds")) {
				cdsCookieBuilder.addParameter(elem.getName(), elem.getValue());
			}
		}

		Assert.assertNotNull(cdsCookieBuilder.getQueryParams().get(0));
		Assert.assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response
				.getStatusLine().getStatusCode());
		EntityUtils.consume(response.getEntity());

		// build direct download link
		httpget = new HttpGet(cdsLoginLocation);
		response = httpclient.execute(httpget, localContext);
		String reportDownloadLocation = null;
		for (Header h : response.getAllHeaders()) {
			if (h.getName().equals("Location")) {
				reportDownloadLocation = h.getValue();
			}
		}

		Assert.assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response
				.getStatusLine().getStatusCode());
		Assert.assertNotNull(reportDownloadLocation);
		EntityUtils.consume(response.getEntity());

		httpget = new HttpGet(reportDownloadLocation);
		response = httpclient.execute(httpget, localContext);
		RawCookieBuilder cdsAuthFull = new RawCookieBuilder();
		its = new BasicHeaderElementIterator(
				response.headerIterator("Set-Cookie"));
		while (its.hasNext()) {
			HeaderElement elem = its.nextElement();
			if (elem.getName().startsWith("AUTH_")) {
				cdsAuthFull.addParameter(elem.getName(), elem.getValue());
			}
		}

		String testLocation = null;
		for (Header h : response.getAllHeaders()) {
			if (h.getName().equals("Location")) {
				testLocation = h.getValue();
			}
		}

		Assert.assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response
				.getStatusLine().getStatusCode());
		EntityUtils.consume(response.getEntity());

		// dowload link after authentication
		httpget = new HttpGet(testLocation);
		response = httpclient.execute(httpget, localContext);
		for (Header h : response.getAllHeaders()) {
			if (h.getName().equals("Location")) {
				reportDownloadLocation = h.getValue();
			}
		}

		Assert.assertNotNull(reportDownloadLocation);
		Assert.assertTrue(reportDownloadLocation
				.contains("commondatastorage.googleapis.com"));
		EntityUtils.consume(response.getEntity());

		// actual download link
		httpget = new HttpGet(reportDownloadLocation);
		httpget.setHeader("Cookie", cdsAuthFull.toString());
		response = httpclient.execute(httpget, localContext);

		Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine()
				.getStatusCode());

		ZipInputStream zipInputStream = new ZipInputStream(response.getEntity()
				.getContent());
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try {
			zipInputStream.getNextEntry();
			IOUtils.copy(zipInputStream, byteArrayOutputStream);
		} finally {
			IOUtils.closeQuietly(zipInputStream);
		}

		String report = byteArrayOutputStream.toString("UTF-8");
		Assert.assertNotNull(report);
		Assert.assertTrue(report.length() > 0);

		EntityUtils.consume(response.getEntity());
	}

}
