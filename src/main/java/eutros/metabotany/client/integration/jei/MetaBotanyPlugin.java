package eutros.metabotany.client.integration.jei;

import eutros.metabotany.common.block.flower.MetaBotanyFlowers;
import eutros.metabotany.common.crafting.MetaBotanyRecipeTypes;
import eutros.metabotany.common.utils.Reference;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class MetaBotanyPlugin implements IModPlugin {

    public static final ResourceLocation LOCATION = new ResourceLocation(Reference.MOD_ID, "jei_plugin");
    public static IJeiRuntime runtime;

    @NotNull
    @Override
    public ResourceLocation getPluginUid() {
        return LOCATION;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers helpers = registration.getJeiHelpers();

        registration.addRecipeCategories(new RecipeCategoryBouganvillea(helpers.getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientWorld world = Minecraft.getInstance().world;
        assert world != null;
        RecipeManager rm = world.getRecipeManager();
        registration.addRecipes(rm.getRecipes(MetaBotanyRecipeTypes.BOUGANVILLEA_TYPE.type).values(), RecipeCategoryBouganvillea.LOCATION);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(MetaBotanyFlowers.bouganvillea), RecipeCategoryBouganvillea.LOCATION);
        registration.addRecipeCatalyst(new ItemStack(MetaBotanyFlowers.bouganvilleaFloating), RecipeCategoryBouganvillea.LOCATION);
    }

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

}
