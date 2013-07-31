package no.sveinub.play.sales.builder;

import lombok.Data;
import no.sveinub.play.bean.PlayReportRequestBean;
import no.sveinub.play.sales.prepare.SalesReportContext;

/**
 * 
 * @author georgilambov
 * 
 */
@Data
public class PlayReportEntity {

	private SalesReportContext salesReportContext;
	private PlayReportRequestBean requestBean;
	private String reportContent;

}
