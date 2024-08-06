package me.aleksilassila.litematica.printer.v1_20_4.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.litematica.config.Configs;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigDouble;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import me.aleksilassila.litematica.printer.v1_20_4.Printer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;

import javax.annotation.Nullable;
import java.util.List;

public class PrinterConfig {
    private PrinterConfig() {}
    @Nullable
    public static PrinterConfig INSTANCE;

    public static PrinterConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PrinterConfig();
        }
        return INSTANCE;
    }
    public static final ConfigInteger TICK_DELAY = new ConfigInteger("printerTickDelay", 0, 0, 100, "Tick delay between actions. 0 = no delay.");
    public static final ConfigInteger BLOCK_TIMEOUT = new ConfigInteger("printerBlockTimeout", 10, 0, 100, "How many ticks to wait before trying to place the same block again.");
    public static final ConfigBoolean ROTATE_PLAYER = new ConfigBoolean("printerRotatePlayer", true, "Rotate the player to face the block to place.");
    public static final ConfigBoolean SNAP_BACK = new ConfigBoolean("printerSnapBask", false, "Snap back to the view direction after placing a block.");
    public static final ConfigBoolean STOP_ON_MOVEMENT = new ConfigBoolean("printerStopOnMovement", false, "Stop the printer if the player velocity is to high.");
    public static final ConfigBoolean INTERPOLATE_LOOK = new ConfigBoolean("printerInterpolateLook", true, "Interpolate the player look direction packets.");
    public static final ConfigDouble INTERPOLATE_LOOK_MAX_ANGLE = new ConfigDouble("printerInterpolateLookMaxAngle", 10, "Interpolate the player look direction packets.");
    public static final ConfigBoolean LENIENT_STEALTH = new ConfigBoolean("printerLeaneantStealth", true, "Lenient stealth mode. Good enough for grim and offers better placement speed.");
    public static final ConfigBoolean CARPET_MODE = new ConfigBoolean("printerCarpetMode", true, "Carpet mode. For placing carpets on the top of blocks.");
    public static final ConfigInteger INACTIVE_SNAP_BACK = new ConfigInteger("printerInactiveSnapBack", 10, "Snap back to the view direction after placing a block.");
    public static final ConfigInteger INVENTORY_DELAY = new ConfigInteger("printerInventoryDelay", 10, 0, 100, "The delay between each inventory action. 0 = no delay.");
    public static final ConfigBoolean INVENTORY_NO_MULTI_ACTION = new ConfigBoolean("printerInventoryNoMultiAction", true, "Only allow one inventory action at a time.");
    public static final ConfigBoolean INVENTORY_PAUSE_PLACEMENT = new ConfigBoolean("printerInventoryPausePlacement", true, "Pause the printing process when the inventory is waiting on an action to finish.");
    public static final ConfigInteger INVENTORY_AFTER_EQUIP_USE_DELAY = new ConfigInteger("printerInventoryAfterEquipUseDelay", 10, 0, 100, "Delay on an item usage after it landed in the hotbar slot.");
    public static final ConfigBoolean RAYCAST = new ConfigBoolean("printerRaycast", false, "Raycast the block to place to check if it is visible.");
    public static final ConfigBoolean NO_PLACEMENT_CACHE = new ConfigBoolean("printerNoPlacementCache", false, "Disable the placement cache. This will make the printer slower but more accurate.");
    public static final ConfigBoolean RAYCAST_STRICT_BLOCK_HIT = new ConfigBoolean("printerRaycastStrictBlockHit", false, "Check if the right side of the block is hit.");
    public static final ConfigBoolean PREVENT_DOUBLE_TAP_SPRINTING = new ConfigBoolean("printerPreventDoubleTapSprinting", false, "Prevent double tap sprinting when the printer is active.");
    public static final ConfigBoolean MOVE_WHILE_IN_INVENTORY = new ConfigBoolean("printerMoveWhileInInventory", false, "Allows the player to move while the player inventory is open.");
    public static final ConfigBoolean FREE_LOOK = new ConfigBoolean("printerFreeLook", false, "Free look mode. Allows you to look around while the printer is active.");
    public static final ConfigHotkey FREE_LOOK_TOGGLE = new ConfigHotkey("printerFreeLookToggle", "", KeybindSettings.MODIFIER_INGAME, "Free look mode. Allows you to look around while the printer is active.");
    public static final ConfigBoolean FREE_LOOK_THIRD_PERSON = new ConfigBoolean("printerFreeLookThirdPerson", true, "Free look mode. Allows you to look around while the printer is active.");
    public static final ConfigInteger FREE_LOOK_LOOK_BACK = new ConfigInteger("printerFreeLookLookBackDelay", 1, 0, 100, "Time in ticks until the player character is rotated back to the camera view. 0 to disable.");
    public static final ConfigBoolean FREE_LOOK_LOOK_BACK_ALWAYS_ROTATE_PLAYER = new ConfigBoolean("printerFreeLookLookBackAlwaysRotatePlayer", false, "Always rotate the player back to the camera view.\nMakes it more compatible with Baritone edge cases but is more intrusive and might cause other issues.");
    public static final ConfigBoolean STRICT_BLOCK_FACE_CHECK = new ConfigBoolean("printerStrictBlockFaceCheck", true, "Places only against block faces that are facing the player.");
    public static final ConfigHotkey PRINTER_PICK_BLOCK = new ConfigHotkey("printerPickBlock", "MIDDLE_MOUSE", KeybindSettings.PRESS_ALLOWEXTRA_EMPTY, "Pick block while printer is active.");
    public static final ConfigBoolean PRINTER_DEBUG_LOG = new ConfigBoolean("printerDebugLog", false, "Print debug messages to the console.");
    public static final ConfigBoolean PRINTER_IGNORE_ROTATION = new ConfigBoolean("printerIgnoreRotation", false, "Ignore the block rotation when placing.");
    public static final ConfigBoolean PRINTER_ALLOW_NONE_EXACT_STATES = new ConfigBoolean("printerAllowNoneExactStates", false, "Allow none exact block states to be placed.\nThis includes things like lichen, muchroom stems, etc.");
    public static final ConfigBoolean PRINTER_DISABLE_IN_GUIS = new ConfigBoolean("printerDisableInGuis", true, "Disable the printer in GUIs.");
    public static final ConfigBoolean PRINTER_AIRPLACE = new ConfigBoolean("printerAirPlace", false, "Place blocks in the air.");
    public static final ConfigDouble PRINTER_AIRPLACE_RANGE = new ConfigDouble("printerAirPlaceRange", 5, 0, 10, "Range at which the printer can air place at");
    public static final ConfigBoolean PRINTER_AIRPLACE_FLOATING_ONLY = new ConfigBoolean("printerAirPlaceFloatingOnly", false, "Only attempt to air place if the block position is surrounded by air.");
    public static final ConfigInteger PRINTER_MIN_INACTIVE_TIME_AIR_PLACE = new ConfigInteger("printerMinInactiveTimeAirPlace", 5, "Minimum time in ticks to wait before placing a block in the air.");
    public ImmutableList<IConfigBase> getOptions() {
        List<IConfigBase> list = new java.util.ArrayList<>(Configs.Generic.OPTIONS);
        list.add(TICK_DELAY);
        list.add(BLOCK_TIMEOUT);
        list.add(ROTATE_PLAYER);
        list.add(STOP_ON_MOVEMENT);
        list.add(CARPET_MODE);
        list.add(INVENTORY_DELAY);
        list.add(INVENTORY_NO_MULTI_ACTION);
        list.add(INVENTORY_PAUSE_PLACEMENT);
        list.add(INVENTORY_AFTER_EQUIP_USE_DELAY);
        list.add(RAYCAST);
        list.add(NO_PLACEMENT_CACHE);
        list.add(RAYCAST_STRICT_BLOCK_HIT);
        list.add(PREVENT_DOUBLE_TAP_SPRINTING);
        list.add(MOVE_WHILE_IN_INVENTORY);
        list.add(FREE_LOOK);
        list.add(FREE_LOOK_TOGGLE);
        list.add(STRICT_BLOCK_FACE_CHECK);
        list.add(FREE_LOOK_THIRD_PERSON);
        list.add(FREE_LOOK_LOOK_BACK);
        list.add(FREE_LOOK_LOOK_BACK_ALWAYS_ROTATE_PLAYER);
        list.add(PRINTER_PICK_BLOCK);
        list.add(PRINTER_DEBUG_LOG);
        list.add(PRINTER_IGNORE_ROTATION);
        list.add(PRINTER_ALLOW_NONE_EXACT_STATES);
        list.add(PRINTER_DISABLE_IN_GUIS);
        list.add(PRINTER_AIRPLACE);
        list.add(PRINTER_AIRPLACE_RANGE);
        list.add(PRINTER_AIRPLACE_FLOATING_ONLY);
        list.add(PRINTER_MIN_INACTIVE_TIME_AIR_PLACE);

        PRINTER_DEBUG_LOG.setValueChangeCallback(config -> {
            if (config.getBooleanValue()) {
                MaLiLib.logger.info("Printer debug logging enabled");
                Configurator.setLevel(LogManager.getLogger(Printer.logger.getName()), Level.DEBUG);
            } else {
                MaLiLib.logger.info("Printer debug logging disabled");
                Configurator.setLevel(LogManager.getLogger(Printer.logger.getName()), Level.INFO);
            }
        });

        return ImmutableList.copyOf(list);
    }

    public static void onInitialize() {
        MaLiLib.logger.info("PrinterConfig.onInitialize");
        FREE_LOOK_TOGGLE.getKeybind().setCallback(new FreeLookKeyCallbackToggle(FREE_LOOK));
        PRINTER_PICK_BLOCK.getKeybind().setCallback(new PrinterPickBlockKeyCallback());
        InputEventHandler.getKeybindManager().registerKeybindProvider(PrinterInputHandler.getInstance());
    }
}