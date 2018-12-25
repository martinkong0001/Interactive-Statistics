import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.annotations.XYShapeAnnotation;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.function.Function2D;
import org.jfree.data.function.NormalDistributionFunction2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ConfidenceInterval extends JPanel implements ActionListener, ChangeListener
{
	
	private JLabel popuMeanLb, popuStdLb;
	private JSlider popuMeanSd, popuStdSd;
	private JLabel confLevelLb, sampleSizeLb;
	private JSlider confLevelSd, sampleSizeSd;
	private ButtonGroup intervalTypes;
	private JRadioButton zInterval, tInterval;
	private JButton sampleButton, constructButton;
	private JButton sampleConstructButton;
	private JLabel emptySpace;
	private JButton restartButton, goBackButton;
	private ChartPanel graph;
	
	private double[] sampleData;
	private XYShapeAnnotation[] sampleDataPoints;
	private double[] lowerEnds, upperEnds;
	private XYLineAnnotation[] confidenceIntervals;
	private InteractiveStatistics main;
	private static final Font DEFAULT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 20);
	private static final Stroke DEFAULT_STROKE = new BasicStroke(2);
	
	public ConfidenceInterval(InteractiveStatistics main)
	{
		sampleData = new double[100];
		sampleDataPoints = new XYShapeAnnotation[100];
		lowerEnds = new double[20];
		upperEnds = new double[20];
		confidenceIntervals = new XYLineAnnotation[20];
		this.main = main;
		initializeComponents();
		setUpStructure();
		for (Component component : getComponents())
			component.setFont(DEFAULT_FONT);
	}
	
	public void initializeComponents()
	{
		popuMeanLb = new JLabel("Mean of Population Distribution:");
		popuMeanSd = new JSlider(-100, 100, 0);
		popuMeanSd.setMajorTickSpacing(20);
		popuMeanSd.setMinorTickSpacing(5);
		popuMeanSd.setPaintTicks(true);
		popuMeanSd.setPaintLabels(true);
		popuMeanSd.addChangeListener(this);
		
		popuStdLb = new JLabel("Standard Deviation of Population Distribution:");
		popuStdSd = new JSlider(1, 20, 1);
		popuStdSd.setMajorTickSpacing(3);
		popuStdSd.setMinorTickSpacing(1);
		popuStdSd.setPaintTicks(true);
		popuStdSd.setPaintLabels(true);
		popuStdSd.addChangeListener(this);
		
		confLevelLb = new JLabel("Confidence Level (%):");
		confLevelSd = new JSlider(80, 99, 95);
		confLevelSd.setMajorTickSpacing(3);
		confLevelSd.setMinorTickSpacing(1);
		confLevelSd.setPaintTicks(true);
		confLevelSd.setPaintLabels(true);
		
		sampleSizeLb = new JLabel("Sample Size:");
		sampleSizeSd = new JSlider(5, 100, 5);
		sampleSizeSd.setMajorTickSpacing(10);
		sampleSizeSd.setMinorTickSpacing(1);
		sampleSizeSd.setPaintTicks(true);
		sampleSizeSd.setPaintLabels(true);
		sampleSizeSd.addChangeListener(this);
		
		intervalTypes = new ButtonGroup();
		zInterval = new JRadioButton("Z-Interval");
		intervalTypes.add(zInterval);
		tInterval = new JRadioButton("T-Interval");
		intervalTypes.add(tInterval);
		zInterval.setSelected(true);
		
		sampleButton = new JButton("Take a simple random sample of specified size");
		sampleButton.addActionListener(this);
		constructButton = new JButton("Construct a confidence interval using sample data");
		constructButton.addActionListener(this);
		sampleConstructButton = new JButton("Take sample and construct confidence interval");
		sampleConstructButton.addActionListener(this);
		
		emptySpace = new JLabel(" ");
		restartButton = new JButton("Restart the simulation");
		restartButton.addActionListener(this);
		goBackButton = new JButton("Go back to previous page");
		goBackButton.addActionListener(this);
		
		makeGraph();
	}
	
	public void setUpStructure()
	{
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(popuMeanLb)
					.addComponent(popuMeanSd)
					.addComponent(popuStdLb)
					.addComponent(popuStdSd)
					.addComponent(confLevelLb)
					.addComponent(confLevelSd)
					.addComponent(sampleSizeLb)
					.addComponent(sampleSizeSd)
					.addComponent(zInterval)
					.addComponent(tInterval)
					.addComponent(sampleButton)
					.addComponent(constructButton)
					.addComponent(sampleConstructButton)
					.addComponent(emptySpace)
					.addComponent(restartButton)
					.addComponent(goBackButton)
				)
				.addComponent(graph)
		);
		layout.linkSize(SwingConstants.HORIZONTAL, sampleButton, constructButton,
				sampleConstructButton, restartButton, goBackButton);
		
		layout.setVerticalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(graph)
				.addGroup(layout.createSequentialGroup()
					.addComponent(popuMeanLb)
					.addComponent(popuMeanSd)
					.addComponent(popuStdLb)
					.addComponent(popuStdSd)
					.addComponent(confLevelLb)
					.addComponent(confLevelSd)
					.addComponent(sampleSizeLb)
					.addComponent(sampleSizeSd)
					.addComponent(zInterval)
					.addComponent(tInterval)
					.addComponent(sampleButton)
					.addComponent(constructButton)
					.addComponent(sampleConstructButton)
					.addComponent(emptySpace)
					.addComponent(restartButton)
					.addComponent(goBackButton)
				)
		);
	}
	
	public void makeGraph()
	{
		double popuMean = popuMeanSd.getValue();
		double popuStd = popuStdSd.getValue();
		double start = popuMean - popuStd * 4;
		double end = popuMean + popuStd * 4;
		Function2D popuDist = new NormalDistributionFunction2D(popuMean, popuStd);
        XYSeries popuSeries = DatasetUtilities.sampleFunction2DToSeries
        		(popuDist, start, end, 100, "Population Distribution");
        double sampleSize = sampleSizeSd.getValue();
        double sampMean = popuMean;
        double sampStd = popuStd / Math.sqrt(sampleSize);
        Function2D sampDist = new NormalDistributionFunction2D(sampMean, sampStd);
        XYSeries sampSeries = DatasetUtilities.sampleFunction2DToSeries
        		(sampDist, start, end, 100, "Sampling Distribution");
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(popuSeries);
        dataset.addSeries(sampSeries);
        JFreeChart chart = ChartFactory.createXYLineChart
        		("Constructing Confidence Intervals Using Sample Data",
        		null, null,  dataset, PlotOrientation.VERTICAL, true, false, false);
        chart.getXYPlot().getRangeAxis().setVisible(false);
        ValueMarker meanMarker = new ValueMarker(popuMean, Color.BLACK, DEFAULT_STROKE);
        chart.getXYPlot().addDomainMarker(meanMarker);
        if (graph == null)
        	graph = new ChartPanel(chart);
        else
        	graph.setChart(chart);
		graph.setDomainZoomable(false);
		graph.setRangeZoomable(false);
	}
	
	public void takeSimpleRandomSample()
	{
		double popuMean = popuMeanSd.getValue();
		double popuStd = popuStdSd.getValue();
		NormalDistribution popuDist = new NormalDistribution(popuMean, popuStd);
		int sampleSize = sampleSizeSd.getValue();
		for (int i = 0; i < sampleSize; i++)
			sampleData[i] = popuDist.sample();
		displaySampleData();
	}
	
	public void displaySampleData()
	{
		XYPlot plot = graph.getChart().getXYPlot();
		for (int i = 0; i < sampleDataPoints.length; i++)
		{
			if (sampleDataPoints[i] != null)
			{
				plot.removeAnnotation(sampleDataPoints[i]);
				sampleDataPoints[i] = null;
			}
		}
		double xDiameter = (plot.getDomainAxis().getUpperBound()
				- plot.getDomainAxis().getLowerBound()) / 100;
		double yDiameter = (plot.getRangeAxis().getUpperBound()
				- plot.getRangeAxis().getLowerBound()) / 100;
		for (int i = 0; i < sampleData.length; i++)
		{
			XYShapeAnnotation dataPoint = new XYShapeAnnotation
					(new Ellipse2D.Double(sampleData[i] - xDiameter / 2, 0,
					xDiameter, yDiameter), DEFAULT_STROKE, Color.BLACK, Color.BLACK);
			plot.addAnnotation(dataPoint);
			sampleDataPoints[i] = dataPoint;
		}
	}
	
	public void constructConfidenceInterval()
	{
		SummaryStatistics summary = new SummaryStatistics();
		for (int i = 0; i < sampleSizeSd.getValue(); i++)
			summary.addValue(sampleData[i]);
		if (zInterval.isSelected() == true)
			constructZInterval(summary);
		else if (tInterval.isSelected() == true)
			constructTInterval(summary);
	}
	
	public void constructZInterval(SummaryStatistics summary)
	{
		double confLevel = confLevelSd.getValue() / 100.0;
		double probability = confLevel + (1 - confLevel) / 2;
		NormalDistribution stdNormal = new NormalDistribution(0, 1);
		double zScore = stdNormal.inverseCumulativeProbability(probability);
		double sampleMean = summary.getMean();
		double popuStd = popuStdSd.getValue();
		int sampleSize = sampleSizeSd.getValue();
		double lower = sampleMean - zScore * popuStd / Math.sqrt(sampleSize);
		double upper = sampleMean + zScore * popuStd / Math.sqrt(sampleSize);
		displayConfidenceInterval(lower, upper);
	}
	
	public void constructTInterval(SummaryStatistics summary)
	{
		double confLevel = confLevelSd.getValue() / 100.0;
		double probability = confLevel + (1 - confLevel) / 2;
		int sampleSize = sampleSizeSd.getValue();
		TDistribution tDistribution = new TDistribution(sampleSize - 1);
		double tScore = tDistribution.inverseCumulativeProbability(probability);
		double sampleMean = summary.getMean();
		double sampleStd = summary.getStandardDeviation();
		double lower = sampleMean - tScore * sampleStd / Math.sqrt(sampleSize);
		double upper = sampleMean + tScore * sampleStd / Math.sqrt(sampleSize);
		displayConfidenceInterval(lower, upper);
	}
	
	
	public void displayConfidenceInterval(double lower, double upper)
	{
		double trueMean = popuMeanSd.getValue();
		XYPlot plot = graph.getChart().getXYPlot();
		double yMax = plot.getRangeAxis().getUpperBound();
		double yMin = plot.getRangeAxis().getLowerBound();
		double diff = (yMax - yMin) / (confidenceIntervals.length + 1);
		if (confidenceIntervals[0] != null)
			plot.removeAnnotation(confidenceIntervals[0]);
		for (int i = 1; i < confidenceIntervals.length; i++)
		{
			if (confidenceIntervals[i] == null)
				continue;
			XYLineAnnotation interval = null;
			if (lowerEnds[i] <= trueMean && upperEnds[i] >= trueMean)
				interval = new XYLineAnnotation(lowerEnds[i], yMax - diff * i,
						upperEnds[i], yMax - diff * i, DEFAULT_STROKE, Color.BLACK);
			else
				interval = new XYLineAnnotation(lowerEnds[i], yMax - diff * i,
						upperEnds[i], yMax - diff * i, DEFAULT_STROKE, Color.RED);
			plot.addAnnotation(interval);
			plot.removeAnnotation(confidenceIntervals[i]);
			confidenceIntervals[i - 1] = interval;
			lowerEnds[i - 1] = lowerEnds[i];
			upperEnds[i - 1] = upperEnds[i];
		}
		XYLineAnnotation interval = null;
		if (lower <= trueMean && upper >= trueMean)
			interval = new XYLineAnnotation(lower, yMin + diff,
					upper, yMin + diff, DEFAULT_STROKE, Color.BLACK);
		else
			interval = new XYLineAnnotation(lower, yMin + diff,
					upper, yMin + diff, DEFAULT_STROKE, Color.RED);
		plot.addAnnotation(interval);
		confidenceIntervals[confidenceIntervals.length - 1] = interval;
		lowerEnds[confidenceIntervals.length - 1] = lower;
		upperEnds[confidenceIntervals.length - 1] = upper;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == sampleButton)
		{
			takeSimpleRandomSample();
		}
		else if (e.getSource() == constructButton)
		{
			if (sampleDataPoints[0] == null)
			{
				JLabel errorMessage = new JLabel();
				errorMessage.setFont(DEFAULT_FONT);
				errorMessage.setText("Please take a sample first!");
				JOptionPane.showMessageDialog(this, errorMessage,
						"Error", JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				constructConfidenceInterval();
			}
		}
		else if (e.getSource() == sampleConstructButton)
		{
			takeSimpleRandomSample();
			constructConfidenceInterval();
		}
		else if (e.getSource() == restartButton)
		{
			ConfidenceInterval newPanel = new ConfidenceInterval(main);
			main.remove(this);
			main.add(newPanel);
			main.setVisible(true);
		}
		else if (e.getSource() == goBackButton)
		{
			MainMenu menu = new MainMenu(main);
			main.remove(this);
			main.add(menu);
			main.setVisible(true);
		}
	}

	public void stateChanged(ChangeEvent arg0)
	{
		makeGraph();
		for (int i = 0; i < confidenceIntervals.length; i++)
		{
			sampleData[i] = 0;
			sampleDataPoints[i] = null;
			lowerEnds[i] = 0;
			upperEnds[i] = 0;
			confidenceIntervals[i] = null;
		}
	}

}
