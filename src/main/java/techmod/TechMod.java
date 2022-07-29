package techmod;

import necesse.engine.commands.CommandsManager;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.registries.*;
import necesse.entity.mobs.friendly.human.humanShop.GunsmithHumanMob;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import necesse.inventory.recipe.Tech;
import necesse.level.gameObject.ProcessingForgeObject;
import necesse.level.maps.biomes.Biome;
import techmod.items.*;
import techmod.mobs.CarMob;
import techmod.mobs.MachinistHumanMob;
import techmod.objects.*;

import static necesse.engine.registries.RecipeTechRegistry.registerTech;

@ModEntry
public class TechMod {
    public static Tech LATHE;
    public static Tech CRUSHER;
    public void init() {

        CRUSHER = registerTech("crusher");
        LATHE = registerTech("lathe");

        //items
        ItemRegistry.registerItem("irondust", new IronDust(), 10, true);
        ItemRegistry.registerItem("copperdust", new CopperDust(), 20, true);
        ItemRegistry.registerItem("caritem", new CarItem(), 30, true);
        ItemRegistry.registerItem("testitem", new TestItem(), 40, true);

        //objects
        CrusherObject.registerCrusher();
        LatheObject.registerLathe();
        ObjectRegistry.registerObject("coalengine", new CoalEngineObject(), 30, true);
        //mobs
        MobRegistry.registerMob("car", CarMob.class, true);
        MobRegistry.registerMob("machinisthuman", MachinistHumanMob.class, true);

    }

    public void initResources() {

    }

    public void postInit() {
        // Add recipes

        Recipes.registerModRecipe(new Recipe(
                "copperdust",
                1,
                CRUSHER,
                new Ingredient[]{
                        new Ingredient("copperore", 3)
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "copperdust",
                3,
                CRUSHER,
                new Ingredient[]{
                        new Ingredient("brokencoppertool", 1)
                }
        ));

        Recipes.registerModRecipe(new Recipe(
                "irondust",
                1,
                CRUSHER,
                new Ingredient[]{
                        new Ingredient("ironore", 3)
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "irondust",
                3,
                CRUSHER,
                new Ingredient[]{
                        new Ingredient("brokenirontool", 1)
                }
        ));

        Recipes.registerModRecipe(new Recipe(
                "ironbar",
                1,
                RecipeTechRegistry.FORGE,
                new Ingredient[]{
                        new Ingredient("irondust", 1)
                }
        ));

        Recipes.registerModRecipe(new Recipe(
                "crusher",
                1,
                LATHE,
                new Ingredient[]{
                        new Ingredient("ironbar", 10),
                        new Ingredient("wire", 10)
                }
        ));

        Recipes.registerModRecipe(new Recipe(
                "caritem",
                1,
                LATHE,
                new Ingredient[]{
                        new Ingredient("ironbar", 20),
                        new Ingredient("forge", 2)
                }
        ));
    }

}
