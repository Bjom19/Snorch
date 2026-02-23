package dk.bjom.snorch;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;

public class SnorchRecipes {
    private final Snorch plugin;

    public SnorchRecipes(Snorch plugin) {
        this.plugin = plugin;
        registerRecipes();
    }

    private void registerRecipes() {
        // Build the Snorch item (soul torch with custom name + lore)
        ItemStack snorch = new ItemStack(Material.SOUL_TORCH);
        ItemMeta meta = snorch.getItemMeta();
        if (meta != null) {
            meta.customName(Component.text("Snorch").color(NamedTextColor.GOLD));
            meta.lore(List.of(Component.text("Infernal snow melting in a greater radius.").color(NamedTextColor.GRAY)));
            snorch.setItemMeta(meta);
        }

        // Register the shaped recipe:
        //   [ ] [M] [ ]
        //   [M] [T] [M]
        //   [ ] [M] [ ]
        NamespacedKey key = new NamespacedKey(plugin, "snorch_recipe");
        ShapedRecipe recipe = new ShapedRecipe(key, snorch);
        recipe.shape(" M ", "MTM", " M ");
        recipe.setIngredient('M', Material.MAGMA_BLOCK);
        recipe.setIngredient('T', Material.TORCH);
        Bukkit.addRecipe(recipe);

        // Build the Snorch Lantern item (soul lantern with custom name + lore)
        ItemStack snorchLantern = new ItemStack(Material.SOUL_LANTERN);
        ItemMeta lanternMeta = snorchLantern.getItemMeta();
        if (lanternMeta != null) {
            lanternMeta.customName(Component.text("Snorch Lantern").color(NamedTextColor.GOLD));
            lanternMeta.lore(List.of(Component.text("Infernal snow melting in a greater radius.").color(NamedTextColor.GRAY)));
            snorchLantern.setItemMeta(lanternMeta);
        }

        // Register the Snorch Lantern shaped recipe:
        //   [ ] [M] [ ]
        //   [M] [L] [M]
        //   [ ] [M] [ ]
        NamespacedKey lanternKey = new NamespacedKey(plugin, "snorch_lantern_recipe");
        ShapedRecipe lanternRecipe = new ShapedRecipe(lanternKey, snorchLantern);
        lanternRecipe.shape(" M ", "MLM", " M ");
        lanternRecipe.setIngredient('M', Material.MAGMA_BLOCK);
        lanternRecipe.setIngredient('L', Material.LANTERN);
        Bukkit.addRecipe(lanternRecipe);

        // Build the Snorch Campfire item (soul campfire with custom name + lore)
        ItemStack snorchCampfire = new ItemStack(Material.SOUL_CAMPFIRE);
        ItemMeta campfireMeta = snorchCampfire.getItemMeta();
        if (campfireMeta != null) {
            campfireMeta.customName(Component.text("Snorch Campfire").color(NamedTextColor.GOLD));
            campfireMeta.lore(List.of(Component.text("Infernal snow melting in a greater radius.").color(NamedTextColor.GRAY)));
            snorchCampfire.setItemMeta(campfireMeta);
        }

        // Register the Snorch Campfire shaped recipe:
        //   [ ] [M] [ ]
        //   [M] [C] [M]
        //   [ ] [M] [ ]
        NamespacedKey campfireKey = new NamespacedKey(plugin, "snorch_campfire_recipe");
        ShapedRecipe campfireRecipe = new ShapedRecipe(campfireKey, snorchCampfire);
        campfireRecipe.shape(" M ", "MCM", " M ");
        campfireRecipe.setIngredient('M', Material.MAGMA_BLOCK);
        campfireRecipe.setIngredient('C', Material.CAMPFIRE);
        Bukkit.addRecipe(campfireRecipe);
    }
}
