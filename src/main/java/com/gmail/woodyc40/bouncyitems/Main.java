package com.gmail.woodyc40.bouncyitems;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IInventory;
import net.minecraft.server.v1_8_R3.PacketPlayOutSetSlot;
import net.minecraft.server.v1_8_R3.PlayerInventory;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private static final ItemStack ITEM_TO_GIVE = new ItemStack(Material.DIAMOND_SWORD);
    
    @Override
    public void onEnable() {
        this.getCommand("givemeitem").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && "givemeitem".equalsIgnoreCase(command.getName())) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("You are not a player!");
                return true;
            }

            Player player = (Player) sender;
            
            boolean showBounce = Boolean.parseBoolean(args[0]);
            giveItem(player, showBounce, ITEM_TO_GIVE);

            sender.sendMessage("Did you get the animation?");

            return true;
        }

        return false;
    }

    private static void giveItem(Player bukkitPlayer, boolean showBounce, ItemStack... bukkitItems) {
        CraftPlayer craftPlayer = (CraftPlayer) bukkitPlayer;
        EntityPlayer player = craftPlayer.getHandle();
        PlayerInventory inventory = player.inventory;

        // Initialize the wrapper inventory to override the
        // method that removes the bouncing effect
        InvWrapper inv = new InvWrapper(inventory, showBounce);
        inv.addItem(bukkitItems);
    }

    // Proxy class used to more clearly demonstrate what
    // is going on behind-the-scenes of CraftInventoryPlayer
    // The setItem(...) method is overridden with the source
    // copied and slightly modified in order to show how
    // PacketPlayOutSetSlot removes the bouncing effect
    private static class InvWrapper extends CraftInventory {
        private boolean shouldBounce;

        public InvWrapper(IInventory inventory, boolean shouldBounce) {
            super(inventory);
            this.shouldBounce = shouldBounce;
        }

        @Override
        public void setItem(int index, ItemStack item) {
            // CraftInventoryPlayer#setItem(...)
            super.setItem(index, item);

            // If the item should have the bouncing
            // animation,
            if (shouldBounce) {
                return;
            }

            if (this.getHolder() != null) {
                EntityPlayer player = ((CraftPlayer)this.getHolder()).getHandle();
                if (player.playerConnection != null) {
                    // Convert the index from bukkit format
                    // into protocol format
                    if (index < net.minecraft.server.v1_8_R3.PlayerInventory.getHotbarSize()) {
                        index += 36;
                    } else if (index > 35) {
                        index = 8 - (index - 36);
                    }

                    // Overrides the bouncing effect
                    player.playerConnection.sendPacket(new PacketPlayOutSetSlot(
                            player.defaultContainer.windowId,
                            index,
                            CraftItemStack.asNMSCopy(item)));
                }
            }
            // end
        }
    }
}