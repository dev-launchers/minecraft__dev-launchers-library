package devlaunchers.plugin;

import devlaunchers.config.DevLauncherConfiguration;
import devlaunchers.items.ItemRepository;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class DevLaunchersPlugin extends JavaPlugin {

  private Map<String, DevLauncherConfiguration> configurations = new HashMap<>();

  @Override
  public DevLauncherConfiguration getConfig() {
    return getConfig("config.yml");
  }

  public DevLauncherConfiguration getConfig(String fileName) {
    if (!configurations.containsKey(fileName)) {
      reloadConfig(fileName);
    }
    return configurations.get(fileName);
  }

  @Override
  public void reloadConfig() {
    reloadConfig("config.yml");
  }

  public void reloadConfig(String fileName) {
    configurations.put(fileName, new DevLauncherConfiguration(this, fileName));
  }

  /**
   * Registers a Item Definition that is to be shared between Plugins.
   *
   * @param name Item Name, must be unique
   * @param item Item Definition
   * @return true, only if Item Name is unique and registering the Item Definition was successfull.
   */
  public static boolean registerItem(String name, ItemStack item) {
    return ItemRepository.registerItem(name, item);
  }

  /**
   * Registers a Item Definition that is to be shared between Plugins.
   *
   * @param <T>
   * @param itemName Item Name, must be unique
   * @param item Item Definition
   * @return true, only if Item Name is unique and registering the Item Definition was successfull.
   */
  public static <T extends Enum> boolean registerItem(T itemName, ItemStack item) {
    return ItemRepository.registerItem(itemName, item);
  }

  public static ItemStack getItem(String name) {
    return ItemRepository.getItem(name);
  }

  public static <T extends Enum> ItemStack getItem(T name) {
    return ItemRepository.getItem(name);
  }
}
