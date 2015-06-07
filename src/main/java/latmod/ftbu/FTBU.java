package latmod.ftbu;
import java.io.File;

import latmod.ftbu.cmd.*;
import latmod.ftbu.core.*;
import latmod.ftbu.core.net.MessageLM;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;

@Mod(modid = FTBUFinals.MOD_ID, version = FTBUFinals.VERSION, name = FTBUFinals.MOD_NAME, dependencies = FTBUFinals.DEPENDENCIES, guiFactory = FTBUFinals.GUI_FACTORY)
public class FTBU
{
	@Mod.Instance(FTBUFinals.MOD_ID)
	public static FTBU inst;
	
	@SidedProxy(clientSide = "latmod.ftbu.client.FTBUClient", serverSide = "latmod.ftbu.FTBUCommon")
	public static FTBUCommon proxy;
	
	@LMMod.Instance(FTBUFinals.MOD_ID)
	public static LMMod mod;
	
	public FTBU() { LatCoreMC.addEventHandler(FTBUEventHandler.instance, true, true, true); }
	
	private ModMetadata modMeta;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		if(LatCoreMC.isDevEnv)
			LatCoreMC.logger.info("Loading LatCoreMC, Dev Build");
		else
			LatCoreMC.logger.info("Loading LatCoreMC, Build #" + FTBUFinals.VERSION);
		
		modMeta = e.getModMetadata();
		
		LatCoreMC.latmodFolder = new File(e.getModConfigurationDirectory().getParentFile(), "latmod/");
		if(!LatCoreMC.latmodFolder.exists()) LatCoreMC.latmodFolder.mkdirs();
		
		LMMod.init(this, new FTBUConfig(e), null);
		mod.logger = LatCoreMC.logger;
		
		ODItems.preInit();
		
		mod.onPostLoaded();
		proxy.preInit(e);
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent e)
	{
		MessageLM.init();
		proxy.init(e);
		
		FMLInterModComms.sendMessage("Waila", "register", "latmod.core.event.RegisterWailaEvent.registerHandlers");
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		ODItems.postInit();
		mod.loadRecipes();
		FTBUConfig.Recipes.loadRecipes();
		proxy.postInit(e);
		
		boolean addedDesc = false;
		if(modMeta != null) for(int i = 0; i < LMMod.modsMap.size(); i++)
		{
			LMMod m = LMMod.modsMap.values.get(i);
			
			if(!m.modID.equals(mod.modID))
			{
				if(!addedDesc)
				{
					modMeta.description += EnumChatFormatting.GREEN + "\n\nMods using LatCoreMC:";
					addedDesc = true;
				}
				
				modMeta.description += "\n" + m.modID;
			}
		}
		
		for(String s : FTBUGuiHandler.IDs) LatCoreMC.addLMGuiHandler(s, FTBUGuiHandler.instance);
	}
	
	@Mod.EventHandler
	public void registerCommands(FMLServerStartingEvent e)
	{
		e.registerServerCommand(new CmdFTBU());
		e.registerServerCommand(new CmdFTBUAdmin());
		
		proxy.serverStarting(e);
	}
	
	@Mod.EventHandler
	public void shuttingDown(FMLServerStoppingEvent e)
	{
		if(LatCoreMC.hasOnlinePlayers()) for(EntityPlayerMP ep : LatCoreMC.getAllOnlinePlayers().values)
			FTBUEventHandler.instance.playerLoggedOut(new cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent(ep));
	}
	
	/*
	@NetworkCheckHandler
	public boolean checkNetwork(Map<String, String> m, Side side)
	{
		String s = m.get(MOD_ID);
		return s == null || s.equals(VERSION) || VERSION.equals(LatCoreMC.DEV_VERSION);
	}
	*/
}