package eutros.metabotany.common.core.hacks;

import eutros.metabotany.common.block.MetaBotanyBlocks;
import eutros.metabotany.common.item.MetaBotanyItems;
import eutros.metabotany.common.utils.Reference;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.FlatPresetsScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.FlatLayerInfo;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collections;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class HackFlatPreset {

    private static boolean initialized = false;

    @SubscribeEvent
    public static void guiOpened(GuiOpenEvent event) {
        if(!initialized && event.getGui() instanceof FlatPresetsScreen) {
            addPreset();
            initialized = true;
        }
    }

    static void addPreset() {
        FlatPresetsScreen.addPreset(
                I18n.format("metabotany.customize.preset.botanist_ready"),
                MetaBotanyItems.BOTANIA_TWIG_WAND,
                Biomes.PLAINS,
                Collections.emptyList(),
                new FlatLayerInfo(64, MetaBotanyBlocks.BOTANIA_LIVINGROCK_BRICKS), new FlatLayerInfo(1, Blocks.BEDROCK)
        );
    }

}
