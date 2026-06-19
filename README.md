# Mace Roulette Companion

Minecraft mod that adds a few quality of life features to Mace Roulette.

## Features

- **AutoGG**  
  Automatically sends a message at the end of a game. Including "gg" or "good game" will give you five extra stars each game.
- **AutoGL**  
  Automatically sends a message at the start of a game. Including "gl", "hf", "good luck", or "have fun" will give you five extra stars each game.
- ~~**AutoBet**  
  Automatically bets on a player during Mace Showdown. This player can be decided based on their amount of tracked wins, the user's preference, or just picked randomly.~~
- **Round Info HUD**  
  Places information about the current round (such as how long the game has been going for, the amount of players alive, and the active modifiers) on the HUD.
- **Toasts**  
  Displays notifications such as bounties, events, and players joining as toasts.
- ~~**Server List Info**  
  Displays status updates such as leaderboard/market resets, ongoing events, and announcements next to MR server listings~~

## Roadmap

- [ ] Basic Functionality
  - [x] Track round information
    - [x] Round Number
    - [x] Players alive
    - [x] Modifiers
    - [x] Modifier Boosters
  - [x] Configuration
    - [x] Load config from file
    - [x] Configuration UI through YACL
    - [x] Save config to file
  - [x] AutoGG
    - [x] Send message to chat automatically
    - [x] Pick message randomly
    - [ ] ...with weights (optional)
    - [x] Include delay
    - [x] Pull from config
    - [x] Hide GG Messages
  - [x] AutoGL
    - [x] Send message to chat automatically
    - [x] Pick message randomly
    - [ ] ...with weights (optional)
    - [x] Include delay
    - [x] Pull from config
    - [x] Hide GL Messages
  - [ ] AutoBet
    - [ ] Vote for red
    - [ ] ...for blue
    - [ ] ...randomly
    - [ ] Include delay
    - [ ] Vote based on user preference
    - [ ] ...based on existing votes
    - [ ] ...based on tracked wins
- [ ] HUD Elements
  - [x] Round Info HUD
    - [x] Display information, with round number displaying larger than others.
    - [x] Display modifier icons
    - [x] Display modifier boosters as player heads
    - [x] Configure HUD's...
      - [x] ...position
      - [x] ...X/Y padding
      - [x] ...scale (optional)
      - [x] ...order
  - [x] Display toasts...
    - [x] ...for events
    - [x] ...for bounties
    - [x] ...for modifier chargers
    - [x] ...for players joining
    - [ ] ...for purchases/earned cosmetics
    - [ ] ...for daily resets
- [ ] Out-of-game Stuff
  - [ ] Discord Bot
    - [ ] Have bot pull events, markets, and leaderboards from channel (currently impossible without self-botting >_<')
  - [ ] Status
    - [ ] Provide status updates and patch notes
  - [ ] Server List Info
    - [ ] Display hoverable info for events
    - [ ] ...market resets
    - [ ] ...leaderboard resets
    - [ ] ...updates
