package org.beatfx.app.service;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.beatfx.app.model.BeatfxModel;
import org.beatfx.app.model.Cycle;
import org.beatfx.app.model.FullComposition;
import org.beatfx.app.model.PlayerRow;
import org.beatfx.app.service.player.CyclePlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Player {

    private BeatfxModel beatfxModel;

    public Player(BeatfxModel beatfxModel) {
        this.beatfxModel = beatfxModel;
    }

    public void play() {
        System.out.println("======================= PLAY");
        FullComposition fullComposition = beatfxModel2FullCompo(this.beatfxModel);
    }

    private FullComposition beatfxModel2FullCompo(BeatfxModel beatfxModel) {
        /*Map<String, PlayerRow> playerRowByLabel = beatfxModel.getPlayerRows().stream()
                .filter(p -> p.getLabel() != null && !p.getLabel().isEmpty())
                .collect(Collectors.toMap(p -> p.getLabel(), p -> p));*/

        Map<String, Integer> indexByLabel = new HashMap<>();
        for (int i = 0; i < beatfxModel.getPlayerRows().size(); i++) {
            PlayerRow playerRow = beatfxModel.getPlayerRows().get(i);
            String label = playerRow.getLabel().trim();
            if (label != null && !label.isEmpty()) {
                if (indexByLabel.containsKey(label)) {
                    throw new IllegalStateException(String.format("Several rows has the same label '%S': %s & %s ", label, indexByLabel.get(label), i));
                }
                indexByLabel.put(label, i);
                //System.out.println(String.format("indexByLabel: %s / %s", label, i));
            }
        }



        Map<Integer, Integer> repeat = new HashMap<>();
        //List<Integer> doneRepeats = new ArrayList<>();
        FullComposition fullComposition = new FullComposition();
        for (int i = 0; i < beatfxModel.getPlayerRows().size(); i++) {
            System.out.println("*********************  Index = " + i);
            // Get PlayerRow
            PlayerRow playerRow = beatfxModel.getPlayerRows().get(i);

            // Is there any gotoLabel ? What is its index ?
            int gotoIndex = -1;
            String gotoLabel = playerRow.getGotoLabel().trim();
            if (gotoLabel != null && !gotoLabel.isEmpty()) {
                gotoIndex = Optional.ofNullable(indexByLabel.get(gotoLabel)).orElse(-1);
            }
            //System.out.println(String.format("GotoLabel %s : %s", gotoLabel, gotoIndex));
            if (gotoIndex > i) {
                throw new IllegalStateException(String.format("Gotolabel of a row can't reference a subsequent row. %s refers %s:%s", i, gotoIndex, gotoLabel));
            }

            // If there is a gotoLabel, how many times should we repeat ?
            if (!repeat.containsKey(i)) {
                Integer gotoRepeat = Optional.ofNullable(playerRow.getGotoRepeat()).orElse(1);
                repeat.put(i, gotoRepeat);
            }
            Integer gotoRepeat = repeat.get(i);

            List<FullComposition.PlayableCycle> row = playerRow.getCycles().stream().map(c -> new FullComposition.PlayableCycle(c)).collect(Collectors.toList());
            fullComposition.getCompo().add(row);

            gotoRepeat--;
            repeat.put(i, gotoRepeat);
            //repeat.entrySet().stream().forEach(e -> System.out.println(String.format("Repeat: %s:%s", e.getKey(), e.getValue())));

            if(gotoRepeat > -1){
                i = gotoIndex - 1;
            }
            else{
                repeat.remove(i);
            }

        }

        return fullComposition;
    }

    public void playx() throws InterruptedException {
        System.out.println("Play model...");
        Cycle first = beatfxModel.getCycles().get(0);
        CyclePlayer cyclePlayer = new CyclePlayer(first);

        AtomicInteger nbActivePlayer = new AtomicInteger(0);

        int i = 1;
        for (MediaPlayer mediaPlayer : cyclePlayer.getMediaPlayer()) {
            System.out.println("Play " + i);
            nbActivePlayer.incrementAndGet();
            mediaPlayer.play();
            mediaPlayer.setOnEndOfMedia(() -> {
                nbActivePlayer.decrementAndGet();
            });
            int sleep = cyclePlayer.getDuration();
            System.out.println("SLEEP : " + sleep);
            Thread.sleep(sleep);
            i++;
        }

        // Wait for all player to have finished
        while (nbActivePlayer.get() > 0) {
            Thread.sleep(1000);
        }

    }

}
