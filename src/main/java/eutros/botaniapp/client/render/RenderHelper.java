package eutros.botaniapp.client.render;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.container.PlayerContainer;
import org.lwjgl.opengl.GL11;

public final class RenderHelper {

    public static final RenderType ICON_OVERLAY;

    static {
        RenderState.LightmapState enableLightmap = new RenderState.LightmapState(true);
        RenderState.TransparencyState translucentTransparency = net.minecraftforge.fml.common.ObfuscationReflectionHelper.getPrivateValue(RenderState.class, null, "field_228515_g_");
        RenderState.TextureState mipmapBlockAtlasTexture = new RenderState.TextureState(PlayerContainer.BLOCK_ATLAS_TEXTURE, false, true);

        assert translucentTransparency != null;
        RenderType.State glState = RenderType.State.builder().texture(mipmapBlockAtlasTexture).transparency(translucentTransparency).lightmap(enableLightmap).build(true);
        ICON_OVERLAY = RenderType.of("botania:icon_overlay", DefaultVertexFormats.POSITION_COLOR_TEXTURE_LIGHT, GL11.GL_QUADS, 128, glState);
    }
}
