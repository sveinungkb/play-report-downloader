package no.sveinub.play.sales.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import no.sveinub.play.bean.EstimatedSalesReportBean;
import no.sveinub.play.sales.PlayReportInfo;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;

/**
 * Fields mapping and parser for estimated sales reports.
 * 
 * @author georgilambov
 * 
 */
public class ParserForEstimatedSalesReport implements
		PlayReportInfo<List<EstimatedSalesReportBean>> {

	@Getter
	private String content;

	/*
	 * (non-Javadoc)
	 * 
	 * @see no.sveinub.play.sales.PlayReportInfo#getReportContent()
	 */
	@Override
	public List<EstimatedSalesReportBean> getReportContent() throws IOException {
		if (content == null) {
			throw new IllegalArgumentException("report content is required");
		}

		Map<String, String> columns = new HashMap<String, String>();
		columns.put(FieldForEstimatedSalesReport.CHARGED_AMOUNT.getValue(),
				FieldForEstimatedSalesReport.CHARGED_AMOUNT.getCamelCaseValue());
		columns.put(FieldForEstimatedSalesReport.CITY_OF_BUYER.getValue(),
				FieldForEstimatedSalesReport.CITY_OF_BUYER.getCamelCaseValue());
		columns.put(FieldForEstimatedSalesReport.COUNTRY_OF_BUYER.getValue(),
				FieldForEstimatedSalesReport.COUNTRY_OF_BUYER
						.getCamelCaseValue());
		columns.put(FieldForEstimatedSalesReport.CURRENCY_OF_SALE.getValue(),
				FieldForEstimatedSalesReport.CURRENCY_OF_SALE
						.getCamelCaseValue());
		columns.put(FieldForEstimatedSalesReport.DEVICE_MODEL.getValue(),
				FieldForEstimatedSalesReport.DEVICE_MODEL.getCamelCaseValue());
		columns.put(FieldForEstimatedSalesReport.FINANCIAL_STATUS.getValue(),
				FieldForEstimatedSalesReport.FINANCIAL_STATUS
						.getCamelCaseValue());
		columns.put(FieldForEstimatedSalesReport.ITEM_PRICE.getValue(),
				FieldForEstimatedSalesReport.ITEM_PRICE.getCamelCaseValue());
		columns.put(FieldForEstimatedSalesReport.ORDER_CHARGED_DATE.getValue(),
				FieldForEstimatedSalesReport.ORDER_CHARGED_DATE
						.getCamelCaseValue());
		columns.put(FieldForEstimatedSalesReport.ORDER_CHARGED_TIMESTAMP
				.getValue(),
				FieldForEstimatedSalesReport.ORDER_CHARGED_TIMESTAMP
						.getCamelCaseValue());
		columns.put(FieldForEstimatedSalesReport.ORDER_NUMBER.getValue(),
				FieldForEstimatedSalesReport.ORDER_NUMBER.getCamelCaseValue());
		columns.put(FieldForEstimatedSalesReport.POSTAL_CODE_OF_BUYER
				.getValue(), FieldForEstimatedSalesReport.POSTAL_CODE_OF_BUYER
				.getCamelCaseValue());
		columns.put(FieldForEstimatedSalesReport.PRODUCT_TITLE.getValue(),
				FieldForEstimatedSalesReport.PRODUCT_TITLE.getCamelCaseValue());
		columns.put(FieldForEstimatedSalesReport.PRODUCT_ID.getValue(),
				FieldForEstimatedSalesReport.PRODUCT_ID.getCamelCaseValue());
		columns.put(FieldForEstimatedSalesReport.PRODUCT_TYPE.getValue(),
				FieldForEstimatedSalesReport.PRODUCT_TYPE.getCamelCaseValue());
		columns.put(FieldForEstimatedSalesReport.SKU_ID.getValue(),
				FieldForEstimatedSalesReport.SKU_ID.getCamelCaseValue());
		columns.put(FieldForEstimatedSalesReport.STATE_OF_BUYER.getValue(),
				FieldForEstimatedSalesReport.STATE_OF_BUYER.getCamelCaseValue());
		columns.put(FieldForEstimatedSalesReport.TAXES_COLLECTED.getValue(),
				FieldForEstimatedSalesReport.TAXES_COLLECTED
						.getCamelCaseValue());

		HeaderColumnNameTranslateMappingStrategy<EstimatedSalesReportBean> strat = new HeaderColumnNameTranslateMappingStrategy<EstimatedSalesReportBean>();
		strat.setType(EstimatedSalesReportBean.class);
		strat.setColumnMapping(columns);
		CsvToBean<EstimatedSalesReportBean> csv = new CsvToBean<EstimatedSalesReportBean>();

		StringReader stringReader = new StringReader(content);
		CSVReader csvreader = new CSVReader(stringReader);
		List<EstimatedSalesReportBean> mappedData = csv.parse(strat, csvreader);

		try {
			csvreader.close();
		} catch (IOException e) {
			throw e;
		}

		return mappedData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see no.sveinub.play.sales.PlayReportInfo#setContent(java.lang.String)
	 */
	@Override
	public void setContent(String content) {
		this.content = content;
	}

}
