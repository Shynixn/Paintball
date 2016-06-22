package me.synapz.paintball.compat;

import fr.neatmonster.nocheatplus.NCPAPIProvider;
import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.checks.access.IViolationInfo;
import fr.neatmonster.nocheatplus.components.NoCheatPlusAPI;
import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;
import fr.neatmonster.nocheatplus.hooks.NCPHook;
import fr.neatmonster.nocheatplus.hooks.NCPHookManager;
import org.bukkit.entity.Player;

/**
 * Created by Jeremy Lugo on 6/21/2016.
 */
public class NoCheatPlusCompat implements NCPHook{

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
        switch (checkType) {
            case FIGHT_SPEED:
                double vl = iViolationInfo.getTotalVl();
                break;
        }
        return false;
    }
}
