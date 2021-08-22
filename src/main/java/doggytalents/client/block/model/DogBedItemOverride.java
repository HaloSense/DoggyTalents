package doggytalents.client.block.model;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import doggytalents.api.registry.IBeddingMaterial;
import doggytalents.api.registry.ICasingMaterial;
import doggytalents.common.util.DogBedUtil;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DogBedItemOverride extends ItemOverrides {

    @Override
    public BakedModel resolve(BakedModel modelOriginal, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity livingEntity) {
        if (modelOriginal instanceof DogBedModel) {
            Pair<ICasingMaterial, IBeddingMaterial> materials = DogBedUtil.getMaterials(stack);
            return ((DogBedModel) modelOriginal).getModelVariant(materials.getLeft(), materials.getRight(), Direction.NORTH);
        }

        return modelOriginal;
    }
}