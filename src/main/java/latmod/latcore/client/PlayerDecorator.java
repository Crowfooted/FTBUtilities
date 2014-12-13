package latmod.latcore.client;

import latmod.core.FastMap;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderPlayerEvent;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public abstract class PlayerDecorator
{
	public static final FastMap<String, PlayerDecorator> map = new FastMap<String, PlayerDecorator>();
	
	static
	{
		map.put("reddust", new PDReddust());
		map.put("townaura", new PDTownaura());
		map.put("smoke", new PDSmoke());
	}
	
	private String name = null; public String toString()
	{ if(name == null) name = map.getKey(this); return name; }
	
	public boolean hasMoved(Entity e)
	{
		if(e.prevPosX != e.posX) return true;
		if(e.prevPosY != e.posY) return true;
		if(e.prevPosZ != e.posZ) return true;
		return false;
	}
	
	public abstract void onPlayerRender(RenderPlayerEvent.Specials.Post e);
}