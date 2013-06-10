package no.sveinub.play;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import no.sveinub.play.download.PlayCredentials;
import no.sveinub.play.download.GameStatsReportDownloader;
import no.sveinub.play.download.ReportDownloaderException;
import no.sveinub.play.http.RawCookieBuilder;

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
			}
		}
		Assert.assertNotNull(adCookie);

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

		// download sales report
		builder.setScheme("https").setHost("play.google.com")
				.setPath("/apps/publish/salesreport/download")
				.setParameter("report_date", "2013_05")
				.setParameter("report_type", "sales_report")
				.setParameter("dev_acc", devAcc.toString());
		uri = builder.build();

		httpget = new HttpGet(uri);
		response = httpclient.execute(httpget, localContext);
		Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine()
				.getStatusCode());

		String report = EntityUtils.toString(response.getEntity());
		Assert.assertNotNull(report);
		Assert.assertTrue(report.length() > 0);
		EntityUtils.consume(response.getEntity());
	}

}
