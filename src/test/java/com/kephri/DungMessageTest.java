package com.kephri;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.util.List;
import org.junit.Test;

public class DungMessageTest
{
	@Test
	public void matchesTheKnockbackMessage()
	{
		assertTrue(KephriPlugin.isDungMessage("Kephri throws you back!"));
		assertTrue(KephriPlugin.isDungMessage("<col=ef1020>Kephri throws you back!</col>"));
	}

	@Test
	public void ignoresOtherMessages()
	{
		assertFalse(KephriPlugin.isDungMessage("Kephri throws it back!"));
		assertFalse(KephriPlugin.isDungMessage("Your Kephri kill count is: 50."));
		assertFalse(KephriPlugin.isDungMessage(null));
	}

	@Test
	public void findsEveryBossByName()
	{
		assertEquals(Boss.AKKHA, Boss.of("Akkha"));
		assertEquals(Boss.BABA, Boss.of("Ba-Ba"));
		assertEquals(Boss.KEPHRI, Boss.of("Kephri"));
		assertEquals(Boss.ZEBAK, Boss.of("Zebak"));
		assertEquals(Boss.TUMEKEN, Boss.of("Tumeken's Warden"));
		assertEquals(Boss.ELIDINIS, Boss.of("Elidinis' Warden"));
		assertNull(Boss.of("Akkha's Shadow"));
		assertNull(Boss.of(null));
	}

	@Test
	public void everyBossHasLinesThatFitOverhead()
	{
		check(KephriPlugin.DUNG_LINES);
		check(KephriPlugin.RECOVERING_LINES);
		for (Boss b : Boss.values())
		{
			check(b.greetings);
			check(b.idle);
			check(b.lowHealth);
			check(b.death);
		}
	}

	private static void check(List<String> lines)
	{
		assertFalse(lines.isEmpty());
		for (String line : lines)
		{
			assertTrue(line, line.length() <= 80);
		}
	}
}
