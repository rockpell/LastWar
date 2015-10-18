package pack_001;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;
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
	private AudioManagement am;
	
	private Image mshi, hp_potion, hp_plus, wall_hp, wall_time;
	private Image arrow_right, arrow_left, arrow_up, arrow_down;
	private Image arrow_right_red, arrow_left_red, arrow_up_red, arrow_down_red;
	private Image excavator_001, excavator_002, brick_black;
	private Image closed_door, open_door;
	
//	private boolean gameStart = false;
	private boolean stopOn = true, beforeStart = false;
	private boolean pup = false, pdown = false; // game before control screen value
	private boolean story_on = false, story_end = false;
	private boolean temp_stoper = false;
	
	private int gui_x = screenWidth / 2 - 140;
	private int gui_y1 = 430, gui_y2 = 500, gui_y3 = 570;
	private int index = 0;
	
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
					dm.getSkill(0).skillExcute();
				} else if(e.getKeyCode() == KeyEvent.VK_S){
					dm.getSkill(1).skillExcute();
				} else if(e.getKeyCode() == KeyEvent.VK_D){
					dm.getSkill(2).skillExcute();
				} else if(e.getKeyCode() == KeyEvent.VK_F){
					dm.getSkill(3).skillExcute();
				} else if(e.getKeyCode() == KeyEvent.VK_G){
					dm.getSkill(4).skillExcute();
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
					beforeControl(0);
				} else if(e.getKeyCode() == KeyEvent.VK_DOWN){
					player.move("down");
					beforeControl(1);
				} else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
					player.move("right");
				} else if(e.getKeyCode() == KeyEvent.VK_LEFT){
					player.move("left");
				} else if(e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER){
					if(!dm.getGameEnd()){
						if(stopOn){
							if(!dm.getGameStart()){ // before game
								if(story_on){ // story screen after
									Engine.getInstance().loadThread();
									am = dm.getAudio();
									beforeStart = true;
									dm.createGameLevel(0);
									story_on = false;
									return;
								}
								if(index == 0){ // story mode
									story_on = true;
								} else if(index == 1){ // never ending mode
									Engine.getInstance().loadThread();
									am = dm.getAudio();
									beforeStart = true;
									dm.createGameLevel(1);
								} else if(index == 2){ // game exit
									System.exit(0);
								}
							} else {  // restart
//								am.play();
								if(!temp_stoper){
									stopScreenOn();
									Engine.getInstance().startLoop();
								}
							}
							
						} else { // game stop
							if(!temp_stoper){
								stopScreenOn();
								Engine.getInstance().stopLoop();
								am.stop();
							}
						}
					} else { // game end after replay
						if(story_end){
							return;
						}
						dm.initData();
						dm.setGameStart(true);
						Engine.getInstance().newLoop();
						Engine.getInstance().startLoop();
						stopOn = false;
						player = dm.getPlayer();
						beforeStart = true;
						setStoryEnd(false);
					}
					repaint();
				} else if(e.getKeyCode() == KeyEvent.VK_ESCAPE){ // return main menu
					if(dm.getGameEnd()){
						dm.initData();
						stopOn = true;
						Engine.getInstance().initLoop();
						setStoryEnd(false);
						player = dm.getPlayer();
						index = 0;
						repaint();
					} else if(stopOn){
						dm.initData();
						Engine.getInstance().initLoop();
						player = dm.getPlayer();
						index = 0;
						beforeStart = false;
						repaint();
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
		 
		 this.addMouseListener(new MouseAdapter(){
			 public void mouseClicked(MouseEvent arg0) {
				 if(!stopOn){
					 System.out.println("stop");
					 return;
				 }
				 dm.getSkill(0).skillClick(arg0.getPoint().x, arg0.getPoint().y);
				 dm.getSkill(1).skillClick(arg0.getPoint().x, arg0.getPoint().y);
				 dm.getSkill(2).skillClick(arg0.getPoint().x, arg0.getPoint().y);
				 dm.getSkill(3).skillClick(arg0.getPoint().x, arg0.getPoint().y);
				 dm.getSkill(4).skillClick(arg0.getPoint().x, arg0.getPoint().y);
			 }
		 });
	}
	
	public void loadImage() {
//		dm.loadImage();
		
		mshi = dm.mshi;
		arrow_right = dm.arrow_right;
		arrow_left = dm.arrow_left;
		arrow_up = dm.arrow_up;
		arrow_down = dm.arrow_down;
		arrow_right_red = dm.arrow_right_red;
		arrow_left_red = dm.arrow_left_red;
		arrow_up_red = dm.arrow_up_red;
		arrow_down_red = dm.arrow_down_red;
//		brick_wall_001 = dm.brick_wall_001;
		excavator_001 = dm.excavator_001;
		excavator_002 = dm.excavator_002;
		closed_door = dm.closed_door;
		open_door = dm.open_door;
		hp_potion = dm.hp_potion;
		hp_plus = dm.hp_plus;
		brick_black = dm.brick_black;
		wall_hp = dm.wall_hp;
		wall_time = dm.wall_time;
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
	    
		if(dm.getGameStart()){
			warpGate();
		    drawWall();
		    drawLaser(mgc);
		    drawEnemy();
		    drawPlayer();
		    drawAlarmText();
		    
	        mgc.setColor(Color.black);
	        mgc.setFont(new Font("default", Font.BOLD, 16));
	        
	        mgc.drawString("Time : " + Engine.getInstance().getPlayTime() / 10, 800, screenHeight - 80);
	        mgc.drawString("Score :" + String.valueOf(dm.getScore()), 800, screenHeight - 50);
	        mgc.drawString("Point : " + String.valueOf(dm.getMoney()), 800, screenHeight - 20);
	        
	        mgc.drawString("fps : " + Engine.getInstance().getFps(), 20, 60);
       
        	mgc.setColor(Color.LIGHT_GRAY);
    	    mgc.fillRect(50, screenHeight - 100, screenWidth - 800, 100);
    	    
            drawArrow();
            drawSkill();
        }

        stopScreen();
        beforeScreen();
        storyScreen();
        afterScreen();
        stroyEndingScreen();
        
        
		g.drawImage(memoryimage, 0, 0, this);
		
	}
	
	private void drawPlayer(){
		Point2D.Float point = player.getPosition();
		AffineTransform t = new AffineTransform();
        t.translate(point.x, point.y); // x/y set here
        t.scale(1, 1); // scale = 1 
        
        float opacity = 1 - (float)(dm.getPlayer().getDamageCount() % 10)/10;
        mgc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        
        mgc.drawImage(mshi, t, null);
        mgc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
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
    		
    		int hp_x = 12;
    		
    		if(dm.getPlayer().getMaxHp() >= 10){
    			if(dm.getPlayer().getHp() >= 10){
    				hp_x = 20;
    			} else {
    				hp_x = 16;
    			}
    		}
    		
    		mgc.setFont(new Font("default", Font.PLAIN, 12));
            mgc.setColor(Color.white);
            mgc.drawString(String.valueOf(player.getHp()) + " / " + String.valueOf(player.getMaxHp()), player.getPosition().x + player.getWidth()/2 - hp_x, player.getPosition().y - 3);
        }
        
	}
	
	private void drawWall(){
		for(Wall1 wa : dm.getWallSet()){
			float wx = wa.getPosition("x");
			float wy = wa.getPosition("y");
			
			AffineTransform t = new AffineTransform();
	        t.translate(wx, wy); // x/y set here
	        t.scale(1, 1);
	        
//			mgc.drawImage(brick_wall_001, t, null);
			mgc.drawImage(brick_black, t, null);
			
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
	        mgc.setFont(new Font("default", Font.PLAIN, 12));
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
			if(!la.getIsActive()){
				continue;
			}
			
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
			int bar_x = 0;
			AffineTransform t = new AffineTransform();
	        t.translate(en.getX(), en.getY());
	        
	        float opacity = 1 - (float)(en.getDamageCount() % 10)/10;
	        mgc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
	        
	        if(en.getTypeName().equals("enemy1")){
	        	mgc.drawImage(excavator_001, t, null);
	        } else if(en.getTypeName().equals("boss")){
	        	mgc.drawImage(excavator_002, t, null);
	        	bar_x = 12;
	        }
			
	        mgc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
	        
	        if(!stopOn){
	        	mgc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.03f));
	        	
	        	float dia = en.getVision() * 2;
	        	mgc.setColor(Color.LIGHT_GRAY);
	        	Ellipse2D.Double circle = new Ellipse2D.Double(en.getX() + en.getWidth()/2 - dia/2, en.getY() + en.getHeight()/2 - dia/2, dia, dia);
	        	mgc.fill(circle);
	        	
	        	mgc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	        	
	        	if(!en.getRandMove()){
					
					Rectangle2D out_line1 = new Rectangle2D.Float(en.getX() + bar_x, en.getY() - 18, 45, 15);
			        Rectangle2D in_line1 = new Rectangle2D.Float(en.getX() + bar_x, en.getY() - 18, 45 * ((float)en.getHp()/(float)en.getMaxHp()), 15);
			        
			        mgc.setColor(Color.gray);
					mgc.fill(out_line1);
					
					if(!(en.getHp() <= 0)){
						mgc.setColor(Color.red);
						mgc.fill(in_line1);
					}
					
			        mgc.setColor(Color.black);
					mgc.draw(out_line1);
					
					int enemey_hp_x = 12;
					
					if(en.getMaxHp() >= 10){
						if(en.getHp() >= 10){
							enemey_hp_x = 20;
						} else {
							enemey_hp_x = 16;
						}
						
					}
					
			        mgc.setColor(Color.white);
			        mgc.setFont(new Font("default", Font.PLAIN, 12));
			        mgc.drawString(String.valueOf(en.getHp()) + " / " + String.valueOf(en.getMaxHp()), en.getX() + en.getWidth() / 2 - enemey_hp_x, en.getY() - 6);
			        
			        mgc.setColor(Color.black);
			        mgc.setFont(new Font("default", Font.PLAIN, 12));
			        mgc.drawString("Lv "+String.valueOf(en.getLevel()), en.getX(), en.getY() - 20);
				}
	        }
			
		}
	}
	
	private void drawSkill(){
		Skill skill_temp = dm.getSkill(0);
		Skill skill_temp2 = dm.getSkill(1);
		Skill skill_temp3 = dm.getSkill(2);
		Skill skill_temp4 = dm.getSkill(3);
		Skill skill_temp5 = dm.getSkill(4);
		
		mgc.setColor(Color.black);
		mgc.drawRect(skill_temp.getX() - 2, skill_temp.getY() - 3, skill_temp.getWidth() + 4, skill_temp.getHeight() + 4);
		mgc.drawRect(skill_temp2.getX() - 2, skill_temp2.getY() - 3, skill_temp2.getWidth() + 4, skill_temp2.getHeight() + 4);
		mgc.drawRect(skill_temp3.getX() - 2, skill_temp3.getY() - 3, skill_temp3.getWidth() + 4, skill_temp3.getHeight() + 4);
		mgc.drawRect(skill_temp4.getX() - 2, skill_temp4.getY() - 3, skill_temp4.getWidth() + 4, skill_temp4.getHeight() + 4);
		mgc.drawRect(skill_temp5.getX() - 2, skill_temp5.getY() - 3, skill_temp5.getWidth() + 4, skill_temp5.getHeight() + 4);
		
		ImageManagement abc = new ImageManagement(brick_black);
		
//		mgc.drawImage(brick_wall_001, 200, 725, null); // draw wall icon
		mgc.drawImage(abc.grayImage(), skill_temp.getX(), skill_temp.getY(), null);
		mgc.drawImage(hp_potion, skill_temp2.getX(), skill_temp2.getY(), null);
		mgc.drawImage(hp_plus, skill_temp3.getX(), skill_temp3.getY(), null);
		mgc.drawImage(wall_hp, skill_temp4.getX(), skill_temp4.getY(), null);
		mgc.drawImage(wall_time, skill_temp5.getX(), skill_temp5.getY(), null);
		
		mgc.setFont(new Font("default", Font.BOLD, 14));
		mgc.drawString("A", skill_temp.getX() + 18, skill_temp.getY() + 62);
		mgc.drawString("S", skill_temp2.getX() + 18, skill_temp2.getY() + 62);
		mgc.drawString("D", skill_temp3.getX() + 18, skill_temp3.getY() + 62);
		mgc.drawString("F", skill_temp4.getX() + 18, skill_temp4.getY() + 62);
		mgc.drawString("G", skill_temp5.getX() + 18, skill_temp5.getY() + 62);
		
		mgc.drawString(String.valueOf(dm.getCost(0)) + "P", skill_temp.getX(), skill_temp.getY() - 2);
		mgc.drawString(String.valueOf(dm.getCost(1)) + "P", skill_temp2.getX(), skill_temp2.getY() - 2);
		mgc.drawString(String.valueOf(dm.getCost(2)) + "P", skill_temp3.getX(), skill_temp3.getY() - 2);
		mgc.drawString(String.valueOf(dm.getCost(3)) + "P", skill_temp4.getX(), skill_temp4.getY() - 2);
		mgc.drawString(String.valueOf(dm.getCost(4)) + "P", skill_temp5.getX(), skill_temp5.getY() - 2);
		
		if(dm.getCoolTimeLeft() != 0){
			mgc.setFont(new Font("default", Font.PLAIN, 12));
			mgc.drawString(String.valueOf(dm.getCoolTimeLeft()), skill_temp.getX() + 18, skill_temp.getY() - 13);
		}
		
		if(Engine.getInstance().isMessage()){
//			mgc.setColor(Color.red);
			mgc.setFont(new Font("default", Font.BOLD, 20));
			mgc.drawString(Engine.getInstance().getMessage(), skill_temp5.getX() + 180, skill_temp5.getY() + 10);
			if(Engine.getInstance().getMessage2() != null)
				mgc.drawString(Engine.getInstance().getMessage2(), skill_temp5.getX() + 180, skill_temp5.getY() + 38);
		}
		
		if(dm.getGameLevel().getMode() == 0){
			String ntext = "left wave : " + String.valueOf(dm.getScenario().getPatterns() - dm.getGameLevel().getAllIndex());
			mgc.setFont(new Font("default", Font.BOLD, 20));
			mgc.drawString(ntext, skill_temp5.getX() + 180, skill_temp5.getY() + 60);
		}
	}
	
	private void drawAlarmText(){
		mgc.setColor(Color.black);
		mgc.setFont(new Font("default", Font.PLAIN, 12));
		for(AlarmText at : dm.getAlarmList()){
			mgc.drawString(at.getText(), at.getFX(), at.getFY());
		}
	}
	
	public void stopScreenOn(){
		stopOn = !stopOn;
	}
	
	public void beforeStartOn(){
		beforeStart = !beforeStart;
	}
	
	public void setTempStoper(boolean val){
		temp_stoper = val;
	}
	
	public boolean getTempStoper(){
		return temp_stoper;
	}
	
	public boolean getStopOn(){
		return stopOn;
	}
	
	public void setStoryEnd(boolean value){
		story_end = value;
	}
	
	private void stopScreen(){
		if(stopOn && dm.getGameStart()){
			mgc.setFont(new Font("TimesRoman", Font.BOLD, 70));
			mgc.setColor(Color.red);
			mgc.drawString("STOP", screenWidth / 2 - 100, screenHeight / 2);
			
			mgc.setFont(new Font("TimesRoman", Font.BOLD, 30));
			mgc.drawString("Return Main Menu?", screenWidth / 2 - 100, screenHeight / 2 + 50);
			mgc.setFont(new Font("TimesRoman", Font.BOLD, 25));
			mgc.drawString("Press the Esc Key", screenWidth / 2 - 100, screenHeight / 2 + 80);
			
			mgc.setFont(new Font("default", Font.PLAIN, 12));
		} else if(stopOn && !dm.getGameStart() && beforeStart){
			mgc.setFont(new Font("TimesRoman", Font.BOLD, 70));
			mgc.setColor(Color.red);
			mgc.drawString("LOADING", screenWidth / 2 - 160, screenHeight / 2);
			mgc.setFont(new Font("default", Font.PLAIN, 12));
		}
		
		if(temp_stoper){
			int number_time = 3 - Engine.getInstance().getTempTime();
			mgc.setFont(new Font("TimesRoman", Font.BOLD, 150));
			mgc.setColor(Color.red);
			mgc.drawString(String.valueOf(number_time), screenWidth / 2 - 40, screenHeight / 2);
			
		}
	}
	
	private void beforeScreen(){
		if(!dm.getGameStart() && !dm.getGameEnd() && !beforeStart && !story_on){
			mgc.setFont(new Font("TimesRoman", Font.BOLD, 85));
			mgc.setColor(Color.black);
			mgc.drawString("Last War", screenWidth / 2 - 180, 160);
			
			mgc.setFont(new Font("TimesRoman", Font.BOLD, 50));
			mgc.setColor(Color.red);
			mgc.drawString("Story Mode", gui_x, gui_y1);
			
			mgc.setFont(new Font("TimesRoman", Font.BOLD, 50));
			mgc.setColor(Color.red);
			mgc.drawString("Never Ending Mode", gui_x, gui_y2);
			
			mgc.setFont(new Font("TimesRoman", Font.BOLD, 50));
			mgc.setColor(Color.red);
			mgc.drawString("Exit", gui_x, gui_y3);
			
			mgc.setFont(new Font("TimesRoman", Font.BOLD, 30));
			mgc.setColor(Color.red);
			mgc.drawString("Press the SpaceBar Or Enter", screenWidth / 2 - 230, 750);
			
			// string
			
			mgc.setFont(new Font("TimesRoman", Font.BOLD, 45));
			mgc.setColor(Color.red);
			
			switch(index){
			case 0:
				mgc.drawString("▶", gui_x - 60, gui_y1);
				break;
			case 1:
				mgc.drawString("▶", gui_x - 60, gui_y2);
				break;
			case 2:
				mgc.drawString("▶", gui_x - 60, gui_y3);
				break;
			}
			mgc.setFont(new Font("default", Font.PLAIN, 12));
		}
	}
	
	private void controlPosition(){
		if(pup){
			pup = false;
			if(index > 0){
				index -= 1;
			}
		}
		
		if(pdown){
			pdown = false;
			if(index < 2){
				index += 1;
			}
		}
		
	}
	
	private void beforeControl(int type){
		if(!dm.getGameStart() && !dm.getGameEnd() && !beforeStart && !story_on){
			if(type == 0){
				pup = true;
			} else if(type == 1){
				pdown = true;
			}
		}
		
		controlPosition();
		repaint();
	}
	
	private void afterScreen(){
		if(dm.getGameEnd() && !story_end){
			mgc.setFont(new Font("TimesRoman", Font.BOLD, 80));
			
			mgc.setColor(Color.red);
			mgc.drawString("Game Over", screenWidth / 2 - 180, 220);
			
			mgc.setColor(Color.red);
			mgc.drawString("Score : " + String.valueOf(dm.getScore()), screenWidth / 2 - 330, 390);
			
			mgc.setFont(new Font("TimesRoman", Font.BOLD, 55));
			mgc.setColor(Color.red);
			mgc.drawString("REPLAY?", screenWidth / 2 - 330, 510);
			
			mgc.setFont(new Font("TimesRoman", Font.BOLD, 40));
			mgc.drawString("Press the SpaceBar Or Enter", screenWidth / 2 - 330, 590);
			
			mgc.setFont(new Font("TimesRoman", Font.BOLD, 55));
			mgc.drawString("Return Main Menu?", screenWidth / 2 - 330, 690);
			
			mgc.setFont(new Font("TimesRoman", Font.BOLD, 40));
			mgc.drawString("Press the Esc Key", screenWidth / 2 - 330, 760);
		}
	}
	
	private void storyScreen(){
		if(story_on){
			String[] text = new String[13];
			text[0] = "벽돌공 브릭슨은 우수한 벽돌공이다.";
			text[1] = "그는 뛰어난 솜씨를 가졌을 뿐더러 자신의 일에 긍지를 가지고 있기에 많은 사람들은 그를 장인이라 불렀다.";
			text[2] = "어느날 한 건설회사에서 그에게 일을 맡기게 되는데";
			text[3]	= "브릭슨은 우연치 않게 건설회사에서 대대적으로 비리를 저지르는것을 알게 된다.";
			text[4] = "건설회사에서는 브릭슨에게 보수를 약속하며 비리와 관련된 일을 함구할 것을 약속하지만";
			text[5] = "브릭슨은 자신의 일에 높은 긍지를 가지고 있기에 이를 거절한다.";
			text[6] = "또한 브릭슨은 건설회사에서 저지른 비리를 폭로하겠다고 선언해버린다.";
			text[7] = "브릭슨은 많은 사람들에게 장인이라 알려져있기 때문에";
			text[8] = "브릭슨의 폭로로 인해 건설회사이 받을 피해는 막대할 것이라 예상되었다.";
			text[9] = "건설회사는 브릭슨의 폭로를 막기 위해 브릭슨을 아무도 모르게 처지하려 한다.";
			text[10] = "브릭슨은 건설회사에서 보낸 건설 로봇들의 위협을 피해 비리를 폭로하려 하나 상황이 여의치 않다.";
			text[11] = "고민 끝에 브릭슨은 입구 하나만 존재하는 위험한 방으로 로봇들을 끌어들여";
			text[12] = "모두 처치한 후 방을 빠져나가 비리를 폭로하기로 결정한다....";
			
			mgc.setColor(Color.black);
			mgc.setFont(new Font("TimesRoman", Font.BOLD, 20));
			
			for(int i = 0; i < text.length; i++){
				mgc.drawString(text[i], 40, 100 + 40*i);
			}
			
			mgc.setColor(Color.red);
			mgc.setFont(new Font("TimesRoman", Font.BOLD, 25));
			mgc.drawString("Next?", 820, 700);
			mgc.drawString("Press SpaceBar Or Enter Key", 820, 750);
		}
	}
	
	private void stroyEndingScreen(){
		if(story_end){
			String[] text = new String[9];
			
			text[0] = "브릭슨은 탈출에 성공하였다.";
			text[1] = "탈출에 성공한 브릭슨은 건설회사의 비리를 폭로하는데 성공하여 건설회사는 처벌을 받게 되었다.";
			text[2] = "그러나 막대한 비리를 저지른 것에 비해 너무나도 작은 벌금을 내게 된다.";
			text[3] = "브릭슨과 그를 지지하는 사람들은 부당한 처사이며";
			text[4] = "건설회사를 해체 혹은 막대한 벌금과 책임자를 감옥에 보낼 것을 요구하지만";
			text[5] = "정부는 그들의 요청을 들어주지 않는다.";
			text[6] = "이후 브릭슨은 건설회사가 처벌을 받도록 노력하나 시간이 갈수록 많은 사람들의 관심이 없어져가며";
			text[7] = "건설회사의 비리는 지나간 일이 되버리고 만다.";
			text[8] = "이에 환멸을 느낀 브릭슨은 몇 년후에 외국으로 떠난다.";
			
			
			mgc.setColor(Color.black);
			mgc.setFont(new Font("TimesRoman", Font.BOLD, 20));
			
			for(int i = 0; i < text.length; i++){
				mgc.drawString(text[i], 40, 150 + 40 * i);
			}
			
			mgc.setFont(new Font("TimesRoman", Font.BOLD, 55));
			mgc.drawString("Return Main Menu?", screenWidth / 2 - 330, 690);
			
			mgc.setFont(new Font("TimesRoman", Font.BOLD, 40));
			mgc.drawString("Press the Esc Key", screenWidth / 2 - 330, 760);
		}
	}
	
	private void warpGate(){
		
		float wx = dm.getWarpGate().x;
		float wy = dm.getWarpGate().y;
		
		AffineTransform t = new AffineTransform();
        t.translate(wx, wy); // x/y set here
        t.scale(1, 1);
        
        if(dm.getWarpGate().isOpen()){
        	mgc.drawImage(open_door, t, null);
        } else {
        	mgc.drawImage(closed_door, t, null);
        }
	}
	
}

class ImageManagement{
	private DataManagement dm;
	BufferedImage image;
	private int width;
    private int height;
    private float progress = 0.1f;
    
    ImageManagement(BufferedImage target){
    	image = target;
    	width = image.getWidth();
        height = image.getHeight();
        
    }
    
    public ImageManagement(Image target) {
    	BufferedImage bimage = new BufferedImage(target.getWidth(null), target.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(target, 0, 0, null);
        bGr.dispose();
        
        this.image = bimage;
        this.width = image.getWidth();
        this.height = image.getHeight();
        
        dm = DataManagement.getInstance();
    }
    
    public BufferedImage grayImage(){
    	
    	setProgress((float)(dm.getMaxCoolTime() - dm.getCoolTimeLeft()) / (float)dm.getMaxCoolTime());
    	double centre_width = Math.ceil(width / 2), centre_height = Math.ceil(height / 2);
    	
    	for(int i=0; i < height; i++){
            for(int j=0; j < width; j++){
            	double angle = Math.atan2((double)i - centre_height, (double)j - centre_width) * (180 / Math.PI) + 90;
            	
            	if(angle < 0){
            		angle += 360; //change angles to go from 0 to 360
                }
            	
            	if(angle >= progress*360.0){
	            	Color c = new Color(image.getRGB(j, i));
	            	int red = (int)(c.getRed() * 0.199);
	            	int green = (int)(c.getGreen() * 0.287);
	            	int blue = (int)(c.getBlue() *0.014);
	              	Color newColor = new Color(red+green+blue, red+green+blue, red+green+blue);
//	              	Color newColor = new Color(0,0,0);
	               
	            	image.setRGB(j, i, newColor.getRGB());
            	} else {
            		
            	}
            }
        }
    	return image;
    }
    
    public void setProgress(float result){
    	progress = result;
    }
}