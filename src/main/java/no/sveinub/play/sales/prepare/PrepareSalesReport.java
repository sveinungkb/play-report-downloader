package no.sveinub.play.sales.prepare;

import java.io.IOException;
import java.net.URISyntaxException;

import no.sveinub.play.download.ReportDownloaderException;

import org.apache.http.client.ClientProtocolException;

/**
 * 
 * @author georgilambov
 * 
 */
public interface PrepareSalesReport<T> {

	/**
	 * 
	 * @return
	 * @throws ReportDownloaderException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws URISyntaxException 
	 */
	T execute() throws URISyntaxException, ClientProtocolException, IOException, ReportDownloaderException;

}
