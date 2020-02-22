package eutros.botaniapp.client.core.helper;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class HUDHelper {

    public static final ResourceLocation MANA_HUD = new ResourceLocation("botania", "textures/gui/mana_hud.png");

    private HUDHelper() {}

    public static void drawSimpleManaHUD(int color, int mana, int maxMana, String name) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Minecraft mc = Minecraft.getInstance();
        int x = mc.mainWindow.getScaledWidth() / 2 - mc.fontRenderer.getStringWidth(name) / 2;
        int y = mc.mainWindow.getScaledHeight() / 2 + 10;

        mc.fontRenderer.drawStringWithShadow(name, x, y, color);

        x = mc.mainWindow.getScaledWidth() / 2 - 51;
        y += 10;

        renderManaBar(x, y, color, mana < 0 ? 0.5F : 1F, mana, maxMana);

        if(mana < 0) {
            String text = I18n.format("botaniamisc.statusUnknown");
            x = mc.mainWindow.getScaledWidth() / 2 - mc.fontRenderer.getStringWidth(text) / 2;
            y -= 1;
            mc.fontRenderer.drawString(text, x, y, color);
        }

        GlStateManager.disableBlend();
    }

    public static void drawComplexManaHUD(int color, int mana, int maxMana, String name, ItemStack bindDisplay, boolean properlyBound) {
        drawSimpleManaHUD(color, mana, maxMana, name);

        Minecraft mc = Minecraft.getInstance();

        int x = mc.mainWindow.getScaledWidth() / 2 + 55;
        int y = mc.mainWindow.getScaledHeight() / 2 + 12;

        net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();
        mc.getItemRenderer().renderItemAndEffectIntoGUI(bindDisplay, x, y);
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();

        GlStateManager.disableDepthTest();
        if(properlyBound) {
            mc.fontRenderer.drawStringWithShadow("\u2714", x + 10, y + 9, 0x004C00);
            mc.fontRenderer.drawStringWithShadow("\u2714", x + 10, y + 8, 0x0BD20D);
        } else {
            mc.fontRenderer.drawStringWithShadow("\u2718", x + 10, y + 9, 0x4C0000);
            mc.fontRenderer.drawStringWithShadow("\u2718", x + 10, y + 8, 0xD2080D);
        }
        GlStateManager.enableDepthTest();
    }

    public static void renderManaBar(int x, int y, int color, float alpha, int mana, int maxMana) {
        Minecraft mc = Minecraft.getInstance();

        GlStateManager.color4f(1F, 1F, 1F, alpha);
        mc.textureManager.bindTexture(MANA_HUD);
        RenderHelper.drawTexturedModalRect(x, y, 0, 0, 0, 102, 5);

        int manaPercentage = Math.max(0, (int) ((double) mana / (double) maxMana * 100));

        if(manaPercentage == 0 && mana > 0)
            manaPercentage = 1;

        RenderHelper.drawTexturedModalRect(x + 1, y + 1, 0, 0, 5, 100, 3);

        Color color_ = new Color(color);
        GlStateManager.color4f(color_.getRed() / 255F, color_.getGreen() / 255F, color_.getBlue() / 255F, alpha);
        RenderHelper.drawTexturedModalRect(x + 1, y + 1, 0, 0, 5, Math.min(100, manaPercentage), 3);
        GlStateManager.color4f(1, 1, 1, 1);
    }
}
