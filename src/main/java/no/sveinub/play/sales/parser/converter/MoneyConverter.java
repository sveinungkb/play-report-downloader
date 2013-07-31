package no.sveinub.play.sales.parser.converter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.dozer.DozerConverter;

/**
 * 
 * @author Georgi Lambov
 * 
 */
public class MoneyConverter extends DozerConverter<BigDecimal, String> {

	public MoneyConverter() {
		super(BigDecimal.class, String.class);
	}

	@Override
	public String convertTo(BigDecimal source, String destination) {
		return source.toString();
	}

	@Override
	public BigDecimal convertFrom(String source, BigDecimal destination) {
		DecimalFormat format = (DecimalFormat) NumberFormat
				.getInstance(Locale.US);
		format.setParseBigDecimal(true);
		BigDecimal number = null;
		try {
			number = (BigDecimal) format.parse(source);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return number;
	}
}
