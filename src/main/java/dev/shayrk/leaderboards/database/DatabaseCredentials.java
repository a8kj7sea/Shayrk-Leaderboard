package dev.shayrk.leaderboards.database;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DatabaseCredentials {

    private String host;
    private String userName;
    private String password;
    private String database;
    private int port;

}
