package eutros.botaniapp.common.crafting.recipe;

import eutros.botaniapp.api.internal.config.Configurable;
import eutros.botaniapp.client.integration.jei.JEIBotaniaPPPlugin;
import eutros.botaniapp.client.integration.jei.RecipeCategoryTNTPool;
import eutros.botaniapp.common.block.BotaniaPPBlocks;
import eutros.botaniapp.common.utils.Reference;
import mezz.jei.api.recipe.IRecipeManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vazkii.botania.api.mana.IManaPool;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class RecipeLeakyPool {

    private static RecipeLeakyPool INSTANCE = null;

    @Configurable(path = "crafting",
                  comment = "Should the default Leaky Mana Pool recipe be enabled?",
                  callback = "onChange")
    public static boolean LEAKY_POOL_EXPLOSION_RECIPE = true;

    @SuppressWarnings("unused")
    public static void onChange(String name, boolean value) {
        if(JEIBotaniaPPPlugin.runtime == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if(!minecraft.isOnExecutionThread()) {
            minecraft.runImmediately(() -> onChange(name, value));
            return;
        }

        IRecipeManager manager = JEIBotaniaPPPlugin.runtime.getRecipeManager();
        if(value) {
            manager.unhideRecipe(INSTANCE, RecipeCategoryTNTPool.LOCATION);
        } else {
            manager.hideRecipe(INSTANCE, RecipeCategoryTNTPool.LOCATION);
        }
    }

    @Configurable(path = "crafting",
                  comment = "The mana cost of the Leaky Mana Pool recipe, as the number of full diluted mana pools it could fill.")
    public static float LEAKY_POOL_COST = 1;

    public static RecipeLeakyPool getInstance() {
        if(INSTANCE == null)
            INSTANCE = new RecipeLeakyPool();
        return INSTANCE;
    }

    @SubscribeEvent
    public static void onExplosion(ExplosionEvent.Detonate event) {
        if(!LEAKY_POOL_EXPLOSION_RECIPE)
            return;

        Explosion explosion = event.getExplosion();
        World world = event.getWorld();
        BlockPos pos = new BlockPos(explosion.getPosition());
        BlockState state = world.getBlockState(pos);

        if(state.getBlock() == BotaniaPPBlocks.BOTANIA_MANA_POOL) {
            int trueCost = (int) (10000 * LEAKY_POOL_COST);

            TileEntity tile = world.getTileEntity(pos);
            int mana = 0;
            if(tile instanceof IManaPool) {
                mana = ((IManaPool) tile).getCurrentMana();
            }

            if(mana < trueCost)
                return;

            world.setBlockState(pos, BotaniaPPBlocks.leakyPool.getDefaultState());
            tile = world.getTileEntity(pos);

            if(tile instanceof IManaPool)
                ((IManaPool) tile).recieveMana(mana - trueCost);

            event.getAffectedBlocks().remove(pos);
        }
    }

}
