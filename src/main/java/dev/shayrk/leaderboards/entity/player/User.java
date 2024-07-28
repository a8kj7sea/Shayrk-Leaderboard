package dev.shayrk.leaderboards.entity.player;

import dev.shayrk.leaderboards.enums.PlayerDataType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class User {

    private int kills, deaths;
    private final String name;

    public int getPlayerDataAmount(PlayerDataType type) {
        try {
            // Convert enum type to field name
            String fieldName = type.name().toLowerCase();

            // Get the field by name and make it accessible
            java.lang.reflect.Field field = this.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            // Return the field's value
            return (int) field.get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Error accessing field: " + type, e);
        }
    }

    public int setPlayerDataAmount(PlayerDataType type, int amount) {
        try {
            // Convert enum type to field name
            String fieldName = type.name().toLowerCase();

            // Get the field by name and make it accessible
            java.lang.reflect.Field field = this.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            // Set the field's value
            field.set(this, amount);
            return amount;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Error accessing field: " + type, e);
        }
    }
}
