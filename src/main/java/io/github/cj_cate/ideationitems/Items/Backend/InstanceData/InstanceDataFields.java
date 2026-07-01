package io.github.cj_cate.ideationitems.Items.Backend.InstanceData;

import io.github.cj_cate.ideationitems.Items.Backend.InstanceData.PdcTypes.ColorPdcType;
import org.bukkit.Color;
import org.bukkit.inventory.meta.LeatherArmorMeta;

// Fields for instance data
public class InstanceDataFields {

    public static final InstanceDataField<Color> LEATHER_COLOR = new InstanceDataField<>(
            "leather_color",
            Color.RED,
            meta -> ((LeatherArmorMeta) meta).getColor(),
            (meta, color) -> ((LeatherArmorMeta) meta).setColor(color),
            new ColorPdcType()
    );
}
