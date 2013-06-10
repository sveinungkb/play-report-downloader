package no.sveinub.play.sales.parser;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import no.sveinub.play.bean.EstimatedSalesReportBean;
import no.sveinub.play.domain.PlayEstimatedSalesReport;
import no.sveinub.play.domain.PlayEstimatedSalesReportStory;

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
	public PlayEstimatedSalesReportStory getReportContent()
			throws MappingException, IOException {
		DozerBeanMapper mapper = new DozerBeanMapper();
		InputStream stream = this.getClass().getResourceAsStream(
				"/META-INF/mapping.xml");

		mapper.addMapping(stream);

		PlayEstimatedSalesReportStory story = new PlayEstimatedSalesReportStory();
		story.setContent(parserForEstimatedSalesReport.getContent());

		List<PlayEstimatedSalesReport> reports = new ArrayList<PlayEstimatedSalesReport>();

		List<EstimatedSalesReportBean> reportContent = parserForEstimatedSalesReport
				.getReportContent();

		Collections.sort(reportContent,
				new Comparator<EstimatedSalesReportBean>() {

					@Override
					public int compare(EstimatedSalesReportBean s1,
							EstimatedSalesReportBean s2) {
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
								"dd-MM-yyyy");

						Date chargedData1 = null;
						Date chargedData2 = null;
						try {
							chargedData1 = simpleDateFormat.parse(s1
									.getOrderChargedDate());
							chargedData2 = simpleDateFormat.parse(s2
									.getOrderChargedDate());
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return -(chargedData1.compareTo(chargedData2));
					}
				});

		int i = 0;
		for (ListIterator<EstimatedSalesReportBean> it = reportContent
				.listIterator(); it.hasNext();) {

			PlayEstimatedSalesReport report = mapper.map(it.next(),
					PlayEstimatedSalesReport.class);
			reports.add(report);

			// TODO google play is sorted by date
			if (i == 0) {
				story.setToDate(report.getOrderChargedDate());
			}

			if (!it.hasNext()) {
				// last element for first date smallest
				story.setFromDate(report.getOrderChargedDate());
			}

			i++;
		}

		story.setReports(reports);

		return story;
	}
}
