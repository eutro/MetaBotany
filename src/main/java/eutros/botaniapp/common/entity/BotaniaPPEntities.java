package eutros.botaniapp.common.entity;

import eutros.botaniapp.common.entity.cart.EntityGenericBlockCart;
import eutros.botaniapp.common.entity.cart.EntityGenericTileEntityCart;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Reference.MOD_ID)
public class BotaniaPPEntities {

    @SubscribeEvent()
    public static void registerEntities(RegistryEvent.Register<EntityType<?>> evt) {
        IForgeRegistry<EntityType<?>> r = evt.getRegistry();

        r.register(EntityType.Builder.<EntityGenericBlockCart>create(EntityGenericBlockCart::new, EntityClassification.MISC)
                    .size(0.98F, 0.7F)
                    .setTrackingRange(80)
                    .setUpdateInterval(3)
                    .setShouldReceiveVelocityUpdates(true)
                    .build("")
                    .setRegistryName("generic_block_cart"));
        r.register(EntityType.Builder.<EntityGenericTileEntityCart>create(EntityGenericTileEntityCart::new, EntityClassification.MISC)
                .size(0.98F, 0.7F)
                .setTrackingRange(80)
                .setUpdateInterval(3)
                .setShouldReceiveVelocityUpdates(true)
                .build("")
                .setRegistryName("generic_tile_entity_cart"));
    }
}
