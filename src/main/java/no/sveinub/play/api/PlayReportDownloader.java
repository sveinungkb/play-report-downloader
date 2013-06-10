package no.sveinub.play.api;

import java.io.IOException;

import lombok.Getter;
import lombok.Setter;
import no.sveinub.play.domain.PlayEstimatedSalesReportStory;
import no.sveinub.play.download.Credentials;
import no.sveinub.play.sales.builder.EstimatedSalesReportBuilder;
import no.sveinub.play.sales.builder.PlayReportDirector;
import no.sveinub.play.sales.parser.EstimatedSalesReportMapper;
import no.sveinub.play.sales.parser.ParserForEstimatedSalesReport;

import org.dozer.MappingException;

/**
 * API interface to retrieve reports
 * 
 * @author georgilambov
 * 
 */
public class PlayReportDownloader implements ReportDownloader {

	@Getter
	@Setter
	private Credentials credentials;

	/*
	 * (non-Javadoc)
	 * 
	 * @see no.sveinub.play.api.ReportDownloader#retrieveEstimatedSalesReport()
	 */
	@Override
	public PlayEstimatedSalesReportStory retrieveEstimatedSalesReport()
			throws MappingException, IOException {
		if (credentials == null) {
			throw new IllegalArgumentException("credentials are required");
		}

		PlayReportDirector director = new PlayReportDirector();
		director.setPlayReportBuilder(new EstimatedSalesReportBuilder());
		director.constructReport(credentials);

		String reportContent = director.getReport().getReportContent();

		ParserForEstimatedSalesReport parserForEstimatedSalesReport = new ParserForEstimatedSalesReport();
		parserForEstimatedSalesReport.setContent(reportContent);

		EstimatedSalesReportMapper mapper = new EstimatedSalesReportMapper(
				parserForEstimatedSalesReport);

		return mapper.getReportContent();
	}

}
