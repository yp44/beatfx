package org.beatfx.app.service.player;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import lombok.Data;
import org.beatfx.app.model.Beat;
import org.beatfx.app.model.Cycle;

import java.util.ArrayList;
import java.util.List;

@Data
public class CyclePlayer {

    private int duration;
    private List<MediaPlayer> mediaPlayer = new ArrayList<>();
    private List<Beat> beats;

    public CyclePlayer(Cycle cycle){
        this.beats = cycle.getBeats();
        this.duration = cycle.getDuration().get() / cycle.getNbSlots().get();
        System.out.println(String.format("Duration: %s, nbSlots: %s, sleep: %s", cycle.getDuration().get(), cycle.getNbSlots().get(), duration));

        System.out.println("Nb beats : " + cycle.getBeats().size());

        for(Beat beat : this.beats){
            System.out.println("New media from beat");
            String file = String.format("file://%s", beat.getFile().get());
            System.out.println("file : " + file);
            Media media = new Media(file);
            MediaPlayer player = new MediaPlayer(media);
            mediaPlayer.add(player);
        }
    }

}
