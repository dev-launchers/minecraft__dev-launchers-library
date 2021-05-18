package devlaunchers.plugin;

import devlaunchers.items.ItemRepository;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

public class LibraryPlugin extends DevLaunchersPlugin {

  @Override
  public void onEnable() {
    ConfigurationSerialization.registerClass(devlaunchers.items.ConfigItemStack.class);

    this.getLogger()
        .info(
            "Loading "
                + getName()
                + "-"
                + getClass().getPackage().getImplementationVersion()
                + "...");
  }

  @Override
  public void onDisable() {
    ItemRepository.clearRepository();
  }
}
