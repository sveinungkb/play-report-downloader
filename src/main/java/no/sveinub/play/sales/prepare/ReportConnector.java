package no.sveinub.play.sales.prepare;

import lombok.Getter;
import lombok.Setter;
import no.sveinub.play.http.RawCookieBuilder;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

/**
 * Hold connector instances for each strategy.
 * 
 * @author georgilambov
 * @param <T>
 * 
 */
public abstract class ReportConnector<T> implements PrepareSalesReport<T> {

	@Getter
	@Setter
	protected DefaultHttpClient httpclient;

	@Getter
	@Setter
	protected HttpContext localContext;

	@Getter
	@Setter
	protected RawCookieBuilder rawCookieBuilder;

}
