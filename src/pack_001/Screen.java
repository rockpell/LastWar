package pack_001;

import java.awt.Color;
import java.awt.Font;
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
import java.awt.geom.Rectangle2D;
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
	
	private int screenWidth = 1200, screenHeight = 800;
	
//	private static final long serialVersionUID = -711163588504124217L;
	
	BufferedImage memoryimage;
	Graphics2D mgc;
	
	private Player player;
	private DataManagement dm;
	private Image mshi;
	private Image arrow_right, arrow_left, arrow_up, arrow_down;
	private Image arrow_right_red, arrow_left_red, arrow_up_red, arrow_down_red;
	private Image brick_wall_001, excavator_001;
	
	private boolean gameStart = false;
	private boolean stopOn = true;
	
	private Screen() {
		 super("Last War");
		 
		 dm = DataManagement.getInstance();
		 player = dm.getPlayer();
		 
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
				} else if(e.getKeyCode() == KeyEvent.VK_A){
					if(dm.getCoolTimeLeft() == 0 && dm.getPlayer().isWallAble()){
						if(!dm.getPlayer().getOutTrigger()){
							if(dm.getWallSetCount() < dm.getWallLimit())
								dm.addWall(dm.getPlayer().getPosition().x, dm.getPlayer().getPosition().y);
						}
						dm.getPlayer().setOutTrigger(true);
						dm.initCoolTime();
					}
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
						
					}
					
					if(temp > 1){
						player.move(keyValue);
						return;
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
				} else if(e.getKeyCode() == KeyEvent.VK_SPACE){
					if(stopOn){
						stopScreenOn();
						gameStart = true;
						Engine.getInstance().startLoop();
					} else {
						stopScreenOn();
//						gameStart = false;
						Engine.getInstance().stopLoop();
					}
				}
				
			}

			@Override
			public synchronized void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
				keyList.remove(e.getKeyCode());
				
				for(int it : keyList){
					switch(it){
					case KeyEvent.VK_UP:
						player.move("up");
						break;
					case KeyEvent.VK_DOWN:
						player.move("down");
						break;
					case KeyEvent.VK_RIGHT:
						player.move("right");
						break;
					case KeyEvent.VK_LEFT:
						player.move("left");
						break;
					}
					return;
				}
				
				if(keyList.size() == 0){
					player.move("stop");
				}
			}

			@Override
			public synchronized void keyTyped(KeyEvent e) { // not working arrow key
				// TODO Auto-generated method stub
//				System.out.println(e.getKeyChar());
			}
			 
		 });
		 
//		 this.addMouseListener(new MouseAdapter() {
//			 public void mouseClicked(MouseEvent e){
////				 player.setPosition(e.getPoint());
////				 repaint();
//			 }
//		 });
		 
		
		 
		 loadImage();
	}
	
	private void loadImage() {
		 mshi = new ImageIcon("resource/people.png").getImage();
		 arrow_right = new ImageIcon("resource/arrow_right.png").getImage();
		 arrow_left = new ImageIcon("resource/arrow_left.png").getImage();
		 arrow_up = new ImageIcon("resource/arrow_up.png").getImage();
		 arrow_down = new ImageIcon("resource/arrow_down.png").getImage();
		 arrow_right_red = new ImageIcon("resource/arrow_right_red.png").getImage();
		 arrow_left_red = new ImageIcon("resource/arrow_left_red.png").getImage();
		 arrow_up_red = new ImageIcon("resource/arrow_up_red.png").getImage();
		 arrow_down_red = new ImageIcon("resource/arrow_down_red.png").getImage();
		 brick_wall_001 = new ImageIcon("resource/brick_001.png").getImage();
		 excavator_001 = new ImageIcon("resource/excavator.png").getImage();
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
	    
		if(gameStart){
			warpGate();
		    drawWall();
		    drawLaser(mgc);
		    drawEnemy();
		    
		    drawPlayer();
	        
	        mgc.setColor(Color.black);
	        mgc.drawString("time : " + Engine.getInstance().getPlayTime() / 10, screenWidth - 100, 50);
       
        	mgc.setColor(Color.pink);
    	    mgc.fillRect(0, screenHeight - 100, screenWidth, 100);
    	    
            drawArrow();
            drawSkill();
        }

        stopScreen();
		g.drawImage(memoryimage, 0, 0, this);
		
	}
	
	private void drawPlayer(){
		Point2D.Float point = player.getPosition();
		AffineTransform t = new AffineTransform();
        t.translate(point.x, point.y); // x/y set here
        t.scale(1, 1); // scale = 1 
        
        mgc.drawImage(mshi, t, null);
        
        if(!stopOn){
        	Rectangle2D out_line1 = new Rectangle2D.Float(player.getPosition().x, player.getPosition().y - 15, 45, 15);
            Rectangle2D in_line1 = new Rectangle2D.Float(player.getPosition().x, player.getPosition().y - 15, 45 * ((float)player.getHp()/(float)player.getMaxHp()), 15);
            
            mgc.setColor(Color.gray);
    		mgc.fill(out_line1);
    		
    		if(!(player.getHp() <= 0)){
    			mgc.setColor(Color.red);
    			mgc.fill(in_line1);
    		}
    		
            mgc.setColor(Color.black);
    		mgc.draw(out_line1);
    		
            mgc.setColor(Color.white);
            mgc.drawString(String.valueOf(player.getHp()) + " / " + String.valueOf(player.getMaxHp()), player.getPosition().x + player.getWidth()/2 - 12, player.getPosition().y - 3);
        }
        
        
	}
	
	private void drawWall(){
        
		for(Wall1 wa : dm.getWallSet()){
			float wx = wa.getPosition("x");
			float wy = wa.getPosition("y");
			
			AffineTransform t = new AffineTransform();
	        t.translate(wx, wy); // x/y set here
	        t.scale(1, 1);
	        
			mgc.drawImage(brick_wall_001, t, null);
			
//			mgc.setColor(Color.red);
//			mgc.drawString(String.valueOf(wa.getHp()), wx + wa.getSize("width") / 2 - 3, wy - 10);
			
			Rectangle2D out_line1 = new Rectangle2D.Float(wa.getX(), wa.getY() - 18, wa.getWidth(), 15);
	        Rectangle2D in_line1 = new Rectangle2D.Float(wa.getX(), wa.getY() - 18, wa.getWidth() * ((float)wa.getHp()/(float)wa.getMaxHp()), 15);
	        
	        mgc.setColor(Color.gray);
			mgc.fill(out_line1);
			
			if(!(wa.getHp() <= 0)){
				mgc.setColor(Color.red);
				mgc.fill(in_line1);
			}
			
	        mgc.setColor(Color.black);
			mgc.draw(out_line1);
			
	        mgc.setColor(Color.white);
	        mgc.drawString(String.valueOf(wa.getHp()) + " / " + String.valueOf(wa.getMaxHp()), wa.getX() + wa.getWidth()/2 - 12, wa.getY() - 6);
		}
	}
	
	private void drawArrow(){
		for(LaserArrow lar : dm.getArrowSet()){
			
			switch(lar.getIndexX()){
			case 0:
				if(lar.isExist()){
					mgc.drawImage(arrow_right_red, dm.rowStartX1, dm.rowStartY + lar.getIndexY()*50, null);
				} else {
					mgc.drawImage(arrow_right, dm.rowStartX1, dm.rowStartY + lar.getIndexY()*50, null);
				}
				break;
			case 1:
				if(lar.isExist()){
					mgc.drawImage(arrow_left_red, dm.rowStartX2, dm.rowStartY + lar.getIndexY()*50, null);
				} else {
					mgc.drawImage(arrow_left, dm.rowStartX2, dm.rowStartY + lar.getIndexY()*50, null);
				}
				
				break;
			case 2:
				if(lar.isExist()){
					mgc.drawImage(arrow_down_red, dm.colStartX + lar.getIndexY()*50, dm.colStartY1 , null);
				} else {
					mgc.drawImage(arrow_down, dm.colStartX + lar.getIndexY()*50, dm.colStartY1 , null);
				}
				
				break;
			case 3:
				if(lar.isExist()){
					mgc.drawImage(arrow_up_red, dm.colStartX + lar.getIndexY()*50, dm.colStartY2, null);
				} else {
					mgc.drawImage(arrow_up, dm.colStartX + lar.getIndexY()*50, dm.colStartY2, null);
				}
				
			}
			
		}
		
	}
	
	private void drawLaser(Graphics2D g){
		for(Laser1 la : dm.getColiderSet()){
			if(!la.getTrigger()){
				g.setColor(Color.red);
				
				if(la.getName().contains("row")){
					float temp = la.calLaserSize("height");
					float temp2 = 1;
					if(temp >= 1){
						temp2 = temp;
					}
					
					float laX = 0, laY = 0, laH = 0, laW = 0;
					
					laX = la.getPosition("x");
					laY = la.getPosition("y") - temp2/2;
					laW = la.getSize("width");
					laH = temp2;
					
					g.fill(new Rectangle2D.Float(laX, laY, laW, laH));
					

				} else {
					float temp = la.calLaserSize("width");
					float temp2 = 1;
					if(temp >= 1){
						temp2 = temp;
					}
					
					g.fill(new Rectangle2D.Float(la.getPosition("x") - temp2/2, la.getPosition("y"), temp2, la.getSize("height") ) );
					
				}
			} else {
				g.setColor(Color.blue);
				
				if(la.getName().contains("row")){
			    	g.fill(new Rectangle2D.Float(la.getPosition("x"), la.getPosition("y") - la.getSize("height")/2, la.getSize("width"), la.getSize("height")) );
				} else {
			    	g.fill(new Rectangle2D.Float(la.getPosition("x") - la.getSize("width")/2, la.getPosition("y"), la.getSize("width"), la.getSize("height")) );
				}
			}
		}
	}
	
	private void drawEnemy(){
		for(Enemy1 en : dm.getEnemySet()){
			AffineTransform t = new AffineTransform();
	        t.translate(en.getX(), en.getY());
			
			mgc.drawImage(excavator_001, t, null);
			
			if(!stopOn && !en.getRandMove()){
				
				Rectangle2D out_line1 = new Rectangle2D.Float(en.getX(), en.getY() - 18, 45, 15);
		        Rectangle2D in_line1 = new Rectangle2D.Float(en.getX(), en.getY() - 18, 45 * ((float)en.getHp()/(float)en.getMaxHp()), 15);
		        
		        mgc.setColor(Color.gray);
				mgc.fill(out_line1);
				
				if(!(en.getHp() <= 0)){
					mgc.setColor(Color.red);
					mgc.fill(in_line1);
				}
				
		        mgc.setColor(Color.black);
				mgc.draw(out_line1);
				
		        mgc.setColor(Color.white);
		        mgc.drawString(String.valueOf(en.getHp()) + " / " + String.valueOf(en.getMaxHp()), en.getX() + en.getWidth() / 2 - 12, en.getY() - 6);
			}
			
		}
	}
	
	private void drawSkill(){
		mgc.setColor(Color.black);
		mgc.drawRect(192, 716, 64, 64);
		mgc.drawImage(brick_wall_001, 200, 725, null); // draw wall icon
		
		if(dm.getCoolTimeLeft() != 0)
			mgc.drawString(String.valueOf(dm.getCoolTimeLeft()), 188 + 30, 712);
	}
	
	private void stopScreenOn(){
		stopOn = !stopOn;
	}
	
	private void stopScreen(){
		if(stopOn){
			mgc.setFont(new Font("TimesRoman", Font.BOLD, 70));
			mgc.setColor(Color.red);
			mgc.drawString("STOP", screenWidth / 2 - 100, screenHeight / 2);
			mgc.setFont(new Font("default", Font.PLAIN, 12));
		}
	}
	
	private void warpGate(){
		mgc.setColor(Color.black);
//		mgc.drawRect(548, 348, 60, 60);
		mgc.draw(dm.getWarpGate().getBounds());
		
	}
	
}