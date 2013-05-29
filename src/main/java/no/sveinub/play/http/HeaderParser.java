package no.sveinub.play.http;

import org.apache.http.Header;

/***
 * 
 * @author georgilambov
 * 
 */
public class HeaderParser {

	/**
	 * Retrieves location header.
	 * 
	 * @param headers
	 * @return
	 */
	public String location(Header[] headers) {
		for (Header header : headers) {
			if (header.getName().equals("Location")) {
				return header.getValue();
			}
		}

		return null;
	}

}
