package eutros.botaniapp.common.crafting;

import eutros.botaniapp.api.recipe.RecipeBouganvillea;
import eutros.botaniapp.common.crafting.recipe.RecipeBlackHoleTalismanInsert;
import eutros.botaniapp.common.crafting.recipe.RecipeLensBinding;
import eutros.botaniapp.common.crafting.recipe.RecipeLensUnbinding;
import eutros.botaniapp.common.crafting.recipe.bouganvillea.*;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.*;

import static eutros.botaniapp.common.item.BotaniaPPItems.register;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BotaniaPPRecipeTypes {

    public static final BotaniaPPRecipeType<RecipeBouganvillea> BOUGANVILLEA_TYPE = new BotaniaPPRecipeType<>("bouganvillea");

    private static final Map<IRecipeType<?>, BotaniaPPRecipeType<?>> types = new HashMap<>();

    static {
            types.put(BOUGANVILLEA_TYPE.type, BOUGANVILLEA_TYPE);
    }

    @SubscribeEvent
    public static void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        IForgeRegistry<IRecipeSerializer<?>> r = event.getRegistry();

        register(r, RecipeLensBinding.SERIALIZER, "lens_binding");
        register(r, RecipeLensUnbinding.SERIALIZER, "lens_unbinding");
        register(r, RecipeBlackHoleTalismanInsert.SERIALIZER, "black_hole_talisman_insert");
        register(r, RecipeBouganvilleaRegular.SERIALIZER, "bouganvillea_regular");
        register(r, RecipeBouganvilleaRename.SERIALIZER,"bouganvillea_rename");
        register(r, RecipeBouganvilleaAnvil.SERIALIZER,"bouganvillea_anvil");
        register(r, RecipeBouganvilleaNameTag.SERIALIZER,"bouganvillea_name_tag");
        register(r, RecipeBouganvilleaFormattedRename.SERIALIZER,"bouganvillea_formatted_rename");
    }

    @Mod.EventBusSubscriber(modid = Reference.MOD_ID)
    public static class BotaniaPPRecipeType<T extends IRecipe<? extends IInventory>> {

        public final ResourceLocation location;
        public final IRecipeType<T> type;
        private List<T> recipes = Collections.emptyList();

        public BotaniaPPRecipeType(String path) {
            location = new ResourceLocation(Reference.MOD_ID, path);
            type = registerType(location);
        }

        public void clearRecipes() {
            recipes = new ArrayList<>();
        }

        public List<T> getRecipes() {
            return recipes;
        }

        @SuppressWarnings("unchecked")
        public void addRecipe(IRecipe<?> recipe) {
            recipes.add((T) recipe);
        }

        private static <T extends IRecipe<?>> IRecipeType<T> registerType(ResourceLocation location) {
            return Registry.register(Registry.RECIPE_TYPE, location,
                    new IRecipeType<T>() {
                        public String toString() {
                            return location.toString();
                        }
                    });
        }

        @SubscribeEvent
        public static void reloadRecipes(RecipesUpdatedEvent event) {
            RecipeManager manager = event.getRecipeManager();

            types.values().forEach(BotaniaPPRecipeType::clearRecipes);

            for(IRecipe<?> recipe : manager.getRecipes()) {
                Optional.ofNullable(types.getOrDefault(recipe.getType(), null))
                        .ifPresent(i -> i.addRecipe(recipe));
            }
        }
    }
}
