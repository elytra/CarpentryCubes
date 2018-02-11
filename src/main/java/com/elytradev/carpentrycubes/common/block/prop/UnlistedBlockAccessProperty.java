package com.elytradev.carpentrycubes.common.block.prop;

import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedBlockAccessProperty implements IUnlistedProperty<IBlockAccess> {

    private final String name;

    private UnlistedBlockAccessProperty(String name) {
        this.name = name;
    }

    public static UnlistedBlockAccessProperty create(String name) {
        return new UnlistedBlockAccessProperty(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(IBlockAccess value) {
        return value != null;
    }

    @Override
    public Class<IBlockAccess> getType() {
        return IBlockAccess.class;
    }

    @Override
    public String valueToString(IBlockAccess value) {
        return "UnlistedBlockStateProperty{" +
                "name='" + name + '\'' +
                "access='" + value + "\'" +
                '}';
    }
}
