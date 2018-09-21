package doggytalents.talent;

import java.util.Iterator;
import java.util.List;

import doggytalents.api.inferface.ITalent;
import doggytalents.entity.EntityDog;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.util.DamageSource;

/**
 * @author ProPercivalalb
 */
public class PestFighter extends ITalent {

	@Override
	public void onLivingUpdate(EntityDog dog) {
		int level = dog.talents.getLevel(this);
		
		if(level >= 0) {
            byte damage = 1;

            if (level == 5)
                damage = 2;

            List<EntitySilverfish> list = dog.world.getEntitiesWithinAABB(EntitySilverfish.class, dog.getEntityBoundingBox().grow(level * 3, 4D, level * 3));
            Iterator<EntitySilverfish> iterator = list.iterator();
            
            while(iterator.hasNext()) {
            	EntitySilverfish entitySilverfish = (EntitySilverfish)iterator.next();
            	if(dog.getRNG().nextInt(20) == 0)
            		entitySilverfish.attackEntityFrom(DamageSource.GENERIC, damage);
            }
        }
	}
	
	@Override
	public String getKey() {
		return "pestfighter";
	}

}
