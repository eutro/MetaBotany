/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 * File Created @ [May 20, 2014, 10:56:14 PM (GMT)]
 */
package eutros.botaniapp.common.item;

import com.google.common.math.BigIntegerMath;
import eutros.botaniapp.common.core.helper.ItemNBTHelper;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.botania.common.core.handler.ModSounds;
import vazkii.botania.common.core.helper.PlayerHelper;
import vazkii.botania.common.item.ItemTemperanceStone;
import vazkii.botania.common.item.equipment.tool.ToolCommons;
import vazkii.botania.common.item.equipment.tool.terrasteel.ItemTerraPick;
import vazkii.botania.common.item.relic.ItemThorRing;

import javax.annotation.Nonnull;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

public class ItemTerraPickPP extends ItemTerraPick {

    private static final BigInteger MAX_INT = BigInteger.valueOf(Integer.MAX_VALUE);

    private static final String TAG_ENABLED = "enabled";
    private static final String TAG_MANA = "mana";
    private static final String TAG_TRUE_MANA = "true_mana";

    private static final List<Material> MATERIALS = Arrays.asList(Material.ROCK, Material.IRON, Material.ICE,
            Material.GLASS, Material.PISTON, Material.ANVIL, Material.ORGANIC, Material.EARTH, Material.SAND,
            Material.SNOW, Material.SNOW_BLOCK, Material.CLAY);

    private static final int[] CREATIVE_MANA = new int[] {
            10000 - 1, 1000000 - 1, 10000000 - 1, 100000000 - 1, 1000000000 - 1, Integer.MAX_VALUE - 1
    };

    public ItemTerraPickPP(Properties props) {
        super(props);
    }

    @Override
    public void fillItemGroup(@Nonnull ItemGroup tab, @Nonnull NonNullList<ItemStack> list) {
        if(isInGroup(tab)) {
            for(int mana : CREATIVE_MANA) {
                ItemStack stack = new ItemStack(this);
                setTrueMana(stack, BigInteger.valueOf(mana));
                list.add(stack);
            }
            ItemStack stack = new ItemStack(this);
            setTrueMana(stack, BigInteger.valueOf(CREATIVE_MANA[1]));
            setTipped(stack);
            list.add(stack);
        }
    }

    @Override
    public void breakOtherBlock(PlayerEntity player, ItemStack stack, BlockPos pos, BlockPos originPos, Direction side) {
        if(!isEnabled(stack))
            return;

        World world = player.world;
        Material mat = world.getBlockState(pos).getMaterial();
        if(!MATERIALS.contains(mat))
            return;

        if(world.isAirBlock(pos))
            return;

        boolean thor = !ItemThorRing.getThorRing(player).isEmpty();
        boolean doX = thor || side.getXOffset() == 0;
        boolean doY = thor || side.getYOffset() == 0;
        boolean doZ = thor || side.getZOffset() == 0;

        int origLevel = getLevel(stack);
        int level = origLevel + (thor ? 1 : 0);
        if(ItemTemperanceStone.hasTemperanceActive(player) && level > 2)
            level = 2;

        int range = level - 1;
        int rangeY = Math.max(1, range);

        if(range == 0 && level != 1)
            return;

        Vec3i beginDiff = new Vec3i(doX ? -range : 0, doY ? -1 : 0, doZ ? -range : 0);
        Vec3i endDiff = new Vec3i(doX ? range : 0, doY ? rangeY * 2 - 1 : 0, doZ ? range : 0);

        ToolCommons.removeBlocksInIteration(player, stack, world, pos, beginDiff, endDiff, state -> MATERIALS.contains(state.getMaterial()), isTipped(stack));

        if(origLevel == 5) {
            PlayerHelper.grantCriterion((ServerPlayerEntity) player, new ResourceLocation("botania:challenge/rank_ss_pick"), "code_triggered");
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<ITextComponent> stacks, ITooltipFlag flags) {
        int level = getLevel(stack);
        ITextComponent rank = new TranslationTextComponent("botania.rank" + Math.min(5, level));

        int pluses = 0;
        if(level >= ItemTerraPickPP.LEVELS.length - 1)
            pluses = (level - ItemTerraPickPP.LEVELS.length) + 1;
        rank.appendText(new String(new char[pluses]).replace("\0", "S"));

        ITextComponent rankFormat = new TranslationTextComponent("botaniamisc.toolRank", rank);
        stacks.add(rankFormat);
        if(getTrueMana(stack).compareTo(MAX_INT) >= 0)
            stacks.add(new TranslationTextComponent("botaniamisc.getALife").applyTextStyle(TextFormatting.RED));
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getHeldItem(hand);

        getMana(stack);
        int level = getLevel(stack);

        if(level != 0) {
            setEnabled(stack, !isEnabled(stack));
            if(!world.isRemote)
                world.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.terraPickMode, SoundCategory.PLAYERS, 0.5F, 0.4F);
        }

        return ActionResult.success(stack);
    }

    public int getMana(ItemStack stack) {
        return 0;
    }

    public static void setTrueMana(ItemStack stack, BigInteger mana) {
        ItemNBTHelper.setInt(stack, TAG_MANA, mana.min(MAX_INT).intValueExact());
        ItemNBTHelper.setByteArray(stack, TAG_TRUE_MANA, mana.toByteArray());
    }

    void setEnabled(ItemStack stack, boolean enabled) {
        ItemNBTHelper.setBoolean(stack, TAG_ENABLED, enabled);
    }

    public static BigInteger getTrueMana(ItemStack stack) {
        return new BigInteger(ItemNBTHelper.getByteArray(stack, TAG_TRUE_MANA, 1));
    }

    public static int getLevel(ItemStack stack) {
       BigInteger mana = getTrueMana(stack);

        int level = BigIntegerMath.log10(mana.max(BigInteger.ONE), RoundingMode.FLOOR);

        return Math.max(0, level - 4);
    }

    @Override
    public void addMana(ItemStack stack, int mana) {
        setTrueMana(stack, getTrueMana(stack).add(BigInteger.valueOf(mana)));
    }

}
