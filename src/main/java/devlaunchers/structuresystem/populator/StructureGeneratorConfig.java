package devlaunchers.structuresystem.populator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import devlaunchers.config.DevLauncherConfiguration;
import devlaunchers.plugin.DevLaunchersPlugin;

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
