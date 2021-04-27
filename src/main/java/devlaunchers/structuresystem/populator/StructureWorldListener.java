package devlaunchers.structuresystem.populator;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.util.ArrayList;
import java.util.List;

public class StructureWorldListener implements Listener {

    private static List<StructurePopulator> populators = new ArrayList<StructurePopulator>();

    private static List<String> populatedWorlds = new ArrayList<>();

    @EventHandler
    public void onWorldInit(WorldInitEvent e) {
        World world = e.getWorld();
        if (!populatedWorlds.contains(world.getName())) {
            Bukkit.getLogger().info("onWorldInit: WORLD '" + world.getName() + "' HAS INITIALIZED");
            // spawn is generated before the next line is called
            world.getPopulators().addAll(populators);
            populatedWorlds.add(world.getName());
        }
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent e) {
        populatedWorlds.remove(e.getWorld().getName());
    }

    public static void registerPopulator(StructurePopulator populator) {
        populators.add(populator);
    }
}
