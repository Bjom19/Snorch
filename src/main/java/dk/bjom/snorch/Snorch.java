package dk.bjom.snorch;

import org.bukkit.plugin.java.JavaPlugin;

public final class Snorch extends JavaPlugin {
    private EventListener eventListener;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        SnorchTracker tracker = new SnorchTracker(this);
        new SnorchRecipes(this);
        eventListener = new EventListener(this, tracker);
    }

    @Override
    public void onDisable() {
        if (eventListener != null) eventListener.shutdown();
    }
}
