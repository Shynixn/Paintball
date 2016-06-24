package me.synapz.paintball.compat;

import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.checks.access.IViolationInfo;
import fr.neatmonster.nocheatplus.hooks.NCPHook;
import fr.neatmonster.nocheatplus.hooks.NCPHookManager;
import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.enums.Items;
import me.synapz.paintball.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Jeremy Lugo on 6/21/2016.
 */
public class NoCheatPlusCompat implements NCPHook {

    /*
    This needs to be in this class because if the Paintball main class has an import to NCP, the plugin will not load unless NCP is installed
    This way it is not required to install since this class will only load if the plugin has "NoCheatPlus"
     */
    public static void addHook() {
        NCPHookManager.addHook(
                new CheckType[]{ CheckType.FIGHT_SPEED, CheckType.MOVING_SURVIVALFLY }, new NoCheatPlusCompat());
    }

    @Override
    public String getHookName() {
        return "Paintball";
    }

    @Override
    public String getHookVersion() {
        return "1.0";
    }

    @Override
    public boolean onCheckFailure(CheckType checkType, Player player, IViolationInfo iViolationInfo) {
        Arena arena = ArenaManager.getArenaManager().getArena(player);
        if (arena == null) return false;

        switch (checkType) {
            case FIGHT_SPEED:
                return true;
            case MOVING_SURVIVALFLY:
                if (inventoryContainsItem(player)) return true;
        }
        return false;
    }
    private boolean inventoryContainsItem(Player player) {
        for (ItemStack itemStack : player.getInventory()) {
            if (Utils.equals(itemStack, Items.PAINTBALL_SHOWER.getName()))
                return true;
        }
        return false;
    }
}
