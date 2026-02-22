# MCSTP - Minecraft State Transfer Protocol

A Fabric mod that adds comprehensive game state telemetry to [MCCTP](https://github.com/lucasoyen/mcctp). It broadcasts rich state snapshots over MCCTP's WebSocket every tick — player info, combat context, threat scanning, input state, screen state, status effects, and more.

## Features

- **Full game state every tick** — position, health, inventory, combat, threats, inputs, screen, status effects
- **Threat scanning** — detects hostile mobs within 16 blocks, nearest distance/angle, count
- **Input mirroring** — reports what keys the player is pressing and mouse deltas
- **Screen awareness** — knows when a GUI is open, cursor position, mouse/shift state
- **Status effect tracking** — boolean flags for 11 common effects
- **GUI actions** — cursor positioning and click simulation for open screens
- **Plugin architecture** — registers with MCCTP's API, shares the same WebSocket connection

## Requirements

- Minecraft 1.21.11
- Fabric Loader >= 0.16.0
- Fabric API
- [MCCTP](https://github.com/lucasoyen/mcctp) >= 1.0.0
- Java 21+

## Install

```bash
# Build MCCTP first (MCSTP depends on its JAR)
cd mcctp
./gradlew build

# Then build MCSTP
cd ../MCSTP
./gradlew build
```

Copy both JARs from `build/libs/` to your `.minecraft/mods/` folder.

## How It Works

MCSTP plugs into MCCTP as a module. On init, it registers a state provider and two action handlers with MCCTP's API. It doesn't run its own server — MCCTP's WebSocket handles everything.

When both mods are installed, clients receive a handshake on connect:

```json
{"type": "handshake", "modules": ["mcctp", "mcstp"], "version": "1.0"}
```

With only MCCTP installed, modules is `["mcctp"]` and no state is broadcast.

## Protocol

### Game State (Server → Client)

MCSTP extends the game state payload that MCCTP broadcasts. All of MCCTP's base fields are present, plus these additional sections:

```json
{
  "type": "game_state",
  "timestamp": 1700000000000,
  "selectedSlot": 0,
  "heldItem": {
    "name": "minecraft:diamond_sword",
    "category": "SWORD",
    "stackCount": 1,
    "maxDurability": 1561,
    "currentDurability": 1400
  },
  "offhandItem": {
    "name": "minecraft:shield",
    "category": "SHIELD",
    "stackCount": 1,
    "maxDurability": 336,
    "currentDurability": 336
  },
  "playerState": {
    "health": 18.0,
    "maxHealth": 20.0,
    "hunger": 19,
    "saturation": 3.0,
    "x": 142.5,
    "y": 72.0,
    "z": -38.3,
    "yaw": 45.0,
    "pitch": -10.0,
    "onGround": true,
    "sprinting": false,
    "sneaking": false,
    "swimming": false,
    "flying": false,
    "inWater": false,
    "onFire": false,
    "experienceLevel": 24,
    "experienceProgress": 0.65,
    "totalExperience": 897
  },
  "combatContext": {
    "isUsingItem": false,
    "isBlocking": false,
    "activeHand": "MAIN_HAND",
    "crosshairTarget": "ENTITY",
    "crosshairEntityType": "minecraft:zombie",
    "crosshairBlockPos": null,
    "crosshairDistance": 3.2,
    "crosshairEntityHealth": 14.0,
    "crosshairEntityMaxHealth": 20.0
  },
  "playerInput": {
    "movementForward": 1.0,
    "movementSideways": 0.0,
    "jump": false,
    "sprint": true,
    "sneak": false,
    "attack": false,
    "useItem": false,
    "drop": false,
    "swapOffhand": false,
    "openInventory": false,
    "yawDelta": 2.35,
    "pitchDelta": -0.5
  },
  "screenState": {
    "screenOpen": false,
    "screenType": null,
    "cursorX": 0.0,
    "cursorY": 0.0,
    "mouseLeft": false,
    "mouseRight": false,
    "shiftHeld": false
  },
  "statusEffects": {
    "speed": false,
    "slowness": false,
    "strength": true,
    "fireResistance": false,
    "poison": false,
    "wither": false,
    "regeneration": false,
    "resistance": false,
    "invisibility": false,
    "waterBreathing": false,
    "absorption": false,
    "activeEffectCount": 1
  },
  "threat": {
    "targetEntityHostile": true,
    "targetDistance": 3.2,
    "nearestHostileDist": 3.2,
    "nearestHostileYaw": 45.0,
    "hostileCount": 3
  }
}
```

### Game State Fields

Fields from MCCTP (`heldItem`, `offhandItem`, `playerState` base fields, `combatContext` base fields) are documented in the [MCCTP README](https://github.com/lucasoyen/mcctp). MCSTP adds the following:

**`playerState` (extended):**

| Field | Type | Description |
|-------|------|-------------|
| `experienceLevel` | int | Current XP level |
| `experienceProgress` | float | Progress to next level (0-1) |
| `totalExperience` | int | Total XP points |

**`combatContext` (extended):**

| Field | Type | Description |
|-------|------|-------------|
| `crosshairDistance` | float | Distance to crosshair target (-1 if miss) |
| `crosshairEntityHealth` | float | Target entity health (-1 if not living) |
| `crosshairEntityMaxHealth` | float | Target entity max health (-1 if not living) |

**`playerInput`:**

| Field | Type | Description |
|-------|------|-------------|
| `movementForward` | float | Forward/backward input (-1 to 1) |
| `movementSideways` | float | Left/right input (-1 to 1) |
| `jump` | bool | Jump key held |
| `sprint` | bool | Sprint key held |
| `sneak` | bool | Sneak key held |
| `attack` | bool | Attack key held |
| `useItem` | bool | Use key held |
| `drop` | bool | Drop key held |
| `swapOffhand` | bool | Swap hands key held |
| `openInventory` | bool | Inventory key held |
| `yawDelta` | float | Yaw change since last tick (degrees) |
| `pitchDelta` | float | Pitch change since last tick (degrees) |

**`screenState`:**

| Field | Type | Description |
|-------|------|-------------|
| `screenOpen` | bool | Any GUI screen is open |
| `screenType` | string? | Screen class name (e.g. `"InventoryScreen"`), or null |
| `cursorX` | float | Cursor X position normalized (0-1) |
| `cursorY` | float | Cursor Y position normalized (0-1) |
| `mouseLeft` | bool | Left mouse button held |
| `mouseRight` | bool | Right mouse button held |
| `shiftHeld` | bool | Shift key held (for shift-clicking) |

**`statusEffects`:**

| Field | Type | Description |
|-------|------|-------------|
| `speed` | bool | Speed effect active |
| `slowness` | bool | Slowness effect active |
| `strength` | bool | Strength effect active |
| `fireResistance` | bool | Fire Resistance effect active |
| `poison` | bool | Poison effect active |
| `wither` | bool | Wither effect active |
| `regeneration` | bool | Regeneration effect active |
| `resistance` | bool | Resistance effect active |
| `invisibility` | bool | Invisibility effect active |
| `waterBreathing` | bool | Water Breathing effect active |
| `absorption` | bool | Absorption effect active |
| `activeEffectCount` | int | Total number of active effects |

**`threat`:**

| Field | Type | Description |
|-------|------|-------------|
| `targetEntityHostile` | bool | Crosshair target is a hostile mob |
| `targetDistance` | float | Distance to crosshair target (-1 if not hostile) |
| `nearestHostileDist` | float | Distance to nearest hostile within 16 blocks (-1 if none) |
| `nearestHostileYaw` | float | Yaw angle to nearest hostile (degrees) |
| `hostileCount` | int | Number of hostile mobs within 16 blocks |

### Actions (Client → Server)

MCSTP adds two actions for GUI interaction, on top of MCCTP's 14 base actions:

| Action | Params | Description |
|--------|--------|-------------|
| `cursor` | `x`: float (0-1), `y`: float (0-1) | Move cursor to normalized screen position. Only works when a screen is open. |
| `click` | `button`: `"left"` `"right"` | Click at current cursor position. Only works when a screen is open. |

#### Action Examples

```json
// Move cursor to center of screen
{"action": "cursor", "params": {"x": 0.5, "y": 0.5}}

// Left click
{"action": "click", "params": {"button": "left"}}

// Right click
{"action": "click", "params": {"button": "right"}}
```

## Configuration

Config file: `.minecraft/config/mcstp.json` (created on first launch)

```json
{
  "tickInterval": 1
}
```

| Field | Default | Description |
|-------|---------|-------------|
| `tickInterval` | 1 | Broadcast game state every N ticks (20 ticks = 1 second) |

This overrides MCCTP's tick interval when MCSTP is installed.

## Architecture

```
Any Client ←── WebSocket (JSON) ──→ MCCTP (Fabric Mod)
                                       │
                                       ├── WebSocketServer (Netty)
                                       ├── ConnectionManager
                                       ├── ActionDispatcher
                                       │     ├── 14 base handlers (move, look, jump, ...)
                                       │     ├── CursorHandler ← MCSTP
                                       │     └── ClickHandler  ← MCSTP
                                       │
                                       └── StateProviderRegistry
                                             └── MCSTPStateProvider ← MCSTP
                                                   ├── PlayerStateInfo (extended)
                                                   ├── HeldItemInfo + ItemCategorizer
                                                   ├── CombatContextInfo (extended)
                                                   ├── PlayerInputInfo
                                                   ├── ScreenStateInfo
                                                   ├── StatusEffectInfo
                                                   └── ThreatInfo
```

**Key implementation details:**

- MCSTP registers with MCCTP via `MCCTPApi.registerModule("mcstp")` — this adds it to the handshake
- State collection implements MCCTP's `StateProvider` interface — MCCTP calls it every tick and broadcasts the result
- Action handlers implement MCCTP's `ActionHandler` interface and are registered via `ActionDispatcher.registerHandler()`
- Threat scanning uses `getEntitiesByClass(HostileEntity.class)` within a 16-block bounding box
- Screen state reads GLFW mouse/key state directly
- Player input uses `Input.getMovementInput()` for movement vector and `GameOptions` key bindings for button state

## Building from Source

```bash
# MCCTP must be built first
cd mcctp
./gradlew build

# Then MCSTP
cd ../MCSTP
./gradlew build

# Output jar
build/libs/mcstp-1.0.0.jar
```

Build targets Java 21. Tested with Gradle 9.2.0 and Fabric Loom 1.15.3.

## Python Client

A Python package is included in `python/`. Provides typed dataclasses for the full game state:

```python
import time
from mcctp import SyncMCCTPClient, Actions
from mcstp import GameState
from mcstp import Actions as MCSTPActions

with SyncMCCTPClient("localhost", 8765) as client:

    def on_state(data: dict):
        state = GameState.from_dict(data)
        print(f"HP: {state.player_state.health} | Hostiles: {state.threat.hostile_count}")

        if state.combat_context.crosshair_target == "ENTITY":
            print(f"  Target: {state.combat_context.crosshair_entity_type} "
                  f"({state.combat_context.crosshair_entity_health} HP)")

    client.on_state(on_state)

    # MCCTP actions work as normal
    client.send(Actions.move("forward", "start"))
    time.sleep(2)
    client.send(Actions.move("forward", "stop"))

    # MCSTP adds cursor/click for GUIs
    client.send(Actions.open_inventory())
    time.sleep(0.1)
    client.send(MCSTPActions.cursor(0.5, 0.3))
    client.send(MCSTPActions.click("left"))
```

`GameState.to_control_dict()` returns a flat dictionary with the most useful fields for decision-making (health, position, held item, crosshair target, hostile count, screen state).

## License

MIT
