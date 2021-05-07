package devlaunchers.economy;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public abstract class Economy {

  public static Economy instance;

  private Plugin economyPlugin;

  public Economy(Plugin economyPlugin) {
    this.economyPlugin = economyPlugin;
    Economy.instance = this;
  }

  public abstract int getBalance(Player player);

  public abstract MoneyTransferResult giveMoney(Player player, int amount);

  public abstract MoneyTransferResult takeMoney(Player player, int amount);

  public abstract MoneyTransferResult transferMoney(Player sender, Player receiver, int amount);

  public static Economy getInstance() {
    return instance;
  }

  public Plugin getEconomyPlugin() {
    return economyPlugin;
  }

  public enum MoneyTransferResult {
    SUCCESS(true),
    INSUFFICIENT_BALANCE(false),
    INVENTORY_OVERFLOW(false);

    private boolean _success;

    private MoneyTransferResult(boolean success) {
      this._success = success;
    }

    public boolean isSuccessfull() {
      return _success;
    }
  }
}
