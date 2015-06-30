package latmod.ftbu.mod;

import java.io.*;
import java.util.*;

import latmod.ftbu.core.*;
import latmod.ftbu.core.util.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.google.gson.annotations.Expose;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class FTBUConfig extends LMConfig implements IServerConfig
{
	public FTBUConfig(FMLPreInitializationEvent e)
	{
		super(e, "/LatMod/FTBU.cfg");
		load();
		
		try
		{
			File f = LatCore.newFile(new File(LatCoreMC.latmodFolder, "/ftbu/readme.txt"));
			FileOutputStream fos = new FileOutputStream(f);
			InputStream is = FTBUConfig.class.getResourceAsStream("/assets/ftbu/lang/readme.txt");
			
			byte[] b = new byte[is.available()];
			is.read(b);
			fos.write(b);
			
			fos.close();
			is.close();
			
			b = null;
			is = null;
			fos = null;
		}
		catch(Exception ex)
		{ ex.printStackTrace(); }
	}
	
	public void load()
	{
		General.load(get("general"));
		Login.load(get("login"));
		WorldBorder.load();
		save();
	}
	
	public void readConfig(NBTTagCompound tag)
	{
		/*{
			NBTTagCompound tag1 = tag.getCompoundTag("WB");
			WorldBorder.inst.enabled = tag1.getBoolean("E");
			WorldBorder.inst.radius = tag1.getInteger("R");
			WorldBorder.inst.custom.clear();
			int[] cr = tag1.getIntArray("C");
			for(int i = 0; i < cr.length / 2; i++)
				WorldBorder.inst.custom.put(cr[i * 2 + 0], cr[i * 2 + 1]);
		}*/
	}
	
	public void writeConfig(NBTTagCompound tag)
	{
		/*{
			NBTTagCompound tag1 = new NBTTagCompound();
			tag1.setBoolean(p_74757_1_, p_74757_2_);
		}*/
	}
	
	public static class General
	{
		public static boolean allowCreativeInteractSecure;
		public static String commandFTBU;
		public static String commandAdmin;
		public static double restartTimer;
		
		public static void load(Category c)
		{
			allowCreativeInteractSecure = c.getBool("allowCreativeInteractSecure", true);
			c.setComment("allowCreativeInteractSecure", "If set to true, creative players will be able to access protected chests / chunks");
			
			commandFTBU = c.getString("commandFTBU", "ftbu");
			c.setComment("commandFTBU", "Command name for ftbu command");
			
			commandAdmin = c.getString("commandAdmin", "admin");
			c.setComment("commandAdmin", "Command name for admin command");
			
			restartTimer = c.getFloat("restartTimer", 0F, 0F, 720F);
			c.setComment("restartTimer",
					"Server will automatically shut down after X hours",
					"0 - Disabled",
					"0.5 - 30 minutes",
					"1 - 1 Hour",
					"24 - 1 Day",
					"168 - 1 Week",
					"720 - 1 Month");
		}
	}
	
	public static class Login
	{
		public static FastList<String> motd;
		public static String rules;
		
		public static void load(Category c)
		{
			motd = c.getStringArray("motd", new String[] { "Welcome to the server!" });
			c.setComment("motd", "Message of the day. Will appear when player logs in");
			
			rules = c.getString("rules", "");
			c.setComment("rules", "Rules page link, blank - ");
		}

		public static FastList<ItemStack> getStartingItems(UUID id)
		{
			return null;
		}
	}
	
	public static class WorldBorder
	{
		public static WorldBorder inst;
		private static File saveFile;
		
		@Expose public boolean enabled;
		@Expose private int radius;
		@Expose private Map<Integer, Integer> custom;
		
		public static void load()
		{
			saveFile = new File(LatCoreMC.latmodFolder, "ftbu/world_border.txt");
			inst = LatCore.fromJsonFromFile(saveFile, WorldBorder.class);
			if(inst == null) save();
		}
		
		public static void save()
		{
			if(inst == null)
			{
				inst = new WorldBorder();
				inst.custom = new HashMap<Integer, Integer>();
				inst.radius = 10000;
				inst.enabled = false;
			}
			
			if(!LatCore.toJsonFile(saveFile, inst))
				LatCoreMC.logger.warn(saveFile.getName() + " failed to save!");
		}
		
		public void setWorldBorder(int dim, int rad)
		{
			if(dim == 0) radius = rad;
			else custom.put(dim, rad);
		}
		
		public int getWorldBorder(int dim)
		{
			if(!enabled) return 0;
			if(dim == 0) return radius;
			
			if(custom != null && !custom.isEmpty())
			{
				Integer i = custom.get(dim);
				if(i != null) return i.intValue();
			}
			
			return (int)(radius * LatCoreMC.getWorldScale(dim));
		}
	}
}