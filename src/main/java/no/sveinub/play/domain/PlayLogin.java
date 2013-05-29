package no.sveinub.play.domain;

import lombok.Getter;
import lombok.Setter;
import no.sveinub.play.http.RawCookieBuilder;

import org.apache.http.client.entity.UrlEncodedFormEntity;

/**
 * 
 * @author georgilambov
 * 
 */
public class PlayLogin {

	@Getter
	@Setter
	private String loginAction;
	@Getter
	@Setter
	private RawCookieBuilder cookieBuilder;
	@Getter
	@Setter
	private UrlEncodedFormEntity formEntity;

}
