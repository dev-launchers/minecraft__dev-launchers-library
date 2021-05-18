package devlaunchers.items;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public final class ConfigItemStack extends ItemStack implements ConfigurationSerializable {

    public static ItemStack deserialize(Map<String, Object> args) {
        String itemName = (String) args.get("devLauncherItem");
        int amount = 1;

        ItemStack item = ItemRepository.getItem(itemName);
        if(args.containsKey("amount")){
            amount =  (Integer) args.get("amount");
        }
        item.setAmount(amount);

        return item;
    }

}
