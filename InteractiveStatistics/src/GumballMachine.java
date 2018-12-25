import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GumballMachine extends JPanel
{
	
	private boolean isSpinningWheel;
	private int wheelOrientation;
	private boolean isFiringBall;
	private int ballColor;
	private int ballPosition;
	private int numBallsNeeded;
	private int numBallsObtained;
	private int numBluesObtained;
	private double populationProp;
	
	private BufferedImage machine;
	private BufferedImage wheel;
	private BufferedImage[] balls;
	private SamplingDistribution mainControl;
	
	public GumballMachine(SamplingDistribution mainControl)
	{
		try
		{
			machine = ImageIO.read(new File("images/GumballMachine.png"));
			wheel = ImageIO.read(new File("images/GumballWheel.png"));
			balls = new BufferedImage[6];
			balls[0] = ImageIO.read(new File("images/Gumball1.png"));
			balls[1] = ImageIO.read(new File("images/Gumball2.png"));
			balls[2] = ImageIO.read(new File("images/Gumball3.png"));
			balls[3] = ImageIO.read(new File("images/Gumball4.png"));
			balls[4] = ImageIO.read(new File("images/Gumball5.png"));
			balls[5] = ImageIO.read(new File("images/Gumball6.png"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		setPreferredSize(new Dimension(400, 600));
		this.mainControl = mainControl;
	}
	
	public void spinWheel(double popuProp, int sampleSize, int numSamples)
	{
		populationProp = popuProp;
		numBallsNeeded = sampleSize;
		spinWheel(numSamples);
	}
	
	private void spinWheel(int numSamples)
	{
		if (numSamples == 0)
		{
			mainControl.allSamplesTaken();
			return;
		}
		isSpinningWheel = true;
		wheelOrientation = 0;
		numBallsObtained = 0;
		numBluesObtained = 0;
		mainControl.updateTextField();
		Thread animation = new Thread()
		{
			public void run()
			{
				try
				{
					while (isSpinningWheel || isFiringBall)
					{
						sleep(20);
						repaint();
					}
					mainControl.oneSampleTaken();
					Thread.sleep(1500);
					spinWheel(numSamples - 1);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};
		animation.start();
	}
	
	private void fireGumball()
	{
		isFiringBall = true;
		ballPosition = 0;
		double random = Math.random();
		double probOthers = (1 - populationProp) / 5;
		if (random < probOthers)
			ballColor = 1;
		else if (random < probOthers * 2)
			ballColor = 2;
		else if (random < probOthers * 3)
			ballColor = 3;
		else if (random < probOthers * 4)
			ballColor = 4;
		else if (random < probOthers * 5)
			ballColor = 5;
		else
		{
			ballColor = 0;
			numBluesObtained ++;
		}
		numBallsObtained ++;
		mainControl.updateTextField();
	}

	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(machine, 0, 0, this);
		AffineTransform at = new AffineTransform();
		at.translate(160, 320);
		at.rotate(Math.toRadians(wheelOrientation), 35, 22);
		g2.drawImage(wheel, at, this);
		if (isSpinningWheel == true)
		{
			wheelOrientation = (wheelOrientation + 10) % 360;
			if (wheelOrientation % 360 == 0)
			{
				fireGumball();
				if (numBallsObtained >= numBallsNeeded)
					isSpinningWheel = false;
			}
		}
		if (isFiringBall == true)
		{
			ballPosition += 5;
			g2.drawImage(balls[ballColor], 170, 400 + ballPosition, this);
			if (ballPosition >= 150)
				isFiringBall = false;
		}
	}
	
	public int getSampleFrequency()
	{
		return numBluesObtained;
	}
	
	public double getSampleProportion()
	{
		if (numBallsObtained == 0)
			return 0;
		double proportion = (double)numBluesObtained / numBallsObtained;
		BigDecimal unrounded = new BigDecimal(proportion);
		BigDecimal rounded = unrounded.setScale(2, RoundingMode.HALF_UP);
		return rounded.doubleValue();
	}

}
