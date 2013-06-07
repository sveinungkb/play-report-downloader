package no.sveinub.play.sales.prepare;

import java.io.IOException;
import java.net.URISyntaxException;

import lombok.Getter;
import lombok.Setter;
import no.sveinub.play.download.ReportDownloaderException;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

/**
 * Sales report entry point.
 * 
 * @author georgilambov
 * 
 */
public class SalesReportContext {

	@Getter
	@Setter
	private ReportConnector<?> reportConnector;

	private DefaultHttpClient httpclient;
	private HttpContext localContext;

	public SalesReportContext() {
		postConstruct();
	}

	public SalesReportContext(ReportConnector<?> reportConnector) {
		this.reportConnector = reportConnector;
		postConstruct();
	}

	/**
	 * Creates request to Google Play authentication.
	 * 
	 * @param clazz
	 * @return
	 */
	public <T> T createStep(Class<T> clazz) {
		reportConnector.setLocalContext(localContext);
		reportConnector.setHttpclient(httpclient);
		try {
			// TODO suppress warning overcome
			return clazz.cast(reportConnector.execute());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReportDownloaderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Builds HTTPClient for strategies execution.
	 */
	private void postConstruct() {
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

}
