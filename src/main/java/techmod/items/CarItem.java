package techmod.items;

import java.awt.Color;

import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.summon.WoodBoatMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlaceableItemInterface;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.mountItem.MountItem;
import necesse.level.gameObject.MinecartTrackObject;
import necesse.level.maps.Level;

public class CarItem extends MountItem implements PlaceableItemInterface {
    public CarItem() {
        super("car");
    }



    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.swingRotation(attackProgress);
    }

    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        if (this.canPlace(level, x, y, player, item, contentReader) == null) {
            if (level.isServerLevel()) {
                Mob mob = MobRegistry.getMob("car", level);
                if (mob != null) {
                    mob.resetUniqueID();
                    mob.dir = player.dir;
                    level.entityManager.addMob(mob, (float) x, (float) y);
                }
            } else if (level.isClientLevel() && Settings.showControlTips) {
                level.getClient().setMessage(new LocalMessage("misc", "boatplacetip"), Color.WHITE, 5.0F);
            }

            if (level.isClientLevel()) {
                Screen.playSound(GameResources.tap, SoundEffect.effect((float) x, (float) y).volume(0.8F));
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
                if (!mob.collidesWith(level)) {
                    return "collision";
                }
            }

            return null;
        }
    }

    public void drawPlacePreview(Level level, int x, int y, GameCamera camera, PlayerMob player, InventoryItem item, PlayerInventorySlot slot) {
        String error = this.canPlace(level, x, y, player, item, (PacketReader) null);
        if (error == null) {
            WoodBoatMob.drawPlacePreview(level, x, y, player.dir, camera);
        }

    }

    public String canUseMount(InventoryItem item, PlayerMob player, Level level) {

        Mob lastMount = player.getMount();
        if (lastMount != null) {
            return null;
        } else {
            if (!player.inLiquid()) {
                return null;
            }

            return Localization.translate("misc", "cannotusemounthere", "mount", this.getDisplayName(item));
        }
    }


}
