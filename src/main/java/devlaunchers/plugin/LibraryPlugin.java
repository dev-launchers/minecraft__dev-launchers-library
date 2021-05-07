package devlaunchers.plugin;

import devlaunchers.items.ItemRepository;

public class LibraryPlugin extends DevLaunchersPlugin {

  @Override
  public void onEnable() {
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
