package no.sveinub.play.sales.parser;

/**
 * Original Estimated sales reports google play.
 * 
 * @author georgilambov
 * 
 */
enum FieldForEstimatedSalesReport {

	ORDER_NUMBER("Order Number"), ORDER_CHARGED_DATE("Order Charged Date"), ORDER_CHARGED_TIMESTAMP(
			"Order Charged Timestamp"), FINANCIAL_STATUS("Financial Status"), DEVICE_MODEL(
			"Device Model"), PRODUCT_TITLE("Product Title"), PRODUCT_ID(
			"Product ID"), PRODUCT_TYPE("Product Type"), SKU_ID("SKU ID"), CURRENCY_OF_SALE(
			"Currency of Sale"), ITEM_PRICE("Item Price"), TAXES_COLLECTED(
			"Taxes Collected"), CHARGED_AMOUNT("Charged Amount"), CITY_OF_BUYER(
			"City of Buyer"), STATE_OF_BUYER("State of Buyer"), POSTAL_CODE_OF_BUYER(
			"Postal Code of Buyer"), COUNTRY_OF_BUYER("Country of Buyer");

	/*
	 * Order Charged Timestamp Financial Status Device Model Product Title
	 * Product ID Product Type SKU ID Currency of Sale Item Price Taxes
	 * Collected Charged Amount City of Buyer State of Buyer Postal Code of
	 * Buyer Country of Buyer
	 */

	private String value;

	private FieldForEstimatedSalesReport(String value) {
		this.value = value;
	}

	/**
	 * 
	 * @return
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Returns object from its string representation.
	 * 
	 * @param value
	 * @return
	 */
	public static FieldForEstimatedSalesReport getField(String value) {
		if (ORDER_NUMBER.getValue().equals(value)) {
			return ORDER_NUMBER;
		}

		return null;
	}

	/**
	 * Retrieves destination filed for mapped class with camelCase values.
	 * 
	 * @param value
	 * @return
	 */
	public String getCamelCaseValue() {
		if (value == null || (value != null && value.length() == 0)) {
			return value;
		}

		final StringBuilder builder = new StringBuilder();
		int i = 0;
		for (String part : value.split(" ")) {
			i++;
			if (i > 1) {
				builder.append(part.substring(0, 1).toUpperCase());
			} else {
				builder.append(part.substring(0, 1).toLowerCase());
			}
			builder.append(part.substring(1).toLowerCase());
		}

		return builder.toString().trim();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("FieldForEstimatedSalesReport");
		sb.append("{value='").append(value).append('\'');
		sb.append("}");
		return sb.toString();
	}

}
