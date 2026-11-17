package com.kephri;

import java.util.List;

/** Every Tombs of Amascut boss and what it says. Bosses are matched by NPC name. */
enum Boss
{
	AKKHA("Akkha",
		List.of(
			"Another raider? Fine. I'll remember you.",
			"Welcome to the Path of Het. Mind the furniture."),
		List.of(
			"Memorise this: you're bad.",
			"My shadows have better dps than you.",
			"Pick a quadrant, any quadrant.",
			"Fire, ice, lightning, darkness, and your mistakes.",
			"Orbs incoming. Hope you can walk.",
			"Stay in the light. Or don't, I'm not your mum.",
			"Het sends his regards. Actually he doesn't.",
			"I've got a lot of elements. You've got one brain cell."),
		List.of(
			"Final stand! Like your last raid.",
			"Fine. Now I'm angry."),
		List.of(
			"I... forgot... the memory blast...",
			"Het... I've failed you... again...")),

	BABA("Ba-Ba",
		List.of(
			"Ooh ooh! Fresh raiders!",
			"Welcome to the Path of Apmeken. No refunds."),
		List.of(
			"Look up. LOOK UP.",
			"Mind the boulders, mind your business.",
			"Banana break? No? Cool, rocks then.",
			"Gravity is my love language.",
			"Who's a good baboon? Not you.",
			"Apmeken said be nice. I didn't listen.",
			"I'm the baboon in charge here.",
			"Ooh ooh, wipe wipe!"),
		List.of(
			"Ba-Ba goes bananas!",
			"Now you've done it. Monkey mode."),
		List.of(
			"Ba-Ba... bye-bye...",
			"Tell my boulders... I loved them...")),

	KEPHRI("Kephri",
		List.of(
			"Fresh meat for the swarm!",
			"Welcome to the Path of Scabaras. Wipe your feet."),
		List.of(
			"My scarabs have scarabs.",
			"Amascut pays me in dung.",
			"I'm not slow, I'm rolling.",
			"Is that your best dps?",
			"Bring a bucket next time.",
			"This shield is load-bearing.",
			"I push the sun every morning. You're nothing.",
			"Smells like a wipe in here.",
			"Your purple is in another chest.",
			"Don't make me call the swarm.",
			"Your gear is giving dung."),
		List.of(
			"Last roll. Make it count.",
			"My shell is cracked. My spirit isn't."),
		List.of(
			"I rolled... too close... to the sun...",
			"Tell my scarabs... I loved them...",
			"This is so... crappy...")),

	ZEBAK("Zebak",
		List.of(
			"In a while, crocodile? No. Right now.",
			"Welcome to the Path of Crondis. Bring a towel."),
		List.of(
			"Snap snap, you're done.",
			"The water's lovely, come on in.",
			"Hands off my vases.",
			"See you later, raider.",
			"These aren't crocodile tears. They're yours.",
			"I eat raiders for breakfast. And second breakfast.",
			"Surf's up!",
			"Can you swim? Doesn't matter."),
		List.of(
			"Now I'm properly snappy!",
			"Enraged and well hydrated."),
		List.of(
			"Tell Crondis... sorry about the floor...",
			"I'm going to be... a handbag...")),

	TUMEKEN("Tumeken's Warden",
		List.of(
			"You made it this far? Adorable.",
			"The sun watches you. It's unimpressed."),
		List.of(
			"Feel the heat!",
			"Dodge the skulls, champion.",
			"We've been watching. It wasn't good.",
			"Do you even prayer flick?",
			"Amascut can't save you. Neither can your team.",
			"Two wardens, zero chance.",
			"Is it hot in here or is it just me? It's me.",
			"Behold the purple chest. Not for you."),
		List.of(
			"This isn't even my final form.",
			"Okay, gloves off."),
		List.of(
			"Tell Amascut... I tried...",
			"The sun... sets...")),

	ELIDINIS("Elidinis' Warden",
		List.of(
			"You made it this far? Adorable.",
			"The waters remember everyone. You'll be forgotten."),
		List.of(
			"Stay hydrated.",
			"Dodge the skulls, champion.",
			"We've been watching. It wasn't good.",
			"Do you even prayer flick?",
			"Two wardens, zero chance.",
			"Feeling drained? That's me.",
			"Your loot is going down the river.",
			"Behold the purple chest. Not for you."),
		List.of(
			"This isn't even my final form.",
			"The tide turns. Badly, for you."),
		List.of(
			"Washed... up...",
			"Tell Amascut... I tried..."));

	final String npcName;
	final List<String> greetings;
	final List<String> idle;
	final List<String> lowHealth;
	final List<String> death;

	Boss(String npcName, List<String> greetings, List<String> idle, List<String> lowHealth, List<String> death)
	{
		this.npcName = npcName;
		this.greetings = greetings;
		this.idle = idle;
		this.lowHealth = lowHealth;
		this.death = death;
	}

	static Boss of(String name)
	{
		if (name == null)
		{
			return null;
		}
		for (Boss b : values())
		{
			if (b.npcName.equalsIgnoreCase(name))
			{
				return b;
			}
		}
		return null;
	}
}
