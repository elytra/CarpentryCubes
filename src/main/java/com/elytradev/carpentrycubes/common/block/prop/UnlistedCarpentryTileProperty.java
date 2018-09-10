package com.elytradev.carpentrycubes.common.block.prop;

import com.elytradev.carpentrycubes.common.tile.TileCarpentry;
import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedCarpentryTileProperty implements IUnlistedProperty<TileCarpentry> {

    private final String name;

    private UnlistedCarpentryTileProperty(String name) {
        this.name = name;
    }

    public static UnlistedCarpentryTileProperty create(String name) {
        return new UnlistedCarpentryTileProperty(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(TileCarpentry value) {
        return value != null;
    }

    @Override
    public Class<TileCarpentry> getType() {
        return TileCarpentry.class;
    }

    @Override
    public String valueToString(TileCarpentry value) {
        return "UnlistedBlockStateProperty{" +
                "name='" + name + '\'' +
                "tile='" + value + "\'" +
                '}';
    }
}
