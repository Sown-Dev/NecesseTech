package techmod;

import necesse.engine.GameEventListener;
import necesse.engine.GameEvents;
import necesse.engine.commands.CommandsManager;
import necesse.engine.events.worldGeneration.GeneratedCaveOresEvent;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.registries.*;
import necesse.entity.mobs.HumanTextureFull;
import necesse.entity.mobs.friendly.human.humanShop.GunsmithHumanMob;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import necesse.inventory.recipe.Tech;
import necesse.level.gameObject.ProcessingForgeObject;
import necesse.level.gameObject.RockObject;
import necesse.level.gameObject.RockOreObject;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.forest.ForestBiome;
import necesse.level.maps.levelData.settlementData.settler.GenericSettler;
import necesse.level.maps.levelData.settlementData.settler.MageSettler;
import necesse.level.maps.levelData.settlementData.settler.Settler;
import techmod.items.*;
import techmod.mobs.CarMob;
import techmod.mobs.MachinistHumanMob;
import techmod.mobs.MachinistSettler;
import techmod.objects.*;

import java.awt.*;

import static necesse.engine.registries.RecipeTechRegistry.registerTech;
import static techmod.util.Bruh.humanTextureFullfromString;

@ModEntry
public class TechMod {
    public static Tech LATHE;
    public static Tech CRUSHER;
    public static Tech RECYCLER;

    public static HumanTextureFull MachinistTexture;
    public void init() {


        CRUSHER = registerTech("crusher");
        LATHE = registerTech("lathe");
        RECYCLER = registerTech("recycler");

        //items
        ItemRegistry.registerItem("irondust", new IronDust(), 10, true);
        ItemRegistry.registerItem("copperdust", new CopperDust(), 8, true);
        ItemRegistry.registerItem("golddust", new GoldDust(), 20, true);
        ItemRegistry.registerItem("axle", new Axle(), 30, true);
        ItemRegistry.registerItem("coal", new Coal(), 5, true);

        ItemRegistry.registerItem("caritem", new CarItem(), 100, true);
        ItemRegistry.registerItem("testitem", new TestItem(), 0, true);

        //objects
        //RockObject rock= new RockObject("rock", new Color(105, 105, 105), "stone");

        CrusherObject.registerCrusher();
        LatheObject.registerLathe();
        RecyclerObject.registerRecycler();
        ObjectRegistry.registerObject("coalengine", new CoalEngineObject(), 50, true);
        int coalID=ObjectRegistry.registerObject("coalorerock" , new RockOreObject((RockObject)ObjectRegistry.getObject("rock"), "oremask", "coalore", new Color(10, 10, 10), "coal"), 0.0F, true);

        //mobs
        MobRegistry.registerMob("car", CarMob.class, true);
        MobRegistry.registerMob("machinisthuman", MachinistHumanMob.class, true);



        //settlers
        SettlerRegistry.registerSettler("machinist", new MachinistSettler());

        GameEvents.addListener(GeneratedCaveOresEvent.class, new GameEventListener<GeneratedCaveOresEvent>() {
            @Override
            public void onEvent(GeneratedCaveOresEvent event) {
                if (event.level.biome instanceof ForestBiome) {
                    event.caveGeneration.generateOreVeins(1, 2,12, coalID );
                }
            }
        });
    }

    public void initResources() {
        MachinistTexture= humanTextureFullfromString("mobs/humans/machinist");
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
                "golddust",
                1,
                CRUSHER,
                new Ingredient[]{
                        new Ingredient("goldore", 3)
                }
        ));


        //Forge Recipes:
        Recipes.registerModRecipe(new Recipe("ironbar", 1,
                RecipeTechRegistry.FORGE,
                new Ingredient[]{
                        new Ingredient("irondust", 1)
                }
        ));
        Recipes.registerModRecipe(new Recipe("goldbar", 1,
                RecipeTechRegistry.FORGE,
                new Ingredient[]{
                        new Ingredient("golddust", 1)
                }
        ));
        Recipes.registerModRecipe(new Recipe("copperbar", 1,
                RecipeTechRegistry.FORGE,
                new Ingredient[]{
                        new Ingredient("copperdust", 1)
                }
        ));


        Recipes.registerModRecipe(new Recipe("crusher", 1,
                LATHE,
                new Ingredient[]{
                        new Ingredient("ironbar", 12),
                        new Ingredient("wire", 40)
                }
        ));
        Recipes.registerModRecipe(new Recipe("recycler", 1,
                LATHE,
                new Ingredient[]{
                        new Ingredient("copperbar", 5),
                        new Ingredient("axle", 2),
                        new Ingredient("ironbar", 5)
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "axle",
                1,
                LATHE,
                new Ingredient[]{
                        new Ingredient("ironbar", 3),
                }
        ));

        Recipes.registerModRecipe(new Recipe(
                "caritem",
                1,
                LATHE,
                new Ingredient[]{
                        new Ingredient("ironbar", 20),
                        new Ingredient("axle", 5),
                        new Ingredient("forge", 2)
                }
        ));



        //recycler:
        Recipes.registerModRecipe(new Recipe(
                "stone",
                1,
                RECYCLER,
                new Ingredient[]{
                        new Ingredient("stonearrow", 2)
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "ironbar",
                1,
                RECYCLER,
                new Ingredient[]{
                        new Ingredient("ironarrow", 8)
                }
        ));


        Recipes.registerModRecipe(new Recipe(
                "lathe",
                1,
                RecipeTechRegistry.WORKSTATION,
                new Ingredient[]{
                        new Ingredient("ironbar", 30),
                        new Ingredient("brokenirontool", 2),
                }
        ));
    }

}
