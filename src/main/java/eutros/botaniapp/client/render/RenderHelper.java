package eutros.botaniapp.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class RenderHelper {

    public static final RenderType ICON_OVERLAY;
    private static final RenderHelper INSTANCE = new RenderHelper();


    private RenderHelper() {
    }

    public Map<DyeColor, TextureAtlasSprite> sparkPainterSpriteMap;


    static {
        RenderState.LightmapState enableLightmap = new RenderState.LightmapState(true);
        RenderState.TransparencyState translucentTransparency = ObfuscationReflectionHelper.getPrivateValue(RenderState.class, null, "field_228515_g_");
        RenderState.TextureState mipmapBlockAtlasTexture = new RenderState.TextureState(PlayerContainer.LOCATION_BLOCKS_TEXTURE, false, true);

        assert translucentTransparency != null;
        RenderType.State glState = RenderType.State.getBuilder().texture(mipmapBlockAtlasTexture).transparency(translucentTransparency).lightmap(enableLightmap).build(true);
        ICON_OVERLAY = RenderType.makeType("botaniapp:icon_overlay", DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP, GL11.GL_QUADS, 128, glState);
    }

    public static RenderHelper getInstance() {
        return INSTANCE;
    }

    @SubscribeEvent
    public void onPreStitch(TextureStitchEvent.Pre evt) {
        if(evt.getMap().getTextureLocation().equals(PlayerContainer.LOCATION_BLOCKS_TEXTURE)) {
            for(DyeColor col : DyeColor.values()) {
                evt.addSprite(new ResourceLocation(Reference.MOD_ID,
                        "blocks/spark_painter/" + col));
            }
        }
    }

    @SubscribeEvent
    public void onPostStitch(TextureStitchEvent.Post evt) {
        if(evt.getMap().getTextureLocation().equals(PlayerContainer.LOCATION_BLOCKS_TEXTURE)) {
            AtlasTexture map = evt.getMap();

            sparkPainterSpriteMap = Arrays.stream(DyeColor.values())
                    .collect(Collectors.toMap(Function.identity(),
                            col -> map.getSprite(new ResourceLocation(Reference.MOD_ID,
                                    "blocks/spark_painter/" + col))));
        }
    }

    public static void renderIcon(MatrixStack ms, IVertexBuilder buffer, float x, float y, TextureAtlasSprite icon, float width, float height, float alpha) {
        renderIcon(ms, buffer, 0, 0, 1, 1, x, y, icon, width, height, alpha);
    }

    public static void renderIcon(MatrixStack ms, IVertexBuilder buffer, float minU, float minV, float maxU, float maxV, float x, float y, TextureAtlasSprite icon, float width, float height, float alpha) {
        if(maxU > 1) {
            float singleIconWidth = width / maxU;
            renderIcon(ms, buffer, 0, minV, maxU - 1, maxV, x + singleIconWidth, y, icon, width - singleIconWidth, height, alpha);
            width = singleIconWidth;
            maxU = 1;
        }
        if(maxV > 1) {
            float singleIconHeight = height / maxV;
            renderIcon(ms, buffer, minU, 0, maxU, maxV - 1, x, y + singleIconHeight, icon, width, height - singleIconHeight, alpha);
            height = singleIconHeight;
            maxV = 1;
        }
        Matrix4f mat = ms.getLast().getMatrix();
        int fullBrightness = 0xF000F0;
        float uDiff = icon.getMaxU() - icon.getMinU();
        float vDiff = icon.getMaxV() - icon.getMinV();
        buffer.pos(mat, x, y + height, 0)
                .color(1, 1, 1, alpha).tex(icon.getMinU() + uDiff * minU, icon.getMaxV() - vDiff * (1 - maxV)).lightmap(fullBrightness).endVertex();
        buffer.pos(mat, x + width, y + height, 0)
                .color(1, 1, 1, alpha).tex(icon.getMaxU() - uDiff * (1 - maxU), icon.getMaxV() - vDiff * (1 - maxV)).lightmap(fullBrightness).endVertex();
        buffer.pos(mat, x + width, y, 0)
                .color(1, 1, 1, alpha).tex(icon.getMaxU() - uDiff * (1 - maxU), icon.getMinV() + vDiff * minV).lightmap(fullBrightness).endVertex();
        buffer.pos(mat, x, y, 0)
                .color(1, 1, 1, alpha).tex(icon.getMinU() + uDiff * minU, icon.getMinV() + vDiff * minV).lightmap(fullBrightness).endVertex();
    }

}
