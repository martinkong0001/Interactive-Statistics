import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class RollingDice extends JPanel
{
	
	private ArrayList<Die> dice;
	public static final int xSize = 500;
	public static final int ySize = 420;
	private ChanceProbability1 mainControl;
	
	public RollingDice(ChanceProbability1 mainControl)
	{
		this.mainControl = mainControl;
		setPreferredSize(new Dimension(xSize, ySize));
		setBackground(Color.CYAN.darker());
	    setBorder(new LineBorder(Color.ORANGE, 3));
	}
	
	public void setNumDice(int numDice)
	{
		dice = new ArrayList<Die>();
		for (int i = 0; i < numDice; i++)
			dice.add(new Die());
	}
	
	public void rollDice(int numTimes)
	{
		if (numTimes == 0)
		{
			mainControl.allRollsFinished();
			return;
		}
		for (Die die : dice)
		{
			die.rollDie();
		}
		Thread animation = new Thread()
		{
			public void run()
			{
				try
				{
					while (isRollingFinished() == false)
					{
						sleep(20);
						checkCollision();
						repaint();
					}
					mainControl.oneRollFinished();
					Thread.sleep(1000);
					rollDice(numTimes - 1);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		};
		animation.start();
	}
	
	public boolean isRollingFinished()
	{
		for (Die die : dice)
			if (die.isRolling() == true)
				return false;
		return true;
	}
	
	public void checkCollision()
	{
		for (Die die1 : dice)
		{
			die1.boundaryCollision();
			for (Die die2 : dice)
			{
				if (die1 != die2)
				{
					die1.diceCollision(die2);
				}
			}
		}
	}
	
	public int getTotalNumDots()
	{
		int count = 0;
		for (Die die : dice)
			count += die.getNumDots();
		return count;
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if (dice != null)
			for (Die die : dice)
				die.drawDie(g);
	}

}

class Die
{
	
	private double xCenter, yCenter;
	private double xSpeed, ySpeed;
	private static final int dieRadius = 20;
	private int numDots;
	
	public Die()
	{
		xCenter = -100;
		yCenter = -100;
	}
	
	public void rollDie()
	{
		xCenter = RollingDice.xSize * Math.random();
	    yCenter = RollingDice.ySize * Math.random();
	    xSpeed = RollingDice.xSize * (Math.random() + 1) * 0.04;
	    ySpeed = RollingDice.ySize * (Math.random() - 0.5) * 0.08;
	    numDots = ThreadLocalRandom.current().nextInt(1, 7);
	}
	
	public void moveDie()
	{
		xCenter += xSpeed;
		yCenter += ySpeed;
		xSpeed *= 0.98;
		ySpeed *= 0.98;
	}
	
	public void boundaryCollision()
	{
		if (xCenter < dieRadius)
		{
			xSpeed = -xSpeed;
			xCenter = dieRadius;
		}
	    if (xCenter > RollingDice.xSize - dieRadius)
	    {
	    	xSpeed = -xSpeed;
	    	xCenter = RollingDice.xSize - dieRadius;
	    }
	    if (yCenter < dieRadius)
	    {
	    	ySpeed = -ySpeed;
	    	yCenter = dieRadius;
	    }
	    if (yCenter > RollingDice.ySize - dieRadius)
	    {
	    	ySpeed = -ySpeed;
	    	yCenter = RollingDice.ySize - dieRadius;
	    }
	}
	
	public void diceCollision(Die other)
	{
		while (Math.abs(xCenter - other.xCenter) < dieRadius &&
				Math.abs(yCenter - other.yCenter) < dieRadius)
		{
			moveDie();
		}
	}
	
	public boolean isExisting()
	{
		if (xCenter < 0 || yCenter < 0)
			return false;
		else
			return true;
	}
	
	public boolean isRolling()
	{
		if (Math.abs(xSpeed) > 2 || Math.abs(ySpeed) > 2)
			return true;
		else
			return false;
	}
	
	public int getNumDots()
	{
		return numDots;
	}
	
	public void drawDie(Graphics g)
	{
		if (isExisting() == false)
		{
			return;
		}
		else if (isRolling() == false)
		{
			drawFixedDie(g);
		}
		else
		{
			moveDie();
			drawRollingDie(g);
		}
	}
	
	public void drawFixedDie(Graphics g)
	{
		int x = (int) (xCenter - dieRadius);
	    int y = (int) (yCenter - dieRadius);
	    g.setColor(Color.RED);
	    g.fillRoundRect(x, y, dieRadius * 2, dieRadius * 2, dieRadius / 2, dieRadius / 2);
	    drawDots(g, x, y, numDots);
	}
	
	public void drawRollingDie(Graphics g)
	{
		int x = (int) (xCenter - dieRadius + Math.random() * 3 - 1);
	    int y = (int) (yCenter - dieRadius + Math.random() * 3 - 1);
	    g.setColor(Color.RED);
	    if (x % 2 != 0)
	      g.fillRoundRect(x, y, dieRadius * 2, dieRadius * 2, dieRadius / 2, dieRadius / 2);
	    else
	      g.fillOval(x - 2, y - 2, dieRadius * 2 + 4, dieRadius * 2 + 4);
	    drawDots(g, x, y, ThreadLocalRandom.current().nextInt(1, 7));
	}
	
	public void drawDots(Graphics g, int x, int y, int numDots)
	{
		int dotSize = dieRadius / 2;
		int dotRadius = dieRadius / 4;
		g.setColor(Color.WHITE);
		if (numDots == 1 || numDots == 3 || numDots == 5)
		{
			g.fillOval(x + dotRadius * 3, y + dotRadius * 3, dotSize, dotSize);
			if (numDots == 3 || numDots == 5)
			{
				g.fillOval(x + dotRadius, y + dotRadius, dotSize, dotSize);
				g.fillOval(x + dotRadius * 5, y + dotRadius * 5, dotSize, dotSize);
				if (numDots == 5)
				{
					g.fillOval(x + dotRadius * 5, y + dotRadius, dotSize, dotSize);
					g.fillOval(x + dotRadius, y + dotRadius * 5, dotSize, dotSize);
				}
			}
		}
		else
		{
			g.fillOval(x + dotRadius, y + dotRadius, dotSize, dotSize);
			g.fillOval(x + dotRadius * 5, y + dotRadius * 5, dotSize, dotSize);
			if (numDots == 4 || numDots == 6)
			{
				g.fillOval(x + dotRadius, y + dotRadius * 5, dotSize, dotSize);
				g.fillOval(x + dotRadius * 5, y + dotRadius, dotSize, dotSize);
				if (numDots == 6)
				{
					g.fillOval(x + dotRadius, y + dotRadius * 3, dotSize, dotSize);
					g.fillOval(x + dotRadius * 5, y + dotRadius * 3, dotSize, dotSize);
				}
			}
		}
	}
	
}
