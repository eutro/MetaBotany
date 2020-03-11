package eutros.botaniapp.common.core.hacks;

import eutros.botaniapp.common.utils.Reference;
import joptsimple.internal.Reflection;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.FlatPresetsScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IItemProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.FlatLayerInfo;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.item.ModItems;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class HackFlatPreset {

    @SubscribeEvent
    public static void guiOpened(GuiOpenEvent event) {
        // Just invoke the static block the first time this is fired.
        Inner.prod();
    }

    private static class Inner {

        public static void prod() {}

        static {
            Method addPreset = ObfuscationReflectionHelper.findMethod(FlatPresetsScreen.class,
                    "addPreset",
                    String.class,
                    IItemProvider.class,
                    Biome.class,
                    List.class,
                    FlatLayerInfo[].class);
            Reflection.invoke(addPreset,
                    I18n.format("botaniapp.customize.preset.botanist_ready"),
                    ModItems.twigWand,
                    Biomes.PLAINS,
                    Collections.emptyList(),
                    new FlatLayerInfo[]{new FlatLayerInfo(52, ModBlocks.livingrockBrick), new FlatLayerInfo(3, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK)});
        }
    }
}
