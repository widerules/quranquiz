/****
* Copyright (C) 2011-2013 Quran Quiz Net 
* Tarek Eldeeb <tarekeldeeb@gmail.com>
* License: see LICENSE.txt
****/
package net.quranquiz.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import net.quranquiz.model.QQScoreRecord;
import net.quranquiz.storage.QQProfile;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.chartdemo.demo.chart.AbstractDemoChart;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.Toast;

public class QQScoreChart extends AbstractDemoChart {

	private QQProfile prof;

	public QQScoreChart(QQProfile prof) {
		this.prof = prof;
	}

	public Intent execute(Context context) {
		String[] titles = new String[] { "التقدم الزمني لدرجاتك" };
		List<Date[]> dates = new ArrayList<Date[]>();
		List<double[]> values = new ArrayList<double[]>();

		String QScoresString = prof.getScores();
		Vector<QQScoreRecord> QScores = new Vector<QQScoreRecord>();

		for (String token : QScoresString.split(";")) {
			QScores.add(new QQScoreRecord(token));
		}
		Date[] DateArr = new Date[QScores.size()];
		double[] ScoreArr = new double[QScores.size()];
		int maxScore = 1, tmp;
		for (int i = 0; i < QScores.size(); i++) {
			DateArr[i] = QScores.get(i).getDate();
			tmp = QScores.get(i).getScore();
			ScoreArr[i] = Double.valueOf(tmp);
			maxScore = (tmp > maxScore) ? tmp : maxScore;
		}

		if(QScores.size()<4)
			Toast.makeText(context, "عدد النقاط لازالت قليلة، يمكن اضافة نقطة يوميا على الاكثر",
					Toast.LENGTH_LONG).show();
		dates.add(DateArr);
		values.add(ScoreArr);

		String xAxis = "";
		String yAxis = "";

		String DateStyle = (DateArr[DateArr.length - 1].getYear() == DateArr[0]
				.getYear()) ? " dd/MM" : " MM/yy";
		Date lastDate = new Date();
		lastDate.setTime(DateArr[DateArr.length - 1].getTime() + (long)(0.2*(DateArr[DateArr.length - 1].getTime()-DateArr[0].getTime())));

		int[] colors = new int[] { Color.BLUE };
		PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE };
		XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
		setChartSettings(renderer, "", xAxis, yAxis, DateArr[0].getTime(),
				lastDate.getTime(), 0, maxScore * 1.2,
				Color.GRAY, Color.LTGRAY);
		renderer.setYLabels(7);
		renderer.setXLabels(7);
		renderer.setXLabelsAngle(60);
		renderer.setShowGrid(true);
		renderer.setPointSize((float) 2.5);
		renderer.setZoomEnabled(true);
		return ChartFactory.getTimeChartIntent(context,
				buildDateDataset(titles, dates, values), renderer, DateStyle);
	}

	public String getDesc() {
		return "growth across several years (time chart)";
	}

	public String getName() {
		return "growth";
	}
}
