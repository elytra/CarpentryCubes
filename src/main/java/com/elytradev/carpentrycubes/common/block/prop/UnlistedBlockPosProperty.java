package com.elytradev.carpentrycubes.common.block.prop;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedBlockPosProperty implements IUnlistedProperty<BlockPos> {

    private final String name;

    private UnlistedBlockPosProperty(String name) {
        this.name = name;
    }

    public static UnlistedBlockPosProperty create(String name) {
        return new UnlistedBlockPosProperty(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(BlockPos value) {
        return value != null;
    }

    @Override
    public Class<BlockPos> getType() {
        return BlockPos.class;
    }

    @Override
    public String valueToString(BlockPos value) {
        return "UnlistedBlockStateProperty{" +
                "name='" + name + '\'' +
                "pos='" + value + "\'" +
                '}';
    }
}
