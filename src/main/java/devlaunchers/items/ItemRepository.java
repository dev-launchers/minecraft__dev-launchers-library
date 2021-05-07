package devlaunchers.items;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

public final class ItemRepository {

  private static final Map<String, ItemStack> registeredItems = new HashMap<String, ItemStack>();

  public static void clearRepository() {
    registeredItems.clear();
  }

  /**
   * Registers a Item Definition that is to be shared between Plugins.
   *
   * @param name Item Name, must be unique
   * @param item Item Definition
   * @return true, only if Item Name is unique and registering the Item Definition was successfull.
   */
  public static boolean registerItem(String name, ItemStack item) {
    if (registeredItems.containsKey(name)) {
      return false;
    }
    registeredItems.put(name, item);
    return true;
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
    return registerItem(itemName.name(), item);
  }

  public static ItemStack getItem(String name) {
    return registeredItems.get(name);
  }

  public static <T extends Enum> ItemStack getItem(T name) {
    return getItem(name.name());
  }
}
