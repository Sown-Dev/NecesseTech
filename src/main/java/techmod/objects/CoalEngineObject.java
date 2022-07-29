package techmod.objects;

import necesse.engine.localization.Localization;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.CampfireObjectEntity;
import necesse.entity.objectEntity.FueledInventoryObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class CoalEngineObject extends GameObject {
    public GameTexture texture;

    public CoalEngineObject() {
        super(new Rectangle(4, 6, 24, 50));
        this.mapColor = new Color(90, 71, 41);
        this.displayMapTooltip = true;
        this.drawDmg = false;
        this.objectHealth = 50;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.lightHue = 50.0F;
        this.lightSat = 0.5F;
    }

    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/coalengine");
    }

    public int getLightLevel(Level level, int x, int y) {
        ObjectEntity objectEntity = level.entityManager.getObjectEntity(x, y);
        return objectEntity instanceof FueledInventoryObjectEntity && ((FueledInventoryObjectEntity)objectEntity).isFueled() ? 100 : 0;
    }

    public void tickEffect(Level level, int x, int y) {
        ObjectEntity objectEntity = level.entityManager.getObjectEntity(x, y);
        if (objectEntity instanceof FueledInventoryObjectEntity && ((FueledInventoryObjectEntity)objectEntity).isFueled()) {
            float buffer = 0.5F;

            while(buffer >= 1.0F || GameRandom.globalRandom.getChance(buffer)) {
                --buffer;
                ParticleOption particleOption = level.entityManager.addParticle((float)(x * 32 + GameRandom.globalRandom.getIntBetween(11, 21)), (float)(y * 32 + GameRandom.globalRandom.getIntBetween(10, 16)), GameRandom.globalRandom.getChance(0.75F) ? Particle.GType.CRITICAL : Particle.GType.COSMETIC).movesConstant(GameRandom.globalRandom.getFloatBetween(-1.0F, 1.0F), GameRandom.globalRandom.getFloatBetween(-1.0F, 1.0F)).heightMoves(0.0F, 10.0F).flameColor().sizeFades(10, 14).lifeTime(2000);
                if (GameRandom.globalRandom.nextBoolean()) {
                    particleOption.onProgress(0.5F, (p) -> {
                        for(int i = 0; i < GameRandom.globalRandom.getIntBetween(1, 2); ++i) {
                            level.entityManager.addParticle(p.x + (float)((int)(GameRandom.globalRandom.nextGaussian() * 2.0D)), p.y, Particle.GType.COSMETIC).smokeColor().sizeFades(8, 12).heightMoves(6.0F, 20.0F);
                        }

                    });
                }
            }
        }

    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        final TextureDrawOptions top = this.texture.initDraw().sprite(0, 0, 32).light(light).pos(drawX, drawY - 32 );
        final TextureDrawOptions bot = this.texture.initDraw().sprite(0, 1, 32).light(light).pos(drawX, drawY );
        list.add(new LevelSortedDrawable(this, tileX, tileY) {
            public int getSortY() {
                return 16;
            }

            public void draw(TickManager tickManager) {
                top.draw();
                bot.draw();
            }
        });
    }


    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        this.texture.initDraw().alpha(alpha).draw(drawX, drawY - (this.texture.getHeight() - 32));
    }

    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "opentip");
    }

    public void interact(Level level, int x, int y, PlayerMob player) {
        if (level.isServerLevel()) {
            PacketOpenContainer p = PacketOpenContainer.ObjectEntity(ContainerRegistry.FUELED_OE_INVENTORY_CONTAINER, level.entityManager.getObjectEntity(x, y));
            ContainerRegistry.openAndSendContainer(player.getServerClient(), p);
        }

    }

    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new CoalEngineObjectEntity(level, "coalengine", x, y, true);
    }


}