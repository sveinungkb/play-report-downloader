package no.sveinub.play.api;

import java.io.IOException;
import java.util.List;

import org.dozer.MappingException;

import no.sveinub.play.domain.PlayEstimatedSalesReport;

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
	List<PlayEstimatedSalesReport> retrieveEstimatedSalesReport() throws MappingException, IOException;

}
