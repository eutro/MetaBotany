package eutros.botaniapp.client.integration.jei;

import eutros.botaniapp.common.block.BotaniaPPBlocks;
import eutros.botaniapp.common.block.flower.BotaniaPPFlowers;
import eutros.botaniapp.common.crafting.BotaniaPPRecipeTypes;
import eutros.botaniapp.common.crafting.recipe.RecipeLeakyPool;
import eutros.botaniapp.common.item.BotaniaPPItems;
import eutros.botaniapp.common.utils.Reference;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.common.core.helper.ItemNBTHelper;

import java.util.Collections;

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
        registration.addRecipeCategories(new RecipeCategoryTNTPool(helpers.getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientWorld world = Minecraft.getInstance().world;
        assert world != null;
        RecipeManager rm = world.getRecipeManager();
        registration.addRecipes(rm.getRecipes(BotaniaPPRecipeTypes.BOUGANVILLEA_TYPE.type).values(), RecipeCategoryBouganvillea.LOCATION);
        registration.addRecipes(Collections.singletonList(RecipeLeakyPool.getInstance()), RecipeCategoryTNTPool.LOCATION);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(BotaniaPPFlowers.bouganvillea), RecipeCategoryBouganvillea.LOCATION);
        registration.addRecipeCatalyst(new ItemStack(BotaniaPPFlowers.bouganvilleaFloating), RecipeCategoryBouganvillea.LOCATION);

        registration.addRecipeCatalyst(new ItemStack(BotaniaPPItems.BOTANIA_ENTROPIC_LENS), RecipeCategoryTNTPool.LOCATION);
        registration.addRecipeCatalyst(new ItemStack(Blocks.TNT), RecipeCategoryTNTPool.LOCATION);
        ItemStack renderStack = new ItemStack(BotaniaPPBlocks.BOTANIA_MANA_POOL);
        ItemNBTHelper.setBoolean(renderStack, "RenderFull", true);
        registration.addRecipeCatalyst(renderStack, RecipeCategoryTNTPool.LOCATION);
    }

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
        RecipeLeakyPool.onChange("LEAKY_POOL_EXPLOSION_RECIPE", RecipeLeakyPool.LEAKY_POOL_EXPLOSION_RECIPE);
    }

}
