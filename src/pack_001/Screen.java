package pack_001;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Screen extends JFrame{
	private static Screen instance;
	public static Screen getInstance(){
		if(instance == null){
			instance = new Screen();
		}
		
		return instance;
	}
	
	int screenWidth = 1200, screenHeight = 800;
	
//	private static final long serialVersionUID = -711163588504124217L;
	
	BufferedImage memoryimage;
	Graphics2D mgc;
	
	private Player player;
	private DataManagement dm;
	private Image mshi;
	private BufferedImage beams, beam1, beam2, beam3;
	
	private Screen() {
		 super("Last War");
		 
		 setSize(screenWidth, screenHeight);
		 
		 this.setLocationRelativeTo(null);
		 setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 setVisible(true);
		 
		 this.addKeyListener(new KeyListener(){
			 private final Set<Integer> keyList = new HashSet<Integer>();
			@Override
			public synchronized void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN ||
						e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT){
					keyList.add(e.getKeyCode());
				}
				
				if(keyList.size() > 1){
					String keyValue = "";
					int temp = 0;
					for(int it : keyList){
						
						switch(it){
						case KeyEvent.VK_UP:
							keyValue += "up";
							temp++;
							break;
						case KeyEvent.VK_DOWN:
							keyValue += "down";
							temp++;
							break;
						case KeyEvent.VK_RIGHT:
							keyValue += "right";
							temp++;
							break;
						case KeyEvent.VK_LEFT:
							keyValue += "left";
							temp++;
							break;
						}
						
						if(temp > 1){
							player.move(keyValue);
							return;
						}
					}
				}
				
				if(e.getKeyCode() == KeyEvent.VK_UP){
					player.move("up");
				} else if(e.getKeyCode() == KeyEvent.VK_DOWN){
					player.move("down");
				} else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
					player.move("right");
				} else if(e.getKeyCode() == KeyEvent.VK_LEFT){
					player.move("left");
				}
				
			}

			@Override
			public synchronized void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				keyList.remove(e.getKeyCode());
				if(keyList.size() == 0){
					player.move("stop");
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			 
		 });
		 
		 this.addMouseListener(new MouseAdapter() {
			 public void mouseClicked(MouseEvent e){
				 player.setPosition(e.getPoint());
//				 repaint();
			 }
		 });
		 
		 dm = DataManagement.getInstance();
		 player = dm.getPlayer();
		 
		 loadImage();
	}
	
	private void loadImage() {
		try {
			 mshi = new ImageIcon("resource/people.png").getImage();
			 
			 File file = new File("resource/beams.png");
			 FileInputStream fis = new FileInputStream(file);
			 beams = ImageIO.read(fis);
			 
			 beam1 = beams.getSubimage(38, 170, 36, 30);
			 beam2 = beam1.getSubimage(0, 8, beam1.getWidth(), beam1.getHeight() - 15);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	public void update(Graphics g) {
		player = dm.getPlayer();
        paint(g);
    }
	
	public void paint(Graphics g) {
		if(mgc == null){
			try {
				memoryimage = new BufferedImage(screenWidth, screenHeight, 1);
				mgc = memoryimage.createGraphics();
				mgc.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				mgc.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				mgc.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			} catch(Exception e){
				mgc = null;
			}
		}
		
		mgc.setBackground(Color.white);
		mgc.clearRect(0, 0, screenWidth, screenHeight);
		
		Point2D.Float point = player.getPosition();
		
		mgc.setColor(Color.pink);
	    mgc.fillRect(0, screenHeight - 100, screenWidth, 100);
	    
	    drawLaser(mgc);
		
		AffineTransform t = new AffineTransform();
        t.translate(point.x, point.y); // x/y set here
        t.scale(1, 1); // scale = 1 
        mgc.drawImage(mshi, t, null);

//        mgc.drawImage(beams, 100, 100, null);
//        mgc.drawImage(beam1, 800, 150, null);
        
        mgc.setColor(Color.black);
        mgc.drawString("time : " + Engine.getInstance().getPlayTime(), screenWidth - 100, 50);
        
		g.drawImage(memoryimage, 0, 0, this);
		
	}
	
	void drawLaser(Graphics2D g){
		for(Laser1 la : Engine.getInstance().getColiderSet()){
			if(!la.getTrigger()){
				g.setColor(Color.red);
				
				if(la.getName().equals("row")){
					g.drawLine(la.getPosition("x"), la.getPosition("y"), la.getSize("width"), la.getPosition("y"));
				} else {
					g.drawLine(la.getPosition("x"), la.getPosition("y"), la.getPosition("x"), la.getSize("height"));
				}
			} else {
				g.setColor(Color.blue);
				
				if(la.getName().equals("row")){
					g.drawLine(la.getPosition("x"), la.getPosition("y"), la.getSize("width"), la.getPosition("y"));
			    	g.fillRect(la.getPosition("x"), la.getPosition("y") - 5, la.getSize("width"), 10);
				} else {
					g.drawLine(la.getPosition("x"), la.getPosition("y"), la.getPosition("x"), la.getSize("height"));
			    	g.fillRect(la.getPosition("x") - 5, la.getPosition("y"), 10, la.getSize("height"));
				}
			}
		}
	}
}