package techmod.objects;
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.particle.Particle.GType;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.container.object.CraftingStationContainer;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import necesse.inventory.recipe.Tech;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationObject;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.SideMultiTile;
import techmod.util.PoweredEntity;

import static techmod.TechMod.CRUSHER;

public class AlloyFurnaceObject extends GameObject implements SettlementWorkstationObject {
    public GameTexture texture;
    protected int counterID;

    public AlloyFurnaceObject() {
        super(new Rectangle(32, 32));
        this.mapColor = new Color(170, 150, 150);
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.drawDmg = false;
        this.isLightTransparent = true;
        this.roomProperties.add("metalwork");
        this.lightHue = 50.0F;
        this.lightSat = 0.2F;
    }

    public MultiTile getMultiTile(int rotation) {
        return new SideMultiTile(0, 1, 1, 2, rotation, true, new int[]{this.counterID, this.getID()});
    }
    public int getPlaceRotation(Level level, int targetX, int targetY, PlayerMob player, int playerDir) {
        return Math.floorMod(super.getPlaceRotation(level, targetX, targetY, player, playerDir) - 1, 4);
    }


    public int getLightLevel(Level level, int x, int y) {
        CrusherObjectEntity crusherObjectEntity = this.getCrusherObjectEntity(level, x, y);
        return crusherObjectEntity != null && crusherObjectEntity.isFuelRunning() ? 100 : 0;
    }



    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/crusher");
    }

    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32 + 5, y * 32, 22, 26);
        } else if (rotation == 1) {
            return new Rectangle(x * 32 + 12, y * 32 + 6, 20, 20);
        } else {
            return rotation == 2 ? new Rectangle(x * 32 + 5, y * 32 + 16, 22, 16) : new Rectangle(x * 32, y * 32 + 6, 20, 20);
        }
    }
    public List<ObjectHoverHitbox> getHoverHitboxes(Level level, int tileX, int tileY) {
        List<ObjectHoverHitbox> list = super.getHoverHitboxes(level, tileX, tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        if (rotation == 1 || rotation == 3) {
            list.add(new ObjectHoverHitbox(tileX, tileY, 0, -16, 32, 16));
        }

        return list;
    }


    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int rotation = level.getObjectRotation(tileX, tileY);
        final DrawOptionsList options = new DrawOptionsList();
        if (rotation == 0) {
            options.add(this.texture.initDraw().sprite(1, 1, 32).light(light).pos(drawX, drawY + 2));
        } else if (rotation == 1) {
            options.add(this.texture.initDraw().sprite(1, 2, 32).mirrorX().light(light).pos(drawX, drawY - 24));
            options.add(this.texture.initDraw().sprite(1, 3, 32).mirrorX().light(light).pos(drawX, drawY + 8));
        } else if (rotation == 2) {
            options.add(this.texture.initDraw().sprite(0, 0, 32).light(light).pos(drawX, drawY + 2));
        } else {
            options.add(this.texture.initDraw().sprite(1, 2, 32).light(light).pos(drawX, drawY - 24));
            options.add(this.texture.initDraw().sprite(1, 3, 32).light(light).pos(drawX, drawY + 8));
        }

        list.add(new LevelSortedDrawable(this, tileX, tileY) {
            public int getSortY() {
                return 16;
            }

            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        if (rotation == 0) {
            this.texture.initDraw().sprite(1, 1, 32).alpha(alpha).draw(drawX, drawY + 2);
            this.texture.initDraw().sprite(1, 0, 32).alpha(alpha).draw(drawX, drawY - 32 + 2);
        } else if (rotation == 1) {
            this.texture.initDraw().sprite(0, 2, 32).mirrorX().alpha(alpha).draw(drawX + 32, drawY - 24);
            this.texture.initDraw().sprite(1, 2, 32).mirrorX().alpha(alpha).draw(drawX, drawY - 24);
            this.texture.initDraw().sprite(0, 3, 32).mirrorX().alpha(alpha).draw(drawX + 32, drawY + 8);
            this.texture.initDraw().sprite(1, 3, 32).mirrorX().alpha(alpha).draw(drawX, drawY + 8);
        } else if (rotation == 2) {
            this.texture.initDraw().sprite(0, 0, 32).alpha(alpha).draw(drawX, drawY + 2);
            this.texture.initDraw().sprite(0, 1, 32).alpha(alpha).draw(drawX, drawY + 32 + 2);
        } else {
            this.texture.initDraw().sprite(0, 2, 32).alpha(alpha).draw(drawX - 32, drawY - 24);
            this.texture.initDraw().sprite(1, 2, 32).alpha(alpha).draw(drawX, drawY - 24);
            this.texture.initDraw().sprite(0, 3, 32).alpha(alpha).draw(drawX - 32, drawY + 8);
            this.texture.initDraw().sprite(1, 3, 32).alpha(alpha).draw(drawX, drawY + 8);
        }
    }

    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new CrusherObjectEntity(level, x, y);
    }

    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "opentip");
    }

    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    public void interact(Level level, int x, int y, PlayerMob player) {
        if (level.isServerLevel()) {
            CraftingStationContainer.openAndSendContainer(ContainerRegistry.FUELED_PROCESSING_STATION_CONTAINER, player.getServerClient(), level, x, y);
        }

    }

    public CrusherObjectEntity getCrusherObjectEntity(Level level, int tileX, int tileY) {
        ObjectEntity objectEntity = level.entityManager.getObjectEntity(tileX, tileY);
        return objectEntity instanceof CrusherObjectEntity ? (CrusherObjectEntity)objectEntity : null;
    }

    public Stream<Recipe> streamSettlementRecipes(Level level, int tileX, int tileY) {
        CrusherObjectEntity crusherObjectEntity = this.getCrusherObjectEntity(level, tileX, tileY);
        return crusherObjectEntity != null ? Recipes.streamRecipes(crusherObjectEntity.techs) : Stream.empty();
    }

    public boolean isProcessingInventory(Level level, int tileX, int tileY) {
        return true;
    }

    public boolean canCurrentlyCraft(Level level, int tileX, int tileY, Recipe recipe) {
        CrusherObjectEntity crusherObjectEntity = this.getCrusherObjectEntity(level, tileX, tileY);
        if (crusherObjectEntity == null) {
            return false;
        } else {
            return crusherObjectEntity.getExpectedResults().crafts < 10 && (crusherObjectEntity.isFuelRunning() || crusherObjectEntity.canUseFuel());
        }
    }

    public int getMaxCraftsAtOnce(Level level, int tileX, int tileY, Recipe recipe) {
        return 5;
    }

    public InventoryRange getProcessingInputRange(Level level, int tileX, int tileY) {
        CrusherObjectEntity crusherObjectEntity = this.getCrusherObjectEntity(level, tileX, tileY);
        return crusherObjectEntity != null ? crusherObjectEntity.getInputInventoryRange() : null;
    }

    public InventoryRange getProcessingOutputRange(Level level, int tileX, int tileY) {
        CrusherObjectEntity crusherObjectEntity = this.getCrusherObjectEntity(level, tileX, tileY);
        return crusherObjectEntity != null ? crusherObjectEntity.getOutputInventoryRange() : null;
    }

    public ArrayList<InventoryItem> getCurrentAndFutureProcessingOutputs(Level level, int tileX, int tileY) {
        CrusherObjectEntity crusherObjectEntity = this.getCrusherObjectEntity(level, tileX, tileY);
        return crusherObjectEntity != null ? crusherObjectEntity.getCurrentAndExpectedResults().items : new ArrayList();
    }

    public ItemCategoriesFilter getDefaultFuelFilters(Level level, int tileX, int tileY) {
        return new ItemCategoriesFilter(5, 10, true);
    }

    public InventoryRange getFuelInventoryRange(Level level, int tileX, int tileY) {
        CrusherObjectEntity crusherObjectEntity = this.getCrusherObjectEntity(level, tileX, tileY);
        if (crusherObjectEntity != null) {
            Inventory inventory = crusherObjectEntity.getInventory();
            if (inventory != null && crusherObjectEntity.fuelSlots > 0) {
                return new InventoryRange(inventory, 0, crusherObjectEntity.fuelSlots - 1);
            }
        }

        return null;
    }

    public Tech[] getCraftingTechs() {
        return new Tech[]{CRUSHER};
    }

    public static int[] registerCrusher() {
        CrusherObject cb1o = new CrusherObject();
        Crusher2Object cb2o = new Crusher2Object();
        int cb1i = ObjectRegistry.registerObject("crusher", cb1o, 20.0F, true);
        int cb2i = ObjectRegistry.registerObject("crusher2", cb2o, 0.0F, false);
        cb1o.counterID = cb2i;
        cb2o.counterID = cb1i;
        return new int[]{cb1i, cb2i};
    }
}
