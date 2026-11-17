package com.kephri;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup(KephriConfig.GROUP)
public interface KephriConfig extends Config
{
	String GROUP = "kephrithrowsitback";

	@ConfigItem(
		keyName = "replaceMessage",
		name = "Replace dung message",
		description = "Change \"Kephri throws you back!\" in chat",
		position = 1
	)
	default boolean replaceMessage()
	{
		return true;
	}

	@ConfigItem(
		keyName = "replacement",
		name = "Replacement text",
		description = "What the chat says instead when Kephri dungs you",
		position = 2
	)
	default String replacement()
	{
		return "Kephri throws it back!";
	}

	@ConfigItem(
		keyName = "quipOnDung",
		name = "Kephri gloats on dung",
		description = "Kephri says something over her head when she dungs you",
		position = 3
	)
	default boolean quipOnDung()
	{
		return true;
	}

	@ConfigItem(
		keyName = "bossTalk",
		name = "ToA bosses trash talk",
		description = "Akkha, Ba-Ba, Kephri, Zebak and the Wardens talk when you meet them, when they get low and when they die",
		position = 4
	)
	default boolean bossTalk()
	{
		return true;
	}

	@ConfigItem(
		keyName = "idleQuips",
		name = "Random trash talk every",
		description = "How often the boss says something during the fight. 0 turns it off.",
		position = 5
	)
	@Range(max = 300)
	@Units(Units.SECONDS)
	default int idleQuips()
	{
		return 25;
	}

	@ConfigItem(
		keyName = "echoInChat",
		name = "Show boss lines in chat",
		description = "Also put what the bosses say into the chatbox",
		position = 6
	)
	default boolean echoInChat()
	{
		return true;
	}
}
