package no.sveinub.play.api;

import java.io.IOException;

import no.sveinub.play.domain.PlayEstimatedSalesReportStory;

import org.dozer.MappingException;

/**
 * 
 * @author georgilambov
 * 
 */
public interface ReportDownloader {

	/**
	 * Retrieves play estimated sales reports and maps content to JavaBean.
	 * 
	 * @throws IOException
	 * @throws MappingException
	 */
	PlayEstimatedSalesReportStory retrieveEstimatedSalesReport() throws MappingException, IOException;

}
