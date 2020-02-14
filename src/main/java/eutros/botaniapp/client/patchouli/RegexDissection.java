package eutros.botaniapp.client.patchouli;

import com.mojang.blaze3d.platform.GlStateManager;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.glfw.GLFWGamepadState;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.api.VariableHolder;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

public class RegexDissection implements ICustomComponent {

    @VariableHolder public String text;

    public static void init() {
        ResourceLocation regexDissection = new ResourceLocation(Reference.MOD_ID, "patchouli/templates/regex_dissection.json");

        Supplier<InputStream> regexDissectionSupplier = () -> {
            try {
                return Minecraft.getInstance().getResourceManager().getResource(regexDissection).getInputStream();
            } catch (IOException e) {
                System.out.println("Uh oh, stinky.");
            }
            return null;
        };

        PatchouliAPI.instance.registerTemplateAsBuiltin(new ResourceLocation("botania", "regex_dissection"), regexDissectionSupplier);
    }

    int x, y;

    @Override
    public void build(int componentX, int componentY, int pageNum) {
        this.x = componentX;
        this.y = componentY;
    }

    @Override
    public void render(IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
        FontRenderer font = context.getFont();
        float sf = 1.2f;
        GlStateManager.scalef(sf, sf, 1f);
        String s = I18n.format(text);
        font.drawStringWithShadow(s, (x-font.getStringWidth(s)/2f)/ sf, y/ sf, 0xFFFFFFFF);
        GlStateManager.scalef(1/sf, 1/sf, 1f);
    }
}
