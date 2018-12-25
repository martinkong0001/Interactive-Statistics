import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ChanceProbability1 extends JPanel implements ActionListener, ItemListener
{
	
	private JLabel numRowsLb, numDiceLb;
	private JComboBox<Integer> numRowsCb, numDiceCb;
	private JCheckBox showMean;
	private JButton rollButton, noAnimationButton;
	private JButton resetButton, goBackButton;
	private RollingDice rollDiceTable;
	private ChartPanel graph;
	private ValueMarker meanMarker;
	
	private int numRows, numDice;
	private int totalRows, totalValue;
	private InteractiveStatistics main;
	private static final Font DEFAULT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 20);
	
	public ChanceProbability1(InteractiveStatistics main)
	{
		numRows = 1;
		numDice = 1;
		this.main = main;
		initializeComponents();
		setUpStructure();
		for (Component component : getComponents())
			component.setFont(DEFAULT_FONT);
	}
	
	public void initializeComponents()
	{
		numRowsLb = new JLabel("Number of times to roll the dice: ");
		numRowsCb = new JComboBox<Integer>();
		for (int i = 1; i <= 20; i++)
			numRowsCb.addItem(i);
		numRowsCb.addActionListener(this);
		
		numDiceLb = new JLabel("Number of dice to be rolled: ");
		numDiceCb = new JComboBox<Integer>();
		for (int i = 1; i <= 10; i++)
			numDiceCb.addItem(i);
		numDiceCb.addActionListener(this);
		
		showMean = new JCheckBox("Show expected value of the random variable");
		showMean.addItemListener(this);
		
		rollButton = new JButton("Roll the dice!");
		rollButton.addActionListener(this);
		noAnimationButton = new JButton("Roll the dice (no animation)");
		noAnimationButton.addActionListener(this);
		
		resetButton = new JButton("Reset");
		resetButton.addActionListener(this);
		goBackButton = new JButton("Go back to previous page");
		goBackButton.addActionListener(this);
		
		rollDiceTable = new RollingDice(this);
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
					.addGroup(layout.createSequentialGroup()
						.addComponent(numRowsLb)
						.addComponent(numRowsCb, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGroup(layout.createSequentialGroup()
						.addComponent(numDiceLb)
						.addComponent(numDiceCb, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addComponent(showMean)
					.addComponent(rollButton)
					.addComponent(noAnimationButton)
					.addComponent(resetButton)
					.addComponent(goBackButton)
					.addComponent(rollDiceTable, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				)
				.addComponent(graph)
		);
		layout.linkSize(SwingConstants.HORIZONTAL, rollButton, noAnimationButton,
				resetButton, goBackButton);
		
		layout.setVerticalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(graph)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(numRowsLb)
						.addComponent(numRowsCb))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(numDiceLb)
						.addComponent(numDiceCb))
					.addComponent(showMean)
					.addComponent(rollButton)
					.addComponent(noAnimationButton)
					.addComponent(resetButton)
					.addComponent(goBackButton)
					.addComponent(rollDiceTable, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				)
		);
	}
	
	public void makeGraph()
	{
		XYSeries average = new XYSeries("Average");
		XYSeriesCollection dataset = new XYSeriesCollection(average);
		JFreeChart lineChart = ChartFactory.createXYLineChart
				("An Illustration of the Law of Large Numbers", "Number of Rolls",
				"Total Number of Dots on All Dice (Average)", dataset);
		lineChart.removeLegend();
		XYPlot plot = lineChart.getXYPlot();
		NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
		xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		xAxis.setLowerBound(0);
		xAxis.setAutoRange(true);
		NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
		yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		yAxis.setLowerBound(0.5);
		yAxis.setUpperBound(6.5);
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
		renderer.setBaseShapesVisible(true);
		graph = new ChartPanel(lineChart);
		graph.setDomainZoomable(false);
		graph.setRangeZoomable(false);
	}
	
	public void rollDice()
	{
		numRowsCb.setEnabled(false);
		numDiceCb.setEnabled(false);
		rollButton.setEnabled(false);
		rollDiceTable.setNumDice(numDice);
		rollDiceTable.rollDice(numRows);
	}
	
	public void oneRollFinished()
	{
		totalValue += rollDiceTable.getTotalNumDots();
		totalRows ++;
		double newAverage = (double)totalValue / totalRows;
		XYSeriesCollection dataset = (XYSeriesCollection)
				graph.getChart().getXYPlot().getDataset();
		dataset.getSeries(0).add(totalRows, newAverage);
	}
	
	public void allRollsFinished()
	{
		numRowsCb.setEnabled(true);
		numDiceCb.setEnabled(true);
		rollButton.setEnabled(true);
	}
	
	public void rollDiceNoAnimation()
	{
		XYSeriesCollection dataset = (XYSeriesCollection)
				graph.getChart().getXYPlot().getDataset();
		for (int i = 0; i < numRows; i++)
		{
			for (int j = 0; j < numDice; j++)
			{
				totalValue += ThreadLocalRandom.current().nextInt(1, 7);
			}
			totalRows ++;
			double newAverage = (double)totalValue / totalRows;
			dataset.getSeries(0).add(totalRows, newAverage);
		}
	}
	
	public void numDiceChanged()
	{
		totalRows = 0;
		totalValue = 0;
		showMean.setSelected(false);
		XYPlot plot = graph.getChart().getXYPlot();
		XYSeries average = new XYSeries("Average");
		XYSeriesCollection dataset = new XYSeriesCollection(average);
		plot.setDataset(dataset);
		NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
		yAxis.setLowerBound(1 * numDice - 0.5);
		yAxis.setUpperBound(6 * numDice + 0.5);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == rollButton)
		{
			rollDice();
		}
		else if (e.getSource() == noAnimationButton)
		{
			rollDiceNoAnimation();
		}
		else if (e.getSource() == resetButton)
		{
			ChanceProbability1 newPanel = new ChanceProbability1(main);
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
		else if (e.getSource() == numRowsCb)
		{
			numRows = (int) (numRowsCb.getSelectedItem());
		}
		else if (e.getSource() == numDiceCb)
		{
			numDice = (int) (numDiceCb.getSelectedItem());
			numDiceChanged();
		}
	}

	public void itemStateChanged(ItemEvent arg0)
	{
		if (arg0.getItem() == showMean)
		{
			if (arg0.getStateChange() == ItemEvent.SELECTED)
			{
				meanMarker = new ValueMarker(3.5 * numDice, Color.BLACK, new BasicStroke(1));
				graph.getChart().getXYPlot().addRangeMarker(meanMarker);
			}
			else
			{
				graph.getChart().getXYPlot().removeRangeMarker(meanMarker);
			}
		}
	}

}
