package dev.shayrk.leaderboards.entity.player;

import dev.shayrk.leaderboards.enums.PlayerDataType;

public interface IUserData {

    public void setData(User user, PlayerDataType type, int amount);

    default public void addData(User user, PlayerDataType type, int amount) {
        int currentData = getData(user, type);
        setData(user, type, currentData + Math.abs(amount));
    }

    default public void removeData(User user, PlayerDataType type, int amount) {
        int currentData = getData(user, type);
        if (currentData <= Math.abs(amount)) {
            setData(user, type, 0);
            return;
        }
        setData(user, type, currentData - Math.abs(amount));
    }

    public int getData(User user, PlayerDataType type);

}
