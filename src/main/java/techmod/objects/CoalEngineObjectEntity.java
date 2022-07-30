package techmod.objects;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.objectEntity.AnyLogFueledInventoryObjectEntity;
import necesse.entity.objectEntity.interfaces.OEVicinityBuff;
import necesse.level.maps.Level;
import techmod.util.PoweredEntity;

public class CoalEngineObjectEntity extends AnyLogFueledInventoryObjectEntity implements PoweredEntity {
    int producing=0;
    int using=0;
    int needed=0;

    public CoalEngineObjectEntity(Level level, String type, int x, int y, boolean alwaysOn) {
        super(level, type, x, y, alwaysOn);

    }

    public void clientTick() {
        super.clientTick();
        if (this.isFueled()) {
        }

    }

    public void serverTick() {
        super.serverTick();
        this.producing=this.isFueled() ? 1:0;

    }

    @Override
    public int needed() { return this.needed;}

    @Override
    public boolean generator() { return true;}

    @Override
    public int producing() { return producing; }

    @Override
    public int using() { return using; }
}
