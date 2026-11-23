# MorningJoin

**MorningJoin** is a lightweight PaperMC plugin that automatically sets the world time to morning when the first player joins an empty server. 

Instead of logging into a dark, monster-filled world after your server has been sitting idle, players can immediately start building and exploring—complete with built-in anti-abuse cooldowns and late-afternoon checks so the system cannot be exploited to skip normal nights.

---

## Features
* **Smart Empty-Server Reset:** Automatically advances time to morning (and optionally clears rain/thunderstorms) only when a player joins an otherwise empty server.
* **Anti-Abuse Cooldown:** Prevents players from disconnecting and rejoining just to skip nightfall. Can be configured per-player (blocks only the last player who logged off) or globally (blocks anyone joining within the cooldown window).
* **Late-Afternoon / Night-Only Check:** Only resets the clock if the world is already at or past a configurable tick threshold (default `10500`, roughly 75 seconds before sunset), so it never rewinds an active daytime session.
* **Multi-World Support:** Explicitly targets your configured Overworld(s), ensuring the time resets even if the first player logs in while inside the Nether or End.
* **Zero Dependencies & MiniMessage Support:** Uses Paper's native API with full MiniMessage formatting for custom join messages.

---

## Commands & Permissions

| Command | Description | Permission | Default |
| :--- | :--- | :--- | :--- |
| `/morningjoin reload` | Reloads `config.yml` without restarting the server. | `morningjoin.admin` | `op` |
| *(Passive)* | Bypasses the relog anti-abuse cooldown. | `morningjoin.bypass` | `false` |

---

## Configuration (`config.yml`)

```yaml
# MorningJoin Configuration

# Minutes the server must remain empty before the same player can trigger morning again.
# Set to 0 to disable the anti-abuse cooldown.
cooldown-minutes: 10

# If true, ANY player joining within the cooldown window is blocked from resetting time.
# If false, only the last player who disconnected is blocked.
global-cooldown: false

# Only set time to morning if the world is at or past night-start-time.
# If false, joining an empty server will always set the time to target-time.
night-only: true

# The world tick when "night/late-day" begins for the night-only check (0 to 23999).
# 12000 is exact sunset; 10500 gives a ~75-second late-afternoon buffer so players
# don't log in right as the sun is about to go down.
night-start-time: 10500

# World time in ticks to set when triggered (0 = sunrise, 1000 = morning, 6000 = noon).
target-time: 1000

# Also clear rain and thunderstorms when setting time to morning.
clear-weather: true

# List of worlds to check and update when the first player joins.
worlds:
  - world

# Message sent to the player when the time is set to morning (supports MiniMessage formatting).
# Leave empty ("") to disable.
message: "<gold>Welcome back! <yellow>The time has been set to morning."
```

### Config Option Breakdown
* **`cooldown-minutes`**: How many minutes must pass after the last player disconnects before joining triggers morning again. Set to `0` to disable.
* **`global-cooldown`**: When `false`, only the specific player who was last online is restricted by the cooldown. When `true`, *any* player joining an empty server during the cooldown window will not trigger a reset.
* **`night-only`**: Prevents the plugin from rewinding the clock if it is already morning or midday.
* **`night-start-time`**: The exact tick (`0`–`23999`) at which `night-only` considers it late enough to reset. Setting this slightly before sunset (`10500`–`11000`) stops players from logging in with only seconds of daylight left.
* **`target-time`**: The time of day in ticks to set when triggered (`0` = sunrise, `1000` = morning, `6000` = noon).
* **`clear-weather`**: Clears active rain and thunderstorms whenever the morning reset triggers.
* **`worlds`**: The list of Overworld names to update upon join.
* **`message`**: The chat message shown to the player when the reset occurs (supports standard `<color>` and MiniMessage tags; leave as `""` to disable).

---

## Requirements
* **Server Software:** Paper 26.3+ (or compatible forks)
* **Java Version:** Java 25+
