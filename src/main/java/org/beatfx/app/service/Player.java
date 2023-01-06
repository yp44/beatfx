package org.beatfx.app.service;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.beatfx.app.model.BeatfxModel;
import org.beatfx.app.model.Cycle;
import org.beatfx.app.service.player.CyclePlayer;

public class Player {

    private BeatfxModel beatfxModel;

    public Player(BeatfxModel beatfxModel){
        this.beatfxModel = beatfxModel;
    }

    public void play() throws InterruptedException {
        System.out.println("Play model...");
        Cycle first = beatfxModel.getCycles().get(0);
        CyclePlayer cyclePlayer = new CyclePlayer(first);

        int i = 1;
        for(MediaPlayer mediaPlayer : cyclePlayer.getMediaPlayer()){
            System.out.println("Play " + i);
            mediaPlayer.play();
            int sleep = cyclePlayer.getDuration();
            System.out.println("SLEEP : " + sleep);
            Thread.sleep(sleep);
            i++;
        }

    }

}
