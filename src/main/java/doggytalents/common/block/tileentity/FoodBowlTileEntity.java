package doggytalents.common.block.tileentity;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import doggytalents.DoggyTileEntityTypes;
import doggytalents.api.feature.FoodHandler;
import doggytalents.common.entity.DogEntity;
import doggytalents.common.inventory.container.FoodBowlContainer;
import doggytalents.common.util.InventoryUtil;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class FoodBowlTileEntity extends PlacedTileEntity implements MenuProvider, TickableBlockEntity {

    private final ItemStackHandler inventory = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            // When contents change mark needs save to disc
            FoodBowlTileEntity.this.setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return FoodHandler.isFood(stack).isPresent();
        }
    };
    private final LazyOptional<ItemStackHandler> itemStackHandler = LazyOptional.of(() -> this.inventory);


    public int timeoutCounter;

    public FoodBowlTileEntity() {
        super(DoggyTileEntityTypes.FOOD_BOWL.get());
    }

    @Override
    public void load(BlockState state, CompoundTag compound) {
        super.load(state, compound);
        this.inventory.deserializeNBT(compound);
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        super.save(compound);
        compound.merge(this.inventory.serializeNBT());
        return compound;
    }

    @Override
    public void tick() {

        //Only run update code every 5 ticks (0.25s)
        if (++this.timeoutCounter < 5) { return; }

        List<DogEntity> dogList = this.level.getEntitiesOfClass(DogEntity.class, new AABB(this.worldPosition).inflate(5, 5, 5));

        for (DogEntity dog : dogList) {
            //TODO make dog bowl remember who placed and only their dogs can attach to the bowl
            UUID placerId = this.getPlacerId();
            if (placerId != null && placerId.equals(dog.getOwnerUUID()) && !dog.getBowlPos().isPresent()) {
                dog.setBowlPos(this.worldPosition);
            }

            if (dog.getDogHunger() < dog.getMaxHunger() / 2) {
               InventoryUtil.feedDogFrom(dog, null, this.inventory);
            }
        }

        this.timeoutCounter = 0;
    }

    public ItemStackHandler getInventory() {
        return this.inventory;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (LazyOptional<T>) this.itemStackHandler;
        }
        return super.getCapability(cap, side);
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("container.doggytalents.food_bowl");
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerIn) {
        return new FoodBowlContainer(windowId, this.level, this.worldPosition, playerInventory, playerIn);
    }
}
