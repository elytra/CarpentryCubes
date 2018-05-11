package com.elytradev.carpentrycubes.common.block.prop;

import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedEnumProperty<T extends Enum<T>> implements IUnlistedProperty<T> {

    private final String name;
    private final Class<T> valueClass;

    private UnlistedEnumProperty(String name, Class<T> valueClass) {
        this.name = name;
        this.valueClass = valueClass;
    }

    public static UnlistedEnumProperty create(String name, Class<? extends Enum> e) {
        return new UnlistedEnumProperty(name, e);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<T> getType() {
        return valueClass;
    }

    @Override
    public boolean isValid(Enum value) {
        return value != null;
    }

    @Override
    public String valueToString(Enum value) {
        return "UnlistedBlockStateProperty{" +
            "name='" + name + '\'' +
            "enum='" + value + "\'" +
            '}';
    }
}