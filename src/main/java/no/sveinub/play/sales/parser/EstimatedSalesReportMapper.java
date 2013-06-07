package no.sveinub.play.sales.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import no.sveinub.play.bean.EstimatedSalesReportBean;
import no.sveinub.play.domain.PlayEstimatedSalesReport;

import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

/**
 * Map csv bean content to specific domain bean.
 * 
 * @author georgilambov
 * 
 */
public class EstimatedSalesReportMapper {

	private ParserForEstimatedSalesReport parserForEstimatedSalesReport;

	public EstimatedSalesReportMapper(
			ParserForEstimatedSalesReport parserForEstimatedSalesReport) {
		this.parserForEstimatedSalesReport = parserForEstimatedSalesReport;
	}

	/**
	 * Maps csv bean to domain bean including type casting.
	 * 
	 * @return
	 * @throws IOException
	 * @throws MappingException
	 */
	public List<PlayEstimatedSalesReport> getReportContent()
			throws MappingException, IOException {
		DozerBeanMapper mapper = new DozerBeanMapper();
		InputStream stream = this.getClass().getResourceAsStream(
				"/META-INF/mapping.xml");

		mapper.addMapping(stream);

		List<PlayEstimatedSalesReport> reports = new ArrayList<PlayEstimatedSalesReport>();
		for (EstimatedSalesReportBean reportContent : parserForEstimatedSalesReport
				.getReportContent()) {
			reports.add(mapper.map(reportContent,
					PlayEstimatedSalesReport.class));
		}
		return reports;
	}

}
