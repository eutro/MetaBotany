package eutros.botaniapp.data;

import eutros.botaniapp.common.utils.Reference;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent e) {
        e.getGenerator().addProvider(new BlockLootProvider(e.getGenerator()));
    }

}
