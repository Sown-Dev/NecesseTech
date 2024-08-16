package techmod.items;

import necesse.engine.Settings;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlaceableItemInterface;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.mountItem.MountItem;
import necesse.level.maps.Level;
import techmod.mobs.CarMob;

import java.awt.*;

public class CarItem extends MountItem implements PlaceableItemInterface {
    public CarItem() {
        super("carmount");
    }

    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.swingRotation(attackProgress);
    }

    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        if (this.canPlace(level, x, y, player, item, contentReader) == null) {
            if (level.isServer()) {
                Mob mob = MobRegistry.getMob("car", level);
                if (mob != null) {
                    mob.resetUniqueID();
                    mob.setDir(player.getDir());
                    level.entityManager.addMob(mob, (float) x, (float) y);
                }
            } else if (level.isClient() && Settings.showControlTips) {
                level.getClient().setMessage(new LocalMessage("misc", "carplacetip"), Color.WHITE, 5.0F);
            }

            if (level.isClient()) {
                SoundManager.playSound(GameResources.tap, SoundEffect.effect((float) x, (float) y).volume(0.8F));
            }

            item.setAmount(item.getAmount() - 1);
            return item;
        } else {
            return item;
        }
    }

    public String canAttack(Level level, int x, int y, PlayerMob player, InventoryItem item) {
        return null;
    }

    protected String canPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, PacketReader contentReader) {
        if (player.getPositionPoint().distance((double) x, (double) y) > 100.0D) {
            return "outofrange";
        } else {
            Mob mob = MobRegistry.getMob("car", level);
            if (mob != null) {
                mob.setPos((float) x, (float) y, true);
                if (mob.collidesWith(level)) {
                    return "collision";
                }
            }

            return null;
        }
    }

    public void drawPlacePreview(Level level, int x, int y, GameCamera camera, PlayerMob player, InventoryItem item, PlayerInventorySlot slot) {
        String error = this.canPlace(level, x, y, player, item, (PacketReader) null);
        if (error == null) {
            CarMob.drawPlacePreview(level, x, y, player.getDir(), camera);
        }

    }
}
