package no.sveinub.play.sales.prepare;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;

import lombok.Setter;
import no.sveinub.play.domain.DeveloperAccount;
import no.sveinub.play.download.Credentials;

/**
 * 
 * @author georgilambov
 * 
 */
public class GooglePlayEstimatedSalesReport extends ReportConnector<String> implements PrepareSalesReport<String> {

	@Setter
	private DeveloperAccount developerAccount;
	@Setter
	private Credentials credentials;

	public String execute() throws URISyntaxException, ClientProtocolException,
			IOException {
		if (developerAccount == null && credentials == null) {
			throw new IllegalArgumentException(
					"At least one instance of credentials or developer account must be provided");
		}

		// backwards compatibility with hardcoded devNumber
		String devAcc = (developerAccount == null) ? credentials.getDevNumber()
				: developerAccount.getDevAcc();

		URIBuilder builder = new URIBuilder();
		builder.setScheme("https").setHost("play.google.com")
				.setPath("/apps/publish/salesreport/download")
				.setParameter("report_date", "2013_05")
				.setParameter("report_type", "sales_report")
				.setParameter("dev_acc", devAcc.toString());
		URI uri = builder.build();

		HttpGet httpget = new HttpGet(uri);
		HttpResponse response = httpclient.execute(httpget, localContext);

		String output = EntityUtils.toString(response.getEntity());
		EntityUtils.consume(response.getEntity());

		return output;
	}
}
