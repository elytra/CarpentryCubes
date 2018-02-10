package com.elytradev.carpentrycubes.common.block.prop;

import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.text.MessageFormat;
import java.util.Objects;

public class UnlistedBlockStateProperty implements IUnlistedProperty<IBlockState> {

    private final String name;

    private UnlistedBlockStateProperty(String name) {
        this.name = name;
    }

    public static UnlistedBlockStateProperty create(String name) {
        return new UnlistedBlockStateProperty(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(IBlockState value) {
        return !Objects.isNull(value);
    }

    @Override
    public Class<IBlockState> getType() {
        return IBlockState.class;
    }

    @Override
    public String valueToString(IBlockState value) {
        return new MessageFormat("UnlistedBlockStateProperty { Block: {0}, Meta: {1} }")
                .format(new Object[]{value, value.getBlock().getMetaFromState(value)});
    }
}
