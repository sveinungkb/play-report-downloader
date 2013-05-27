package no.sveinub.play.download;

/*
 * 
 * Play Report Downloader
 * https://github.com/sveinungkb/play-report-downloader
 * 
 * Copyright (2012) Sveinung Kval Bakken
 * sveinung.bakken@gmail.com
 * 
 * Use this code however you like, but please keep this notice if you modify the file.
 * If you want to contribute, add your name and email above and request a merge.
 * 
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ReportDownloader {
	private static final String LOGIN_PAGE = "https://accounts.google.com/ServiceLogin?service=androiddeveloper&passive=true&nui=1&continue=https://play.google.com/apps/publish&followup=https://play.google.com/apps/publish";
	private static final String REPORT_BASE = "https://play.google.com/apps/publish/statistics/download?";

	private static final String REPORT_DATE_FORMAT = "yyyyMMdd";
	private static final String REPORT_DIMENSION = "country";
	private static final String REPORT_METRICS = "daily_device_installs,daily_device_uninstalls,daily_device_upgrades,active_user_installs,total_user_installs,daily_user_installs,daily_user_uninstalls";

	private final Logger logger = Logger.getLogger(ReportDownloader.class);

	private DefaultHttpClient client;
	private BasicHttpContext context;
	private Credentials credentials;

	public ReportDownloader(Credentials credentials) {
		this.credentials = credentials;
		client = new DefaultHttpClient();
		context = new BasicHttpContext();

		initHttpParams();
	}

	private void initHttpParams() {
		client.getParams()
				.setParameter("http.useragent",
						"Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:12.0) Gecko/20100101 Firefox/12.0");
		client.getParams().setParameter("http.protocol.handle-redirects", true);
		client.getParams().setParameter("http.protocol.max-redirects", 10);
	}

	public void login() throws ReportDownloaderException {
		try {
			Element loginForm = fetchLoginForm();
			String formUrl = loginForm.attributes().get("action");
			logger.debug("Found login form");

			loginByUsingForm(loginForm, formUrl);

			if (isLoggedIn()) {
				logger.debug("Logged in and got cookies:");
			} else {
				logger.debug("Login failed :(");
			}

		} catch (Exception e) {
			throw new ReportDownloaderException(e);
		}
	}

	private Element fetchLoginForm() throws HttpException, IOException,
			URISyntaxException {
		HttpResponse loginResponse = client.execute(getRequest(LOGIN_PAGE),
				context);
		Document loginSite = Jsoup.parse(readResponseBody(loginResponse));

		logger.debug("Got login site");

		Element loginForm = loginSite.getElementById("gaia_loginform");
		EntityUtils.consume(loginResponse.getEntity());
		return loginForm;
	}

	private void loginByUsingForm(Element loginForm, String formUrl)
			throws URISyntaxException, UnsupportedEncodingException,
			HttpException, IOException {
		HttpPost loginRequest = postRequest(formUrl);
		addFormValues(loginRequest, loginValuesFromForm(loginForm));
		logger.info("Logging in...");
		HttpResponse loginResponse = client.execute(loginRequest, context);
		EntityUtils.consume(loginResponse.getEntity());
	}

	public File downloadReportToDirectory(File reportsDir, String packageName,
			int days) throws ReportDownloaderException {
		try {
			if (!isLoggedIn())
				throw new ReportDownloaderException(
						"Not logged in, call login first.");
			createReportsDirectory(reportsDir);
			HttpResponse requestReport = requestReport(packageName, days);
			File reportFile = new File(reportsDir, packageName + "-"
					+ System.currentTimeMillis() + ".csv");
			downloadReport(reportFile, requestReport);
			EntityUtils.consume(requestReport.getEntity());
			return reportFile;
		} catch (Exception e) {
			throw new ReportDownloaderException(e);
		}
	}

	private void createReportsDirectory(File reportsDir) {
		if (reportsDir.mkdirs()) {
			logger.info("Created: " + reportsDir);
		}
	}

	private HttpResponse requestReport(String packageName, int days)
			throws URISyntaxException, HttpException, IOException {
		HttpUriRequest request = getRequest(buildStatisticsUri(packageName,
				days));
		logger.info("Will download report at: " + request.getURI().toString());
		return client.execute(request, context);
	}

	private String buildStatisticsUri(String packageName, int days) {
		StringBuilder uriBuilder = new StringBuilder(REPORT_BASE);
		uriBuilder.append("package=").append(packageName);
		appendDateRange(days, uriBuilder);
		uriBuilder.append("&dim=").append(REPORT_DIMENSION);
		uriBuilder.append("&met=").append(REPORT_METRICS);
		uriBuilder.append("&dev_acc=").append(credentials.devNumber);
		return uriBuilder.toString();
	}

	private void appendDateRange(int days, StringBuilder uriBuilder) {
		DateTime endDate = new DateTime().minusDays(1);
		DateTime startDate = endDate.minusDays(days);
		uriBuilder.append("&sd=")
				.append(startDate.toString(REPORT_DATE_FORMAT));
		uriBuilder.append("&ed=").append(endDate.toString(REPORT_DATE_FORMAT));
	}

	private void downloadReport(File reportFile, HttpResponse reportResponse)
			throws ReportDownloaderException, IOException {
		if (reportResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			BufferedWriter out = new BufferedWriter(new FileWriter(reportFile));
			out.write(EntityUtils.toString(reportResponse.getEntity()));
			out.close();
			logger.debug("Wrote " + reportFile.length() + " bytes to "
					+ reportFile);
		} else
			throw new ReportDownloaderException("Invalid response code: "
					+ reportResponse.getStatusLine().toString());
	}

	private HttpUriRequest getRequest(String uri) throws URISyntaxException {
		return new HttpGet(uri);
	}

	private HttpPost postRequest(String url) throws URISyntaxException {
		return new HttpPost(url);
	}

	private List<NameValuePair> loginValuesFromForm(Element loginForm) {
		List<NameValuePair> values = new ArrayList<NameValuePair>();

		Elements inputElements = loginForm.getElementsByTag("input");
		for (int i = 0; i < inputElements.size(); i++) {
			Element element = inputElements.get(i);
			String name = element.attr("name");
			String value = element.attr("value");
			if ("Passwd".equals(name)) {
				value = credentials.password;
			} else if ("Email".equals(name)) {
				value = credentials.email;
			} else if ("PersistentCookie".equals(name)) {
				value = "no";
			}
			values.add(new BasicNameValuePair(name, value));
		}
		return values;
	}

	private void addFormValues(HttpPost loginRequest,
			List<NameValuePair> formValues) throws UnsupportedEncodingException {
		UrlEncodedFormEntity entitity = new UrlEncodedFormEntity(formValues,
				HTTP.UTF_8);
		loginRequest.setEntity(entitity);

	}

	private boolean isLoggedIn() {
		for (Cookie cookie : client.getCookieStore().getCookies()) {
			if ("GAPS".equals(cookie.getName())) {
				return true;
			}
		}
		return false;
	}

	private String readResponseBody(HttpResponse loginResponse)
			throws ParseException, IOException {
		return EntityUtils.toString(loginResponse.getEntity());
	}
}
