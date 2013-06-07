package no.sveinub.play.sales.parser;

import java.io.IOException;
import java.util.List;

import no.sveinub.play.bean.EstimatedSalesReportBean;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author georgilambov
 * 
 */
public class ParserForEstimatedSalesReportTest {

	private ParserForEstimatedSalesReport parserForEstimatedSalesReport;

	@Before
	public void setUp() throws IOException {
		parserForEstimatedSalesReport = new ParserForEstimatedSalesReport();

		String content = IOUtils
				.toString(ClassLoader
						.getSystemResourceAsStream("google-play-report.csv"),
						"UTF-8");

		parserForEstimatedSalesReport.setContent(content);
	}

	@Test
	public void mappingCsvContentToJavaBean() throws IOException {
		List<EstimatedSalesReportBean> reportContent = parserForEstimatedSalesReport
				.getReportContent();

		EstimatedSalesReportBean blueGame = reportContent.get(0);
		EstimatedSalesReportBean redGame = reportContent.get(1);

		Assert.assertNotNull(blueGame);
		Assert.assertNotNull(redGame);

		Assert.assertEquals(1307496652058362L, blueGame.getOrderNumber());
		Assert.assertEquals("2013-05-19", blueGame.getOrderChargedDate());
		Assert.assertEquals(1368936853L, blueGame.getOrderChargedTimestamp());
		Assert.assertEquals("Charged", blueGame.getFinancialStatus());
		Assert.assertEquals("C6603", blueGame.getDeviceModel());
		Assert.assertEquals("Pack 1", blueGame.getProductTitle());
		Assert.assertEquals("com.test.game.blue", blueGame.getProductId());
		Assert.assertEquals("inapp", blueGame.getProductType());
		Assert.assertEquals("blue_pack_1_", blueGame.getSkuId());
		Assert.assertEquals("EUR", blueGame.getCurrencyOfSale());
		Assert.assertEquals("0.80", blueGame.getItemPrice());
		Assert.assertEquals("0.00", blueGame.getTaxesCollected());
		Assert.assertEquals("0.80", blueGame.getChargedAmount());
		Assert.assertEquals("", blueGame.getCityOfBuyer());
		Assert.assertEquals("", blueGame.getStateOfBuyer());
		Assert.assertEquals("06869", blueGame.getPostalCodeOfBuyer());
		Assert.assertEquals("DE", blueGame.getCountryOfBuyer());

		Assert.assertEquals(1352772537650069L, redGame.getOrderNumber());
		Assert.assertEquals("2013-05-27", redGame.getOrderChargedDate());
		Assert.assertEquals(1369637762L, redGame.getOrderChargedTimestamp());
		Assert.assertEquals("Charged", redGame.getFinancialStatus());
		Assert.assertEquals("vanquish", redGame.getDeviceModel());
		Assert.assertEquals("Red Game", redGame.getProductTitle());
		Assert.assertEquals("com.test.game.red", redGame.getProductId());
		Assert.assertEquals("paidapp", redGame.getProductType());
		Assert.assertEquals("", redGame.getSkuId());
		Assert.assertEquals("USD", redGame.getCurrencyOfSale());
		Assert.assertEquals("3.99", redGame.getItemPrice());
		Assert.assertEquals("0.00", redGame.getTaxesCollected());
		Assert.assertEquals("3.99", redGame.getChargedAmount());
		Assert.assertEquals("", redGame.getCityOfBuyer());
		Assert.assertEquals("", redGame.getStateOfBuyer());
		Assert.assertEquals("26062", redGame.getPostalCodeOfBuyer());
		Assert.assertEquals("US", redGame.getCountryOfBuyer());
	}

	@Test(expected = IllegalArgumentException.class)
	public void missingOriginalReportContent() throws IOException {
		parserForEstimatedSalesReport.setContent(null);
		parserForEstimatedSalesReport.getReportContent();
	}

	@Test
	public void parseReportWithEmptyContent() throws IOException {
		parserForEstimatedSalesReport.setContent("");
		List<EstimatedSalesReportBean> reportContent = parserForEstimatedSalesReport
				.getReportContent();
		Assert.assertNotNull(reportContent);
		Assert.assertEquals(0, reportContent.size());
	}

}
