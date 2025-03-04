package doggytalents;

import doggytalents.api.feature.FoodHandler;
import doggytalents.api.feature.InteractHandler;
import doggytalents.client.ClientSetup;
import doggytalents.client.data.DTBlockstateProvider;
import doggytalents.client.data.DTItemModelProvider;
import doggytalents.client.entity.render.world.BedFinderRenderer;
import doggytalents.client.event.ClientEventHandler;
import doggytalents.common.Capabilities;
import doggytalents.common.addon.AddonManager;
import doggytalents.common.command.DogRespawnCommand;
import doggytalents.common.config.ConfigHandler;
import doggytalents.common.data.*;
import doggytalents.common.entity.DogEntity;
import doggytalents.common.entity.HelmetInteractHandler;
import doggytalents.common.entity.MeatFoodHandler;
import doggytalents.common.event.EventHandler;
import doggytalents.common.lib.Constants;
import doggytalents.common.network.PacketHandler;
import doggytalents.common.talent.HappyEaterTalent;
import doggytalents.common.util.BackwardsComp;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author ProPercivalalb
 */
@Mod(Constants.MOD_ID)
public class DoggyTalents2 {

    public static final Logger LOGGER = LogManager.getLogger(Constants.MOD_ID);

    public static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder.named(Constants.CHANNEL_NAME)
            .clientAcceptedVersions(Constants.PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(Constants.PROTOCOL_VERSION::equals)
            .networkProtocolVersion(Constants.PROTOCOL_VERSION::toString)
            .simpleChannel();

    public DoggyTalents2() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Mod lifecycle
        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::interModProcess);

        // Registries
        DoggyBlocks.BLOCKS.register(modEventBus);
        DoggyTileEntityTypes.TILE_ENTITIES.register(modEventBus);
        DoggyItems.ITEMS.register(modEventBus);
        DoggyEntityTypes.ENTITIES.register(modEventBus);
        DoggyContainerTypes.CONTAINERS.register(modEventBus);
        DoggySerializers.SERIALIZERS.register(modEventBus);
        DoggySounds.SOUNDS.register(modEventBus);
        DoggyRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        DoggyTalents.TALENTS.register(modEventBus);
        DoggyAccessories.ACCESSORIES.register(modEventBus);
        DoggyAccessoryTypes.ACCESSORY_TYPES.register(modEventBus);
        DoggyBedMaterials.BEDDINGS.register(modEventBus);
        DoggyBedMaterials.CASINGS.register(modEventBus);
        DoggyAttributes.ATTRIBUTES.register(modEventBus);

        modEventBus.addListener(DoggyRegistries::newRegistry);
        modEventBus.addListener(DoggyEntityTypes::addEntityAttributes);
        modEventBus.addListener(Capabilities::registerCaps);

        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        forgeEventBus.addListener(this::serverStarting);
        forgeEventBus.addListener(this::registerCommands);

        forgeEventBus.register(new EventHandler());
        forgeEventBus.register(new BackwardsComp());

        // Client Events
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            modEventBus.addListener(this::clientSetup);
            modEventBus.addListener(DoggyBlocks::registerBlockColours);
            modEventBus.addListener(DoggyItems::registerItemColours);
            modEventBus.addListener(ClientEventHandler::onModelBakeEvent);
            modEventBus.addListener(ClientSetup::setupTileEntityRenderers);
            modEventBus.addListener(ClientSetup::setupEntityRenderers);
            modEventBus.addListener(ClientSetup::addClientReloadListeners);
            forgeEventBus.register(new ClientEventHandler());
            forgeEventBus.addListener(BedFinderRenderer::onWorldRenderLast);
        });

        ConfigHandler.init(modEventBus);

        AddonManager.init();
    }

    public void commonSetup(final FMLCommonSetupEvent event) {
        PacketHandler.init();
        //TODO CriteriaTriggers.register(criterion)
        FoodHandler.registerHandler(new MeatFoodHandler());

        FoodHandler.registerDynPredicate(HappyEaterTalent.INNER_DYN_PRED);
        InteractHandler.registerHandler(new HelmetInteractHandler());
        ConfigHandler.initTalentConfig();
        DogRespawnCommand.registerSerilizers();
        DogEntity.initDataParameters();
    }

    public void serverStarting(final ServerStartingEvent event) {

    }

    public void registerCommands(final RegisterCommandsEvent event) {
        DogRespawnCommand.register(event.getDispatcher());
    }

    @OnlyIn(Dist.CLIENT)
    public void clientSetup(final FMLClientSetupEvent event) {
        ClientSetup.setupScreenManagers(event);

        ClientSetup.setupCollarRenderers(event);
    }

    protected void interModProcess(final InterModProcessEvent event) {
        BackwardsComp.init();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        AddonManager.init();
    }

    private void gatherData(final GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        if (event.includeClient()) {
            DTBlockstateProvider blockstates = new DTBlockstateProvider(gen, event.getExistingFileHelper());
            gen.addProvider(blockstates);
            gen.addProvider(new DTItemModelProvider(gen, blockstates.getExistingHelper()));
        }

        if (event.includeServer()) {
            // gen.addProvider(new DTBlockTagsProvider(gen));
            gen.addProvider(new DTAdvancementProvider(gen));
            DTBlockTagsProvider blockTagProvider = new DTBlockTagsProvider(gen, event.getExistingFileHelper());
            gen.addProvider(blockTagProvider);
            gen.addProvider(new DTItemTagsProvider(gen, blockTagProvider, event.getExistingFileHelper()));
            gen.addProvider(new DTRecipeProvider(gen));
            gen.addProvider(new DTLootTableProvider(gen));
        }
    }
}
