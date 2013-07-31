package no.sveinub.play.report;

import static no.sveinub.play.report.DateHelper.toDate;

import org.joda.time.DateTime;

public class ReportLine {
	public DateTime date;
	public String country;
	public Integer dailyDeviceInstalls;
	public Integer dailyDeviceUpgrades;
	public Integer activeUserInstalls;
	public Integer dailyUserUninstalls;
	public Integer dailyUserInstalls;
	public Integer totalUserInstalls;
	public int dailyDeviceUninstalls;

	public static class Builder {
		private ReportLine line = new ReportLine();

		public Builder date(String date) {
			line.date = toDate(date);
			return this;
		}

		public Builder country(String country) {
			line.country = country;
			return this;
		}

		public Builder dailyDeviceInstalls(String value) {
			line.dailyDeviceInstalls = Integer.parseInt(value);
			return this;
		}
		
		public Builder dailyDeviceUninstalls(String value) {
			line.dailyDeviceUninstalls = Integer.parseInt(value);
			return this;
		}
		
		public Builder dailyDeviceUpgrades(String value) {
			line.dailyDeviceUpgrades = Integer.parseInt(value);
			return this;
		}
		
		public Builder activeUserInstalls(String value) {
			line.activeUserInstalls = Integer.parseInt(value);
			return this;
		}
		
		public Builder totalUserInstalls(String value) {
			line.totalUserInstalls = Integer.parseInt(value);
			return this;
		}
		
		public Builder dailyUserInstalls(String value) {
			line.dailyUserInstalls = Integer.parseInt(value);
			return this;
		}
		
		public Builder dailyUserUninstalls(String value) {
			line.dailyUserUninstalls = Integer.parseInt(value);
			return this;
		}

		public ReportLine build() {
			return line;
		}
	}
	
	public static Builder builder() {
		return new Builder();
	}

}
