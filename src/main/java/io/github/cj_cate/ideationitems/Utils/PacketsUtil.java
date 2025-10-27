package io.github.cj_cate.ideationitems.Utils;

import io.github.cj_cate.ideationitems.Main;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Collections;

public class PacketsUtil
{
    public static abstract class PacketData
    {
        // Anon inner class used to do some awesome shit
        // Feel free to add constructors as needed for different types of packets... maybe there is a way to make it safer?
            // Idea: make the getters check for null or something? im really tired
        private final CraftPlayer craftPlayer;
        private final ServerPlayer npc;
        public PacketData(CraftPlayer craftPlayer, ServerPlayer npc) {
            this.craftPlayer = craftPlayer;
            this.npc = npc;
        }
        public ServerGamePacketListenerImpl getConnection()
        {
            return craftPlayer.getHandle().connection;
        }
        public abstract PacketData sendPackets();
    }

    // Code from github: https://github.com/JustAPotato06/NPC/blob/master/src/main/java/dev/potato/npcexample/models/NPC.java
    // Other proposed solution: https://discord.com/channels/503656531665879063/1231978886573588591/1232678264670257202 (from kody's server)
    // It appears this is a thing that started happening post 1.20.2 and I have no interest in trying to fix it myself.
    private static void setConnection(Object npc, String fieldName, Object connection) {
        try {
            Field field = npc.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(npc, connection);
        } catch (Exception exception) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "There was an error establishing a non-null connection to the NPC!");
            exception.printStackTrace();
        }
    }

    // Public static method which takes in a player and the npc to spawn (matching the constructor)
    public static PacketData spawnNpcPacket(CraftPlayer craftPlayer, ServerPlayer npc)
    {
        // Immediately returning the object with the custom logic inside
        return new PacketData(craftPlayer, npc) {
            Player p = craftPlayer.getPlayer();
            // Specific method to override
            @Override
            public PacketData sendPackets() {

                p.sendMessage("Packet sent! (from inside the special closet)");

                // Thank you github :pray: (same code from that github snippet above)
                npc.getEntityData().set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) 127);
                setConnection(npc, "c", ((CraftPlayer) p).getHandle().connection);

                // Spawn NPC packets
                getConnection().send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, npc));
                getConnection().send(new ClientboundAddEntityPacket(npc));
                getConnection().send(new ClientboundSetEntityDataPacket(npc.getId(), npc.getEntityData().getNonDefaultValues()));

                // Rotate that sonofabitch (who the fuck did this math)
                getConnection().send(new ClientboundRotateHeadPacket(npc, (byte) ((p.getLocation().getYaw()%360)*256/360)));

                // Take that mfer off the tab list. this is what hypixel does so its probably optimal
                Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getMain(), () -> {
                    getConnection().send(new ClientboundPlayerInfoRemovePacket(Collections.singletonList(npc.getUUID())));
                }, 40); // about 40 ticks to remove them from the tab list i guess, smart thing though

                return this;
            }
        };

    }


}
