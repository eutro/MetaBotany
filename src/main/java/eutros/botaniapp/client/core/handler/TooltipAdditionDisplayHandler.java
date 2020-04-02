package eutros.botaniapp.client.core.handler;

import com.mojang.blaze3d.systems.RenderSystem;
import eutros.botaniapp.common.item.ItemTerraPickPP;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.mana.ICompositableLens;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static eutros.botaniapp.common.item.ItemTerraPickPP.OLD_MAX;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Reference.MOD_ID)
public final class TooltipAdditionDisplayHandler {

    @SubscribeEvent()
    public static void onToolTipRender(RenderTooltipEvent.PostText evt) {

        if(evt.getStack().isEmpty())
            return;

        ItemStack stack = evt.getStack();
        int width = evt.getWidth();
        int height = 3;
        int tooltipX = evt.getX();
        int tooltipY = evt.getY();
        FontRenderer font = evt.getFontRenderer();

        Item item = stack.getItem();
        RenderSystem.pushMatrix();
        RenderSystem.translated(0, 0, 300); // Z level wars
        if(item instanceof ItemTerraPickPP) {
            drawTerraPick(stack, tooltipX, tooltipY, width, height, font);
        }
        if(item instanceof ICompositableLens) {
            ItemStack composite = ((ICompositableLens) item).getCompositeLens(stack);
            drawCompositeLens(composite, tooltipX, tooltipY, evt.getLines(), font);
        }
        RenderSystem.popMatrix();
    }


    @SubscribeEvent()
    public static void preToolTipRender(ItemTooltipEvent evt) {

        ItemStack stack = evt.getItemStack();
        List<ITextComponent> tooltip = evt.getToolTip();
        PlayerEntity player = evt.getPlayer();
        ITooltipFlag flags = evt.getFlags();

        if(stack.isEmpty()) {
            return;
        }

        Item item = stack.getItem();
        if(item instanceof ICompositableLens) {
            addCompositeLensTooltip(stack, tooltip, player, flags, (ICompositableLens) item);
        }
    }

    private static void addCompositeLensTooltip(ItemStack stack, List<ITextComponent> tooltip, PlayerEntity player, ITooltipFlag flags, ICompositableLens item) {
        ItemStack composite = item.getCompositeLens(stack);
        if(!composite.isEmpty()) {
            TranslationTextComponent tooltipStart = new TranslationTextComponent("botaniapp.tooltip.composite");
            tooltipStart.getStyle().setItalic(true).setColor(TextFormatting.GRAY);
            StringTextComponent blank = new StringTextComponent("");

            List<ITextComponent> compositeTooltip = composite.getTooltip(player, flags);
            MinecraftForge.EVENT_BUS.post(new ItemTooltipEvent(composite, player, compositeTooltip, flags));

            List<ITextComponent> newTooltip = new ArrayList<>();
            newTooltip.add(tooltipStart);
            newTooltip.add(blank);
            newTooltip.add(blank);

            compositeTooltip.stream().map(
                    l -> new StringTextComponent("- ")
                            .appendSibling(l))
                    .forEach(newTooltip::add);

            tooltip.addAll(1, newTooltip);
        }
    }

    private static void drawCompositeLens(ItemStack composite, int tooltipX, int tooltipY, List<String> lines, FontRenderer font) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        final Pattern pattern = Pattern.compile(".+\\u00a7o" + I18n.format("botaniapp.tooltip.composite") + ".+");

        int offset = 1;
        for(String line : lines)
            if(pattern.matcher(line).matches())
                break;
        offset++;

        itemRenderer.renderItemAndEffectIntoGUI(composite, tooltipX, tooltipY + 11 * offset);
    }

    private static void drawTerraPick(ItemStack stack, int mouseX, int mouseY, int width, int height, FontRenderer font) {
        mouseY -= 4;
        int level = ItemTerraPickPP.getLevel(stack);
        BigInteger last = BigInteger.TEN.pow(level + 4);
        BigInteger max = BigInteger.TEN.pow(level + 5).subtract(last);
        BigInteger curr = ItemTerraPickPP.getTrueMana(stack).subtract(last);
        float percent = level == 0 ?
                        0F :
                        new BigDecimal(curr).divide(new BigDecimal(max), MathContext.DECIMAL32).floatValue();
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