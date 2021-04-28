package devlaunchers.config;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import com.google.common.base.Charsets;

public class DevLauncherConfiguration extends FileConfiguration {

  private DevLauncherConfiguration parent;

  private ConfigurationSection configuration;
  private File configFile;

  public DevLauncherConfiguration(Plugin plugin, String fileName) {

    plugin.saveResource(fileName, false);

    configFile = new File(plugin.getDataFolder(), fileName);

    configuration = YamlConfiguration.loadConfiguration(configFile);

    final InputStream defConfigStream = plugin.getResource(fileName);
    if (defConfigStream == null) {
      return;
    }

    ((MemoryConfiguration) configuration)
        .setDefaults(
            YamlConfiguration.loadConfiguration(
                new InputStreamReader(defConfigStream, Charsets.UTF_8)));
  }

  private DevLauncherConfiguration(
      DevLauncherConfiguration parent, ConfigurationSection configuration) {
    this.parent = parent;
    this.configuration = configuration;
  }

  public Material getMaterial(String path) {
    if (isMaterial(path)) {
      return Material.valueOf(getString(path));
    }
    return null;
  }

  public Material getMaterial(String path, Material def) {
    Material mat = getMaterial(path);
    if (mat == null) {
      return def;
    }
    return mat;
  }

  public boolean isMaterial(String path) {
    return isString(path) && Material.valueOf(getString(path)) != null;
  }

  public List<Material> getMaterialList(String path) {
    List<String> materialNames = getStringList(path);
    if (materialNames == null) {
      return null;
    }

    return materialNames.stream().map(Material::valueOf).collect(Collectors.toList());
  }

  public List<Material> getMaterialList(String path, List<Material> def) {
    List<Material> materials = getMaterialList(path);
    if (materials == null) {
      return def;
    }
    return materials;
  }

  public ItemMeta getItemMeta(String path) {
    throw new NotImplementedException();
  }

  public ItemMeta getItemMeta(String path, ItemMeta def) {
    throw new NotImplementedException();
  }

  public boolean isItemMeta(String path) {
    return false;
  }

  // -- Reimplementation to remove unreadable Serialization --

  @Override
  public Vector getVector(String path) {
    if (isVector(path)) {
      return new Vector(getDouble(path + ".x"), getDouble(path + ".y"), getDouble(path + ".z"));
    }
    return null;
  }

  @Override
  public Vector getVector(String path, Vector def) {
    Vector vec = getVector(path);
    if (vec == null) {
      return def;
    }
    return vec;
  }

  @Override
  public boolean isVector(String path) {
    if (isSet(path + ".x") && isSet(path + ".y") && isSet(path + ".z")) {
      Object x = get(path + ".x");
      Object y = get(path + ".y");
      Object z = get(path + ".z");
      if (x instanceof Number && y instanceof Number && z instanceof Number) {
        return true;
      }
    }
    return false;
  }

  @Override
  public OfflinePlayer getOfflinePlayer(String path) {
    if (isOfflinePlayer(path)) {
      return Bukkit.getOfflinePlayer(UUID.fromString(getString(path)));
    }
    return null;
  }

  @Override
  public OfflinePlayer getOfflinePlayer(String path, OfflinePlayer def) {
    OfflinePlayer play = getOfflinePlayer(path);
    if (play == null) {
      return def;
    }
    return play;
  }

  @Override
  public boolean isOfflinePlayer(String path) {
    if (isString(path)) {
      try {
        UUID.fromString(getString(path));
        return true;
      } catch (IllegalArgumentException exc) {
      }
    }
    return false;
  }

  @Override
  public ItemStack getItemStack(String path) {
    if (isMaterial(path)) {
      return new ItemStack(getMaterial(path));
    } else if (isMaterial(path + ".material")) {
      Material mat = getMaterial(path + ".material");
      int amount = 1;

      if (isInt(path + ".amount")) {
        amount = getInt(path + ".amount");
      }

      ItemStack itemStack = new ItemStack(mat, amount);

      if (isList(path + ".lore")) {
        itemStack.setLore(getStringList(path + ".lore"));
      }

      if (isItemMeta(path + ".meta")) {
        itemStack.setItemMeta(getItemMeta(path + ".meta"));
      }
      return itemStack;
    } else {
      return null;
    }
  }

  @Override
  public ItemStack getItemStack(String path, ItemStack def) {
    throw new NotImplementedException();
  }

  @Override
  public boolean isItemStack(String path) {
    return isMaterial(path) || isMaterial(path + ".material");
  }

  @Override
  public Color getColor(String path) {
    throw new NotImplementedException();
  }

  @Override
  public Color getColor(String path, Color def) {
    throw new NotImplementedException();
  }

  @Override
  public boolean isColor(String path) {
    throw new NotImplementedException();
  }

  @Override
  public Location getLocation(String path) {
    throw new NotImplementedException();
  }

  @Override
  public Location getLocation(String path, Location def) {
    throw new NotImplementedException();
  }

  @Override
  public boolean isLocation(String path) {
    throw new NotImplementedException();
  }

  // -- Standard Configuration Methods --

  @Override
  public Set<String> getKeys(boolean deep) {
    return configuration.getKeys(deep);
  }

  @Override
  public Map<String, Object> getValues(boolean deep) {
    return configuration.getValues(deep);
  }

  @Override
  public DevLauncherConfiguration getParent() {
    return parent;
  }

  @Override
  public boolean contains(String path) {
    return configuration.contains(path);
  }

  @Override
  public boolean contains(String path, boolean ignoreDefault) {
    return configuration.contains(path, ignoreDefault);
  }

  @Override
  public boolean isSet(String path) {
    return configuration.isSet(path);
  }

  @Override
  public String getCurrentPath() {
    return configuration.getCurrentPath();
  }

  @Override
  public String getName() {
    return configuration.getName();
  }

  @Override
  public Object get(String path) {
    return configuration.get(path);
  }

  @Override
  public Object get(String path, Object def) {
    return configuration.get(path, def);
  }

  @Override
  public void set(String path, Object value) {
    configuration.set(path, value);
  }

  @Override
  public DevLauncherConfiguration createSection(String path) {
    return new DevLauncherConfiguration(this, configuration.createSection(path));
  }

  @Override
  public DevLauncherConfiguration createSection(String path, Map<?, ?> map) {
    return new DevLauncherConfiguration(this, configuration.createSection(path, map));
  }

  @Override
  public String getString(String path) {
    return configuration.getString(path);
  }

  @Override
  public String getString(String path, String def) {
    return configuration.getString(path, def);
  }

  @Override
  public boolean isString(String path) {
    return configuration.isString(path);
  }

  @Override
  public int getInt(String path) {
    return configuration.getInt(path);
  }

  @Override
  public int getInt(String path, int def) {
    return configuration.getInt(path, def);
  }

  @Override
  public boolean isInt(String path) {
    return configuration.isInt(path);
  }

  @Override
  public boolean getBoolean(String path) {
    return configuration.getBoolean(path);
  }

  @Override
  public boolean getBoolean(String path, boolean def) {
    return configuration.getBoolean(path, def);
  }

  @Override
  public boolean isBoolean(String path) {
    return configuration.isBoolean(path);
  }

  @Override
  public double getDouble(String path) {
    return configuration.getDouble(path);
  }

  @Override
  public double getDouble(String path, double def) {
    return configuration.getDouble(path, def);
  }

  @Override
  public boolean isDouble(String path) {
    return configuration.isDouble(path);
  }

  @Override
  public long getLong(String path) {
    return configuration.getLong(path);
  }

  @Override
  public long getLong(String path, long def) {
    return configuration.getLong(path, def);
  }

  @Override
  public boolean isLong(String path) {
    return configuration.isLong(path);
  }

  @Override
  public List<?> getList(String path) {
    return configuration.getList(path);
  }

  @Override
  public List<?> getList(String path, List<?> def) {
    return configuration.getList(path, def);
  }

  @Override
  public boolean isList(String path) {
    return configuration.isList(path);
  }

  @Override
  public List<String> getStringList(String path) {
    return configuration.getStringList(path);
  }

  @Override
  public List<Integer> getIntegerList(String path) {
    return configuration.getIntegerList(path);
  }

  @Override
  public List<Boolean> getBooleanList(String path) {
    return configuration.getBooleanList(path);
  }

  @Override
  public List<Double> getDoubleList(String path) {
    return configuration.getDoubleList(path);
  }

  @Override
  public List<Float> getFloatList(String path) {
    return configuration.getFloatList(path);
  }

  @Override
  public List<Long> getLongList(String path) {
    return configuration.getLongList(path);
  }

  @Override
  public List<Byte> getByteList(String path) {
    return configuration.getByteList(path);
  }

  @Override
  public List<Character> getCharacterList(String path) {
    return configuration.getCharacterList(path);
  }

  @Override
  public List<Short> getShortList(String path) {
    return configuration.getShortList(path);
  }

  @Override
  public List<Map<?, ?>> getMapList(String path) {
    return configuration.getMapList(path);
  }

  @Override
  public <T> T getObject(String path, Class<T> clazz) {
    return configuration.getObject(path, clazz);
  }

  @Override
  public <T> T getObject(String path, Class<T> clazz, T def) {
    return configuration.getObject(path, clazz, def);
  }

  @Override
  public <T extends ConfigurationSerializable> T getSerializable(String path, Class<T> clazz) {
    return configuration.getSerializable(path, clazz);
  }

  @Override
  public <T extends ConfigurationSerializable> T getSerializable(
      String path, Class<T> clazz, T def) {
    return configuration.getSerializable(path, clazz, def);
  }

  @Override
  public DevLauncherConfiguration getConfigurationSection(String path) {
    return new DevLauncherConfiguration(this, configuration.getConfigurationSection(path));
  }

  @Override
  public boolean isConfigurationSection(String path) {
    return configuration.isConfigurationSection(path);
  }

  @Override
  public void addDefault(String path, Object value) {
    configuration.addDefault(path, value);
  }

  @Override
  public String saveToString() {
    if (parent != null) {
      return parent.saveToString();
    }
    return ((FileConfiguration) configuration).saveToString();
  }

  @Override
  public void loadFromString(String contents) throws InvalidConfigurationException {
    if (parent != null) {
      parent.loadFromString(contents);
    } else {
      ((FileConfiguration) configuration).loadFromString(contents);
    }
  }

  // Unused
  @Override
  protected String buildHeader() {
    return "";
  }
}
