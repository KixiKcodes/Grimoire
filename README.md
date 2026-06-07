
# Grimoire

## About

Around Halloween of 2025 I came accross this neat little game called Blood on the Clocktower through some peers. I watched a few videos about it and quickly fell in love. Blood on the Clocktower is a social deduction game similar to Werewolf or Mafia, mainly meant to be played in a public social setting.

The game is based off of "scripts", which are selections of characters and rules desgined by the game creators or the community. Scripts are designed such that the roles work well in conjunction with eachother. Where I saw a gap to fill was in the game setup stage. BotC is played with a game master (referred to as the "Storyteller") who is in charge of assigning the roles and handling all information given to each player every round. As you can imagine, this person has a lot of responsibility and I can confirm, having been the Storyteller a few times.

So you know what they say, necessity is the mother of invention.

I started this project out in C# as a CLI application and later ported it to Kotlin as an Android application. I made use of the official assets taken from the BotC wiki and used Jetpack Compose to build the UI.

In its current state, the App allows you to generate a full list of assigned roles to players of your choice based on a selected script. The scripts are built into the app but are just JSONs that I parse, so the plan is to allow the user to import their own scripts in the future.

## Features

- Script manager that dynamically parses scripts.
- Script preview menu that allows the user to preview all the scripts parsed.
- Game setup screen with a script selection dropdown and player adder.
- Algorithm that automatically assigns roles and subroles to the added players based on the game rules and dynamics.
- Preview screen showing all player's assigned roles and other relevant info for the Storyteller.
- System for computing active role Jinxes (special rules to resolve pairs of conflicting roles).

## Future Plans

I actually have pretty long term plans for this project, I can't promise anything withing reasonable time but some of my ideas are:

- Add final result screen with all players arranged in their positions with their roles/subroles visible.
- Add Loric and Fabled/Jinxes display to said final result screen.
- Add a full game loop that allows the storyteller to advance through the game's rounds with all info being kept track of.
- Port to PC via Kotlin Multiplatform.
- Automate games entirely (auto-storyteller).
