package eutros.botaniapp.client.integration.jei;

import eutros.botaniapp.common.block.flower.BotaniaPPFlowers;
import eutros.botaniapp.common.crafting.BotaniaPPRecipeTypes;
import eutros.botaniapp.common.utils.Reference;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class JEIBotaniaPPPlugin implements IModPlugin { // Ample 'P's here

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
        registration.addRecipes(BotaniaPPRecipeTypes.BOUGANVILLEA_TYPE.getRecipes(), RecipeCategoryBouganvillea.LOCATION);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(BotaniaPPFlowers.bouganvillea), RecipeCategoryBouganvillea.LOCATION);
        registration.addRecipeCatalyst(new ItemStack(BotaniaPPFlowers.bouganvilleaFloating), RecipeCategoryBouganvillea.LOCATION);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

}
