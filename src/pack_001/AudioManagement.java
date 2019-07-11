package pack_001;

import java.io.File;
import java.net.URL;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

final class AudioManagement{
	Clip clip;
	URL url;
	
	AudioManagement(){
		url = this.getClass().getClassLoader().getResource("game_music/hit_and_run.wav");
			
	}
	
	public void play(){
		playSound(url.getPath());
	}
	
	public void stop(){
		clip.stop();
	}
	
	private void playSound(String file_path){
        try {
            File file = new File(file_path);
            clip = AudioSystem.getClip();
            
            clip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event){
                    //CLOSE, OPEN, START, STOP
                    if (event.getType() == LineEvent.Type.STOP)
                        clip.close();
                }

            });

            clip.open(AudioSystem.getAudioInputStream(file));

            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(0f);
            
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
}