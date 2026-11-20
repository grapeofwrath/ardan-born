package dev.grapeofwrath.ardanborn;

import org.bukkit.NamespacedKey;

/// The single namespace used everywhere: plugin name (lowercased), Origins-Reborn addon namespace
/// (ability keys `ardanborn:<id>`), persistent-data keys and recipe keys.
public final class Keys {
    public static final String NAMESPACE = "ardanborn";

    /// On armor pieces: the id of the armor set the piece belongs to.
    public static final NamespacedKey ARMOR_SET = key("armor_set");
    /// On custom items such as set templates: the item id.
    public static final NamespacedKey ITEM = key("item");

    private Keys() {}

    public static NamespacedKey key(String value) {
        return new NamespacedKey(NAMESPACE, value);
    }
}
