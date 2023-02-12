package org.beatfx.app.model;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import lombok.Data;
import org.beatfx.app.util.Defaults;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
public class FullComposition {

    private List<List<PlayableCycle>> compo = new ArrayList<>();

    @Data
    public final static class PlayableCycle{
        private final Cycle cycle;
        private List<MediaPlayer> samples = new ArrayList<>();

        public PlayableCycle(Cycle c){
            this.cycle = c;
            try {
                prepareAudio();
            }
            catch (UnsupportedAudioFileException | IOException | LineUnavailableException e){
                System.err.println(String.format("Exception in preparing audio for cycle %s: %s.", cycle.getId(), e.getMessage()));
                e.printStackTrace(System.err);
            }
        }


        // TODO: maybe coul be optimized to not prepare audio several times for a same cycle
        private void prepareAudio() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
            System.out.println("Prepare audio for " + this.cycle.getId());
            for(Beat b : cycle.getBeats()){
                String file = b.getFile().get();
                if(file == null || file.trim().isEmpty() || file.trim().equals(Defaults.FILE_PLACEHOLDER)){
                    samples.add(null);
                    continue;
                }
                String uri = new File(file).toURI().toString();
                Media media = new Media(uri);
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                samples.add(mediaPlayer);
            }
        }

    }

}
