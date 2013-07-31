package no.sveinub.play.bean;

import lombok.Data;

/**
 * 
 * @author georgilambov
 * 
 */
@Data
public class EstimatedSalesReportBean {

	private long orderNumber;
	private String orderChargedDate;
	private long orderChargedTimestamp;
	private String financialStatus;
	private String deviceModel;
	private String productTitle;
	private String productId;
	private String productType;
	private String skuId;
	private String currencyOfSale;
	private String itemPrice;
	private String taxesCollected;
	private String chargedAmount;
	private String cityOfBuyer;
	private String stateOfBuyer;
	private String postalCodeOfBuyer;
	private String countryOfBuyer;

}
