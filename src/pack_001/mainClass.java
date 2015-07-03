package pack_001;

import java.awt.EventQueue;

public class mainClass {

	public static void main(String[] args){
//		Screen abc = Screen.getInstance();
		Engine engine = Engine.getInstance();
		
		engine.startLoop();
		
		EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Screen ex = Screen.getInstance();
                ex.setVisible(true);
            }
        });
	}
}
