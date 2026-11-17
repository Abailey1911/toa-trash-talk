package com.kephri;

import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

@PluginDescriptor(
	name = "ToA Trash Talk",
	description = "Kephri's dung knockback reads \"Kephri throws it back!\", and every ToA boss trash-talks you",
	tags = {"toa", "tombs", "amascut", "kephri", "akkha", "baba", "zebak", "wardens", "raids", "fun"}
)
public class KephriPlugin extends Plugin
{
	static final String DUNG_MESSAGE = "Kephri throws you back!";

	// NPC ids from the wiki: 11720 is Kephri recovering between shields, 11722 is her dead.
	private static final int KEPHRI_RECOVERING = 11720;
	private static final int KEPHRI_DEAD = 11722;

	// Overhead text lasts this many client cycles (about 20 ms each), so 150 is three seconds.
	private static final int OVERHEAD_CYCLES = 150;

	// Don't greet again within a minute, so the Wardens don't both say hello, or say it again between phases.
	private static final int GREET_COOLDOWN_TICKS = 100;

	static final List<String> DUNG_LINES = List.of(
		"Watch me throw it back!",
		"Return to sender!",
		"Special delivery!",
		"No refunds on the dung.",
		"Who ordered the brown special?",
		"You've been fertilised!",
		"I'm on a roll!",
		"That's a dung deal.",
		"Fresh from the beetle!",
		"Sit. Stay. STUN.",
		"Bug off!",
		"Hope you brought a mop.",
		"Catch! Oh, you didn't.",
		"Ever heard of a dung-eon?"
	);

	static final List<String> RECOVERING_LINES = List.of(
		"Give me a sec, rerolling.",
		"Hold on, I've got bugs.",
		"Shield's down, vibes are up."
	);

	@Inject
	private Client client;

	@Inject
	private KephriConfig config;

	private final Map<NPC, Boss> bosses = new HashMap<>();
	private final Set<NPC> saidLow = new HashSet<>();
	private final Set<NPC> saidDeath = new HashSet<>();
	private int lastGreetTick = -GREET_COOLDOWN_TICKS;
	private int ticksUntilIdle;
	private String lastLine;

	@Override
	protected void startUp()
	{
		clear();
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			for (NPC npc : client.getTopLevelWorldView().npcs())
			{
				Boss boss = Boss.of(npc.getName());
				if (boss != null)
				{
					bosses.put(npc, boss);
				}
			}
		}
		resetIdleTimer();
	}

	@Override
	protected void shutDown()
	{
		clear();
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.GAMEMESSAGE && event.getType() != ChatMessageType.SPAM)
		{
			return;
		}
		if (!isDungMessage(event.getMessage()))
		{
			return;
		}

		if (config.replaceMessage())
		{
			String raw = event.getMessage();
			String replaced = raw.contains(DUNG_MESSAGE)
				? raw.replace(DUNG_MESSAGE, config.replacement())
				: config.replacement();
			event.getMessageNode().setValue(replaced);
			client.refreshChat();
		}

		if (config.quipOnDung())
		{
			say(find(Boss.KEPHRI), "Kephri", pick(DUNG_LINES));
			resetIdleTimer();
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (bosses.isEmpty() || !config.bossTalk())
		{
			return;
		}

		for (Map.Entry<NPC, Boss> e : bosses.entrySet())
		{
			NPC npc = e.getKey();
			Boss boss = e.getValue();
			int ratio = npc.getHealthRatio();
			int scale = npc.getHealthScale();
			if (ratio < 0 || scale <= 0)
			{
				continue;
			}
			if (ratio == 0 && saidDeath.add(npc))
			{
				say(npc, boss, pick(boss.death));
				resetIdleTimer();
				return;
			}
			if (ratio > 0 && ratio * 4 <= scale && saidLow.add(npc))
			{
				say(npc, boss, pick(boss.lowHealth));
				resetIdleTimer();
				return;
			}
		}

		if (config.idleQuips() > 0 && --ticksUntilIdle <= 0)
		{
			List<NPC> alive = new ArrayList<>();
			for (NPC npc : bosses.keySet())
			{
				if (!saidDeath.contains(npc) && npc.getId() != KEPHRI_DEAD)
				{
					alive.add(npc);
				}
			}
			if (!alive.isEmpty())
			{
				NPC npc = alive.get(ThreadLocalRandom.current().nextInt(alive.size()));
				Boss boss = bosses.get(npc);
				say(npc, boss, pick(boss.idle));
			}
			resetIdleTimer();
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		track(event.getNpc());
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		NPC npc = event.getNpc();
		bosses.remove(npc);
		saidLow.remove(npc);
		saidDeath.remove(npc);
	}

	@Subscribe
	public void onNpcChanged(NpcChanged event)
	{
		NPC npc = event.getNpc();
		Boss boss = Boss.of(npc.getName());
		if (boss == null)
		{
			bosses.remove(npc);
			return;
		}
		if (!bosses.containsKey(npc))
		{
			track(npc);
			return;
		}
		if (!config.bossTalk() || boss != Boss.KEPHRI)
		{
			return;
		}
		if (npc.getId() == KEPHRI_DEAD && saidDeath.add(npc))
		{
			say(npc, boss, pick(boss.death));
		}
		else if (npc.getId() == KEPHRI_RECOVERING)
		{
			say(npc, boss, pick(RECOVERING_LINES));
			resetIdleTimer();
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGIN_SCREEN || event.getGameState() == GameState.HOPPING)
		{
			clear();
		}
	}

	static boolean isDungMessage(String message)
	{
		return message != null && Text.removeTags(message).contains(DUNG_MESSAGE);
	}

	private void track(NPC npc)
	{
		Boss boss = Boss.of(npc.getName());
		if (boss == null)
		{
			return;
		}
		bosses.put(npc, boss);
		resetIdleTimer();
		int now = client.getTickCount();
		if (config.bossTalk() && now - lastGreetTick >= GREET_COOLDOWN_TICKS)
		{
			lastGreetTick = now;
			say(npc, boss, pick(boss.greetings));
		}
	}

	private NPC find(Boss boss)
	{
		for (Map.Entry<NPC, Boss> e : bosses.entrySet())
		{
			if (e.getValue() == boss)
			{
				return e.getKey();
			}
		}
		return null;
	}

	private void say(NPC npc, Boss boss, String line)
	{
		say(npc, boss.npcName, line);
	}

	private void say(NPC npc, String name, String line)
	{
		if (npc != null)
		{
			npc.setOverheadText(line);
			npc.setOverheadCycle(OVERHEAD_CYCLES);
		}
		if (config.echoInChat())
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", name + ": <col=0000ff>" + line + "</col>", null);
		}
	}

	private String pick(List<String> lines)
	{
		String line;
		do
		{
			line = lines.get(ThreadLocalRandom.current().nextInt(lines.size()));
		}
		while (lines.size() > 1 && line.equals(lastLine));
		lastLine = line;
		return line;
	}

	private void resetIdleTimer()
	{
		// Game ticks are 0.6 s. Add a little jitter so it doesn't feel like a metronome.
		int base = (int) Math.round(config.idleQuips() / 0.6);
		ticksUntilIdle = base + ThreadLocalRandom.current().nextInt(Math.max(1, base / 3 + 1));
	}

	private void clear()
	{
		bosses.clear();
		saidLow.clear();
		saidDeath.clear();
		lastGreetTick = -GREET_COOLDOWN_TICKS;
	}

	@Provides
	KephriConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(KephriConfig.class);
	}
}
