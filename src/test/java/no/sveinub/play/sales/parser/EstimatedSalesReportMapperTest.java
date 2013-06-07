package no.sveinub.play.sales.parser;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import no.sveinub.play.bean.EstimatedSalesReportBean;
import no.sveinub.play.domain.PlayEstimatedSalesReport;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import org.mockito.MockitoAnnotations;

/**
 * 
 * @author georgilambov
 * 
 */
@RunWith(Parameterized.class)
public class EstimatedSalesReportMapperTest {

	private EstimatedSalesReportMapper mapper;

	@Mock
	private ParserForEstimatedSalesReport parserForEstimatedSalesReport;

	private List<EstimatedSalesReportBean> csvReports;

	public EstimatedSalesReportMapperTest(List<EstimatedSalesReportBean> reports) {
		this.csvReports = reports;
	}

	@Parameters
	public static Collection<Object[]> data() {
		List<EstimatedSalesReportBean> reports = new ArrayList<EstimatedSalesReportBean>();
		EstimatedSalesReportBean report = new EstimatedSalesReportBean();
		report.setChargedAmount("0.99");
		report.setCityOfBuyer("NY");
		report.setCurrencyOfSale("USD");
		report.setDeviceModel("empty");
		report.setFinancialStatus("charged");
		report.setItemPrice("0.99");
		report.setOrderChargedDate("2013-04-05");
		report.setOrderChargedTimestamp(123455667L);
		report.setOrderNumber(566689686L);
		report.setPostalCodeOfBuyer("9023");
		report.setProductId("product-id");
		report.setProductTitle("product-title");
		report.setProductType("product-type");
		report.setSkuId("sku-id");
		report.setStateOfBuyer("");
		report.setTaxesCollected("0.00");
		reports.add(report);

		Object[][] data = new Object[][] { { reports } };
		return Arrays.asList(data);
	}

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mapper = new EstimatedSalesReportMapper(parserForEstimatedSalesReport);
	}

	@Test
	public void dozerMapping() throws IOException {
		when(parserForEstimatedSalesReport.getReportContent()).thenReturn(
				csvReports);
		List<PlayEstimatedSalesReport> reportContent = mapper
				.getReportContent();
		Assert.assertNotNull(reportContent);

		EstimatedSalesReportBean csvReport = csvReports.get(0);
		PlayEstimatedSalesReport mappedReport = reportContent.get(0);
		Assert.assertNotNull(csvReport);
		Assert.assertNotNull(mappedReport);

		Assert.assertEquals(new BigDecimal(csvReport.getChargedAmount()),
				mappedReport.getChargedAmount());
		Assert.assertEquals(csvReport.getCityOfBuyer(),
				mappedReport.getCityOfBuyer());
		Assert.assertEquals(csvReport.getCountryOfBuyer(),
				mappedReport.getCountryOfBuyer());
		Assert.assertEquals(csvReport.getCurrencyOfSale(),
				mappedReport.getCurrencyOfSale());
		Assert.assertEquals(csvReport.getDeviceModel(),
				mappedReport.getDeviceModel());
		Assert.assertEquals(csvReport.getFinancialStatus(),
				mappedReport.getFinancialStatus());
		Assert.assertEquals(new BigDecimal(csvReport.getItemPrice()),
				mappedReport.getItemPrice());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String orderChardgeDate = sdf
				.format(mappedReport.getOrderChargedDate());
		Assert.assertEquals(csvReport.getOrderChargedDate(), orderChardgeDate);
		Assert.assertEquals(csvReport.getOrderChargedTimestamp(),
				mappedReport.getOrderChargedTimestamp());
		Assert.assertEquals(csvReport.getOrderNumber(),
				mappedReport.getOrderNumber());
		Assert.assertEquals(csvReport.getPostalCodeOfBuyer(),
				mappedReport.getPostalCodeOfBuyer());

		Assert.assertEquals(csvReport.getProductId(),
				mappedReport.getProductId());
		Assert.assertEquals(csvReport.getProductTitle(),
				mappedReport.getProductTitle());
		Assert.assertEquals(csvReport.getProductType(),
				mappedReport.getProductType());

		Assert.assertEquals(csvReport.getSkuId(), mappedReport.getSkuId());
		Assert.assertEquals(csvReport.getStateOfBuyer(),
				mappedReport.getStateOfBuyer());
		Assert.assertEquals(new BigDecimal(csvReport.getTaxesCollected()),
				mappedReport.getTaxesCollected());

		verify(parserForEstimatedSalesReport).getReportContent();
	}

}
