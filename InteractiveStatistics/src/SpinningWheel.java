import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class SpinningWheel extends JPanel
{
	
	private int ballPosition;
	private static final int[] numbers = {37, 28, 9, 26, 30,
			11, 7, 20, 32, 17, 5, 22, 34, 15, 3, 24, 36, 13,
			1, 38, 27, 10, 25, 29, 12, 8, 19, 31, 18, 6, 21,
			33, 16, 4, 23, 35, 14, 2};
	private static final int[] colors = {0, 1, 2, 1, 2, 1,
			2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 0, 2, 1,
			2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1};
	
	private double currentRadius;
	private static final double wheelRadius = 200;
	private static final double outerRadius = wheelRadius * 13 / 15;
	private static final double innerRadius = wheelRadius * 7 / 15;
	private static final double ballRadius = 6;
	private static final double whiteMargin = 30;
	
	private boolean isSpinning;
	private double elapsedTime;
	private double refreshTime;
	public static final double animationDuration = 8000;
	
	private BufferedImage wheel;
	private BufferedImage ball;
	
	public SpinningWheel()
	{
		try
		{
			wheel = ImageIO.read(new File("images/RouletteWheel.png"));
			ball = ImageIO.read(new File("images/RouletteBall.png"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		ballPosition = 0;
		currentRadius = outerRadius;
		isSpinning = false;
		int dimension = (int)((wheelRadius + whiteMargin) * 2);
		setPreferredSize(new Dimension(dimension, dimension));
	}
	
	public void spinWheel()
	{
		if (isSpinning == false)
		{
			ballPosition = ThreadLocalRandom.current().nextInt(0, 38);
			currentRadius = outerRadius;
			isSpinning = true;
			elapsedTime = 0;
			refreshTime = 15;
			Thread animation = new Thread()
			{
				public void run()
				{
					while (isSpinning == true)
					{
						try
						{
							sleep((long)refreshTime);
							repaint();
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			};
			animation.start();
		}
	}

	public void paintComponent(Graphics g)
	{
		if (isSpinning == true)
		{
			ballPosition = (ballPosition + 1) % 38;
			elapsedTime += refreshTime;
			if (elapsedTime > animationDuration * 0.2)
			{
				refreshTime += 2;
			}
			if (elapsedTime > animationDuration * 0.85)
			{
				double rollArea = wheelRadius * 6 / 30;
				currentRadius = innerRadius + Math.random() * rollArea;
			}
			if (elapsedTime > animationDuration)
			{
				currentRadius = innerRadius;
				isSpinning = false;
			}
		}
		double angle = (double) ballPosition / 38 * 360;
		angle = Math.toRadians(angle);
		double x = wheelRadius + Math.sin(angle) * currentRadius;
		x = x - ballRadius + whiteMargin;
		double y = wheelRadius - Math.cos(angle) * currentRadius;
		y = y - ballRadius + whiteMargin;
		g.drawImage(wheel, 0, 0, this);
		g.drawImage(ball, (int)x, (int)y, this);
	}
	
	public int getNumber()
	{
		return numbers[ballPosition];
	}
	
	public String getColor()
	{
		int color = colors[ballPosition];
		if (color == 1)
			return "black";
		else if (color == 2)
			return "red";
		else
			return "green";
	}
	
	public void moveBall(int position)
	{
		ballPosition = position;
		currentRadius = innerRadius;
	}

}
