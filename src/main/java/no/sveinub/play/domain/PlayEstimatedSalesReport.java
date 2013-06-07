package no.sveinub.play.domain;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

/**
 * Google Play estimated sales report.
 * 
 * @author georgilambov
 * 
 */
@Data
public class PlayEstimatedSalesReport {

	private long orderNumber;
	private Date orderChargedDate;
	private long orderChargedTimestamp;
	private String financialStatus;
	private String deviceModel;
	private String productTitle;
	private String productId;
	private String productType;
	private String skuId;
	private String currencyOfSale;
	private BigDecimal itemPrice;
	private BigDecimal taxesCollected;
	private BigDecimal chargedAmount;
	private String cityOfBuyer;
	private String stateOfBuyer;
	private String postalCodeOfBuyer;
	private String countryOfBuyer;

}
