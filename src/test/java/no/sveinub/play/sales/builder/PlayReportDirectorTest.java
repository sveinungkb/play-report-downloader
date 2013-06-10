package no.sveinub.play.sales.builder;

import static org.mockito.Mockito.when;
import no.sveinub.play.download.PlayCredentials;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * 
 * @author Georgi Lambov
 * 
 */
public class PlayReportDirectorTest {

	private PlayReportDirector playReportDirector;
	@Mock
	private EstimatedSalesReportBuilder estimatedSalesReportBuilder;
	@Mock
	private PlayCredentials credentials;
	@Mock
	private PlayReportEntity playReportEntity;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		playReportDirector = new PlayReportDirector();
		playReportDirector.setPlayReportBuilder(estimatedSalesReportBuilder);
	}

	@Test
	public void constructReportWithEstimatedSalesReportBuilder() {
		playReportDirector.setPlayReportBuilder(estimatedSalesReportBuilder);
		playReportDirector.constructReport(credentials);

	}

	@Test
	public void repoortContentForEstimatedSalesReport() {
		playReportDirector.setPlayReportBuilder(estimatedSalesReportBuilder);
		when(estimatedSalesReportBuilder.getPlayReportEntity()).thenReturn(
				playReportEntity);

		playReportDirector.constructReport(credentials);
		Assert.assertNull(playReportDirector.getReport().getReportContent());
	}

}
