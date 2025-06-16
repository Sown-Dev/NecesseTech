package techmod.mobs;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameLootUtils;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.QuestMarkerOptions;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.villageShops.ShopItem;
import necesse.level.maps.levelData.villageShops.VillageShopsData;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MachinistHumanMob extends HumanShop {

    public MachinistHumanMob() {
        super(500, 200, "machinist");
        this.attackCooldown = 1000;
        this.attackAnimTime = 500;
        this.equipmentInventory.setItem(6, new InventoryItem("wrench"));
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -41, 28, 48);
    }

    public void setDefaultArmor(HumanDrawOptions drawOptions) {
        drawOptions.helmet(new InventoryItem("alchemistglasses"));
        drawOptions.chestplate(new InventoryItem("smithingapron"));
        drawOptions.boots(new InventoryItem("labboots"));
    }

    public void addDrawables(List list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        if (this.objectUser == null || this.objectUser.drawsUser()) {
            if (this.isVisible()) {
                GameLight light = level.getLightLevel(x / 32, y / 32);
                int drawX = camera.getDrawX(x) - 22 - 10;
                int drawY = camera.getDrawY(y) - 44 - 7;
                int dir = this.getDir();
                Point sprite = this.getAnimSprite(x, y, dir);
                drawY += this.getBobbing(x, y);
                drawY += this.getLevel().getTile(x / 32, y / 32).getMobSinkingAmount(this);
                MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
                this.look.setHair(0);
                HumanDrawOptions humanOptions = new HumanDrawOptions(level, this.look, !this.customLook);
                this.setDefaultArmor(humanOptions);
                this.setDisplayArmor(humanOptions);
                humanOptions.invis((Boolean)this.buffManager.getModifier(BuffModifiers.INVISIBILITY)).blinking(this.isBlinking()).sprite(sprite).mask(swimMask).dir(dir).light(light);
                boolean inLiquid = this.inLiquid(x, y);
                if (inLiquid) {
                    humanOptions.armSprite(2);
                    humanOptions.mask(MobRegistry.Textures.boat_mask[sprite.y % 4], 0, -7);
                }

                humanOptions = this.setCustomItemAttackOptions(humanOptions);
                final DrawOptions drawOptions = humanOptions.pos(drawX, drawY);
                final DrawOptions boat = inLiquid ? MobRegistry.Textures.woodBoat.initDraw().sprite(0, dir % 4, 64).light(light).pos(drawX, drawY + 7) : null;
                final DrawOptions markerOptions = this.getMarkerDrawOptions(x, y, light, camera, 0, -45, perspective);
                list.add(new MobDrawable() {
                    public void draw(TickManager tickManager) {
                        if (boat != null) {
                            boat.draw();
                        }

                        drawOptions.draw();
                        markerOptions.draw();
                    }
                });
                this.addShadowDrawables(tileList, x, y, light, camera);
            }
        }
    }

    public QuestMarkerOptions getMarkerOptions(PlayerMob perspective) {
        return this.isVisitor() ? new QuestMarkerOptions('?', QuestMarkerOptions.orangeColor) : super.getMarkerOptions(perspective);
    }

    protected ArrayList<GameMessage> getMessages(ServerClient client) {
        ArrayList<GameMessage> out = this.getLocalMessages("machinisttalk", 6);
        return out;

    }

    public List<InventoryItem> getRecruitItems(ServerClient client) {
        if (this.isSettler()) {
            return null;
        } else {
            GameRandom random = new GameRandom((long)(this.getSettlerSeed() * 709L));
            if (this.isVisitor()) {
                return Collections.singletonList(new InventoryItem("coin", random.getIntBetween(350, 500)));
            }else{
                LootTable items = new LootTable(new LootItemInterface[]{new LootItem("coin", 2147483647), });
                int value = random.getIntBetween(300, 500);
                ArrayList<InventoryItem> out = GameLootUtils.getItemsValuedAt(random, value, 0.20000000298023224D, items, new Object[0]);
                out.sort(Comparator.comparing(InventoryItem::getBrokerValue).reversed());
                return out;
            }
        }
    }

    public ArrayList<ShopItem> getShopItems(VillageShopsData data, ServerClient client) {
        if (this.isVisitor()) {
            return null;
        } else {
            ArrayList<ShopItem> out = new ArrayList();
            GameRandom random = new GameRandom(this.getShopSeed() + 5L);
            out.add(ShopItem.item("wrench", this.getRandomHappinessPrice(random, 70, 140, 2)));
            out.add(ShopItem.item("lathe", this.getRandomHappinessPrice(random, 1000, 1800, 100)));
            out.add(ShopItem.item("coal", this.getRandomHappinessPrice(random, 15, 35, 3)));
            return out;
        }
    }
}
