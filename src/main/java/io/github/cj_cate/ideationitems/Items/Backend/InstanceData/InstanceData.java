package io.github.cj_cate.ideationitems.Items.Backend.InstanceData;

import io.github.cj_cate.ideationitems.Main;
import io.github.cj_cate.ideationitems.Utils.TagUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class InstanceData {
    private static final NamespacedKey ROOT_KEY = new NamespacedKey(Main.getMain(), TagUtil.Tag.INSTANCE_DATA.getTag());

    private final InstanceDataField<?>[] fields;

    public InstanceData(InstanceDataField<?>... fields) {
        this.fields = fields;
    }

    public InstanceDataField<?>[] fields() {
        return fields;
    }

    // Writes every field's default value onto a freshly created item's meta. Called once, at Blueprint construction.
    public void applyDefaults(ItemMeta meta) {
        for (InstanceDataField<?> field : fields) {
            applyDefault(field, meta);
        }
    }

    private <T> void applyDefault(InstanceDataField<T> field, ItemMeta meta) {
        field.applyTo(meta, field.defaultValue());
        writeToPdc(meta, field, field.defaultValue());
    }

    // Reads each field's value off the old item's PDC and reapplies it onto the fresh meta, before the caller
    // overwrites the rest. If the old item predates this field ever being written to PDC (e.g. it predates
    // InstanceData tracking it at all), falls back to the field's reader to capture whatever value is already
    // live on the old meta, so wrapping an existing mutable property in InstanceData doesn't reset it to default.
    public void carryOver(ItemMeta oldMeta, ItemMeta newMeta) {
        PersistentDataContainer oldRoot = readRoot(oldMeta);

        for (InstanceDataField<?> field : fields) {
            carryOneField(field, oldRoot, oldMeta, newMeta);
        }
    }

    private <T> void carryOneField(InstanceDataField<T> field, PersistentDataContainer oldRoot, ItemMeta oldMeta, ItemMeta newMeta) {
        NamespacedKey key = new NamespacedKey(Main.getMain(), field.key());
        T value = (oldRoot != null && oldRoot.has(key, field.pdcType()))
                ? oldRoot.get(key, field.pdcType())
                : field.readFrom(oldMeta);

        field.applyTo(newMeta, value);
        writeToPdc(newMeta, field, value);
    }

    // Updates the live meta and its PDC mirror together so they can never drift.
    public <T> void set(ItemStack item, InstanceDataField<T> field, T value) {
        ItemMeta meta = item.getItemMeta();
        field.applyTo(meta, value);
        writeToPdc(meta, field, value);
        item.setItemMeta(meta);
    }

    private <T> void writeToPdc(ItemMeta meta, InstanceDataField<T> field, T value) {
        PersistentDataContainer root = meta.getPersistentDataContainer();
        PersistentDataContainer instanceRoot = root.has(ROOT_KEY, PersistentDataType.TAG_CONTAINER)
                ? root.get(ROOT_KEY, PersistentDataType.TAG_CONTAINER)
                : root.getAdapterContext().newPersistentDataContainer();

        instanceRoot.set(new NamespacedKey(Main.getMain(), field.key()), field.pdcType(), value);
        root.set(ROOT_KEY, PersistentDataType.TAG_CONTAINER, instanceRoot);
    }

    private PersistentDataContainer readRoot(ItemMeta meta) {
        PersistentDataContainer root = meta.getPersistentDataContainer();
        if (!root.has(ROOT_KEY, PersistentDataType.TAG_CONTAINER)) return null;
        return root.get(ROOT_KEY, PersistentDataType.TAG_CONTAINER);
    }
}
