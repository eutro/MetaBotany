package eutros.metabotany.common.sound;

import eutros.metabotany.common.utils.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Reference.MOD_ID)
public class MetaBotanySounds {

    public static SoundEvent BOTANIA_DING;
    public static SoundEvent BOTANIA_SPREADER_FIRE;

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void setBotaniaSounds(RegistryEvent.Register<SoundEvent> evt) {
        IForgeRegistry<SoundEvent> r = evt.getRegistry();

        final String b = "botania";

        BOTANIA_DING = r.getValue(new ResourceLocation(b, "ding"));
        BOTANIA_SPREADER_FIRE = r.getValue(new ResourceLocation(b, "spreaderfire"));
    }

}
