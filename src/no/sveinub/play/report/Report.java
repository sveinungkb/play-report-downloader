package no.sveinub.play.report;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

public class Report {

	public String packageName;
	public DateTime startDate;
	public DateTime endDate;
	private List<ReportLine> lines = new ArrayList<ReportLine>();
	
	public void addLine(ReportLine reportLine) {
		lines.add(reportLine);
	}
	
	public List<ReportLine> getLines() {
		return lines;
	}

	public boolean hasValues() {
		return 	hasHeader() && lines.size() > 0;
	}

	public boolean hasHeader() {
		return packageName != null && !packageName.isEmpty() &&
		startDate != null && endDate != null;
	}
}
