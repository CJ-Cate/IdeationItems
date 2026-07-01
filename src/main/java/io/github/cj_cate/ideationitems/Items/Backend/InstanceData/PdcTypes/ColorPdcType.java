package io.github.cj_cate.ideationitems.Items.Backend.InstanceData.PdcTypes;

import org.bukkit.Color;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

// Stores a Color as its RGB int, since PersistentDataType has no built-in support for Color.
public class ColorPdcType implements PersistentDataType<Integer, Color> {
    @Override
    public @NotNull Class<Integer> getPrimitiveType() {
        return Integer.class;
    }

    @Override
    public @NotNull Class<Color> getComplexType() {
        return Color.class;
    }

    @Override
    public @NotNull Integer toPrimitive(@NotNull Color complex, @NotNull PersistentDataAdapterContext context) {
        return complex.asRGB();
    }

    @Override
    public @NotNull Color fromPrimitive(@NotNull Integer primitive, @NotNull PersistentDataAdapterContext context) {
        return Color.fromRGB(primitive);
    }
}
