package com.feed_the_beast.ftbutilities.command.ranks;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.command.CommandUtils;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.ranks.Rank;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
public class CmdSet extends CmdBase
{
	public CmdSet()
	{
		super("set", Level.OP);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return index == 0;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if (args.length == 2)
		{
			return Ranks.isActive() ? getListOfStringsMatchingLastWord(args, Ranks.INSTANCE.getRankNames(true)) : Collections.emptyList();
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (!Ranks.isActive())
		{
			throw FTBLib.error(sender, "feature_disabled_server");
		}

		checkArgs(sender, args, 2);

		if (!Ranks.INSTANCE.getRankNames(true).contains(args[1]))
		{
			throw FTBUtilities.error(sender, "commands.ranks.not_found", args[1]);
		}

		ForgePlayer p = CommandUtils.getForgePlayer(sender, args[0]);
		Rank rank = Ranks.INSTANCE.getRank(args[1]);

		if (Ranks.INSTANCE.setRank(p.getId(), rank))
		{
			sender.sendMessage(FTBUtilities.lang(sender, "commands.ranks.set.set", StringUtils.color(p.getDisplayName(), TextFormatting.BLUE), rank.getDisplayName()));
		}
		else
		{
			sender.sendMessage(FTBLib.lang(sender, "nothing_changed"));
		}
	}
}
