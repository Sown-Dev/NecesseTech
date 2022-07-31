package techmod.mobs;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MobRegistry.Textures;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.summon.WoodBoatMob;
import necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob.MountFollowingMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CarMob extends MountFollowingMob {
    protected double deltaCounter;
   static GameTexture CarTexture = GameTexture.fromFile("mobs/car");
    public CarMob() {
        super(100);
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

    public void serverTick() {
        super.serverTick();
        if (!this.isMounted()) {
            this.remove(0.0F, 0.0F, (Attacker)null, true);
            this.moveX = 0.0F;
            this.moveY = 0.0F;
        }

    }

    public void clientTick() {
        super.clientTick();
        if (!this.isMounted()) {
            this.moveX = 0.0F;
            this.moveY = 0.0F;
        }

    }

    public void tickMovement(float delta) {
        super.tickMovement(delta);
        if (!this.getLevel().isServerLevel() ) {
            this.deltaCounter += (double)(delta * Math.max(0.2F, this.getCurrentSpeed() / 30.0F));
            if (this.deltaCounter >= 50.0D) {
                this.deltaCounter -= 50.0D;
                WoodBoatMob.addParticleEffects(this);
            }
        }

    }

    protected GameMessage getSummonLocalization() {
        return MobRegistry.getLocalization("car");
    }

    public CollisionFilter getLevelCollisionFilter() {
        return super.getLevelCollisionFilter().allLiquidTiles();
    }

    protected String getInteractTip(PlayerMob perspective, boolean debug) {
        return this.isMounted() ? null : Localization.translate("controls", "usetip");
    }

    public void onFollowingAnotherLevel(PlayerMob player) {
        if (this.getRider() == player) {
            super.onFollowingAnotherLevel(player);
        } else {
            this.remove();
        }

    }

    protected void playDeathSound() {
    }

    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(x / 32, y / 32);
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 40 ;

        Point sprite = this.getAnimSprite(x, y, this.dir);
        drawY += this.getBobbing(x, y);
        drawY += this.getLevel().getTile(x / 32, y / 32).getMobSinkingAmount(this);
        final DrawOptions behind = CarTexture.initDraw().sprite(sprite.x, sprite.y, 64).light(light).pos(drawX, drawY);
        list.add(new MobDrawable() {
            public void draw(TickManager tickManager) {
            }

            public void drawBehindRider(TickManager tickManager) {
                behind.draw();
            }
        });
    }

    public Point getAnimSprite(int x, int y, int dir) {
        Point p = new Point(0, dir);
        return p;
    }

    protected TextureDrawOptions getShadowDrawOptions(int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = Textures.boat_shadow;
        int res = shadowTexture.getHeight();
        int drawX = camera.getDrawX(x) - res / 2;
        int drawY = camera.getDrawY(y) - res / 2 - 2;
        drawY += this.getBobbing(x, y);
        return shadowTexture.initDraw().sprite(this.dir, 0, res).light(light).pos(drawX, drawY);
    }

    public int getRockSpeed() {
        return 10;
    }

    public int getWaterRockSpeed() {
        return 10000;
    }

    public Point getSpriteOffset(int spriteX, int spriteY) {
        Point p = new Point(0, 0);
        p.x += this.getRiderDrawXOffset();
        p.y += this.getRiderDrawYOffset();
        return p;
    }

    public int getRiderDrawYOffset() {
        return -3;
    }

    public int getRiderArmSpriteX() {
        return 2;
    }

    public GameTexture getRiderMask() {
        return Textures.boat_mask[GameMath.limit(this.dir, 0, Textures.boat_mask.length - 1)];
    }

    public int getRiderMaskYOffset() {
        return -7;
    }

    public ModifierValue<?>[] getDefaultRiderModifiers() {
        return new ModifierValue[]{new ModifierValue(BuffModifiers.TRAVEL_DISTANCE, 1)};
    }
}
