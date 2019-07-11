package pack_001;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

final class ImageManagement{
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