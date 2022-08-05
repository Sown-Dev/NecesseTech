package techmod.objects;

import necesse.entity.objectEntity.AnyLogFueledProcessingTechInventoryObjectEntity;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Tech;
import necesse.level.maps.Level;
import techmod.TechMod;
import techmod.items.Coal;

public class RecyclerObjectEntity extends AnyLogFueledProcessingTechInventoryObjectEntity {
    public static int logFuelTime = 15000;
    public static int recipeProcessTime = 5000;


    public RecyclerObjectEntity(Level level, int x, int y) {
        super(level, "recycler", x, y, 2, 3, false, false, true, TechMod.RECYCLER);
        this.workingSound = GameResources.shake;
    }

    public boolean isValidFuelItem(InventoryItem item) {
        return item.item instanceof Coal;
    }

    public int getFuelTime(InventoryItem item) {
        return logFuelTime;
    }

    public int getProcessTime(Recipe recipe) {
        return recipeProcessTime;
    }
}
