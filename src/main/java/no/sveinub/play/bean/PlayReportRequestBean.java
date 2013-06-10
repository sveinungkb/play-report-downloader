package no.sveinub.play.bean;

import java.util.Date;

import lombok.Data;

import no.sveinub.play.download.Credentials;

/**
 * 
 * @author georgilambov
 * 
 */
@Data
public class PlayReportRequestBean {

	private Credentials credentials;
	private Date reportDate; // format is yyyy_MM

}
