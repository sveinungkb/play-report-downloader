package no.sveinub.play.sales.prepare;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.zip.ZipInputStream;

import lombok.Setter;
import no.sveinub.play.bean.PlayReportRequestBean;
import no.sveinub.play.domain.DeveloperAccount;
import no.sveinub.play.download.PlayCredentials;
import no.sveinub.play.download.ReportDownloaderException;
import no.sveinub.play.http.HeaderParser;
import no.sveinub.play.http.RawCookieBuilder;

import org.apache.commons.io.IOUtils;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.util.EntityUtils;

/**
 * 
 * @author georgilambov
 * 
 */
public class GooglePlayEstimatedSalesReport extends ReportConnector<String>
		implements PrepareSalesReport<String> {

	@Setter
	private PlayCredentials credentials;
	@Setter
	private DeveloperAccount developerAccount;
	@Setter
	private PlayReportRequestBean requestBean;
	@Setter
	private HeaderParser headerParser = new HeaderParser();

	/*
	 * (non-Javadoc)
	 * 
	 * @see no.sveinub.play.sales.prepare.PrepareSalesReport#execute()
	 */
	public String execute() throws URISyntaxException, ClientProtocolException,
			IOException, ReportDownloaderException {
		if (developerAccount == null && requestBean == null) {
			throw new IllegalArgumentException(
					"At least one instance of credentials or developer account must be provided");
		}

		if (requestBean.getReportDate() == null) {
			throw new IllegalArgumentException("reportDate is required");
		}

		// backwards compatibility with hardcoded devNumber
		String devAcc = (developerAccount == null) ? requestBean
				.getCredentials().getDevNumber() : developerAccount.getDevAcc();

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMM");
		String reportDate = simpleDateFormat
				.format(requestBean.getReportDate());

		URIBuilder builder = new URIBuilder();
		builder.setScheme("https")
				.setHost("storage.cloud.google.com")
				.setPath(
						"/pubsite_prod_rev_" + devAcc + "/sales/salesreport_"
								+ reportDate + ".zip");
		URI uri = builder.build();

		HttpGet httpget = new HttpGet(uri);
		HttpResponse response = httpclient.execute(httpget, localContext);

		String cdsLoginLocation = headerParser.location(response
				.getAllHeaders());
		EntityUtils.consume(response.getEntity());

		for (int i = 0; i < 3; i++) {
			httpget = new HttpGet(cdsLoginLocation);
			response = httpclient.execute(httpget, localContext);
			cdsLoginLocation = headerParser.location(response.getAllHeaders());
			EntityUtils.consume(response.getEntity());
		}

		// build direct download link
		httpget = new HttpGet(cdsLoginLocation);
		response = httpclient.execute(httpget, localContext);
		String reportDownloadLocation = headerParser.location(response
				.getAllHeaders());
		EntityUtils.consume(response.getEntity());

		httpget = new HttpGet(reportDownloadLocation);
		response = httpclient.execute(httpget, localContext);
		RawCookieBuilder cdsAuthFull = new RawCookieBuilder();
		BasicHeaderElementIterator its = new BasicHeaderElementIterator(
				response.headerIterator("Set-Cookie"));
		while (its.hasNext()) {
			HeaderElement elem = its.nextElement();
			if (elem.getName().startsWith("AUTH_")) {
				cdsAuthFull.addParameter(elem.getName(), elem.getValue());
			}
		}

		reportDownloadLocation = headerParser
				.location(response.getAllHeaders());
		EntityUtils.consume(response.getEntity());

		// dowload link after authentication
		httpget = new HttpGet(reportDownloadLocation);
		response = httpclient.execute(httpget, localContext);
		reportDownloadLocation = headerParser
				.location(response.getAllHeaders());
		EntityUtils.consume(response.getEntity());

		// actual download link
		httpget = new HttpGet(reportDownloadLocation);
		httpget.setHeader("Cookie", cdsAuthFull.toString());
		response = httpclient.execute(httpget, localContext);

		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			throw new ReportDownloaderException("Status code is "
					+ response.getStatusLine().getStatusCode());
		}

		ZipInputStream zipInputStream = new ZipInputStream(response.getEntity()
				.getContent());
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try {
			zipInputStream.getNextEntry();
			IOUtils.copy(zipInputStream, byteArrayOutputStream);
		} finally {
			IOUtils.closeQuietly(zipInputStream);
			IOUtils.closeQuietly(byteArrayOutputStream);
		}

		String report = byteArrayOutputStream.toString("UTF-8");
		EntityUtils.consume(response.getEntity());

		return report;
	}

}
