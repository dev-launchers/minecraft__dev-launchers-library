package devlaunchers.structuresystem.populator;

import devlaunchers.config.DevLauncherConfiguration;
import java.util.List;
import java.util.regex.Pattern;
import org.bukkit.plugin.java.JavaPlugin;

public class StructureGeneratorConfig extends DevLauncherConfiguration {

  private List<String> allowedWorlds;

  public StructureGeneratorConfig(String structureName, JavaPlugin plugin) {
    super(plugin, structureName + ".yml");

    allowedWorlds = getStringList("allowedWorlds");
  }

  public boolean shouldWorldBePopulated(String worldName) {
    for (String worldRegex : allowedWorlds) {
      if (Pattern.matches(worldRegex, worldName)) {
        return true;
      }
    }
    return false;
  }
}
