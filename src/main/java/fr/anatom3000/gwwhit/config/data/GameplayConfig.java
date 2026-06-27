package fr.anatom3000.gwwhit.config.data;

import fr.anatom3000.gwwhit.config.SyncOptions;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.BoundedDiscrete;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;

import static fr.anatom3000.gwwhit.config.SyncOptions.*;

@SuppressWarnings("CanBeFinal") // Shut up code inspection
@Config(name = "gameplay")
public class GameplayConfig implements ConfigData {
    GameplayConfig(){}

    @SyncOptions(GROUP) @CollapsibleObject public Items items = new Items();
    @SyncOptions(GROUP) @CollapsibleObject public Blocks blocks = new Blocks();
    @SyncOptions(SERVER) public boolean stoneBlocksAreInfected = false;
    @SyncOptions(SERVER) public boolean mobsMayExplode = false;
    @SyncOptions(SERVER) public boolean randomizedDrops = false;
    @SyncOptions(SERVER) @Tooltip(count = 2) public boolean dreamLuck = false;
    @SyncOptions(CLIENT) @Tooltip public boolean killCulling = false;
    @SyncOptions(SERVER) @Tooltip public boolean farlands = false;

    public static class Items {
        @SyncOptions(BOTH | SYNCED) public boolean everythingIsEdible = false;
        @SyncOptions(BOTH | SYNCED) @Tooltip public boolean hiddenItemsTab = false;
        @SyncOptions(BOTH | SYNCED) @Tooltip public boolean noHardcodedItemCooldown = false;
        @SyncOptions(BOTH | SYNCED) @BoundedDiscrete(min = 1, max = 127) public int maxStackSize = 64;
        @SyncOptions(BOTH | SYNCED) @BoundedDiscrete(min = 1, max = 10) public int yeeterPowerMultiplier = 1;
    }

    public static class Blocks {
        @SyncOptions(CLIENT | SYNCED) @BoundedDiscrete(max = 1) public float defaultSlipperiness = 0.6F;
        @SyncOptions(BOTH | SYNCED) public boolean replaceEverything = false;
        @SyncOptions(SERVER) public boolean everythingBurns = false;
    }
}
