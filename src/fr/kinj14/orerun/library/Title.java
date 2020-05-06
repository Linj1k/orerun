package fr.kinj14.orerun.library;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle.EnumTitleAction;

///////////////////////////////
//	Author : gravenilvec	//
/////////////////////////////

public class Title {
    public void sendTitle(Player player, String title, String subtitle, int ticks){
        IChatBaseComponent basetitle = ChatSerializer.a("{\"text\": \"" + title + "\"}");
        IChatBaseComponent basesubtitle = ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");
        PacketPlayOutTitle titlepacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, basetitle);
        PacketPlayOutTitle subtitlepacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, basesubtitle);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(titlepacket);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitlepacket);
        sendTime(player, ticks);
    }
   
    private void sendTime(Player player, int ticks){
        PacketPlayOutTitle titlepacket = new PacketPlayOutTitle(EnumTitleAction.TIMES, null, 20, ticks, 20);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(titlepacket);
    }
   
    public void sendActionBar(Player player, String message){
        IChatBaseComponent basetitle = ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat packet = new PacketPlayOutChat(basetitle);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}