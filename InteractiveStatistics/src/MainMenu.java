import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainMenu extends JPanel implements MouseListener
{
	
	private JLabel background;
	private JLabel introduction;
	private JLabel chanceProb1;
	private JLabel chanceProb2;
	private JLabel confInterval;
	private JLabel correlationCoef;
	private JLabel descriptiveStats;
	private JLabel lsRegression;
	private JLabel samplingDist;
	
	private BufferedImage backgroundImage;
	private BufferedImage buttonImage;
	private InteractiveStatistics main;
	
	public MainMenu(InteractiveStatistics main)
	{
		readImages();
		initializeComponents();
		setUpStructure();
		this.main = main;
	}
	
	public void readImages()
	{
		try
		{
			backgroundImage = ImageIO.read(new File("images/MainMenu.png"));
			buttonImage = ImageIO.read(new File("images/MainMenuButton.png"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void initializeComponents()
	{
		background = new JLabel(new ImageIcon(backgroundImage));
		Dimension size = background.getPreferredSize();
		background.setBounds(0, 0, size.width, size.height);
		
		introduction = new JLabel("Please select a topic:");
		introduction.setBounds(1000, 30, 400, 30);
		introduction.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
		
		chanceProb1 = new JLabel(new ImageIcon(buttonImage));
		size = chanceProb1.getPreferredSize();
		chanceProb1.setBounds(1000, 75, size.width, size.height);
		chanceProb1.addMouseListener(this);
		chanceProb1.setText("Chance & Probability 1");
		chanceProb1.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
		chanceProb1.setHorizontalTextPosition(JLabel.CENTER);
		chanceProb1.setVerticalTextPosition(JLabel.CENTER);
		
		chanceProb2 = new JLabel(new ImageIcon(buttonImage));
		size = chanceProb2.getPreferredSize();
		chanceProb2.setBounds(1000, 170, size.width, size.height);
		chanceProb2.addMouseListener(this);
		chanceProb2.setText("Chance & Probability 2");
		chanceProb2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
		chanceProb2.setHorizontalTextPosition(JLabel.CENTER);
		chanceProb2.setVerticalTextPosition(JLabel.CENTER);
		
		confInterval = new JLabel(new ImageIcon(buttonImage));
		size = confInterval.getPreferredSize();
		confInterval.setBounds(1000, 265, size.width, size.height);
		confInterval.addMouseListener(this);
		confInterval.setText("Confidence Interval");
		confInterval.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
		confInterval.setHorizontalTextPosition(JLabel.CENTER);
		confInterval.setVerticalTextPosition(JLabel.CENTER);
		
		correlationCoef = new JLabel(new ImageIcon(buttonImage));
		size = correlationCoef.getPreferredSize();
		correlationCoef.setBounds(1000, 360, size.width, size.height);
		correlationCoef.addMouseListener(this);
		correlationCoef.setText("Correlation Coefficient");
		correlationCoef.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
		correlationCoef.setHorizontalTextPosition(JLabel.CENTER);
		correlationCoef.setVerticalTextPosition(JLabel.CENTER);
		
		descriptiveStats = new JLabel(new ImageIcon(buttonImage));
		size = descriptiveStats.getPreferredSize();
		descriptiveStats.setBounds(1000, 455, size.width, size.height);
		descriptiveStats.addMouseListener(this);
		descriptiveStats.setText("Descriptive Statistics");
		descriptiveStats.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
		descriptiveStats.setHorizontalTextPosition(JLabel.CENTER);
		descriptiveStats.setVerticalTextPosition(JLabel.CENTER);
		
		lsRegression = new JLabel(new ImageIcon(buttonImage));
		size = lsRegression.getPreferredSize();
		lsRegression.setBounds(1000, 550, size.width, size.height);
		lsRegression.addMouseListener(this);
		lsRegression.setText("Least-Squares Regression");
		lsRegression.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
		lsRegression.setHorizontalTextPosition(JLabel.CENTER);
		lsRegression.setVerticalTextPosition(JLabel.CENTER);
		
		samplingDist = new JLabel(new ImageIcon(buttonImage));
		size = samplingDist.getPreferredSize();
		samplingDist.setBounds(1000, 645, size.width, size.height);
		samplingDist.addMouseListener(this);
		samplingDist.setText("Sampling Distribution");
		samplingDist.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
		samplingDist.setHorizontalTextPosition(JLabel.CENTER);
		samplingDist.setVerticalTextPosition(JLabel.CENTER);
	}
	
	public void setUpStructure()
	{
		setLayout(null);
		add(background);
		add(introduction);
		add(chanceProb1);
		add(chanceProb2);
		add(confInterval);
		add(correlationCoef);
		add(descriptiveStats);
		add(lsRegression);
		add(samplingDist);
	}

	public void mouseClicked(MouseEvent arg0)
	{
		if (arg0.getSource() == chanceProb1)
		{
			ChanceProbability1 cp1 = new ChanceProbability1(main);
			main.remove(this);
			main.add(cp1);
			main.setVisible(true);
		}
		else if (arg0.getSource() == chanceProb2)
		{
			ChanceProbability2 cp2 = new ChanceProbability2(main);
			main.remove(this);
			main.add(cp2);
			main.setVisible(true);
		}
		else if (arg0.getSource() == confInterval)
		{
			ConfidenceInterval ci = new ConfidenceInterval(main);
			main.remove(this);
			main.add(ci);
			main.setVisible(true);
		}
		else if (arg0.getSource() == correlationCoef)
		{
			CorrelationCoefficient cc = new CorrelationCoefficient(main);
			main.remove(this);
			main.add(cc);
			main.setVisible(true);
		}
		else if (arg0.getSource() == descriptiveStats)
		{
			DescriptiveStatistic ds = new DescriptiveStatistic(main);
			main.remove(this);
			main.add(ds);
			main.setVisible(true);
		}
		else if (arg0.getSource() == lsRegression)
		{
			LSRegression lsr = new LSRegression(main);
			main.remove(this);
			main.add(lsr);
			main.setVisible(true);
		}
		else if (arg0.getSource() == samplingDist)
		{
			SamplingDistribution sd = new SamplingDistribution(main);
			main.remove(this);
			main.add(sd);
			main.setVisible(true);
		}
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
	}

	public void mouseReleased(MouseEvent e)
	{
	}

}
