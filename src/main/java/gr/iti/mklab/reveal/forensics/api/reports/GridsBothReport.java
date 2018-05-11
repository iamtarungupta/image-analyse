package gr.iti.mklab.reveal.forensics.api.reports;

import org.mongodb.morphia.annotations.Embedded;


@Embedded
public class GridsBothReport {
		public Boolean completed=false;
	    public String mapG;
	    public String mapGI;
	    public double maxValueG;
	    public double minValueG;
	    public double maxValueGI;
	    public double minValueGI;
	public GridsNormalReport gridsNormalReport = new GridsNormalReport();
	public GridsInversedReport gridsInversedReport = new GridsInversedReport();
}
