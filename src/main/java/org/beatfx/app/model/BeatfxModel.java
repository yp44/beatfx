package org.beatfx.app.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class BeatfxModel {

    private List<Cycle> cycles = new ArrayList<>(Collections.singleton(new Cycle("First", false)));

}
