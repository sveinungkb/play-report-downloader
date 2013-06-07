package no.sveinub.play.domain;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Report entities with their aggregated meta data.
 * 
 * @author georgilambov
 * 
 */
public class PlayEstimatedSalesReportStory {

	@Getter
	@Setter
	private List<PlayEstimatedSalesReport> reports;
	@Getter
	@Setter
	private Date fromDate;
	@Getter
	@Setter
	private Date toDate;
	@Getter
	@Setter
	private String content;
	@Getter
	@Setter
	private String reportCurrency;

}
