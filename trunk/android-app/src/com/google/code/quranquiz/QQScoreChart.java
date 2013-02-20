package com.google.code.quranquiz;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.chartdemo.demo.chart.AbstractDemoChart;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

public class QQScoreChart extends AbstractDemoChart{

	private QQProfile prof;
	
	public QQScoreChart(QQProfile prof){
		this.prof = prof;
	}
	  public String getName() {
	    return "Sales growth";
	  }

	  public String getDesc() {
	    return "The sales growth across several years (time chart)";
	  }

	  public Intent execute(Context context) {
	    String[] titles = new String[] { "Sales growth January 1995 to December 2000" };
	    List<Date[]> dates = new ArrayList<Date[]>();
	    List<double[]> values = new ArrayList<double[]>();
	    
	    /*
	    Date[] dateValues = new Date[] { new Date(95, 0, 1), new Date(95, 3, 1), new Date(95, 6, 1),
	    
	        new Date(95, 9, 1), new Date(96, 0, 1), new Date(96, 3, 1), new Date(96, 6, 1),
	        new Date(96, 9, 1), new Date(97, 0, 1), new Date(97, 3, 1), new Date(97, 6, 1),
	        new Date(97, 9, 1), new Date(98, 0, 1), new Date(98, 3, 1), new Date(98, 6, 1),
	        new Date(98, 9, 1), new Date(99, 0, 1), new Date(99, 3, 1), new Date(99, 6, 1),
	        new Date(99, 9, 1), new Date(100, 0, 1), new Date(100, 3, 1), new Date(100, 6, 1),
	        new Date(100, 9, 1), new Date(100, 11, 1) };
	    dates.add(dateValues);

	    values.add(new double[] { 4.9, 5.3, 3.2, 4.5, 6.5, 4.7, 5.8, 4.3, 4, 2.3, -0.5, -2.9, 3.2, 5.5,
	        4.6, 9.4, 4.3, 1.2, 0, 0.4, 4.5, 3.4, 4.5, 4.3, 4 });
	    */

	    String QScoresString = prof.getScores();
	    Vector<QQScoreRecord> QScores = new Vector<QQScoreRecord>();
		
	    for (String token : QScoresString.split(";")) {
			QScores.add(new QQScoreRecord(token));
		}
	    Date[] DateArr = new Date[QScores.size()];
	    double[] ScoreArr = new double[QScores.size()];
	    
	    for(int i=0;i<QScores.size();i++){
	    	DateArr[i] = QScores.get(i).getDate();
	    	ScoreArr[i] = Double.valueOf(QScores.get(i).getScore());
	    }

		dates.add(DateArr);
		values.add(ScoreArr);
				
	    int[] colors = new int[] { Color.BLUE };
	    PointStyle[] styles = new PointStyle[] { PointStyle.POINT };
	    XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
	    setChartSettings(renderer, "Sales growth", "Date", "%", DateArr[0].getTime(),
	    		DateArr[DateArr.length - 1].getTime(), -4, 11, Color.GRAY, Color.LTGRAY);
	    renderer.setYLabels(10);
	    return ChartFactory.getTimeChartIntent(context, buildDateDataset(titles, dates, values),
	        renderer, "MMM yyyy");
	  }
}
