package techmod.mobs;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry.Textures;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameLootUtils;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.QuestMarkerOptions;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.villageShops.ShopItem;
import necesse.level.maps.levelData.villageShops.VillageShopsData;
import necesse.level.maps.light.GameLight;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
import static techmod.TechMod.MachinistTexture;

public class MachinistHumanMob extends HumanShop {

    public MachinistHumanMob() {
        super(500, 200, "machinist");
        this.attackCooldown = 1000;
        this.attackAnimSpeed = 500;
        this.equipmentInventory.setItem(6, new InventoryItem("wrench"));
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -41, 28, 48);
    }



    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for(int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MachinistTexture.body, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 10.0F, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }

    }

    public DrawOptions getUserDrawOptions(Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective, Consumer<HumanDrawOptions> humanDrawOptionsModifier) {
        GameLight light = level.getLightLevel(x / 32, y / 32);
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7;
        Point sprite = this.getAnimSprite(x, y, this.dir);
        HumanDrawOptions humanOptions = (new HumanDrawOptions(MachinistTexture)).chestplate(this.getDisplayArmor(1, "labapron")).boots(this.getDisplayArmor(2, "labboots")).sprite(sprite).dir(this.dir).light(light);
        humanDrawOptionsModifier.accept(humanOptions);
        DrawOptions drawOptions = humanOptions.pos(drawX, drawY);
        DrawOptions markerOptions = this.getMarkerDrawOptions(x, y, light, camera, 0, -45, perspective);
        return () -> {
            drawOptions.draw();
            markerOptions.draw();
        };
    }

    public void addDrawables(java.util.List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        if (this.objectUser == null || this.objectUser.object.drawsUsers()) {
            if (this.isVisible()) {
                GameLight light = level.getLightLevel(x / 32, y / 32);
                int drawX = camera.getDrawX(x) - 22 - 10;
                int drawY = camera.getDrawY(y) - 44 - 7;
                Point sprite = this.getAnimSprite(x, y, this.dir);
                drawY += this.getBobbing(x, y);
                drawY += this.getLevel().getTile(x / 32, y / 32).getMobSinkingAmount(this);
                HumanDrawOptions humanOptions = (new HumanDrawOptions(MachinistTexture)).chestplate(this.getDisplayArmor(1, "labapron")).boots(this.getDisplayArmor(2, "labboots")).sprite(sprite).dir(this.dir).light(light);
                if (this.inLiquid(x, y)) {
                    drawY -= 10;
                    humanOptions.armSprite(2);
                    humanOptions.mask(Textures.boat_mask[sprite.y % 4], 0, -7);
                }

                humanOptions = this.setCustomItemAttackOptions(humanOptions);
                final DrawOptions drawOptions = humanOptions.pos(drawX, drawY);
                final DrawOptions boat = this.inLiquid(x, y) ? Textures.woodBoat.initDraw().sprite(0, this.dir % 4, 64).light(light).pos(drawX, drawY + 7) : null;
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
        return this.isTravelingHuman() ? new QuestMarkerOptions('?', QuestMarkerOptions.orangeColor) : super.getMarkerOptions(perspective);
    }

    public HumanGender getHumanGender() {
        return HumanGender.MALE;
    }

    public Point getRecruitedToIsland(ServerClient client) {
        return this.isTravelingHuman() && SettlementLevelData.getSettlementData(this.getLevel()) != null ? new Point(this.getLevel().getIslandX(), this.getLevel().getIslandY()) : null;
    }
    protected ArrayList<GameMessage> getMessages(ServerClient client) {
        ArrayList<GameMessage> out = this.getLocalMessages("machinisttalk", 6);
        return out;

    }

    public void showAttack(int x, int y, int seed, boolean showAllDirections) {
        super.showAttack(x, y, seed, showAllDirections);
        if (this.getLevel().isClientLevel() && this.customShowAttack == null) {
            Screen.playSound(GameResources.swing1, SoundEffect.effect(this));
        }

    }


    public List<InventoryItem> getRecruitItems(ServerClient client) {
        if (this.isSettler()) {
            return null;
        } else {
            GameRandom random = new GameRandom((long)(this.getSettlerSeed() * 709));
            if (this.isTravelingHuman()) {
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
        if (this.isTravelingHuman()) {
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
