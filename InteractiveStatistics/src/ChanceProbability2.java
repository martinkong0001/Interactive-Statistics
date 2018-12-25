import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ChanceProbability2 extends JPanel implements ActionListener
{
	
	private JLabel introPlaceBets;
	private JLabel redBlackLb, redLb, blackLb;
	private JTextField redTf, blackTf;
	private JLabel oddEvenLb, oddLb, evenLb;
	private JTextField oddTf, evenTf;
	private JLabel lowHighLb, lowLb, highLb;
	private JTextField lowTf, highTf;
	private JLabel numberLb;
	private JComboBox<String> numberCb;
	private JTextField numberTf;
	private JButton spinWheelButton, noAnimationButton;
	private JButton restartButton, goBackButton;
	private ArrayList<JTextField> textFields;
	private ArrayList<JButton> buttons;
	private SpinningWheel wheel;
	private JLabel roundLb, betLb, lossLb, winLb, balanceLb;
	private ChartPanel graph;
	private JLabel empty1, empty2, empty3;
	
	private int bet, loss, win;
	private int numRounds, balance;
	private InteractiveStatistics main;
	private static final Font DEFAULT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 20);
	
	public ChanceProbability2(InteractiveStatistics main)
	{
		bet = 0; loss = 0; win = 0;
		numRounds = 0; balance = 5000;
		this.main = main;
		initializeComponents();
		setUpStructure();
		for (Component component : getComponents())
			component.setFont(DEFAULT_FONT);
	}
	
	public void initializeComponents()
	{
		introPlaceBets = new JLabel("Place bets on:");
		
		redBlackLb = new JLabel("Red or black (payout 1 to 1):");
		redLb = new JLabel("Red");
		blackLb = new JLabel("Black");
		redTf = new JTextField();
		blackTf = new JTextField();
		
		oddEvenLb = new JLabel("Odd or even (payout 1 to 1):");
		oddLb = new JLabel("Odd");
		evenLb = new JLabel("Even");
		oddTf = new JTextField();
		evenTf = new JTextField();
		
		lowHighLb = new JLabel("Low or high (payout 1 to 1):");
		lowLb = new JLabel("Low");
		highLb = new JLabel("High");
		lowTf = new JTextField();
		highTf = new JTextField();
		
		numberLb = new JLabel("A single number (payout 35 to 1):");
		numberCb = new JComboBox<String>();
		for (int i = 1; i <= 36; i++)
			numberCb.addItem(i + "");
		numberCb.addItem("0");
		numberCb.addItem("00");
		numberTf = new JTextField();
		
		empty1 = new JLabel(" ");
		spinWheelButton = new JButton("Spin the wheel!");
		spinWheelButton.addActionListener(this);
		noAnimationButton = new JButton("Spin the wheel (no animation)");
		noAnimationButton.addActionListener(this);
		
		empty2 = new JLabel(" ");
		restartButton = new JButton("Restart the simulation");
		restartButton.addActionListener(this);
		goBackButton = new JButton("Go back to previous page");
		goBackButton.addActionListener(this);
		
		textFields = new ArrayList<JTextField>();
		textFields.add(redTf);
		textFields.add(blackTf);
		textFields.add(oddTf);
		textFields.add(evenTf);
		textFields.add(lowTf);
		textFields.add(highTf);
		textFields.add(numberTf);
		for (JTextField textField : textFields)
			textField.setText("0");
		
		buttons = new ArrayList<JButton>();
		buttons.add(spinWheelButton);
		buttons.add(noAnimationButton);
		
		makeGraph();
		
		wheel = new SpinningWheel();

		empty3 = new JLabel(" ");
		roundLb = new JLabel("Round No. " + numRounds);
		betLb = new JLabel("Bet Amount: " + bet);
		lossLb = new JLabel("Loss: " + loss);
		winLb = new JLabel("Win: " + win);
		balanceLb = new JLabel("Balance: " + balance);
	}
	
	public void makeGraph()
	{
		XYSeries myBalance = new XYSeries("Balance");
		myBalance.add(numRounds, balance);
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(myBalance);
		JFreeChart lineChart = ChartFactory.createXYLineChart
				("Can you win money in the long run?", "Number of Rounds", "Balance", dataset);
		lineChart.removeLegend();
		NumberAxis yAxis = (NumberAxis) lineChart.getXYPlot().getRangeAxis();
		yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		yAxis.setLowerBound(0);
		yAxis.setAutoRange(true);
		NumberAxis xAxis = (NumberAxis) lineChart.getXYPlot().getDomainAxis();
		xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		xAxis.setLowerBound(0);
		xAxis.setAutoRange(true);
		graph = new ChartPanel(lineChart);
		graph.setDomainZoomable(false);
		graph.setRangeZoomable(false);
	}
	
	public void setUpStructure()
	{
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(introPlaceBets)
					.addComponent(redBlackLb)
					.addGroup(layout.createSequentialGroup()
						.addComponent(redLb)
						.addComponent(redTf))
					.addGroup(layout.createSequentialGroup()
						.addComponent(blackLb)
						.addComponent(blackTf))
					.addComponent(oddEvenLb)
					.addGroup(layout.createSequentialGroup()
						.addComponent(oddLb)
						.addComponent(oddTf))
					.addGroup(layout.createSequentialGroup()
						.addComponent(evenLb)
						.addComponent(evenTf))
					.addComponent(lowHighLb)
					.addGroup(layout.createSequentialGroup()
						.addComponent(lowLb)
						.addComponent(lowTf))
					.addGroup(layout.createSequentialGroup()
						.addComponent(highLb)
						.addComponent(highTf))
					.addComponent(numberLb)
					.addGroup(layout.createSequentialGroup()
						.addComponent(numberCb)
						.addComponent(numberTf))
					.addComponent(empty1)
					.addComponent(spinWheelButton)
					.addComponent(noAnimationButton)
					.addComponent(empty2)
					.addComponent(restartButton)
					.addComponent(goBackButton)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(wheel, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(empty3)
					.addComponent(roundLb)
					.addComponent(betLb)
					.addComponent(lossLb)
					.addComponent(winLb)
					.addComponent(balanceLb)
				)
				.addComponent(graph)
		);
		layout.linkSize(SwingConstants.HORIZONTAL,
				restartButton, goBackButton, spinWheelButton, noAnimationButton);
		
		layout.setVerticalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(graph)
				.addGroup(layout.createSequentialGroup()
					.addComponent(wheel, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(empty3)
					.addComponent(roundLb)
					.addComponent(betLb)
					.addComponent(lossLb)
					.addComponent(winLb)
					.addComponent(balanceLb)
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(introPlaceBets)
					.addComponent(redBlackLb)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(redLb)
						.addComponent(redTf))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(blackLb)
						.addComponent(blackTf))
					.addComponent(oddEvenLb)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(oddLb)
						.addComponent(oddTf))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(evenLb)
						.addComponent(evenTf))
					.addComponent(lowHighLb)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lowLb)
						.addComponent(lowTf))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(highLb)
						.addComponent(highTf))
					.addComponent(numberLb)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(numberCb)
						.addComponent(numberTf))
					.addComponent(empty1)
					.addComponent(spinWheelButton)
					.addComponent(noAnimationButton)
					.addComponent(empty2)
					.addComponent(restartButton)
					.addComponent(goBackButton)
				)
		);
	}
	
	public boolean verifyInputValidity()
	{
		JLabel errorMessage = new JLabel();
		try
		{
			bet = 0;
			for (JTextField textField : textFields)
			{
				double num = Double.parseDouble(textField.getText());
				if (num % 1 != 0 || num < 0)
					errorMessage.setText("Please enter a positive integer.");
				bet += num;
			}
			if (bet > balance)
				errorMessage.setText("You do not have that much money.");
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
	
	public void spinRouletteWheel()
	{
		for (JTextField textField : textFields)
			textField.setEditable(false);
		for (JButton button : buttons)
			button.setEnabled(false);
		numberCb.setEnabled(false);
		wheel.spinWheel();
		java.util.Timer timer = new java.util.Timer();
		timer.schedule(new TimerTask()
		{
			public void run()
			{
				calculateBetResult();
				updateTextLabels();
				updateGraph();
				for (JTextField textField : textFields)
					textField.setEditable(true);
				for (JButton button : buttons)
					button.setEnabled(true);
				numberCb.setEnabled(true);
				timer.cancel();
			}
		}, (long)(SpinningWheel.animationDuration + 1000));
	}
	
	public void calculateBetResult()
	{
		loss = 0; win = 0;
		int num = wheel.getNumber();
		String color = wheel.getColor();
		int thisBet = Integer.parseInt(redTf.getText());
		if (color.equals("red")) win += thisBet;
		else loss += thisBet;
		thisBet = Integer.parseInt(blackTf.getText());
		if (color.equals("black")) win += thisBet;
		else loss += thisBet;
		thisBet = Integer.parseInt(oddTf.getText());
		if (num % 2 == 1 && num != 37) win += thisBet;
		else loss += thisBet;
		thisBet = Integer.parseInt(evenTf.getText());
		if (num % 2 == 0 && num != 38) win += thisBet;
		else loss += thisBet;
		thisBet = Integer.parseInt(lowTf.getText());
		if (num >= 1 && num <= 18) win += thisBet;
		else loss += thisBet;
		thisBet = Integer.parseInt(highTf.getText());
		if (num >= 19 && num <= 36) win += thisBet;
		else loss += thisBet;
		thisBet = Integer.parseInt(numberTf.getText());
		int userChoice = Integer.parseInt((String)numberCb.getSelectedItem());
		if (num == userChoice) win += thisBet * 35;
		else loss += thisBet;
		numRounds ++;
		balance = balance + win - loss;
	}
	
	public void updateTextLabels()
	{
		roundLb.setText("Round No. " + numRounds);
		betLb.setText("Bet Amount: " + bet);
		lossLb.setText("Loss: " + loss);
		winLb.setText("Win: " + win);
		balanceLb.setText("Balance: " + balance);
	}
	
	public void updateGraph()
	{
		XYSeriesCollection dataset = (XYSeriesCollection)
				graph.getChart().getXYPlot().getDataset();
		dataset.getSeries(0).add(numRounds, balance);
		graph.repaint();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == spinWheelButton)
		{
			if (verifyInputValidity() == true)
			{
				spinRouletteWheel();
			}
		}
		else if (e.getSource() == noAnimationButton)
		{
			if (verifyInputValidity() == true)
			{
				wheel.moveBall(ThreadLocalRandom.current().nextInt(0, 38));
				wheel.repaint();
				calculateBetResult();
				updateTextLabels();
				updateGraph();
			}
		}
		else if (e.getSource() == restartButton)
		{
			ChanceProbability2 newPanel = new ChanceProbability2(main);
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
