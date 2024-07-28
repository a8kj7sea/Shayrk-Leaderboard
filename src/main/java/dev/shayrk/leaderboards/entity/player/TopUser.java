package dev.shayrk.leaderboards.entity.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class TopUser {

    private int dataAmount;
    private String name;
    private int pos;
}
