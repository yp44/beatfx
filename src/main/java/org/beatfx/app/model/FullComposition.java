package org.beatfx.app.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FullComposition {

    private List<List<PlayableCycle>> compo = new ArrayList<>();

    @Data
    public final static class PlayableCycle{
        private final Cycle cycle;

        public PlayableCycle(Cycle c){
            this.cycle = c;
        }
    }

}
