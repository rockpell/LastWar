package pack_001;

import java.awt.EventQueue;

public class mainClass
{

	public static void main(String[] args)
	{
		GameManager.getInstance();

		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				Screen ex = Screen.getInstance();
				
				ex.addKeyListener(InputManager.getInstance().keyBind());
				ex.addMouseListener(InputManager.getInstance().mouseBind());
				
				GameManager.getInstance().loadThread();
				ex.setVisible(true);
			}
		});
	}
}
