package no.sveinub.play.sales.prepare;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import lombok.Setter;
import no.sveinub.play.domain.DeveloperAccount;
import no.sveinub.play.domain.SecurityCheck;
import no.sveinub.play.download.ReportDownloaderException;
import no.sveinub.play.http.HeaderParser;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.util.EntityUtils;

/**
 * 
 * @author georgilambov
 * 
 */
public class GooglePlaySecurityCheck extends ReportConnector<DeveloperAccount>
		implements PrepareSalesReport<DeveloperAccount> {

	@Setter
	private SecurityCheck securityCheck;

	@Setter
	private HeaderParser headerParser = new HeaderParser();

	public DeveloperAccount execute() throws ClientProtocolException,
			IOException, ReportDownloaderException {
		if (securityCheck == null) {
			throw new IllegalArgumentException("invalid request parameter");
		}

		DeveloperAccount developerAccount = new DeveloperAccount();

		HttpGet httpget = new HttpGet(securityCheck.getSecuirtyCheck());
		HttpResponse response = httpclient.execute(httpget, localContext);
		confirmStausCode(response.getStatusLine().getStatusCode(),
				HttpStatus.SC_MOVED_TEMPORARILY);
		EntityUtils.consume(response.getEntity());

		String accountsSetSIDLocation = headerParser.location(response
				.getAllHeaders());

		httpget = new HttpGet(accountsSetSIDLocation);
		response = httpclient.execute(httpget, localContext);
		confirmStausCode(response.getStatusLine().getStatusCode(),
				HttpStatus.SC_MOVED_TEMPORARILY);
		EntityUtils.consume(response.getEntity());

		String publishAuthLocation = headerParser.location(response
				.getAllHeaders());

		httpget = new HttpGet(publishAuthLocation);
		response = httpclient.execute(httpget, localContext);
		confirmStausCode(response.getStatusLine().getStatusCode(),
				HttpStatus.SC_MOVED_TEMPORARILY);
		EntityUtils.consume(response.getEntity());

		httpget = new HttpGet("https://play.google.com/apps/publish/");
		response = httpclient.execute(httpget, localContext);
		confirmStausCode(response.getStatusLine().getStatusCode(),
				HttpStatus.SC_MOVED_TEMPORARILY);
		EntityUtils.consume(response.getEntity());
		
		String devAccLocation = headerParser.location(response.getAllHeaders());
		List<NameValuePair> devAccValues = URLEncodedUtils.parse(
				URI.create(devAccLocation), "UTF-8");
		String devAcc = null;
		for (NameValuePair p : devAccValues) {
			if (p.getName().equals("dev_acc")) {
				devAcc = p.getValue();
			}
		}

		developerAccount.setDevAcc(devAcc);

		return developerAccount;
	}

	/**
	 * 
	 * @param statusCode
	 * @param expectedStatusCode
	 * @throws ReportDownloaderException
	 */
	private void confirmStausCode(int statusCode, int expectedStatusCode)
			throws ReportDownloaderException {
		if (statusCode != expectedStatusCode) {
			throw new ReportDownloaderException("Status code is " + statusCode);
		}
	}

}
