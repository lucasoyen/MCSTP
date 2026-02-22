from __future__ import annotations

from dataclasses import dataclass, field
from typing import Optional


@dataclass
class HeldItemInfo:
    name: str = "minecraft:air"
    category: str = "EMPTY"
    stack_count: int = 0
    max_durability: int = 0
    current_durability: int = 0

    @classmethod
    def from_dict(cls, data: dict) -> HeldItemInfo:
        return cls(
            name=data.get("name", "minecraft:air"),
            category=data.get("category", "EMPTY"),
            stack_count=data.get("stackCount", 0),
            max_durability=data.get("maxDurability", 0),
            current_durability=data.get("currentDurability", 0),
        )


@dataclass
class PlayerState:
    health: float = 20
    max_health: float = 20
    hunger: int = 20
    saturation: float = 5
    x: float = 0
    y: float = 0
    z: float = 0
    yaw: float = 0
    pitch: float = 0
    on_ground: bool = True
    sprinting: bool = False
    sneaking: bool = False
    swimming: bool = False
    flying: bool = False
    in_water: bool = False
    on_fire: bool = False
    experience_level: int = 0
    experience_progress: float = 0
    total_experience: int = 0

    @classmethod
    def from_dict(cls, data: dict) -> PlayerState:
        return cls(
            health=data.get("health", 20),
            max_health=data.get("maxHealth", 20),
            hunger=data.get("hunger", 20),
            saturation=data.get("saturation", 5),
            x=data.get("x", 0),
            y=data.get("y", 0),
            z=data.get("z", 0),
            yaw=data.get("yaw", 0),
            pitch=data.get("pitch", 0),
            on_ground=data.get("onGround", True),
            sprinting=data.get("sprinting", False),
            sneaking=data.get("sneaking", False),
            swimming=data.get("swimming", False),
            flying=data.get("flying", False),
            in_water=data.get("inWater", False),
            on_fire=data.get("onFire", False),
            experience_level=data.get("experienceLevel", 0),
            experience_progress=data.get("experienceProgress", 0),
            total_experience=data.get("totalExperience", 0),
        )


@dataclass
class CombatContext:
    is_using_item: bool = False
    is_blocking: bool = False
    active_hand: str = "MAIN_HAND"
    crosshair_target: str = "MISS"
    crosshair_entity_type: Optional[str] = None
    crosshair_block_pos: Optional[list[int]] = None
    crosshair_distance: float = -1
    crosshair_entity_health: float = -1
    crosshair_entity_max_health: float = -1

    @classmethod
    def from_dict(cls, data: dict) -> CombatContext:
        return cls(
            is_using_item=data.get("isUsingItem", False),
            is_blocking=data.get("isBlocking", False),
            active_hand=data.get("activeHand", "MAIN_HAND"),
            crosshair_target=data.get("crosshairTarget", "MISS"),
            crosshair_entity_type=data.get("crosshairEntityType"),
            crosshair_block_pos=data.get("crosshairBlockPos"),
            crosshair_distance=data.get("crosshairDistance", -1),
            crosshair_entity_health=data.get("crosshairEntityHealth", -1),
            crosshair_entity_max_health=data.get("crosshairEntityMaxHealth", -1),
        )


@dataclass
class PlayerInput:
    movement_forward: float = 0
    movement_sideways: float = 0
    jump: bool = False
    sprint: bool = False
    sneak: bool = False
    attack: bool = False
    use_item: bool = False
    drop: bool = False
    swap_offhand: bool = False
    open_inventory: bool = False
    yaw_delta: float = 0
    pitch_delta: float = 0

    @classmethod
    def from_dict(cls, data: dict) -> PlayerInput:
        return cls(
            movement_forward=data.get("movementForward", 0),
            movement_sideways=data.get("movementSideways", 0),
            jump=data.get("jump", False),
            sprint=data.get("sprint", False),
            sneak=data.get("sneak", False),
            attack=data.get("attack", False),
            use_item=data.get("useItem", False),
            drop=data.get("drop", False),
            swap_offhand=data.get("swapOffhand", False),
            open_inventory=data.get("openInventory", False),
            yaw_delta=data.get("yawDelta", 0),
            pitch_delta=data.get("pitchDelta", 0),
        )


@dataclass
class ScreenState:
    screen_open: bool = False
    screen_type: Optional[str] = None
    cursor_x: float = 0
    cursor_y: float = 0
    mouse_left: bool = False
    mouse_right: bool = False
    shift_held: bool = False

    @classmethod
    def from_dict(cls, data: dict) -> ScreenState:
        return cls(
            screen_open=data.get("screenOpen", False),
            screen_type=data.get("screenType"),
            cursor_x=data.get("cursorX", 0),
            cursor_y=data.get("cursorY", 0),
            mouse_left=data.get("mouseLeft", False),
            mouse_right=data.get("mouseRight", False),
            shift_held=data.get("shiftHeld", False),
        )


@dataclass
class StatusEffects:
    speed: bool = False
    slowness: bool = False
    strength: bool = False
    fire_resistance: bool = False
    poison: bool = False
    wither: bool = False
    regeneration: bool = False
    resistance: bool = False
    invisibility: bool = False
    water_breathing: bool = False
    absorption: bool = False
    active_effect_count: int = 0

    @classmethod
    def from_dict(cls, data: dict) -> StatusEffects:
        return cls(
            speed=data.get("speed", False),
            slowness=data.get("slowness", False),
            strength=data.get("strength", False),
            fire_resistance=data.get("fireResistance", False),
            poison=data.get("poison", False),
            wither=data.get("wither", False),
            regeneration=data.get("regeneration", False),
            resistance=data.get("resistance", False),
            invisibility=data.get("invisibility", False),
            water_breathing=data.get("waterBreathing", False),
            absorption=data.get("absorption", False),
            active_effect_count=data.get("activeEffectCount", 0),
        )


@dataclass
class ThreatInfo:
    target_entity_hostile: bool = False
    target_distance: float = -1
    nearest_hostile_dist: float = -1
    nearest_hostile_yaw: float = 0
    hostile_count: int = 0

    @classmethod
    def from_dict(cls, data: dict) -> ThreatInfo:
        return cls(
            target_entity_hostile=data.get("targetEntityHostile", False),
            target_distance=data.get("targetDistance", -1),
            nearest_hostile_dist=data.get("nearestHostileDist", -1),
            nearest_hostile_yaw=data.get("nearestHostileYaw", 0),
            hostile_count=data.get("hostileCount", 0),
        )


@dataclass
class GameState:
    timestamp: int = 0
    selected_slot: int = 0
    held_item: HeldItemInfo = field(default_factory=HeldItemInfo)
    offhand_item: HeldItemInfo = field(default_factory=HeldItemInfo)
    player_state: PlayerState = field(default_factory=PlayerState)
    combat_context: CombatContext = field(default_factory=CombatContext)
    player_input: PlayerInput = field(default_factory=PlayerInput)
    screen_state: ScreenState = field(default_factory=ScreenState)
    status_effects: StatusEffects = field(default_factory=StatusEffects)
    threat: ThreatInfo = field(default_factory=ThreatInfo)

    @classmethod
    def from_dict(cls, data: dict) -> GameState:
        return cls(
            timestamp=data.get("timestamp", 0),
            selected_slot=data.get("selectedSlot", 0),
            held_item=HeldItemInfo.from_dict(data.get("heldItem", {})),
            offhand_item=HeldItemInfo.from_dict(data.get("offhandItem", {})),
            player_state=PlayerState.from_dict(data.get("playerState", {})),
            combat_context=CombatContext.from_dict(data.get("combatContext", {})),
            player_input=PlayerInput.from_dict(data.get("playerInput", {})),
            screen_state=ScreenState.from_dict(data.get("screenState", {})),
            status_effects=StatusEffects.from_dict(data.get("statusEffects", {})),
            threat=ThreatInfo.from_dict(data.get("threat", {})),
        )

    def to_control_dict(self) -> dict:
        """Convert state to a dict suitable for control decision-making."""
        return {
            "health": self.player_state.health,
            "max_health": self.player_state.max_health,
            "hunger": self.player_state.hunger,
            "x": self.player_state.x,
            "y": self.player_state.y,
            "z": self.player_state.z,
            "yaw": self.player_state.yaw,
            "pitch": self.player_state.pitch,
            "on_ground": self.player_state.on_ground,
            "sprinting": self.player_state.sprinting,
            "sneaking": self.player_state.sneaking,
            "flying": self.player_state.flying,
            "in_water": self.player_state.in_water,
            "on_fire": self.player_state.on_fire,
            "held_item": self.held_item.name,
            "held_category": self.held_item.category,
            "selected_slot": self.selected_slot,
            "crosshair_target": self.combat_context.crosshair_target,
            "crosshair_entity_type": self.combat_context.crosshair_entity_type,
            "crosshair_distance": self.combat_context.crosshair_distance,
            "is_blocking": self.combat_context.is_blocking,
            "hostile_count": self.threat.hostile_count,
            "nearest_hostile_dist": self.threat.nearest_hostile_dist,
            "screen_open": self.screen_state.screen_open,
            "screen_type": self.screen_state.screen_type,
        }
