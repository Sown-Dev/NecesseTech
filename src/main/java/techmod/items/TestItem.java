package techmod.items;

import necesse.engine.network.PacketReader;
import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class TestItem extends Item {

    public TestItem() {
        super(12);
    }

    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        if (level.isServerLevel() ) {
            Mob mob = MobRegistry.getMob("machinisthuman", level);
            System.out.println(mob.toString());
            if(mob !=null){
            level.entityManager.addMob(mob, (float) x, (float) y);
            }

        }
        return item;
    }
}