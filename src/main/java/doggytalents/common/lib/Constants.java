package doggytalents.common.lib;

import doggytalents.common.util.Util;
import net.minecraft.resources.ResourceLocation;

public class Constants {

    public static final String MOD_ID = "doggytalents";
    public static final String MOD_NAME = "Doggy Talents";

    public static final String VANILLA_ID = "minecraft";
    public static final String VANILLA_NAME = "Minecraft";

    // Network
    public static final ResourceLocation CHANNEL_NAME = Util.getResource("channel");
    public static final String PROTOCOL_VERSION = Integer.toString(3);

    // Storage
    public static final String STORAGE_DOG_RESPAWN = MOD_ID + "DeadDogs";
    public static final String STORAGE_DOG_LOCATION = "dog_locations";

    public static class EntityState {

        public static final byte DEATH = 3;
        public static final byte WOLF_SMOKE = 6;
        public static final byte WOLF_HEARTS = 7;
        public static final byte WOLF_START_SHAKING = 8;
        public static final byte GUARDIAN_SOUND = 21;
        public static final byte TOTEM_OF_UNDYING = 35;
        public static final byte SLIDING_DOWN_HONEY = 53;
        public static final byte WOLF_INTERUPT_SHAKING = 56;
    }
}
