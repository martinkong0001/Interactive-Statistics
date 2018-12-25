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
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class CorrelationCoefficient extends JPanel implements ActionListener, ItemListener
{
	
	private JTextArea dataTextArea;
	private JScrollPane scrollTextArea;
	private JButton generateButton;
	private JButton plotButton;
	private ChartPanel graph;
	private XYLineAnnotation bestFitLine;
	private JLabel guessLabel;
	private JTextField userGuess;
	private JButton checkButton;
	private JLabel answerLabel;
	private JLabel congratsLabel;
	private JCheckBox showLine;
	private JLabel lineEquation;
	private JButton goBackButton;
	
	private ArrayList<Double> xValues;
	private ArrayList<Double> yValues;
	private SimpleRegression dataHandler;
	private static final int DEFAULT_SAMPLE_SIZE = 20;
	private static final Font DEFAULT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 20);
	private static final Stroke DEFAULT_STROKE = new BasicStroke(4);
	private static final Color DEFAULT_COLOR = Color.BLUE;
	private InteractiveStatistics main;
	
	public CorrelationCoefficient(InteractiveStatistics main)
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
		
		guessLabel = new JLabel("Guess the correlation coefficient:");
		userGuess = new JTextField();
		checkButton = new JButton("Check your guess");
		checkButton.addActionListener(this);
		answerLabel = new JLabel(" ");
		congratsLabel = new JLabel(" ");
		showLine = new JCheckBox("Display the line of best fit");
		showLine.addItemListener(this);
		lineEquation = new JLabel(" ");
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
					.addComponent(guessLabel)
					.addComponent(userGuess)
					.addComponent(checkButton)
					.addComponent(answerLabel)
					.addComponent(congratsLabel)
					.addComponent(showLine)
					.addComponent(lineEquation)
					.addComponent(goBackButton)
				)
		);
		layout.linkSize(SwingConstants.HORIZONTAL, generateButton, plotButton);
		
		layout.setVerticalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addComponent(guessLabel)
					.addComponent(userGuess)
					.addComponent(checkButton)
					.addComponent(answerLabel)
					.addComponent(congratsLabel)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
							GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(showLine)
					.addComponent(lineEquation)
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
			userGuess.setText("");
			answerLabel.setText(" ");
			congratsLabel.setText(" ");
			showLine.setSelected(false);
		}
		else if (e.getSource() == checkButton)
		{
			try
			{
				Double guess = Double.parseDouble(userGuess.getText());
				BigDecimal rDecimal = new BigDecimal(dataHandler.getR());
				rDecimal = rDecimal.setScale(2, RoundingMode.HALF_UP);
				double r = rDecimal.doubleValue();
				answerLabel.setText("Correlation coefficient r = " + r);
				if (Math.abs(r - guess) < 0.02)
					congratsLabel.setText("Wow! You are a genius!");
				else if (Math.abs(r - guess) < 0.1)
					congratsLabel.setText("Good job! That was very close!");
				else if (Math.abs(r - guess) < 0.3)
					congratsLabel.setText("That was a good guess!");
				else
					congratsLabel.setText("Nice try!");
			}
			catch (NumberFormatException ex)
			{
				JLabel error = new JLabel("Please enter a valid number.");
				error.setFont(DEFAULT_FONT);
				JOptionPane.showMessageDialog
						(this, error, "Error", JOptionPane.ERROR_MESSAGE);
			}
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
			}
			else
			{
				lineEquation.setText(" ");
				graph.getChart().getXYPlot().removeAnnotation(bestFitLine);
			}
		}
	}
	
}
