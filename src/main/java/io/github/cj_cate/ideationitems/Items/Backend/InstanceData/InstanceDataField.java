package io.github.cj_cate.ideationitems.Items.Backend.InstanceData;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.function.BiConsumer;
import java.util.function.Function;

// Declares one piece of per-item-instance state: a key, a default value, how to read/apply it on an ItemMeta,
// and how to (de)serialize it via a PersistentDataType.
public class InstanceDataField<T> {
    private final String key;
    private final T defaultValue;
    private final Function<ItemMeta, T> reader;
    private final BiConsumer<ItemMeta, T> applier;
    private final PersistentDataType<?, T> pdcType;

    public InstanceDataField(String key, T defaultValue, Function<ItemMeta, T> reader,
                              BiConsumer<ItemMeta, T> applier, PersistentDataType<?, T> pdcType) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.reader = reader;
        this.applier = applier;
        this.pdcType = pdcType;
    }

    public String key() { return key; }
    public T defaultValue() { return defaultValue; }
    public T readFrom(ItemMeta meta) { return reader.apply(meta); }
    public void applyTo(ItemMeta meta, T value) { applier.accept(meta, value); }
    public PersistentDataType<?, T> pdcType() { return pdcType; }
}
