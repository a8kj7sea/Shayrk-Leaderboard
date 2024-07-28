package dev.shayrk.leaderboards.entity.general;

import org.bukkit.Location;

import dev.shayrk.leaderboards.enums.PlayerDataType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
public class Leaderboard {

    private String id;
    private String type;
    private String header;
    private String footer;

    private Location location;

    public static Leaderboard of(String id, String type, String header, String footer, Location location) {
        return new Leaderboard(id, type, header, footer, location);
    }

    public static Leaderboard of(String id, PlayerDataType type, String header, String footer, Location location) {
        return new Leaderboard(id, type.name().toLowerCase(), header, footer, location);
    }
}
