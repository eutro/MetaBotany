package eutros.botaniapp.client.render;

import eutros.botaniapp.common.block.tile.TileChargingPlate;
import eutros.botaniapp.common.block.tile.TileLeakyPool;
import eutros.botaniapp.common.utils.Reference;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid= Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CustomRenderHandler {
	
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent evt) {
		ClientRegistry.bindTileEntitySpecialRenderer(TileChargingPlate.class, new RenderTileChargingPlate());
		ClientRegistry.bindTileEntitySpecialRenderer(TileLeakyPool.class, new RenderTileLeakyPool());
	}
	
	private CustomRenderHandler() {}
}