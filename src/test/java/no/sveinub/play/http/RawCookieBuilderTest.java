package no.sveinub.play.http;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Georgi Lambov
 *
 */
public class RawCookieBuilderTest {

	private RawCookieBuilder cookieBuilder;

	@Before
	public void initMethod() {
		cookieBuilder = new RawCookieBuilder();
	}

	@Test
	public void rawCookieStringConversionWithSetParameter() {
		cookieBuilder.setParameter("first_param", "first_value").setParameter("second_param", "second_value");

		Assert.assertEquals(50, cookieBuilder.toString().length());
		Assert.assertEquals("first_param=first_value; second_param=second_value", cookieBuilder.toString());
	}

	@Test
	public void rawCookieStringConversionWithAddParameter() {
		cookieBuilder.addParameter("first_param", "first_value").addParameter("second_param", "second_value");

		Assert.assertEquals("first_param=first_value; second_param=second_value", cookieBuilder.toString());
		Assert.assertEquals(50, cookieBuilder.toString().length());
	}

	@Test
	public void escapingOfAmpersand() {
		cookieBuilder.addParameter("first_param", "first_&value");
		Assert.assertEquals(26, cookieBuilder.toString().length());
		Assert.assertEquals("first_param=first_%26value", cookieBuilder.toString());
	}

}
