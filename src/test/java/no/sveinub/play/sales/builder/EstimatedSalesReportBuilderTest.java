package no.sveinub.play.sales.builder;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import no.sveinub.play.bean.PlayReportRequestBean;
import no.sveinub.play.download.PlayCredentials;
import no.sveinub.play.sales.prepare.SalesReportContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * 
 * @author Georgi Lambov
 * 
 */
public class EstimatedSalesReportBuilderTest {

	private EstimatedSalesReportBuilder estimatedSalesReportBuilder;
	@Mock
	private SalesReportContext salesReportContext;
	@Mock
	private PlayReportRequestBean requestBean;
	@Mock
	private PlayReportEntity playReportEntity;
	@Mock
	private PlayCredentials credentials;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		estimatedSalesReportBuilder = new EstimatedSalesReportBuilder();
		estimatedSalesReportBuilder.setPlayReportEntity(playReportEntity);
	}

	@Test
	public void createNewReport() {
		estimatedSalesReportBuilder.createNewReport(requestBean);
	}

	@Test
	public void buildPlayLogin() {
		when(playReportEntity.getSalesReportContext()).thenReturn(
				salesReportContext);
		when(playReportEntity.getRequestBean()).thenReturn(requestBean);
		when(requestBean.getCredentials()).thenReturn(credentials);
		estimatedSalesReportBuilder.buildPlayLogin();

		verify(playReportEntity).getSalesReportContext();
	}

	@Test
	public void buildSecurityCheck() {
		when(playReportEntity.getSalesReportContext()).thenReturn(
				salesReportContext);

		estimatedSalesReportBuilder.buildSecurityCheck();
		verify(playReportEntity).getSalesReportContext();
	}

	@Test
	public void buildDeveloperAccount() {
		when(playReportEntity.getSalesReportContext()).thenReturn(
				salesReportContext);
		estimatedSalesReportBuilder.buildDeveloperAccount();
		verify(playReportEntity).getSalesReportContext();
	}

}
