package devlaunchers.config;

import com.google.common.base.Charsets;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class DevLauncherConfiguration extends FileConfiguration {

  private static Map<String, Color> colorMapping = new HashMap<String, Color>();

  static {
    initColorMapping();
  }

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

  public ItemMeta loadItemMetaForItemStack(String path, ItemMeta baseMeta) {
    if (isString(path + ".displayName")) {
      baseMeta.setDisplayName(getString(path + ".displayName"));
    }

    if (isInt(path + ".modelData")) {
      baseMeta.setCustomModelData(getInt(path + ".modelData"));
    }

    if (isBoolean(path + ".unbreakable")) {
      baseMeta.setUnbreakable(getBoolean(path + ".unbreakable"));
    }

    if (isList(path + ".placeable")) {
      baseMeta.setPlaceableKeys(
          getStringList(path + ".placeable")
              .stream()
              .map(NamespacedKey::minecraft)
              .collect(Collectors.toList()));
    }

    if (isList(path + ".destroyable")) {
      baseMeta.setDestroyableKeys(
          getStringList(path + ".destroyable")
              .stream()
              .map(NamespacedKey::minecraft)
              .collect(Collectors.toList()));
    }

    if (isConfigurationSection(path + ".attributes")) {
      Map<String, Object> values = getConfigurationSection(path + ".attributes").getValues(true);
      for (String attr : values.keySet()) {
        Attribute attribute = Attribute.valueOf(attr);
        if (attribute == null) continue;

        Map<String, Object> modifier = (Map<String, Object>) values.get(attr);
        AttributeModifier attributeModifier = AttributeModifier.deserialize(modifier);
        baseMeta.addAttributeModifier(attribute, attributeModifier);
      }
    }

    if (isConfigurationSection(path + ".enchantments")) {
      for (String enchantment : getConfigurationSection(path + ".enchantments").getKeys(false)) {
        Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(enchantment));
        if (ench == null) continue;

        int level = getInt(path + ".enchantments." + enchantment);
        baseMeta.addEnchant(ench, level, true);
      }
    }

    if (isList(path + ".flags")) {
      getStringList(path + ".flags")
          .stream()
          .map(ItemFlag::valueOf)
          .forEach(baseMeta::addItemFlags);
    }
    return baseMeta;
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

      if (isConfigurationSection(path + ".meta")) {
        ItemMeta meta = loadItemMetaForItemStack(path + ".meta", itemStack.getItemMeta());
        itemStack.setItemMeta(meta);
      }

      return itemStack;
    } else {
      return null;
    }
  }

  @Override
  public ItemStack getItemStack(String path, ItemStack def) {
    ItemStack is = getItemStack(path);
    if (is == null) {
      return def;
    }
    return is;
  }

  @Override
  public boolean isItemStack(String path) {
    return isMaterial(path) || isMaterial(path + ".material");
  }

  @Override
  public Color getColor(String path) {
    if (isColor(path)) {
      if (isString(path)) {
        return colorMapping.get(getString(path).toLowerCase());
      } else {
        return Color.fromRGB(
            getInt(path + ".red"), getInt(path + ".green"), getInt(path + ".blue"));
      }
    }
    return null;
  }

  @Override
  public Color getColor(String path, Color def) {
    Color color = getColor(path);
    if (color == null) {
      return def;
    }
    return color;
  }

  @Override
  public boolean isColor(String path) {
    if (isString(path) && colorMapping.containsKey(getString(path).toLowerCase())) {
      return true;
    } else {
      return isInt(path + ".red") && isInt(path + ".green") && isInt(path + ".blue");
    }
  }

  @Override
  public Location getLocation(String path) {
    if (isLocation(path)) {
      World world = Bukkit.getWorld(getString(path + ".world"));
      if (world == null) return null;

      double x = getDouble(path + ".x");
      double y = getDouble(path + ".y");
      double z = getDouble(path + ".z");
      float yaw = (float) getDouble(path + ".yaw", 0);
      float pitch = (float) getDouble(path + ".pitch", 0);
      return new Location(world, x, y, z, yaw, pitch);
    }
    return null;
  }

  @Override
  public Location getLocation(String path, Location def) {
    Location loc = getLocation(path);
    if (loc == null) {
      return def;
    }
    return loc;
  }

  @Override
  public boolean isLocation(String path) {
    return isString(path + ".world")
        && isNumber(path + ".x")
        && isNumber(path + ".y")
        && isNumber(path + ".z");
  }

  public boolean isNumber(String path) {
    return get(path) instanceof Number;
  }

  // Override Setter

  @Override
  public void set(String path, Object value) {
    if (value instanceof Color) {
      set(path, (Color) value);
    } else if (value instanceof ItemStack) {
      set(path, (ItemStack) value);
    } else if (value instanceof Location) {
      set(path, (Location) value);
    } else if (value instanceof Material) {
      set(path, (Material) value);
    } else if (value instanceof OfflinePlayer) {
      set(path, (OfflinePlayer) value);
    } else if (value instanceof Vector) {
      set(path, (Vector) value);
    } else if (value instanceof List
        && !((List<?>) value).isEmpty()
        && ((List<?>) value).get(0) instanceof Material) {
      // MaterialList
      List<Material> value2 = (List<Material>) value;
      set(path, value2);
    } else {
      configuration.set(path, value);
    }
  }

  public void set(String path, Color color) {
    if (colorMapping.containsValue(color)) {
      for (String key : colorMapping.keySet()) {
        if (colorMapping.get(key).equals(color)) {
          set(path, key);
          return;
        }
      }
    } else {
      set(path + ".red", color.getRed());
      set(path + ".green", color.getGreen());
      set(path + ".blue", color.getBlue());
    }
  }

  public void set(String path, ItemStack itemStack) {
    if (itemStack.equals(new ItemStack(itemStack.getType()))) {
      set(path, itemStack.getType());
    } else {
      set(path + ".material", itemStack.getType());
      set(path + ".amount", itemStack.getAmount());

      if (itemStack.hasItemMeta()) {
        if (itemStack.getLore() != null) {
          set(path + ".lore", itemStack.getLore());
        }
        ItemMeta meta = itemStack.getItemMeta();

        if (meta.hasDisplayName()) {
          set(path + ".meta.displayName", meta.getDisplayName());
        }
        if (meta.hasCustomModelData()) {
          set(path + ".meta.modelData", meta.getCustomModelData());
        }
        if (meta.isUnbreakable()) {
          set(path + ".meta.unbreakable", true);
        }
        if (meta.hasPlaceableKeys()) {
          set(
              path + ".meta.placeable",
              meta.getPlaceableKeys()
                  .stream()
                  .map((key) -> key.getKey())
                  .collect(Collectors.toList()));
        }
        if (meta.hasDestroyableKeys()) {
          set(
              path + ".meta.destroyable",
              meta.getDestroyableKeys()
                  .stream()
                  .map((key) -> key.getKey())
                  .collect(Collectors.toList()));
        }
        if (meta.hasAttributeModifiers()) {
          meta.getAttributeModifiers()
              .forEach(
                  (attr, mod) -> {
                    set(path + ".meta.attributes." + attr.name(), mod.serialize());
                  });
        }
        if (meta.hasEnchants()) {
          meta.getEnchants()
              .forEach(
                  (ench, level) -> {
                    set(path + ".meta.enchantments." + ench.getKey().getKey(), level);
                  });
        }
        if (!meta.getItemFlags().isEmpty()) {
          set(
              path + ".meta.flags",
              meta.getItemFlags().stream().map((flag) -> flag.name()).collect(Collectors.toList()));
        }
      }
    }
  }

  public void set(String path, Location location) {
    set(path + ".x", location.getX());
    set(path + ".y", location.getY());
    set(path + ".z", location.getZ());
    set(path + ".yaw", location.getYaw());
    set(path + ".pitch", location.getPitch());
    set(path + ".world", location.getWorld().getName());
  }

  public void set(String path, Material material) {
    set(path, material.name());
  }

  public void set(String path, List<Material> materialList) {
    set(path, materialList.stream().map((mat) -> mat.name()).collect(Collectors.toList()));
  }

  public void set(String path, OfflinePlayer player) {
    set(path, player.getUniqueId().toString());
  }

  public void set(String path, Vector vec) {
    set(path + ".x", vec.getX());
    set(path + ".y", vec.getY());
    set(path + ".z", vec.getZ());
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

  private static void initColorMapping() {
    colorMapping.put("aqua", Color.AQUA);
    colorMapping.put("black", Color.BLACK);
    colorMapping.put("blue", Color.BLUE);
    colorMapping.put("fuchsia", Color.FUCHSIA);
    colorMapping.put("gray", Color.GRAY);
    colorMapping.put("green", Color.GREEN);
    colorMapping.put("lime", Color.LIME);
    colorMapping.put("maroon", Color.MAROON);
    colorMapping.put("navy", Color.NAVY);
    colorMapping.put("olive", Color.OLIVE);
    colorMapping.put("orange", Color.ORANGE);
    colorMapping.put("purple", Color.PURPLE);
    colorMapping.put("red", Color.RED);
    colorMapping.put("silver", Color.SILVER);
    colorMapping.put("teal", Color.TEAL);
    colorMapping.put("white", Color.WHITE);
    colorMapping.put("yellow", Color.YELLOW);
  }
}
