package no.sveinub.play.sales.prepare;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import lombok.Setter;
import no.sveinub.play.domain.PlayLogin;
import no.sveinub.play.download.Credentials;
import no.sveinub.play.download.ReportDownloaderException;
import no.sveinub.play.http.RawCookieBuilder;

import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 
 * @author georgilambov
 * 
 */
public class GooglePlayLogin extends ReportConnector<PlayLogin> implements
		PrepareSalesReport<PlayLogin> {

	@Setter
	private Credentials credentials;

	/**
	 * 
	 */
	@Override
	public PlayLogin execute() throws URISyntaxException,
			ClientProtocolException, IOException, ReportDownloaderException {
		if (credentials == null) {
			throw new IllegalArgumentException(
					"google play credentials are required");
		}

		PlayLogin playLogin = new PlayLogin();

		URIBuilder builder = new URIBuilder();
		builder.setScheme("https")
				.setHost("accounts.google.com")
				.setPath("/ServiceLogin")
				.setQuery(
						"?service=androiddeveloper&passive=true&nui=1&continue=https://play.google.com/apps/publish&followup=https://play.google.com/apps/publish");
		URI uri = builder.build();

		HttpGet httpget = new HttpGet(uri);
		HttpResponse response = httpclient.execute(httpget, localContext);

		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			throw new ReportDownloaderException("status code is "
					+ response.getStatusLine().getStatusCode());
		}

		// prepare login form action
		Document loginSite = Jsoup.parse(EntityUtils.toString(response
				.getEntity()));
		EntityUtils.consume(response.getEntity());

		Element loginForm = loginSite.getElementById("gaia_loginform");
		String loginFormAction = loginForm.attributes().get("action");
		playLogin.setLoginAction(loginFormAction);

		// prepare login form params
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();

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

			formparams.add(new BasicNameValuePair(name, value));
		}

		UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(formparams,
				"UTF-8");
		playLogin.setFormEntity(formEntity);

		// prepare cookies
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
		}
		loginCookieBuilder.addParameter("GoogleAccountsLocale_session", "bg");
		playLogin.setCookieBuilder(loginCookieBuilder);
		
		return playLogin;
	}

}
