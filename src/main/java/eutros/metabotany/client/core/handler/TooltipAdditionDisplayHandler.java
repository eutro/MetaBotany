package eutros.metabotany.client.core.handler;

import com.mojang.blaze3d.systems.RenderSystem;
import eutros.metabotany.common.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
import vazkii.botania.api.mana.ICompositableLens;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Reference.MOD_ID)
public final class TooltipAdditionDisplayHandler {

    @SubscribeEvent()
    public static void onToolTipRender(RenderTooltipEvent.PostText evt) {

        if(evt.getStack().isEmpty())
            return;

        ItemStack stack = evt.getStack();
        int tooltipX = evt.getX();
        int tooltipY = evt.getY();

        Item item = stack.getItem();
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0, 0, 300); // Z level wars
        if(item instanceof ICompositableLens) {
            ItemStack composite = ((ICompositableLens) item).getCompositeLens(stack);
            drawCompositeLens(composite, tooltipX, tooltipY, evt.getLines());
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
            TranslationTextComponent tooltipStart = new TranslationTextComponent("metabotany.tooltip.composite");
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

    // TODO fix this or something
    private static void drawCompositeLens(ItemStack composite, int tooltipX, int tooltipY, List<String> lines) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        final Pattern pattern = Pattern.compile(".+\\u00a7o" + I18n.format("metabotany.tooltip.composite") + ".+");

        int offset = 1;
        for(String line : lines)
            if(pattern.matcher(line).matches())
                break;
        offset++;

        itemRenderer.renderItemAndEffectIntoGUI(composite, tooltipX, tooltipY + 11 * offset);
    }
}