package no.sveinub.play.sales.prepare;

import java.io.IOException;
import java.net.URI;

import lombok.Setter;
import no.sveinub.play.domain.PlayLogin;
import no.sveinub.play.domain.SecurityCheck;
import no.sveinub.play.download.ReportDownloaderException;
import no.sveinub.play.http.HeaderParser;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 * 
 * @author georgilambov
 * 
 */
public class GooglePlayOpen extends ReportConnector<SecurityCheck> implements
		PrepareSalesReport<SecurityCheck> {

	@Setter
	private PlayLogin playLogin;
	@Setter
	private HeaderParser headerParser = new HeaderParser();

	/**
	 * 
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws ReportDownloaderException
	 */
	public SecurityCheck execute() throws ClientProtocolException, IOException,
			ReportDownloaderException {
		if (playLogin == null) {
			throw new IllegalArgumentException(
					"Missing required data from playLogin collection");
		}

		SecurityCheck securityCheck = new SecurityCheck();

		HttpPost httpPost = new HttpPost(playLogin.getLoginAction());
		httpPost.setHeader(HTTP.CONTENT_TYPE,
				"application/x-www-form-urlencoded");
		httpPost.setHeader("Cookie", playLogin.getCookieBuilder().toString());
		httpPost.setEntity(playLogin.getFormEntity());

		HttpResponse response = httpclient.execute(httpPost, localContext);

		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_MOVED_TEMPORARILY) {
			throw new ReportDownloaderException("Status code is "
					+ response.getStatusLine().getStatusCode());
		}

		EntityUtils.consume(response.getEntity());

		String checkCookieLocation = headerParser.location(response
				.getAllHeaders());

		if (checkCookieLocation == null) {
			throw new ReportDownloaderException(
					"cookieCheck endpoint is missing");
		}

		// TODO find way to remove this harcoded value
		checkCookieLocation += "&service=androiddeveloper";

		securityCheck.setSecuirtyCheck(URI.create(checkCookieLocation));

		return securityCheck;
	}

}
