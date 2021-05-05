# DevLaunchers Library
A Library for the Development of Minecraft Paper plugins.
## Features
- Custom Made Plugin class called DevLaunchersPlugin<br>
  Extends default JavaPlugin by providing custom getConfig() Methods and exposing the ItemRepository easily.
- Provides a ItemRepository for Plugins to share Item Definitions with each other (Example: Byte Economy "Byte$")<br>
  Preferres Use of Enums for Naming (e.g. DevLauncherItem)
  
## Examples
### Main Class
``` 
public class TestPlugin extends DevLaunchersPlugin {
  
  @Override
  public static void onEnable(){
    // Reads default values of items.yml inside the jar if it exists
    // In all Instances it will try to read the config from plugins/{PluginName}/{configName}
    DevLauncherConfiguration itemConfig = getConfig("items.yml");
    // Registers Item read from the Config with the Identifier "TEST_ITEM"
    registerItem(DevLauncherItem.TEST_ITEM, itemConfig.getItemStack("test.item"));
    
    ItemStack testItem = getItem(DevLauncherItem.TEST_ITEM);
  }
}
```
### items.yml
```
test:
  item:
    material: COMPASS
    amount: 1
    lore:
      - "§fTesting..."
    meta:
      displayName: "§aTest Item"
```

## Maven
### Add Jitpack Repository
```
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>
```
### Add Dependency
```
<dependency>
  <groupId>com.github.dev-launchers</groupId>
  <artifactId>minecraft__dev-launchers-library</artifactId>
  <version>v0.0.2</version>
</dependency>
```

## Contributing
### Code Style
For this Repository the [Google Java Code Style](https://github.com/google/google-java-format) with Version 1.6 is enforced on Pull Requests.<br>
There are Plugins for IntelliJ and Eclipse that format it correctly already. If you have problems with this just ask [Zorro909](https://github.com/Zorro909) for some help. (Also available over Discord)
### Pull Requests
For larger Pull Requests a Issue should first be created to discuss the problem/feature at hand.
Small Changes may directly create a Pull Request with a description of what they changed and why.
