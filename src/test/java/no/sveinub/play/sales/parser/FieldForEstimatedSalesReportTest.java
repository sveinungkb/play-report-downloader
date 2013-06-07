package no.sveinub.play.sales.parser;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author georgilambov
 * 
 */
public class FieldForEstimatedSalesReportTest {

	@Test
	public void defaultFildMappings() {
		Assert.assertEquals("Order Number",
				FieldForEstimatedSalesReport.ORDER_NUMBER.getValue());
		Assert.assertEquals(FieldForEstimatedSalesReport.ORDER_NUMBER,
				FieldForEstimatedSalesReport.getField("Order Number"));
		Assert.assertEquals(
				"FieldForEstimatedSalesReport{value='Order Number'}",
				FieldForEstimatedSalesReport.ORDER_NUMBER.toString());
	}

	@Test
	public void missingType() {
		Assert.assertNull(null,
				FieldForEstimatedSalesReport.getField("None existing"));
	}

	@Test
	public void camelCaseValue() {
		Assert.assertEquals("cityOfBuyer",
				FieldForEstimatedSalesReport.CITY_OF_BUYER.getCamelCaseValue());
		Assert.assertEquals("skuId",
				FieldForEstimatedSalesReport.SKU_ID.getCamelCaseValue());
		Assert.assertEquals("currencyOfSale",
				FieldForEstimatedSalesReport.CURRENCY_OF_SALE
						.getCamelCaseValue());
	}

}
