package eutros.metabotany.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import eutros.metabotany.client.core.handler.ClientTickHandler;
import eutros.metabotany.common.block.tile.TileSparkPainter;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

public class RenderTileSparkPainter extends TileEntityRenderer<TileSparkPainter> {

    public RenderTileSparkPainter(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void render(TileSparkPainter te, float partialTicks, MatrixStack ms, IRenderTypeBuffer buffers, int light, int overlay) {

        float minUV = 6 / 16F;
        float maxUV = 10 / 16F;
        TextureAtlasSprite icon = RenderHelper.getInstance().sparkPainterSpriteMap.get(te.color);
        IVertexBuilder buffer = buffers.getBuffer(RenderHelper.ICON_OVERLAY);

        ms.push();

        ms.translate(0.5, 0.5, 0.5);
        ms.scale(0.1F, 0.1F, 0.1F);

        Random random = new Random(te.getPos().hashCode());

        double BOB_FREQUENCY = 40 + 10 * random.nextGaussian();
        double BOB_HEIGHT = 0.25;
        ms.translate(0, Math.sin(random.nextDouble() * Math.PI * 2 + ClientTickHandler.total / BOB_FREQUENCY) * BOB_HEIGHT, 0);

        double ROTATION_PERIOD = 200 + 50 * random.nextGaussian();
        ms.rotate((random.nextBoolean() ?
                   Vector3f.YP :
                   Vector3f.YN).rotation((float) (Math.PI * 2 * ClientTickHandler.total / ROTATION_PERIOD)));

        ms.rotate(Vector3f.XP.rotation((float) (Math.PI * random.nextGaussian() * 0.02)));

        drawFace(ms, minUV, maxUV, icon, buffer);
        ms.rotate(Vector3f.XP.rotationDegrees(90));
        ms.push();
        for(int i = 0; i < 4; i++) {
            ms.rotate(Vector3f.YP.rotationDegrees(90));
            drawFace(ms, minUV, maxUV, icon, buffer);
        }
        ms.pop();
        ms.rotate(Vector3f.XP.rotationDegrees(90));
        drawFace(ms, minUV, maxUV, icon, buffer);

        ms.pop();
    }

    private void drawFace(MatrixStack ms, float minUV, float maxUV, TextureAtlasSprite icon, IVertexBuilder buffer) {
        ms.push();
        ms.translate(0, 0, -2);
        RenderHelper.renderIcon(ms, buffer, minUV, minUV, maxUV, maxUV, -2, -2, icon, 4, 4, 1);
        ms.pop();
    }

}
