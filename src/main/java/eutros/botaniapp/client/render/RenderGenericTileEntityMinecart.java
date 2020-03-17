package eutros.botaniapp.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import eutros.botaniapp.common.block.BotaniaPPBlocks;
import eutros.botaniapp.common.entity.cart.EntityGenericBlockCart;
import eutros.botaniapp.common.entity.cart.EntityGenericTileEntityCart;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vazkii.botania.client.render.tile.RenderTileFloatingFlower;
import vazkii.botania.client.render.tile.RenderTilePool;
import vazkii.botania.common.block.mana.BlockPool;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RenderGenericTileEntityMinecart extends RenderGenericBlockMinecart {

    private static final Logger LOGGER = LogManager.getLogger();

    @SuppressWarnings("rawtypes")
    public static Set<Class<? extends TileEntityRenderer>> BLACKLIST = new HashSet<>();

    @SuppressWarnings("rawtypes")
    public static Map<Class<? extends TileEntityRenderer>, RenderFunction> SPECIAL_CASES = new HashMap<>();

    static {
        SPECIAL_CASES.put(RenderTilePool.class, (state, renderer, tile, v, ms, buffers, light) -> {
            CompoundNBT cmp = new CompoundNBT();
            cmp = tile.write(cmp);
            RenderCartPool.render(state, ((BlockPool) state.getBlock()).variant == BlockPool.Variant.FABULOUS,
                    cmp.getInt("manaCap"), cmp.getInt("mana"), tile, ms, buffers, light, OverlayTexture.DEFAULT_UV);
        });
        SPECIAL_CASES.put(RenderTileFloatingFlower.class, (state, renderer, tile, v, ms, buffers, light) -> {
            tile.cachedBlockState = state;
            RenderCartFloatingFlower.render(tile, v, ms, buffers, light, OverlayTexture.DEFAULT_UV);
        });
    }

    public RenderGenericTileEntityMinecart(EntityRendererManager manager) {
        super(manager);
    }

    @ParametersAreNonnullByDefault
    @Override
    protected void renderBlock(EntityGenericBlockCart cart, float v, BlockState state, MatrixStack ms, IRenderTypeBuffer buffers, int light) {
        TileEntity tile = ((EntityGenericTileEntityCart) cart).getTile();
        if(tile != null) {
            MatrixStack.Entry top = ms.peek();
            TileEntityRenderer<TileEntity> renderer = TileEntityRendererDispatcher.instance.getRenderer(tile);
            if (renderer != null) {
                if(BLACKLIST.contains(renderer.getClass())) {
                    super.renderBlock(cart, v, BotaniaPPBlocks.fragileBox.getDefaultState(), ms, buffers, light);
                    return;
                }
                super.renderBlock(cart, v, state, ms, buffers, light);
                try {
                    SPECIAL_CASES.getOrDefault(renderer.getClass(), new NormalRender()).render(state, renderer, tile, v, ms, buffers, light);
                } catch (NullPointerException | ObfuscationReflectionHelper.UnableToFindFieldException | ObfuscationReflectionHelper.UnableToAccessFieldException e) {
                    LOGGER.debug("Using " + renderer.getClass().toString() + " threw exception: " + e.getClass().getSimpleName() + ". Blacklisting.", e);
                    BLACKLIST.add(renderer.getClass());
                    while (ms.peek() != top) // Pop everything off or we crash later.
                        ms.pop();
                } catch (Exception e) {
                    throw new UnsupportedOperationException("A TESR in a minecart threw something Botania Plus Plus didn't catch. Please report this.", e);
                }
                return;
            }
        }
        super.renderBlock(cart, v, state, ms, buffers, light);
    }

    @FunctionalInterface
    public interface RenderFunction {
        void render(BlockState state, TileEntityRenderer<TileEntity> renderer, TileEntity tile, float v, MatrixStack ms, IRenderTypeBuffer buffers, int light);
    }

    private static class NormalRender implements RenderFunction {
        @Override
        public void render(BlockState state, TileEntityRenderer<TileEntity> renderer, TileEntity tile, float v, MatrixStack ms, IRenderTypeBuffer buffers, int light) {
            tile.cachedBlockState = state;
            renderer.render(tile, v, ms, buffers, light, OverlayTexture.DEFAULT_UV);
        }
    }
}
