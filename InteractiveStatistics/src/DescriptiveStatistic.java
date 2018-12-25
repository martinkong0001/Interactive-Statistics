import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.StringTokenizer;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class DescriptiveStatistic extends JPanel implements ActionListener, ItemListener
{
	
	private JTextArea dataTextArea;
	private JScrollPane scrollTextArea;
	private JButton generateBt, plotBt;
	private ChartPanel graph;
	private ValueMarker meanMarker, medianMarker;
	private XYLineAnnotation stdDevMarker, IQRMarker;
	private JLabel introPlotType;
	private ButtonGroup plotTypeBts;
	private JRadioButton dotplotBt, histogramBt;
	private JLabel introSummaryStats;
	private JCheckBox meanBt, medianBt, stdDevBt, IQRBt;
	private JLabel meanLb, medianLb, stdDevLb, IQRLb;
	private JButton goBackBt;
	private JLabel emptySpace1, emptySpace2;
	
	private DescriptiveStatistics dataHandler;
	private double mean, median, stdDev, IQR;
	private static final int DEFAULT_SAMPLE_SIZE = 20;
	private static final Font DEFAULT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 20);
	private static final Stroke DEFAULT_STROKE = new BasicStroke(4);
	private static final Color DEFAULT_COLOR_1 = Color.MAGENTA;
	private static final Color DEFAULT_COLOR_2 = Color.BLUE;
	private InteractiveStatistics main;
	
	public DescriptiveStatistic(InteractiveStatistics main)
	{
		dataHandler = new DescriptiveStatistics();
		this.main = main;
		initializeComponents();
		generateRandomNumbers();
		plotGraph();
		generateSummaryStatistics();
		setUpStructure();
		for (Component component : getComponents())
			component.setFont(DEFAULT_FONT);
	}
	
	public void initializeComponents()
	{
		dataTextArea = new JTextArea();
		dataTextArea.setFont(DEFAULT_FONT);
		scrollTextArea = new JScrollPane(dataTextArea);
		generateBt = new JButton("Generate random numbers");
		generateBt.addActionListener(this);
		plotBt = new JButton("Plot graph using above data");
		plotBt.addActionListener(this);
		
		introPlotType = new JLabel("Choose a plot type:");
		plotTypeBts = new ButtonGroup();
		dotplotBt = new JRadioButton("Dotplot");
		plotTypeBts.add(dotplotBt);
		histogramBt = new JRadioButton("Histogram");
		plotTypeBts.add(histogramBt);
		dotplotBt.setSelected(true);
		
		introSummaryStats = new JLabel("Display summary statistics:");
		meanBt = new JCheckBox("Mean");
		meanBt.addItemListener(this);
		medianBt = new JCheckBox("Median");
		medianBt.addItemListener(this);
		stdDevBt = new JCheckBox("Standard deviation");
		stdDevBt.addItemListener(this);
		IQRBt = new JCheckBox("Interquartile range");
		IQRBt.addItemListener(this);
		
		meanLb = new JLabel(" ");
		meanLb.setForeground(DEFAULT_COLOR_1);
		medianLb = new JLabel(" ");
		medianLb.setForeground(DEFAULT_COLOR_2);
		stdDevLb = new JLabel(" ");
		stdDevLb.setForeground(DEFAULT_COLOR_1);
		IQRLb = new JLabel(" ");
		IQRLb.setForeground(DEFAULT_COLOR_2);
		
		goBackBt = new JButton("Go back to previous page");
		goBackBt.addActionListener(this);
		emptySpace1 = new JLabel(" ");
		emptySpace2 = new JLabel(" ");
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
					.addComponent(scrollTextArea)
					.addComponent(generateBt)
					.addComponent(plotBt)
				)
				.addComponent(graph)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(introPlotType)
					.addComponent(dotplotBt)
					.addComponent(histogramBt)
					.addComponent(emptySpace1)
					.addComponent(introSummaryStats)
					.addComponent(meanBt)
					.addComponent(medianBt)
					.addComponent(stdDevBt)
					.addComponent(IQRBt)
					.addComponent(emptySpace2)
					.addComponent(meanLb)
					.addComponent(medianLb)
					.addComponent(stdDevLb)
					.addComponent(IQRLb)
					.addComponent(goBackBt)
				)
		);
		layout.linkSize(SwingConstants.HORIZONTAL, generateBt, plotBt);
		
		layout.setVerticalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addComponent(introPlotType)
					.addComponent(dotplotBt)
					.addComponent(histogramBt)
					.addComponent(emptySpace1)
					.addComponent(introSummaryStats)
					.addComponent(meanBt)
					.addComponent(medianBt)
					.addComponent(stdDevBt)
					.addComponent(IQRBt)
					.addComponent(emptySpace2)
					.addComponent(meanLb)
					.addComponent(medianLb)
					.addComponent(stdDevLb)
					.addComponent(IQRLb)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
							GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(goBackBt)
				)
				.addComponent(graph)
				.addGroup(layout.createSequentialGroup()
					.addComponent(scrollTextArea)
					.addComponent(generateBt)
					.addComponent(plotBt)
				)
		);
	}
	
	public void generateRandomNumbers()
	{
		dataTextArea.setText("");
		for (int i = 0; i < DEFAULT_SAMPLE_SIZE; i++)
		{
			int num = ThreadLocalRandom.current().nextInt(1, 20);;
			dataTextArea.append(num + System.lineSeparator());
		}
	}
	
	public void plotGraph()
	{
		try
		{
			dataHandler.clear();
			StringTokenizer input = new StringTokenizer(dataTextArea.getText());
			while (input.hasMoreTokens())
			{
				dataHandler.addValue(Double.parseDouble(input.nextToken()));
			}
		}
		catch (NumberFormatException ex)
		{
			JLabel error = new JLabel("Please enter a valid number.");
			error.setFont(DEFAULT_FONT);
			JOptionPane.showMessageDialog
					(this, error, "Error", JOptionPane.ERROR_MESSAGE);
		}
		if (dotplotBt.isSelected())
			makeDotplot();
		else if (histogramBt.isSelected())
			makeHistogram();
		graph.setDomainZoomable(false);
		graph.setRangeZoomable(false);
		graph.repaint();
	}
	
	public void makeDotplot()
	{
		XYSeries series = new XYSeries("Test");
		double[] data = dataHandler.getSortedValues();
		for (int i = 0; i < data.length; i++)
		{
			int start = i;
			for (int j = i; j < data.length; j++)
			{
				if (j == start || data[j] == data[j - 1])
				{
					series.add(data[start], j - start + 1);
				}
				else
				{
					i = j - 1;
					break;
				}
			}
		}
		XYDataset dataSet = new XYSeriesCollection(series);
		JFreeChart dotplot = ChartFactory.createScatterPlot
				("Dotplot", "Number", "Test", dataSet);
		dotplot.removeLegend();
		ValueAxis axis = dotplot.getXYPlot().getRangeAxis();
		axis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		axis.setLowerBound(0);
		axis.setUpperBound(30);
		axis.setVisible(false);
		if (graph == null)
			graph = new ChartPanel(dotplot);
		else
			graph.setChart(dotplot);
	}
	
	public void makeHistogram()
	{
		HistogramDataset dataset = new HistogramDataset();
		dataset.addSeries("Test", dataHandler.getValues(), 8);
		JFreeChart histogram = ChartFactory.createHistogram
				("Histogram", "Number", "Frequency", dataset,
				PlotOrientation.VERTICAL, false, false, false);
		ValueAxis axis = histogram.getXYPlot().getRangeAxis();
		axis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		axis.setLowerBound(0);
		axis.setUpperBound(15);
		if (graph == null)
			graph = new ChartPanel(histogram);
		else
			graph.setChart(histogram);
	}
	
	public void generateSummaryStatistics()
	{
		BigDecimal value = new BigDecimal(dataHandler.getMean());
		value = value.setScale(2, RoundingMode.HALF_UP);
		mean = value.doubleValue();
		value = new BigDecimal(dataHandler.getPercentile(50));
		value = value.setScale(2, RoundingMode.HALF_UP);
		median = value.doubleValue();
		value = new BigDecimal(dataHandler.getStandardDeviation());
		value = value.setScale(2, RoundingMode.HALF_UP);
		stdDev = value.doubleValue();
		value = new BigDecimal
				(dataHandler.getPercentile(75) - dataHandler.getPercentile(25));
		value = value.setScale(2, RoundingMode.HALF_UP);
		IQR = value.doubleValue();
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == generateBt)
		{
			generateRandomNumbers();
		}
		else if (e.getSource() == plotBt)
		{
			plotGraph();
			generateSummaryStatistics();
			meanBt.setSelected(false);
			medianBt.setSelected(false);
			stdDevBt.setSelected(false);
			IQRBt.setSelected(false);
		}
		else if (e.getSource() == goBackBt)
		{
			MainMenu menu = new MainMenu(main);
			main.remove(this);
			main.add(menu);
			main.setVisible(true);
		}
	}

	public void itemStateChanged(ItemEvent arg0)
	{
		if (arg0.getItem() == meanBt)
		{
			if (arg0.getStateChange() == ItemEvent.SELECTED)
			{
				meanLb.setText("Mean = " + mean);
				meanMarker = new ValueMarker(mean, DEFAULT_COLOR_1, DEFAULT_STROKE);
				graph.getChart().getXYPlot().addDomainMarker(meanMarker);
			}
			else
			{
				meanLb.setText(" ");
				graph.getChart().getXYPlot().removeDomainMarker(meanMarker);
			}
		}
		else if (arg0.getItem() == medianBt)
		{
			if (arg0.getStateChange() == ItemEvent.SELECTED)
			{
				medianLb.setText("Median = " + median);
				medianMarker = new ValueMarker(median, DEFAULT_COLOR_2, DEFAULT_STROKE);
				graph.getChart().getXYPlot().addDomainMarker(medianMarker);
			}
			else
			{
				medianLb.setText(" ");
				graph.getChart().getXYPlot().removeDomainMarker(medianMarker);
			}
		}
		else if (arg0.getItem() == stdDevBt)
		{
			if (arg0.getStateChange() == ItemEvent.SELECTED)
			{
				stdDevLb.setText("Standard deviation = " + stdDev);
				stdDevMarker = new XYLineAnnotation(mean - stdDev, 11, mean + stdDev, 11,
						DEFAULT_STROKE, DEFAULT_COLOR_1);
				graph.getChart().getXYPlot().addAnnotation(stdDevMarker);
			}
			else
			{
				stdDevLb.setText(" ");
				graph.getChart().getXYPlot().removeAnnotation(stdDevMarker);
			}
		}
		else if (arg0.getItem() == IQRBt)
		{
			if (arg0.getStateChange() == ItemEvent.SELECTED)
			{
				IQRLb.setText("Interquartile range = " + IQR);
				double start = dataHandler.getPercentile(25);
				IQRMarker = new XYLineAnnotation(start, 10, start + IQR, 10,
						DEFAULT_STROKE, DEFAULT_COLOR_2);
				graph.getChart().getXYPlot().addAnnotation(IQRMarker);
			}
			else
			{
				IQRLb.setText(" ");
				graph.getChart().getXYPlot().removeAnnotation(IQRMarker);
			}
		}
	}
	
}
