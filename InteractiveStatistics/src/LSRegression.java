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
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class LSRegression extends JPanel implements ActionListener, ItemListener
{
	
	private JTextArea dataTextArea;
	private JScrollPane scrollTextArea;
	private JButton generateButton;
	private JButton plotButton;
	private ChartPanel graph;
	private XYLineAnnotation bestFitLine;
	private ArrayList<XYLineAnnotation> residuals, sqResiduals;
	private JCheckBox showLine, showResiduals, showSqResiduals;
	private JLabel lineEquation;
	private JCheckBox showR, showRSquared;
	private JLabel rLabel, rSquaredLabel;
	private JCheckBox showSST, showSSR, showSSE;
	private JLabel SSTLabel, SSRLabel, SSELabel;
	private JButton goBackButton;
	
	private ArrayList<Double> xValues;
	private ArrayList<Double> yValues;
	private SimpleRegression dataHandler;
	private static final int DEFAULT_SAMPLE_SIZE = 20;
	private static final Font DEFAULT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 20);
	private static final Stroke DEFAULT_STROKE = new BasicStroke(4);
	private static final Color DEFAULT_COLOR = Color.BLUE;
	private InteractiveStatistics main;
	
	public LSRegression(InteractiveStatistics main)
	{
		xValues = new ArrayList<Double>();
		yValues = new ArrayList<Double>();
		dataHandler = new SimpleRegression();
		this.main = main;
		initializeComponents();
		generateRandomNumbers();
		gatherUserInput();
		plotGraph();
		setUpStructure();
		for (Component component : getComponents())
			component.setFont(DEFAULT_FONT);
		showLine.doClick();
		showLine.doClick();
	}
	
	public void initializeComponents()
	{
		dataTextArea = new JTextArea();
		dataTextArea.setFont(DEFAULT_FONT);
		scrollTextArea = new JScrollPane(dataTextArea);
		generateButton = new JButton("Generate random numbers");
		generateButton.addActionListener(this);
		plotButton = new JButton("Plot graph using above data");
		plotButton.addActionListener(this);
		
		showLine = new JCheckBox("Click to show least-squares regression line");
		showLine.addItemListener(this);
		showResiduals = new JCheckBox("Show residuals");
		showResiduals.addItemListener(this);
		showSqResiduals = new JCheckBox("Show squared residuals");
		showSqResiduals.addItemListener(this);
		lineEquation = new JLabel(" ");
		
		showR = new JCheckBox("Show correlation coefficient");
		showR.addItemListener(this);
		showRSquared = new JCheckBox("Show coefficient of determination");
		showRSquared.addItemListener(this);
		rLabel = new JLabel(" ");
		rSquaredLabel = new JLabel(" ");
		
		showSST = new JCheckBox("Show SST (total sum of squares)");
		showSST.addItemListener(this);
		showSSR = new JCheckBox("Show SSR (regression sum of squares)");
		showSSR.addItemListener(this);
		showSSE = new JCheckBox("Show SSE (error sum of squares)");
		showSSE.addItemListener(this);
		SSTLabel = new JLabel(" ");
		SSRLabel = new JLabel(" ");
		SSELabel = new JLabel(" ");
		
		goBackButton = new JButton("Go back to previous page");
		goBackButton.addActionListener(this);
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
					.addComponent(generateButton)
					.addComponent(plotButton)
				)
				.addComponent(graph)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(showLine)
					.addComponent(lineEquation)
					.addComponent(showResiduals)
					.addComponent(showSqResiduals)
					.addComponent(showR)
					.addComponent(showRSquared)
					.addComponent(rLabel)
					.addComponent(rSquaredLabel)
					.addComponent(showSST)
					.addComponent(showSSR)
					.addComponent(showSSE)
					.addComponent(SSTLabel)
					.addComponent(SSRLabel)
					.addComponent(SSELabel)
					.addComponent(goBackButton)
				)
		);
		layout.linkSize(SwingConstants.HORIZONTAL, generateButton, plotButton);
		
		layout.setVerticalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addComponent(showLine)
					.addComponent(lineEquation)
					.addComponent(showResiduals)
					.addComponent(showSqResiduals)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
							GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(showR)
					.addComponent(showRSquared)
					.addComponent(rLabel)
					.addComponent(rSquaredLabel)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
							GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(showSST)
					.addComponent(showSSR)
					.addComponent(showSSE)
					.addComponent(SSTLabel)
					.addComponent(SSRLabel)
					.addComponent(SSELabel)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
							GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(goBackButton)
				)
				.addComponent(graph)
				.addGroup(layout.createSequentialGroup()
					.addComponent(scrollTextArea)
					.addComponent(generateButton)
					.addComponent(plotButton)
				)
		);
	}
	
	public void generateRandomNumbers()
	{
		dataTextArea.setText("");
		for (int i = 0; i < DEFAULT_SAMPLE_SIZE; i++)
		{
			int x = ThreadLocalRandom.current().nextInt(1, 50);
			int y = ThreadLocalRandom.current().nextInt(1, 50);
			dataTextArea.append(x + " " + y + System.lineSeparator());
		}
	}
	
	public void gatherUserInput()
	{
		try
		{
			xValues.clear();
			yValues.clear();
			dataHandler.clear();
			StringTokenizer input = new StringTokenizer(dataTextArea.getText());
			while (input.hasMoreTokens())
			{
				double x = Double.parseDouble(input.nextToken());
				double y = Double.parseDouble(input.nextToken());
				xValues.add(x);
				yValues.add(y);
				dataHandler.addData(x, y);
			}
		}
		catch (NumberFormatException ex)
		{
			JLabel error = new JLabel("Please enter a valid number.");
			error.setFont(DEFAULT_FONT);
			JOptionPane.showMessageDialog
					(this, error, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void plotGraph()
	{
		XYSeries series = new XYSeries("Test");
		for (int i = 0; i < xValues.size(); i++)
		{
			series.add(xValues.get(i), yValues.get(i));
		}
		XYDataset dataset = new XYSeriesCollection(series);
		JFreeChart scatterplot = ChartFactory.createScatterPlot
				("Scatterplot", "x-values", "y-values", dataset);
		scatterplot.removeLegend();
		if (graph == null)
			graph = new ChartPanel(scatterplot);
		else
			graph.setChart(scatterplot);
		graph.setDomainZoomable(false);
		graph.setRangeZoomable(false);
		graph.repaint();
	}
	
	public void calculateBestFitLine()
	{
		BigDecimal mDecimal = new BigDecimal(dataHandler.getSlope());
		mDecimal = mDecimal.setScale(2, RoundingMode.HALF_UP);
		double m = mDecimal.doubleValue();
		BigDecimal bDecimal = new BigDecimal(dataHandler.getIntercept());
		bDecimal = bDecimal.setScale(2, RoundingMode.HALF_UP);
		double b = bDecimal.doubleValue();
		lineEquation.setText("y = " + m + "x + " + b);
		double lower = graph.getChart().getXYPlot().getDomainAxis().getLowerBound();
		double upper = graph.getChart().getXYPlot().getDomainAxis().getUpperBound();
		bestFitLine = new XYLineAnnotation(lower, m * lower + b,
				upper, m * upper + b, DEFAULT_STROKE, DEFAULT_COLOR);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == generateButton)
		{
			generateRandomNumbers();
		}
		else if (e.getSource() == plotButton)
		{
			gatherUserInput();
			plotGraph();
			showLine.setSelected(false);
		}
		else if (e.getSource() == goBackButton)
		{
			MainMenu menu = new MainMenu(main);
			main.remove(this);
			main.add(menu);
			main.setVisible(true);
		}
	}

	public void itemStateChanged(ItemEvent arg0)
	{
		if (arg0.getItem() == showLine)
		{
			if (arg0.getStateChange() == ItemEvent.SELECTED)
			{
				calculateBestFitLine();
				graph.getChart().getXYPlot().addAnnotation(bestFitLine);
				showResiduals.setVisible(true);
				showSqResiduals.setVisible(true);
				showR.setVisible(true);
				showRSquared.setVisible(true);
				showSST.setVisible(true);
				showSSR.setVisible(true);
				showSSE.setVisible(true);
			}
			else
			{
				graph.getChart().getXYPlot().removeAnnotation(bestFitLine);
				lineEquation.setText(" ");
				showResiduals.setSelected(false);
				showResiduals.setVisible(false);
				showSqResiduals.setSelected(false);
				showSqResiduals.setVisible(false);
				showR.setSelected(false);
				showR.setVisible(false);
				showRSquared.setSelected(false);
				showRSquared.setVisible(false);
				rLabel.setText(" ");
				rSquaredLabel.setText(" ");
				showSST.setSelected(false);
				showSST.setVisible(false);
				showSSR.setSelected(false);
				showSSR.setVisible(false);
				showSSE.setSelected(false);
				showSSE.setVisible(false);
				SSTLabel.setText(" ");
				SSRLabel.setText(" ");
				SSELabel.setText(" ");
			}
		}
		else if (arg0.getItem() == showResiduals)
		{
			if (arg0.getStateChange() == ItemEvent.SELECTED)
			{
				residuals = new ArrayList<XYLineAnnotation>();
				XYPlot plot = graph.getChart().getXYPlot();
				for (int i = 0; i < xValues.size(); i++)
				{
					XYLineAnnotation residual = new XYLineAnnotation(xValues.get(i), yValues.get(i),
							xValues.get(i), dataHandler.predict(xValues.get(i)));
					plot.addAnnotation(residual);
					residuals.add(residual);
				}
			}
			else
			{
				XYPlot plot = graph.getChart().getXYPlot();
				for (int i = 0; i < residuals.size(); i++)
				{
					plot.removeAnnotation(residuals.get(i));
				}
			}
		}
		else if (arg0.getItem() == showSqResiduals)
		{
			if (arg0.getStateChange() == ItemEvent.SELECTED)
			{
				sqResiduals = new ArrayList<XYLineAnnotation>();
				XYPlot plot = graph.getChart().getXYPlot();
				for (int i = 0; i < xValues.size(); i++)
				{
					double x1 = xValues.get(i);
					double y1 = yValues.get(i);
					double y2 = dataHandler.predict(x1);
					double x2 = x1 + Math.abs(y2 - y1);
					XYLineAnnotation line1 = new XYLineAnnotation(x1, y1, x1, y2);
					plot.addAnnotation(line1);
					sqResiduals.add(line1);
					XYLineAnnotation line2 = new XYLineAnnotation(x1, y1, x2, y1);
					plot.addAnnotation(line2);
					sqResiduals.add(line2);
					XYLineAnnotation line3 = new XYLineAnnotation(x2, y2, x1, y2);
					plot.addAnnotation(line3);
					sqResiduals.add(line3);
					XYLineAnnotation line4 = new XYLineAnnotation(x2, y2, x2, y1);
					plot.addAnnotation(line4);
					sqResiduals.add(line4);
				}
			}
			else
			{
				XYPlot plot = graph.getChart().getXYPlot();
				for (int i = 0; i < sqResiduals.size(); i++)
				{
					plot.removeAnnotation(sqResiduals.get(i));
				}
			}
		}
		else if (arg0.getItem() == showR)
		{
			if (arg0.getStateChange() == ItemEvent.SELECTED)
			{
				BigDecimal r = new BigDecimal(dataHandler.getR());
				r = r.setScale(2, RoundingMode.HALF_UP);
				rLabel.setText("Correlation coefficient = " + r.doubleValue());
			}
			else
				rLabel.setText(" ");
		}
		else if (arg0.getItem() == showRSquared)
		{
			if (arg0.getStateChange() == ItemEvent.SELECTED)
			{
				BigDecimal r2 = new BigDecimal(dataHandler.getRSquare());
				r2 = r2.setScale(2, RoundingMode.HALF_UP);
				rSquaredLabel.setText
						("Coefficient of determination = " + r2.doubleValue());
			}
			else
				rSquaredLabel.setText(" ");
		}
		else if (arg0.getItem() == showSST)
		{
			if (arg0.getStateChange() == ItemEvent.SELECTED)
			{
				BigDecimal sst = new BigDecimal(dataHandler.getTotalSumSquares());
				sst = sst.setScale(2, RoundingMode.HALF_UP);
				SSTLabel.setText("SST = " + sst.doubleValue());
			}
			else
				SSTLabel.setText(" ");
		}
		else if (arg0.getItem() == showSSR)
		{
			if (arg0.getStateChange() == ItemEvent.SELECTED)
			{
				BigDecimal ssr = new BigDecimal(dataHandler.getRegressionSumSquares());
				ssr = ssr.setScale(2, RoundingMode.HALF_UP);
				SSRLabel.setText("SSR = " + ssr.doubleValue());
			}
			else
				SSRLabel.setText(" ");
		}
		else if (arg0.getItem() == showSSE)
		{
			if (arg0.getStateChange() == ItemEvent.SELECTED)
			{
				BigDecimal sse = new BigDecimal(dataHandler.getSumSquaredErrors());
				sse = sse.setScale(2, RoundingMode.HALF_UP);
				SSELabel.setText("SSE = " + sse.doubleValue());
			}
			else
				SSELabel.setText(" ");
		}
	}
	
}
