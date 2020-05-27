package fr.kinj14.orerun.library;

import java.lang.reflect.Field;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_12_R1.PlayerConnection;

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
    
    public Object getField(Object object, String name) throws Exception {
        Field field = object.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(object);
     }
    
    public void setField(Object packet, String field, Object value) throws Exception {
        Field f = packet.getClass().getDeclaredField(field);
        f.setAccessible(true);
        f.set(packet, value);
     }
    
    public void sendTabHF(Player player, String header, String footer) {
    	PacketPlayOutPlayerListHeaderFooter headerPacket = new PacketPlayOutPlayerListHeaderFooter();
        try {
          if (!header.equals("")) {
          	headerPacket.getClass().getDeclaredField("a").setAccessible(true);
          	headerPacket.getClass().getDeclaredField("a").set(headerPacket, header);
          	headerPacket.getClass().getDeclaredField("b").setAccessible(true);
          	headerPacket.getClass().getDeclaredField("b").set(headerPacket, footer);
          	
          }
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
          connection.sendPacket(headerPacket);
        }
      }
    
    public int getPing(Player p) {
    	return (((CraftPlayer)p).getHandle()).ping;
    }
}