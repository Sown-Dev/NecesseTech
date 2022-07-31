package techmod.mobs;

import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.packet.PacketQuestGiverRequest;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameLootUtils;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.*;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.HumanAI;
import necesse.entity.mobs.ai.behaviourTree.trees.MeleeHumanAI;
import necesse.entity.mobs.ai.behaviourTree.util.AIMover;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.humanShop.BlacksmithHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.CountOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.villageShops.ShopItem;
import necesse.level.maps.levelData.villageShops.VillageShopsData;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import static techmod.TechMod.MachinistTexture;
import static techmod.util.Bruh.humanTextureFullfromString;

public class MachinistHumanMob extends HumanShop {

    public MachinistHumanMob() {
        super(500, 200, "machinist", 50, 60);
        this.attackCooldown = 1000L;
        this.attackAnimSpeed = 500;
        this.attackWeaponStringID = "wrench";
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
                float animProgress = this.getAttackAnimProgress();
                if (this.isAttacking) {
                    humanOptions.itemAttack(new InventoryItem(this.attackWeaponStringID), (PlayerMob)null, animProgress, this.attackDir.x, this.attackDir.y);
                }

                humanOptions = this.setCustomItemAttackOptions(humanOptions);
                final DrawOptions drawOptions = humanOptions.pos(drawX, drawY);
                final DrawOptions markerOptions = this.getMarkerDrawOptions(x, y, light, camera, 0, -45, perspective);
                list.add(new MobDrawable() {
                    public void draw(TickManager tickManager) {
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

            return out;
        }
    }
}
