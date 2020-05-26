package eutros.metabotany.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

public class RenderTileComposite<T extends TileEntity> extends TileEntityRenderer<T> {

    private TileEntityRenderer<? super T> first;
    private TileEntityRenderer<? super T> second;

    private RenderTileComposite(TileEntityRendererDispatcher dispatcher,
                               TileEntityRenderer<? super T> first,
                               TileEntityRenderer<? super T> second) {
        super(dispatcher);
        this.first = first;
        this.second = second;
    }

    public static <T extends TileEntity> Function<? super TileEntityRendererDispatcher, RenderTileComposite<T>> of(
            Function<? super TileEntityRendererDispatcher, ? extends TileEntityRenderer<? super T>> first,
            Function<? super TileEntityRendererDispatcher, ? extends TileEntityRenderer<? super T>> second) {
        return d -> new RenderTileComposite<>(d, first.apply(d), second.apply(d));
    }

    @ParametersAreNonnullByDefault
    @Override
    public void render(T tile, float v, MatrixStack ms, IRenderTypeBuffer buffers, int light, int overlay) {
        first.render(tile, v, ms, buffers, light, overlay);
        second.render(tile, v, ms, buffers, light, overlay);
    }

    @Override
    public boolean isGlobalRenderer(T tile) {
        return first.isGlobalRenderer(tile) || second.isGlobalRenderer(tile);
    }

}
