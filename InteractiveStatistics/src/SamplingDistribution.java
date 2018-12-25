import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.statistics.HistogramDataset;

public class SamplingDistribution extends JPanel implements ActionListener
{
	
	private JLabel popuPropLb, sampleSizeLb, numSamplesLb;
	private JTextField popuPropTf, sampleSizeTf, numSamplesTf;
	private JButton takeSamplesButton, noAnimationButton;
	private JButton restartButton, goBackButton;
	private GumballMachine gumballMachine;
	private JLabel mostRecentSample, numBluesLb, propBluesLb;
	private ChartPanel graph;
	private ArrayList<Double> sampleProportions;
	private InteractiveStatistics main;
	private static final Font DEFAULT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 20);
	
	public SamplingDistribution(InteractiveStatistics main)
	{
		sampleProportions = new ArrayList<Double>();
		this.main = main;
		initializeComponents();
		setUpStructure();
		for (Component component : getComponents())
			component.setFont(DEFAULT_FONT);
	}
	
	public void initializeComponents()
	{
		popuPropLb = new JLabel("Proportion of blue balls in the gumball machine:");
		popuPropTf = new JTextField("0.5");
		sampleSizeLb = new JLabel("Number of gumballs in one sample:");
		sampleSizeTf = new JTextField("10");
		numSamplesLb = new JLabel("Number of simple random samples to be taken:");
		numSamplesTf = new JTextField("1");
		
		takeSamplesButton = new JButton("Take simple random samples!");
		takeSamplesButton.addActionListener(this);
		noAnimationButton = new JButton("Take simple random samples (no animation)");
		noAnimationButton.addActionListener(this);
		
		restartButton = new JButton("Restart the simulation");
		restartButton.addActionListener(this);
		goBackButton = new JButton("Go back to previous page");
		goBackButton.addActionListener(this);
		
		gumballMachine = new GumballMachine(this);
		mostRecentSample = new JLabel(" ");
		numBluesLb = new JLabel(" ");
		propBluesLb = new JLabel(" ");
		
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
					.addComponent(popuPropLb)
					.addComponent(popuPropTf)
					.addComponent(sampleSizeLb)
					.addComponent(sampleSizeTf)
					.addComponent(numSamplesLb)
					.addComponent(numSamplesTf)
					.addComponent(takeSamplesButton)
					.addComponent(noAnimationButton)
					.addComponent(restartButton)
					.addComponent(goBackButton)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(gumballMachine, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(mostRecentSample)
					.addComponent(numBluesLb)
					.addComponent(propBluesLb)
				)
				.addComponent(graph)
		);
		layout.linkSize(SwingConstants.HORIZONTAL,
				takeSamplesButton, noAnimationButton, restartButton, goBackButton);
		
		layout.setVerticalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(graph)
				.addGroup(layout.createSequentialGroup()
					.addComponent(gumballMachine, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(mostRecentSample)
					.addComponent(numBluesLb)
					.addComponent(propBluesLb)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(popuPropLb)
					.addComponent(popuPropTf)
					.addComponent(sampleSizeLb)
					.addComponent(sampleSizeTf)
					.addComponent(numSamplesLb)
					.addComponent(numSamplesTf)
					.addComponent(takeSamplesButton)
					.addComponent(noAnimationButton)
					.addComponent(restartButton)
					.addComponent(goBackButton)
				)
		);
	}
	
	public void makeGraph()
	{
		HistogramDataset dataset = new HistogramDataset();
		JFreeChart histogram = ChartFactory.createHistogram
				("Distribution of Sample Proportion",
				"Proportion of Blue Gumballs in One Sample", "Test", dataset,
				PlotOrientation.VERTICAL, false, false, false);
		histogram.removeLegend();
		ValueAxis domainAxis = histogram.getXYPlot().getDomainAxis();
		domainAxis.setAutoRange(true);
		ValueAxis rangeAxis = histogram.getXYPlot().getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		rangeAxis.setLowerBound(0);
		rangeAxis.setAutoRange(true);
		rangeAxis.setVisible(false);
		graph = new ChartPanel(histogram);
		graph.setDomainZoomable(false);
		graph.setRangeZoomable(false);
	}
	
	public void updateGraph()
	{
		double[] sampleProp = new double[sampleProportions.size()];
		for (int i = 0; i < sampleProportions.size(); i++)
			sampleProp[i] = sampleProportions.get(i);
		HistogramDataset dataset = new HistogramDataset();
		dataset.addSeries("Test", sampleProp, 20);
		XYPlot plot = (XYPlot)graph.getChart().getPlot();
		plot.setDataset(dataset);
	}
	
	public boolean verifyInputValidity()
	{
		JLabel errorMessage = new JLabel();
		try
		{
			Double popuProp = Double.parseDouble(popuPropTf.getText());
			if (popuProp < 0 || popuProp > 1)
				errorMessage.setText("Please enter a number between 0 and 1 for the "
						+ "\"proportion of blue balls in the gumball machine\".");
			Double sampleSize = Double.parseDouble(sampleSizeTf.getText());
			if (sampleSize % 1 != 0 || sampleSize < 0)
				errorMessage.setText("Please enter a positive integer for the "
						+ "\"number of gumballs in one sample\".");
			Double numSamples = Double.parseDouble(numSamplesTf.getText());
			if (numSamples % 1 != 0 || numSamples < 0)
				errorMessage.setText("Please enter a positive integer for the "
						+ "\"number of simple random samples to be taken\".");
		}
		catch (NumberFormatException ex)
		{
			errorMessage.setText("Please enter a valid number.");
		}
		if (errorMessage.getText().equals(""))
		{
			return true;
		}
		else
		{
			errorMessage.setFont(DEFAULT_FONT);
			JOptionPane.showMessageDialog
					(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
	public void takeSamples()
	{
		popuPropTf.setEditable(false);
		sampleSizeTf.setEditable(false);
		numSamplesTf.setEditable(false);
		takeSamplesButton.setEnabled(false);
		noAnimationButton.setEnabled(false);
		Double popuProp = Double.parseDouble(popuPropTf.getText());
		int sampleSize = Integer.parseInt(sampleSizeTf.getText());
		int numSamples = Integer.parseInt(numSamplesTf.getText());
		gumballMachine.spinWheel(popuProp, sampleSize, numSamples);
	}
	
	
	public void oneSampleTaken()
	{
		sampleProportions.add(gumballMachine.getSampleProportion());
		updateGraph();
	}
	
	public void allSamplesTaken()
	{
		popuPropTf.setEditable(true);
		sampleSizeTf.setEditable(true);
		numSamplesTf.setEditable(true);
		takeSamplesButton.setEnabled(true);
		noAnimationButton.setEnabled(true);
	}
	
	public void updateTextField()
	{
		mostRecentSample.setText("In the most recent sample: ");
		numBluesLb.setText("Number of blue balls: "
				+ gumballMachine.getSampleFrequency());
		propBluesLb.setText("Proportion of blue balls: "
				+ gumballMachine.getSampleProportion());
	}
	
	public void sampleWithoutAnimation()
	{
		Double popuProp = Double.parseDouble(popuPropTf.getText());
		int sampleSize = Integer.parseInt(sampleSizeTf.getText());
		int numSamples = Integer.parseInt(numSamplesTf.getText());
		for (int i = 0; i < numSamples; i++)
		{
			int countSuccess = 0;
			for (int j = 0; j < sampleSize; j++)
			{
				if (Math.random() < popuProp)
					countSuccess ++;
			}
			sampleProportions.add((double)countSuccess / sampleSize);
		}
		updateGraph();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == takeSamplesButton)
		{
			if (verifyInputValidity() == true)
			{
				takeSamples();
			}
		}
		else if (e.getSource() == noAnimationButton)
		{
			if (verifyInputValidity() == true)
			{
				sampleWithoutAnimation();
			}
		}
		else if (e.getSource() == restartButton)
		{
			SamplingDistribution newPanel = new SamplingDistribution(main);
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

}
