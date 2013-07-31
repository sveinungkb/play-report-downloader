package no.sveinub.play.bean;

import java.util.Date;

import lombok.Data;

import no.sveinub.play.download.PlayCredentials;

/**
 * 
 * @author georgilambov
 * 
 */
@Data
public class PlayReportRequestBean {

	private PlayCredentials credentials;
	private Date reportDate; // format is yyyy_MM

}
