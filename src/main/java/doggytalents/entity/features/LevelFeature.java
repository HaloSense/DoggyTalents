package doggytalents.entity.features;

import java.util.UUID;

import doggytalents.api.feature.ILevelFeature;
import doggytalents.entity.EntityDog;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author ProPercivalalb
 */
public class LevelFeature extends DogFeature implements ILevelFeature {

    private static UUID HEALTH_BOOST_ID = UUID.fromString("da97255c-6281-45db-8198-f79226438583");

    public LevelFeature(EntityDog dogIn) {
        super(dogIn);
    }

    @Override
    public void writeAdditional(NBTTagCompound compound) {
        compound.setInteger("level_normal", this.getLevel());
        compound.setInteger("level_dire", this.getDireLevel());
    }

    @Override
    public void readAdditional(NBTTagCompound compound) {
        if(compound.hasKey("level_normal"))
            this.setLevel(compound.getInteger("level_normal"));

        if(compound.hasKey("level_dire"))
            this.setDireLevel(compound.getInteger("level_dire"));

        //Backwards compatibility
        if(compound.hasKey("levels", 8)) {
            String[] split = compound.getString("levels").split(":");
            this.setLevel(new Integer(split[0]));
            this.setDireLevel(new Integer(split[1]));
        }
    }

    @Override
    public int getLevel() {
        return this.dog.getLevel();
    }

    @Override
    public int getDireLevel() {
        return this.dog.getDireLevel();
    }

    @Override
    public void increaseLevel() {
        this.setLevel(this.getLevel() + 1);
    }

    @Override
    public void increaseDireLevel() {
        this.setDireLevel(this.getDireLevel() + 1);
    }

    @Override
    public void setLevel(int level) {
        this.dog.setLevel(level);
        this.updateHealthModifier();
    }

    @Override
    public void setDireLevel(int level) {
        this.dog.setDireLevel(level);
        this.updateHealthModifier();
    }

    public void updateHealthModifier() {
        IAttributeInstance iattributeinstance = this.dog.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);

        AttributeModifier healthModifier = this.createHealthModifier(this.dog.effectiveLevel() + 1.0D);

        if(iattributeinstance.getModifier(HEALTH_BOOST_ID) != null)
            iattributeinstance.removeModifier(healthModifier);

        iattributeinstance.applyModifier(healthModifier);
    }

    public AttributeModifier createHealthModifier(double health) {
        return new AttributeModifier(HEALTH_BOOST_ID, "Dog Health", health, 0);
    }

    public boolean isDireDog() {
        return this.getDireLevel() == 30;
    }
}
