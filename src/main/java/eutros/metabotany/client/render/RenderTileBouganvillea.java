package eutros.metabotany.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import eutros.metabotany.api.internal.config.Configurable;
import eutros.metabotany.client.core.handler.ClientTickHandler;
import eutros.metabotany.common.block.flower.functional.SubtileBouganvillea;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;

public class RenderTileBouganvillea extends TileEntityRenderer<SubtileBouganvillea> {

    public RenderTileBouganvillea(TileEntityRendererDispatcher p_i226006_1_) {
        super(p_i226006_1_);
    }

    @Configurable(path = {"render", "bouganvillea"},
                  side = ModConfig.Type.CLIENT,
                  comment = "Disable the Bouganvillea's special rendering altogether.")
    public static boolean DISABLE = false;

    @Configurable(path = {"render", "bouganvillea"},
                  side = ModConfig.Type.CLIENT,
                  comment = "The time, in ticks, that it takes to swap the item being shown.")
    public static float SWAP_PERIOD = 63;

    @Configurable(path = {"render", "bouganvillea"},
                  side = ModConfig.Type.CLIENT,
                  comment = "The time, in ticks, that it takes for an item to go around the flower once, +- 10%")
    public static float ROTATION_PERIOD = 47;

    @Configurable(path = {"render", "bouganvillea"},
                  side = ModConfig.Type.CLIENT,
                  comment = "The time, in ticks, that it takes the item to go from 0 to MAX_TILT and back, +- 10%.")
    public static float TILT_PERIOD = 11;

    @Configurable(path = {"render", "bouganvillea"},
                  side = ModConfig.Type.CLIENT,
                  comment = {"The maximum angle, in degrees, that an item is rotated, about an axis parallel to its horizontal motion.",
                          "e.g. at 0, the item won't move up and down at all, at 90, the item will go all the way above and below the flower."})
    public static float MAX_TILT = 30;

    @Configurable(path = {"render", "bouganvillea"},
                  side = ModConfig.Type.CLIENT,
                  comment = "The time, in ticks, that it takes for an item to rotate, about its own vertical axis.")
    public static float ITEM_ROTATION_PERIOD = 119;

    @Configurable(path = {"render", "bouganvillea"},
                  side = ModConfig.Type.CLIENT,
                  comment = "The maximum number of items that will be floating around the Bouganvillea at once.")
    public static int MAX_ITEMS = 3;

    @Configurable(path = {"render", "bouganvillea"},
                  side = ModConfig.Type.CLIENT,
                  comment = "The distance, in blocks, that the item is from the flower.")
    public static float DISTANCE = 0.5F;

    @Override
    @ParametersAreNonnullByDefault
    public void render(@Nonnull SubtileBouganvillea flower, float v, MatrixStack ms, @NotNull IRenderTypeBuffer buffers, int light, int overlay) {
        if(DISABLE)
            return;

        List<ItemStack> memory = flower.getMemory();
        if(memory.isEmpty() ||
        SWAP_PERIOD * ROTATION_PERIOD * TILT_PERIOD * ITEM_ROTATION_PERIOD == 0)
            return;

        World world = flower.getWorld();
        assert world != null;
        Random random = new Random(flower.hashCode());

        boolean flipped = random.nextBoolean(); // Flip orbit direction.
        float swapPhase = (ClientTickHandler.total + random.nextInt(1000)) / SWAP_PERIOD; // Random phase, synced between items.
        Vec3d offset = world.getBlockState(flower.getPos()).getOffset(world, flower.getPos());

        ms.push();
        ms.translate(0.5 + offset.x, 0.5 + offset.y, 0.5 + offset.z); // Move the stack to the center of the flower.
        for(int i = 0; i < MAX_ITEMS && !memory.isEmpty(); i++) {
            ms.push();
            ItemStack stack = memory.remove(((int) (swapPhase) + i) % memory.size());

            ms.rotate( // Rotate about the vertical axis of the flower.
                    (i % 2 == 0 // Alternate between anticlockwise and clockwise.
                             ^ flipped ? Vector3f.YP : Vector3f.YN)
                            .rotation((float) (random.nextFloat() * Math.PI + // Random offset.
                                            (ClientTickHandler.total * Math.PI *
                                                    2 /
                                                    (ROTATION_PERIOD * (1 - 0.2 * random.nextFloat())) // Random offset to de-sync phases.
                                            )
                                    )
                            )
            );
            ms.rotate(Vector3f.ZP // Tilt perpendicular to the vertical axis of the flower.
                    .rotationDegrees((float) (Math.sin(ClientTickHandler.total / (TILT_PERIOD * (1 - 0.2 * random.nextFloat())) + (random.nextFloat() * Math.PI)) * MAX_TILT)));

            ms.translate(DISTANCE, 0, 0); // Move the item so it orbits around the flower.

            float size = Math.abs((float) Math.sin(swapPhase * Math.PI));
            ms.scale(size, size, size); // Resize the item.
            ms.rotate((random.nextBoolean() ? Vector3f.YP : Vector3f.YN) // Rotate the item about its own vertical axis.
                    .rotation((float) (ClientTickHandler.total / ITEM_ROTATION_PERIOD + random.nextFloat() * Math.PI)));

            Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, light, overlay, ms, buffers);

            ms.pop();
        }
        ms.pop();
    }

}
