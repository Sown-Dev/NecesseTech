package techmod.mobs;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.summon.WoodBoatMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import techmod.TechMod;

import java.awt.*;
import java.util.List;

public class CarMob extends WoodBoatMob {
    public static LootTable lootTable = new LootTable(new LootItemInterface[]{new LootItem("caritem")});

    public CarMob() {
        super();
        this.isSummoned = true;
        this.setSpeed(90.0F);
        this.setFriction(2.0F);
        this.setSwimSpeed(1.0F);
        this.accelerationMod = 0.11F;
        this.setKnockbackModifier(0.1F);
        this.collision = new Rectangle(-10, -10, 20, 14);
        this.hitBox = new Rectangle(-14, -15, 28, 24);
        this.selectBox = new Rectangle(-16, -26, 32, 36);
        this.overrideMountedWaterWalking = false;
    }

    public void tickMovement(float delta) {
        super.tickMovement(delta);
        if (!this.isServer()) {
            this.deltaCounter += (double)(delta * Math.max(0.2F, this.getCurrentSpeed() / 30.0F));
            if (this.deltaCounter >= 50.0D) {
                this.deltaCounter -= 50.0D;
                addParticleEffects(this);
            }
        }

    }

    public LootTable getLootTable() {
        return lootTable;
    }

    public CollisionFilter getLevelCollisionFilter() {
        CollisionFilter baseMobCollisionFilter = (new CollisionFilter()).mobCollision();
        CollisionFilter car = !this.isMounted() ? baseMobCollisionFilter.addFilter((tp) -> {
            return !tp.object().object.isDoor;
        }).summonedMobCollision() : baseMobCollisionFilter;
        return car.allLiquidTiles();
    }

    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(x / 32, y / 32);
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 40;
        Point sprite = this.getAnimSprite(x, y, this.getDir());
        drawY += this.getBobbing(x, y);
        drawY += this.getLevel().getTile(x / 32, y / 32).getMobSinkingAmount(this);
        final DrawOptions behind = TechMod.carMobTexture.initDraw().sprite(sprite.x, sprite.y, 64).light(light).pos(drawX, drawY);
        list.add(new MobDrawable() {
            public void draw(TickManager tickManager) {
            }

            public void drawBehindRider(TickManager tickManager) {
                behind.draw();
            }
        });
    }

    public static void drawPlacePreview(Level level, int levelX, int levelY, int dir, GameCamera camera) {
        int drawX = camera.getDrawX(levelX) - 32;
        int drawY = camera.getDrawY(levelY) - 47;
        drawY += level.getLevelTile(levelX / 32, levelY / 32).getLiquidBobbing();
        TechMod.carMobTexture.initDraw().sprite(0, dir, 64).alpha(0.5F).draw(drawX, drawY);
    }

    public int getRockSpeed() {
        return 10;
    }

    public int getWaterRockSpeed() {
        return 10000;
    }

    public int getRiderDrawYOffset() {
        return -3;
    }
}
