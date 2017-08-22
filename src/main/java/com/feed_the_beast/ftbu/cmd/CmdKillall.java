package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbl.lib.util.CommonUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class CmdKillall extends CmdBase
{
	private static final Predicate<Entity> ITEM = entity -> entity instanceof EntityItem;
	private static final Predicate<Entity> XP = entity -> entity instanceof EntityXPOrb;
	private static final Predicate<Entity> MOB = entity -> entity instanceof IMob;
	private static final Predicate<Entity> ANIMAL = entity -> entity instanceof EntityAnimal;
	private static final Predicate<Entity> LIVING = entity -> entity instanceof EntityLivingBase;
	private static final Predicate<Entity> PLAYER = entity -> entity instanceof EntityPlayer;
	private static final Predicate<Entity> NON_LIVING = entity -> !(entity instanceof EntityLivingBase);
	private static final Predicate<Entity> NON_PLAYER = entity -> !(entity instanceof EntityPlayer);
	private static final List<String> TAB = Arrays.asList("items", "xp", "monsters", "animals", "living", "players", "non_living", "non_players", "all");

	public CmdKillall()
	{
		super("killall", Level.OP);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, TAB);
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		Predicate<Entity> predicate = CommonUtils.alwaysTruePredicate();

		if (args.length >= 1)
		{
			switch (args[0])
			{
				case "item":
				case "items":
					predicate = ITEM;
					break;
				case "xp":
				case "experience":
					predicate = XP;
					break;
				case "mob":
				case "mobs":
				case "monster":
				case "monsters":
					predicate = MOB;
					break;
				case "animal":
				case "animals":
					predicate = ANIMAL;
					break;
				case "living":
				case "alive":
					predicate = LIVING;
					break;
				case "player":
				case "players":
					predicate = PLAYER;
					break;
				case "non_living":
					predicate = NON_LIVING;
					break;
				case "non_players":
					predicate = NON_PLAYER;
					break;
			}
		}

		int killed = 0;

		for (World world : server.worlds)
		{
			for (Entity entity : world.getLoadedEntityList())
			{
				if (predicate.test(entity))
				{
					entity.setDead();
					killed++;
				}
			}
		}

		sender.sendMessage(new TextComponentString("Killed " + killed + " entities")); //LANG
	}
}