package eutros.metabotany.common.crafting;

import eutros.metabotany.api.recipe.RecipeBouganvillea;
import eutros.metabotany.common.crafting.recipe.*;
import eutros.metabotany.common.crafting.recipe.bouganvillea.*;
import eutros.metabotany.common.utils.Reference;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import static eutros.metabotany.common.item.MetaBotanyItems.register;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MetaBotanyRecipeTypes {

    public static final MetaBotanyRecipeType<RecipeBouganvillea> BOUGANVILLEA_TYPE = new MetaBotanyRecipeType<>("bouganvillea");

    @SubscribeEvent
    public static void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        IForgeRegistry<IRecipeSerializer<?>> r = event.getRegistry();

        register(r, RecipeLensBinding.SERIALIZER, "lens_binding");
        register(r, RecipeLensUnbinding.SERIALIZER, "lens_unbinding");
        register(r, RecipeBlackHoleTalismanInsert.SERIALIZER, "black_hole_talisman_insert");
        register(r, RecipeBouganvilleaRegular.SERIALIZER, "bouganvillea_regular");
        register(r, RecipeBouganvilleaRename.SERIALIZER, "bouganvillea_rename");
        register(r, RecipeBouganvilleaAnvil.SERIALIZER, "bouganvillea_anvil");
        register(r, RecipeBouganvilleaNameTag.SERIALIZER, "bouganvillea_name_tag");
        register(r, RecipeBouganvilleaFormattedRename.SERIALIZER, "bouganvillea_formatted_rename");
        register(r, RecipeLensSplit.SERIALIZER, "lens_split");
        register(r, RecipeCombineBrews.SERIALIZER, "combine_brews");
    }

    public static class MetaBotanyRecipeType<T extends IRecipe<? extends IInventory>> {

        public final ResourceLocation location;
        public final IRecipeType<T> type;

        public MetaBotanyRecipeType(String path) {
            location = new ResourceLocation(Reference.MOD_ID, path);
            type = registerType(location);
        }

        private static <T extends IRecipe<?>> IRecipeType<T> registerType(ResourceLocation location) {
            return Registry.register(Registry.RECIPE_TYPE, location,
                    new IRecipeType<T>() {
                        public String toString() {
                            return location.toString();
                        }
                    });
        }

    }

}
