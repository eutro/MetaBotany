package eutros.botaniapp.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import eutros.botaniapp.api.internal.config.Configurable;
import eutros.botaniapp.common.entity.cart.EntityGenericBlockCart;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.ParametersAreNonnullByDefault;

public class RenderGenericBlockMinecart extends MinecartRenderer<EntityGenericBlockCart> {

    public RenderGenericBlockMinecart(EntityRendererManager manager) {
        super(manager);
    }

    @Configurable(side=ModConfig.Type.CLIENT,
            path={"render", "minecart"},
            comment="Height of the ground when a plant is in a minecart.")
    public static double GROUND_HEIGHT = 0.2;

    @ParametersAreNonnullByDefault
    @Override
    protected void renderBlock(EntityGenericBlockCart cart, float v, BlockState state, MatrixStack ms, IRenderTypeBuffer buffers, int light) {
        BlockState groundState = cart.getGroundState();
        if(groundState != Blocks.AIR.getDefaultState()) {
            ms.push();
            ms.scale(1, (float) GROUND_HEIGHT, 1);
            super.renderBlock(cart, v, groundState, ms, buffers, light);
            ms.pop();

            ms.push();
            ms.translate(0, GROUND_HEIGHT, 0);
            super.renderBlock(cart, v, state, ms, buffers, light);
            ms.pop();
            return;
        }
        super.renderBlock(cart, v, state, ms, buffers, light);
    }
}
