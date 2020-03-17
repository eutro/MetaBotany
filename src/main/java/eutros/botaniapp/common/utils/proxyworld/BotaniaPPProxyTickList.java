package eutros.botaniapp.common.utils.proxyworld;

import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.TickPriority;
import net.minecraft.world.server.ServerTickList;

import java.util.List;

@SuppressWarnings({"ConstantConditions", "NullableProblems"})
public class BotaniaPPProxyTickList<T> extends ServerTickList<T> {
    private ServerTickList<T> innerList;

    public BotaniaPPProxyTickList(ServerTickList<T> tickList) {
        super(null, null, null, null, null);
        innerList = tickList;
    }

    @Override
    public void tick() {
        innerList.tick();
    }

    @Override
    public boolean isTickPending(BlockPos p_205361_1_, T p_205361_2_) {
        return innerList.isTickPending(p_205361_1_, p_205361_2_);
    }

    @Override
    public List<NextTickListEntry<T>> getPending(MutableBoundingBox p_205366_1_, boolean p_205366_2_, boolean p_205366_3_) {
        return innerList.getPending(p_205366_1_, p_205366_2_, p_205366_3_);
    }

    @Override
    public void copyTicks(MutableBoundingBox p_205368_1_, BlockPos p_205368_2_) {
        innerList.copyTicks(p_205368_1_, p_205368_2_);
    }

    @Override
    public ListNBT func_219503_a(ChunkPos p_219503_1_) {
        return innerList.func_219503_a(p_219503_1_);
    }

    @Override
    public boolean isTickScheduled(BlockPos p_205359_1_, T p_205359_2_) {
        return innerList.isTickScheduled(p_205359_1_, p_205359_2_);
    }

    @Override
    public void scheduleTick(BlockPos p_205362_1_, T p_205362_2_, int p_205362_3_, TickPriority p_205362_4_) {
        innerList.scheduleTick(p_205362_1_, p_205362_2_, p_205362_3_, p_205362_4_);
    }

    @Override
    public int func_225420_a() {
        return innerList.func_225420_a();
    }
}
