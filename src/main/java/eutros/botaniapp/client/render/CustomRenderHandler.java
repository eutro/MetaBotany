package eutros.botaniapp.client.render;

import eutros.botaniapp.client.render.model.FloatingFlowerModel;
import eutros.botaniapp.common.block.flower.functional.SubtileBouganvillea;
import eutros.botaniapp.common.block.tile.TileChargingPlate;
import eutros.botaniapp.common.block.tile.TileLeakyPool;
import eutros.botaniapp.common.block.tinkerer.tile.TileFrameTinkerer;
import eutros.botaniapp.common.entity.cart.EntityGenericBlockCart;
import eutros.botaniapp.common.entity.cart.EntityGenericTileEntityCart;
import eutros.botaniapp.common.utils.Reference;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import vazkii.botania.client.render.tile.RenderTileFloatingFlower;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid= Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CustomRenderHandler {
	
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent evt) {
		ModelLoaderRegistry.registerLoader(FloatingFlowerModel.Loader.ID, FloatingFlowerModel.Loader.INSTANCE);

		ClientRegistry.bindTileEntityRenderer(SubtileBouganvillea.TYPE, RenderTileFloatingFlower::new);

		ClientRegistry.bindTileEntityRenderer(TileChargingPlate.TYPE, RenderTileChargingPlate::new);
		ClientRegistry.bindTileEntityRenderer(TileFrameTinkerer.TYPE, RenderTileFrameTinkerer::new);
		ClientRegistry.bindTileEntityRenderer(TileLeakyPool.TYPE, RenderTileLeakyPool::new);

		RenderingRegistry.registerEntityRenderingHandler(EntityGenericBlockCart.TYPE, RenderGenericBlockMinecart::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityGenericTileEntityCart.TYPE, RenderGenericTileEntityMinecart::new);
	}
	
	private CustomRenderHandler() {}
}