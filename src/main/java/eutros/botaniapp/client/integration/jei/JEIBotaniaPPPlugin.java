package eutros.botaniapp.client.integration.jei;

import eutros.botaniapp.common.utils.Reference;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class JEIBotaniaPPPlugin implements IModPlugin { // Ample 'P's here

    public static final ResourceLocation LOCATION = new ResourceLocation(Reference.MOD_ID, "jei_plugin");

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
}
