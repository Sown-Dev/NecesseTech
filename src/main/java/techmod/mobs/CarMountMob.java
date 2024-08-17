package techmod.mobs;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob.WoodBoatMountMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import techmod.TechMod;

import java.awt.*;
import java.util.List;

public class CarMountMob extends WoodBoatMountMob {
    public CarMountMob() {
        super();
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

    protected GameMessage getSummonLocalization() {
        return MobRegistry.getLocalization("car");
    }

    public CollisionFilter getLevelCollisionFilter() {
        CollisionFilter baseMobCollisionFilter = (new CollisionFilter()).mobCollision();
        CollisionFilter car = !this.isMounted() ? baseMobCollisionFilter.addFilter((tp) -> {
            return !tp.object().object.isDoor;
        }).summonedMobCollision() : baseMobCollisionFilter;
        return car.allLiquidTiles();
    }

    public void addDrawables(List list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(x / 32, y / 32);
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 47 + 5;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
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
}
