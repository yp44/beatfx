package org.beatfx.app.service;

import javafx.scene.media.MediaPlayer;
import org.beatfx.app.model.BeatfxModel;
import org.beatfx.app.model.Cycle;
import org.beatfx.app.model.FullComposition;
import org.beatfx.app.model.PlayerRow;
import org.beatfx.app.service.player.CyclePlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Player {

    private final BeatfxModel beatfxModel;

    public Player(BeatfxModel beatfxModel) {
        this.beatfxModel = beatfxModel;
    }

    public void play() {
        System.out.println("======================= PLAY");
        FullComposition fullComposition = beatfxModel2FullCompo(this.beatfxModel);

        List<List<FullComposition.PlayableCycle>> compo = fullComposition.getCompo();
        for (List<FullComposition.PlayableCycle> row : compo) { // For each compo row
            CountDownLatch syncRow = new CountDownLatch(row.size());
            CountDownLatch latch = new CountDownLatch(1);
            for (FullComposition.PlayableCycle cycle : row) { // For each cycle of a row
                Thread thread = new Thread(() -> {
                    long sleep = cycle.getCycle().getDuration().get() / cycle.getCycle().getNbSlots().get();
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        System.err.println("CountDownLatch failed: " + e.getMessage());
                        throw new RuntimeException(e);
                    }
                    for (MediaPlayer mediaPlayer : cycle.getSamples()) { // For each beat of a cycle
                        if(mediaPlayer != null) {
                            mediaPlayer.play();
                            mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.dispose());
                        }
                        try {
                            Thread.sleep(sleep);
                        } catch (InterruptedException e) {
                            System.err.println("Cycle sleep failed: " + e.getMessage());
                            throw new RuntimeException(e);
                        }
                    }
                    syncRow.countDown();
                });
                thread.start();
            }
            latch.countDown();
            try {
                syncRow.await();
            } catch (InterruptedException e) {
                System.err.println("Row CountDownLatch failed: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    private FullComposition beatfxModel2FullCompo(BeatfxModel beatfxModel) {
        Map<String, Integer> indexByLabel = new HashMap<>();
        for (int i = 0; i < beatfxModel.getPlayerRows().size(); i++) {
            PlayerRow playerRow = beatfxModel.getPlayerRows().get(i);
            String label = playerRow.getLabel().trim();
            if (!label.isEmpty()) {
                if (indexByLabel.containsKey(label)) {
                    throw new IllegalStateException(String.format("Several rows has the same label '%s': %s & %s ", label, indexByLabel.get(label), i));
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
            if (!gotoLabel.isEmpty()) {
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

            if (gotoRepeat > -1) {
                i = gotoIndex - 1;
            } else {
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
            mediaPlayer.setOnEndOfMedia(() -> nbActivePlayer.decrementAndGet());
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
