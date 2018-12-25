
import javax.swing.JFrame;

public class InteractiveStatistics extends JFrame
{
	
	public InteractiveStatistics()
	{
		setTitle("Interactive Statistics");
		setLocation(200, 100);
		setSize(1500, 800);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		MainMenu mainMenu = new MainMenu(this);
		add(mainMenu);
		
		setVisible(true);
	}

	public static void main(String[] args)
	{
		new InteractiveStatistics();
	}

}
