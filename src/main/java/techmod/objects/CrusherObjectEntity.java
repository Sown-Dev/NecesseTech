package techmod.objects;

import necesse.engine.registries.GlobalIngredientRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.entity.objectEntity.AnyLogFueledProcessingTechInventoryObjectEntity;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Tech;
import necesse.level.maps.Level;
import techmod.TechMod;
import techmod.items.Coal;

public class CrusherObjectEntity extends AnyLogFueledProcessingTechInventoryObjectEntity {
    public static int logFuelTime = 16000;
    public static int recipeProcessTime = 8000;

    public CrusherObjectEntity(Level level, int x, int y) {
        super(level, "crusher", x, y, 2, 2, false, false, true, new Tech[]{TechMod.CRUSHER});
        this.workingSound = GameResources.shake;
    }
    public boolean isValidFuelItem(InventoryItem item) {
        return item.item instanceof Coal;
    }

    public int getNextFuelBurnTime(boolean useFuel) {
        return this.itemToBurnTime(useFuel, (item) -> item.item instanceof Coal ? this.getFuelTime(item) : 0);
    }

    public int getFuelTime(InventoryItem item) {
        return logFuelTime;
    }

    public int getProcessTime(Recipe recipe) {
        return recipeProcessTime;
    }

}
