package eutros.botaniapp.client.core.handler;

import com.mojang.blaze3d.systems.RenderSystem;
import eutros.botaniapp.common.item.ItemTerraPickPP;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import static eutros.botaniapp.common.item.ItemTerraPickPP.OLD_MAX;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Reference.MOD_ID)
public final class TooltipAdditionDisplayHandler {

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onToolTipRender(RenderTooltipEvent.PostText evt) {

        if(evt.getStack().isEmpty())
            return;

        ItemStack stack = evt.getStack();
        int width = evt.getWidth();
        int height = 3;
        int tooltipX = evt.getX();
        int tooltipY = evt.getY() - 4;
        FontRenderer font = evt.getFontRenderer();

        if (stack.getItem() instanceof ItemTerraPickPP) {
            drawTerraPick(stack, tooltipX, tooltipY, width, height, font);
        }
    }

    private static void drawTerraPick(ItemStack stack, int mouseX, int mouseY, int width, int height, FontRenderer font) {
        int level = ItemTerraPickPP.getLevel(stack);
        BigInteger last = BigInteger.TEN.pow(level+4);
        BigInteger max = BigInteger.TEN.pow(level+5).subtract(last);
        BigInteger curr = ItemTerraPickPP.getTrueMana(stack).subtract(last);
        float percent = level == 0 ? 0F : new BigDecimal(curr).divide(new BigDecimal(max), MathContext.DECIMAL32).floatValue();
        int rainbowWidth = Math.min(width, (int) (width * percent));
        float huePer = width == 0 ? 0F : 1F / width;
        float hueOff = ClientTickHandler.total * 0.01F;

        RenderSystem.disableDepthTest();
        AbstractGui.fill(mouseX - 1, mouseY - height - 1, mouseX + width + 1, mouseY, 0xFF000000);
        for(int i = 0; i < rainbowWidth; i++)
            AbstractGui.fill(mouseX + i, mouseY - height, mouseX + i + 1, mouseY, MathHelper.hsvToRGB(hueOff + huePer * i, 1F, 1F));
        AbstractGui.fill(mouseX + rainbowWidth, mouseY - height, mouseX + width, mouseY, 0xFF555555);

        String rank = getRank(level);

        GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
        RenderSystem.disableLighting();
        font.drawStringWithShadow(rank, mouseX, mouseY - 12, 0xFFFFFF);
        rank = getRank(level + 1);
        int stringWidth = font.getStringWidth(rank);
        if(stringWidth < width / 2)
            font.drawStringWithShadow(rank, mouseX + width - stringWidth, mouseY - 12, 0xFFFFFF);
        RenderSystem.enableLighting();
        RenderSystem.enableDepthTest();
        GL11.glPopAttrib();
    }

    private static String getRank(int level) {
        String plain = I18n.format("botania.rank" + Math.min(5, level)).replaceAll("&", "\u00a7");
        int pluses = 0;
        if(level >= OLD_MAX)
            pluses = level - OLD_MAX;
        return plain + new String(new char[pluses]).replace("\0", "+");
    }
}