package dev.shayrk.leaderboards.manager;

import java.util.Map;
import com.google.common.collect.Maps;

import dev.shayrk.leaderboards.entity.player.User;
import lombok.Getter;

@Getter
public class UsersManager {

    private Map<String, User> users = Maps.newHashMap();

    public void updateUsersMap(Map<String, User> newMap) {
        users.clear();
        users = newMap;
    }

    public void addUser(String name, User user) {
        if (users.containsKey(name) || users.containsValue(user))
            return;
        users.put(name, user);
    }

    public void removeUser(String name) {
        users.remove(name);
    }

    public User getUserByName(String name) {
        return users.get(name);
    }

    public Map<String, User> getUsers() {
        return users;
    }

}
