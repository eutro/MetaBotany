package eutros.botaniapp.client.render;

import eutros.botaniapp.client.render.model.FloatingFlowerModel;
import eutros.botaniapp.common.block.flower.BotaniaPPFlowers;
import eutros.botaniapp.common.block.tile.TileChargingPlate;
import eutros.botaniapp.common.block.tile.TileLeakyPool;
import eutros.botaniapp.common.block.tinkerer.tile.TileFrameTinkerer;
import eutros.botaniapp.common.entity.cart.EntityGenericBlockCart;
import eutros.botaniapp.common.entity.cart.EntityGenericTileEntityCart;
import eutros.botaniapp.common.utils.Reference;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid= Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CustomRenderHandler {
	
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent evt) {
		ModelLoaderRegistry.registerLoader(FloatingFlowerModel.Loader.ID, FloatingFlowerModel.Loader.INSTANCE);

		BotaniaPPFlowers.getTypes().stream()
				.map(Pair::getRight)
				.map(rl -> Registry.BLOCK_ENTITY_TYPE.getValue(rl).orElseThrow(NullPointerException::new))
				.forEach(typ -> ClientRegistry.bindTileEntityRenderer(typ, RenderTileFloatingFlower::new));

		ClientRegistry.bindTileEntityRenderer(TileChargingPlate.TYPE, RenderTileChargingPlate::new);
		ClientRegistry.bindTileEntityRenderer(TileFrameTinkerer.TYPE, RenderTileFrameTinkerer::new);
		ClientRegistry.bindTileEntityRenderer(TileLeakyPool.TYPE, RenderTileLeakyPool::new);

		RenderingRegistry.registerEntityRenderingHandler(EntityGenericBlockCart.TYPE, MinecartRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityGenericTileEntityCart.TYPE, RenderGenericTileEntityMinecart::new);
	}
	
	private CustomRenderHandler() {}
}