package no.sveinub.play.sales;

import java.io.IOException;

/**
 * 
 * @author georgilambov
 * 
 * @param <T>
 */
public interface PlayReportInfo<T> {

	/**
	 * Retrieves report conetnt info.
	 * 
	 * @return
	 * @throws IOException
	 */
	T getReportContent() throws IOException;

	/**
	 * Receives report content as String.
	 * 
	 */
	void setContent(String content);

}
