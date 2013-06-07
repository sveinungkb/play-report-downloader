package no.sveinub.play.sales.builder;

import lombok.Data;
import no.sveinub.play.domain.DeveloperAccount;
import no.sveinub.play.download.Credentials;
import no.sveinub.play.sales.prepare.SalesReportContext;

/**
 * 
 * @author georgilambov
 * 
 */
@Data
public class PlayReportEntity {

	// private PlayLogin playLogin;
	// private SecurityCheck securityCheck;
	
	private SalesReportContext salesReportContext;
	private DeveloperAccount developerAccount;
	private Credentials credentials;
}
