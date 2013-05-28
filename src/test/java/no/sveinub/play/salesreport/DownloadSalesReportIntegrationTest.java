package no.sveinub.play.salesreport;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import no.sveinub.play.download.Credentials;
import no.sveinub.play.download.ReportDownloader;
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
		Credentials credentials = new Credentials();
		credentials.email = prop.getProperty("play.email");
		credentials.password = prop.getProperty("play.password");

		reportDownload = new ReportDownloader(credentials);

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

		for (Header h : response.getAllHeaders()) {
			// System.out.println("headers " + h.getName() + ": " +
			// h.getValue());
		}

		// System.out.println("end checkpoit 1");

		RawCookieBuilder loginCookieBuilder = new RawCookieBuilder();
		BasicHeaderElementIterator its = new BasicHeaderElementIterator(
				response.headerIterator("Set-Cookie"));
		while (its.hasNext()) {
			HeaderElement elem = its.nextElement();
			if (elem.getName().equals("GoogleAccountsLocale_session")
					|| elem.getName().equals("GAPS")
					|| elem.getName().equals("GALX")) {
				loginCookieBuilder
						.addParameter(elem.getName(), elem.getValue());
			}

			if (elem.getName().equals("GAPS")) {
				System.out.println("GAPS first: " + elem.getValue());
			}

		}

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

		System.out.println("Post location follow: " + checkCookieLocation);
		Assert.assertNotNull(checkCookieLocation);

		RawCookieBuilder checkCookieBuilder = new RawCookieBuilder();
		its = new BasicHeaderElementIterator(
				response.headerIterator("Set-Cookie"));
		while (its.hasNext()) {
			HeaderElement elem = its.nextElement();
			if (elem.getName().equals("GAPS")) {
				System.out.println("GAPS second: " + elem.getValue());
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

		for (Header h : response.getAllHeaders()) {
			// System.out.println(h.getName() + ": " + h.getValue());

		}

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
		
		for (Header h : response.getAllHeaders()) {
			System.out.println(h.getName() + ": " + h.getValue());
		}

		/*
		 * 
		 * httpget = new HttpGet(checkCookieLocation);
		 * httpget.setHeader("Cookie", checkCookieBuilder.toString()); response
		 * = httpclient.execute(httpget, localContext);
		 * 
		 * Assert.assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response
		 * .getStatusLine().getStatusCode());
		 * 
		 * EntityUtils.consume(response.getEntity());
		 * 
		 * for (Header h : response.getAllHeaders()) { System.out.println(">>- "
		 * + h.getName() + ": " + h.getValue()); }
		 * 
		 * // retrieve dev_acc id
		 * builder.setScheme("https").setHost("play.google.com")
		 * .setPath("/apps/publish/"); uri = builder.build();
		 * 
		 * httpget = new HttpGet(uri); httpget.setHeader( "Cookie",
		 * "__utma=45884901.1560423851.1369658506.1369741661.1369744770.6; " +
		 * "__utmz=45884901.1369744770.6.6.utmcsr=accounts.google.com|utmccn=(referral)|utmcmd=referral|utmcct=/CheckCookie; "
		 * + "__utmb=45884901.8.10.1369744770; __utmc=45884901; " +
		 * "AD=DQAAAKwAAAB0R1fCI72K6hzi5z6YO8SYNvo8n5ddqkrgHoChd2gOLMzseGjVtr4FBvvR_n84cuyCsqjQaVz5Kr_88-nmftiQdx_SNhhO8xYMYSlmbk4vL_rMyPEmPD0IAJdKsfqVldv0Gi0zPzCoV0KO_wYSEEWA8vj9-hcnKtRRGp7Lu2a-1DquPx6t7jR2vtlk71f3LKqIKdnJaPSRM3Xf_MEy3oRvz6w19po8tqhOXi7x5JgPNQ; "
		 * + "__utma=45884901.1017694580.1369659331.1369659331.1369728191.2; " +
		 * "__utmz=45884901.1369659331.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); enabledapps.uploader=0; "
		 * +
		 * "NID=67=iCtshF9pUSaJDQQGIdzv3_DiaLC0JN79vIGBcFoJ2gBeSkvmrpmjkvK_wr813-uu9TRI2ubB8JoWn1ulp1ECOUf1pk_FuGZ3W9vOwzFbrbbGGGvGz7C8_LH1FjiO6seNaQdSTA; "
		 * +
		 * "PREF=ID=39ede62c342d4241:TM=1369745190:LM=1369745190:S=xt-GgJq4GiNSKsYJ; "
		 * +
		 * "SID=DQAAAKgAAABBIpkeD4TjKbXlcRdYK53a7K6nkMFhNL71G5pUzEE-CZBYeFOYaHVUPo08kjkRyFRfD3oAlyEBKIsgs131H_C-vkPu1h97hLjMeo-j45kd5NqCkAz5987RdfOt1yvwT8aDi6_QTDlDWteWUMNH3VPP2Dt57CFk11UAPR05vifh6oP3TfKIY3y3bXUquOPkY2Tgx9isNaljLJ5aOcHjVBpE4siSTNvFjpj5ROoklvHYcA; "
		 * + "HSID=AxKvzUusKtBGpvvxn; " + "SSID=APIya0kxGOI7bb-SH; " +
		 * "APISID=XYBnf9wsnSYjm1tQ/AZqZl8OcdYCCol-vA; " +
		 * "SAPISID=kPv6qVhLqOlp40Wi/AbZepeTNkrV-QF2q5"); response =
		 * httpclient.execute(httpget, localContext);
		 * 
		 * for (Header h : response.getAllHeaders()) { //
		 * System.out.println(h.getName() + ": " + h.getValue()); }
		 * 
		 * EntityUtils.consume(response.getEntity());
		 */
	}

	@Test
	public void movedTemporaryStatusForPlayPublishToken()
			throws ClientProtocolException, IOException, URISyntaxException {
		HttpGet g = new HttpGet(
				"https://play.google.com/apps/publish?auth=DQAAAIMAAABFi9fo7YkvaI9mhQLip2b4yahVCfgGpvoJjq_tyjSbeItGMy5wuMwPT27gjxF4jTVwOrbfda95XkpdVqYD2diZXH2ijukRlmPyeX3IDMJy-x9jwnTlMZ_LuX00nM3WYnPRIvlbirPLQudd_SJJ-qwS5I2YCj6Z0fVdKt7nzaWN3DNIamf-JoFh2ZnQSMD7CCw");
		HttpResponse response = httpclient.execute(g, localContext);

		Assert.assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response
				.getStatusLine().getStatusCode());

		for (Header h : response.getAllHeaders()) {
			System.out.println(h.getName() + ": " + h.getValue());
		}
	}

	@Test
	public void movedTemporaryStatusForLoginAuth() throws URISyntaxException,
			ClientProtocolException, IOException {
		URIBuilder builder = new URIBuilder();
		builder.setScheme("https").setHost("accounts.google.com")
				.setPath("/ServiceLoginAuth");
		URI uri = builder.build();

		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("Email", prop
				.getProperty("play.email")));
		formparams.add(new BasicNameValuePair("Passwd", prop
				.getProperty("play.password")));
		formparams.add(new BasicNameValuePair("PersistentCookie", "no"));

		UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(formparams,
				"UTF-8");

		DefaultHttpClient client = new DefaultHttpClient();
		client.setRedirectStrategy(new RedirectStrategy() {
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

		HttpPost httpPost = new HttpPost(uri);
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
		httpPost.setEntity(formEntity);

		HttpResponse response = client.execute(httpPost, localContext);

		for (Header h : response.getAllHeaders()) {
			System.out.println(h.getName() + ": " + h.getValue());
		}

		Assert.assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response
				.getStatusLine().getStatusCode());

	}

}
