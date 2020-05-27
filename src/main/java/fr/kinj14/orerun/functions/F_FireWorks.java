package fr.kinj14.orerun.functions;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import fr.kinj14.orerun.Main;

public class F_FireWorks {
	protected final Main main = Main.getInstance();
	public void SpawnFireWorks(Location loc) {
		Firework f = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
		f.detonate();
		FireworkMeta fm = f.getFireworkMeta();
		FireworkEffect effect = FireworkEffect.builder()
				.flicker(true)
				.withColor(Color.GREEN)
				.withFade(Color.BLUE)
				.with(Type.STAR)
				.trail(true)
				.build();
		fm.addEffect(effect);
		f.setFireworkMeta(fm);
	}
}
