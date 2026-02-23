"""
MCSTP Test Session
Connects to MCCTP/MCSTP, runs a sequence of inputs for N seconds, and logs
all received game state + actions to a timestamped JSON log file.

Usage:
    python test_session.py              # 30 second session, 5s countdown
    python test_session.py --delay 10    # 10 second countdown before start
    python test_session.py --duration 60
    python test_session.py --host localhost --port 8765 --duration 20
"""

import argparse
import json
import time
import sys
from datetime import datetime
from pathlib import Path

from mcctp import SyncMCCTPClient, Actions
from mcstp import GameState
from mcstp import Actions as MCSTPActions


def main():
    parser = argparse.ArgumentParser(description="MCSTP test session with logging")
    parser.add_argument("--host", default="localhost")
    parser.add_argument("--port", type=int, default=8765)
    parser.add_argument("--delay", type=int, default=5, help="Countdown before starting (seconds)")
    parser.add_argument("--duration", type=int, default=30, help="Session length in seconds")
    parser.add_argument("--output", default=None, help="Log file path (default: auto-generated)")
    args = parser.parse_args()

    # Log file
    if args.output:
        log_path = Path(args.output)
    else:
        ts = datetime.now().strftime("%Y%m%d_%H%M%S")
        log_path = Path(f"session_{ts}.json")

    log = {
        "session": {
            "host": args.host,
            "port": args.port,
            "duration": args.duration,
            "started": datetime.now().isoformat(),
        },
        "handshake": None,
        "events": [],
    }

    state_count = 0
    last_state = None

    def on_handshake(modules):
        log["handshake"] = {"modules": modules, "time": time.time()}
        print(f"[handshake] modules: {modules}")

    def on_state(data):
        nonlocal state_count, last_state
        state_count += 1
        gs = GameState.from_dict(data)
        last_state = gs

        log["events"].append({
            "type": "state",
            "time": time.time(),
            "tick": state_count,
            "summary": gs.to_control_dict(),
        })

    def log_action(name, action_dict):
        log["events"].append({
            "type": "action",
            "time": time.time(),
            "name": name,
            "payload": action_dict,
        })
        print(f"[action] {name}: {json.dumps(action_dict)}")

    # --- Action sequence ---
    # Defines timed actions as (delay_from_start, name, action_dict)
    action_sequence = [
        # Walk forward for 4 seconds
        (0.5, "move_forward_start", Actions.move("forward", "start")),
        (4.5, "move_forward_stop", Actions.move("forward", "stop")),

        # Look around
        (5.0, "look_right", Actions.look(45, 0)),
        (6.0, "look_left", Actions.look(-90, 0)),
        (7.0, "look_center", Actions.look(45, 0)),

        # Jump a few times
        (8.0, "jump_1", Actions.jump()),
        (8.5, "jump_2", Actions.jump()),
        (9.0, "jump_3", Actions.jump()),

        # Sprint forward
        (10.0, "sprint_start", Actions.sprint("start")),
        (10.0, "move_forward_start", Actions.move("forward", "start")),
        (14.0, "move_forward_stop", Actions.move("forward", "stop")),
        (14.0, "sprint_stop", Actions.sprint("stop")),

        # Cycle all 9 hotbar slots
        (15.0, "slot_0", Actions.select_slot(0)),
        (15.3, "slot_1", Actions.select_slot(1)),
        (15.6, "slot_2", Actions.select_slot(2)),
        (15.9, "slot_3", Actions.select_slot(3)),
        (16.2, "slot_4", Actions.select_slot(4)),
        (16.5, "slot_5", Actions.select_slot(5)),
        (16.8, "slot_6", Actions.select_slot(6)),
        (17.1, "slot_7", Actions.select_slot(7)),
        (17.4, "slot_8", Actions.select_slot(8)),
        (17.7, "slot_0_back", Actions.select_slot(0)),

        # Attack
        (18.0, "attack_1", Actions.attack()),
        (18.3, "attack_2", Actions.attack()),
        (18.6, "attack_3", Actions.attack()),

        # Sneak
        (19.5, "sneak_start", Actions.sneak("start")),
        (21.0, "sneak_stop", Actions.sneak("stop")),

        # Strafe left then right
        (22.0, "move_left_start", Actions.move("left", "start")),
        (23.0, "move_left_stop", Actions.move("left", "stop")),
        (23.0, "move_right_start", Actions.move("right", "start")),
        (24.0, "move_right_stop", Actions.move("right", "stop")),

        # Open and close inventory
        (25.0, "open_inventory", Actions.open_inventory()),
        (25.5, "cursor_center", MCSTPActions.cursor(0.5, 0.5)),
        (26.0, "click_left", MCSTPActions.click("left")),
        (26.5, "close_screen", Actions.close_screen()),

        # Final look around
        (27.5, "look_up", Actions.look(0, -30)),
        (28.5, "look_down", Actions.look(0, 30)),
    ]

    print(f"Connecting to {args.host}:{args.port}...")

    with SyncMCCTPClient(args.host, args.port) as client:
        client.on_handshake(on_handshake)
        client.on_state(on_state)

        print(f"Connected. Logging to: {log_path}")
        print()

        # Countdown to give you time to tab into Minecraft
        for i in range(args.delay, 0, -1):
            print(f"  Starting in {i}...", end="\r")
            time.sleep(1)
        print(f"  Running {args.duration}s test session!   ")
        print()

        start = time.time()
        action_idx = 0

        # Print state summary periodically
        last_print = 0

        while time.time() - start < args.duration:
            elapsed = time.time() - start

            # Fire actions that are due
            while action_idx < len(action_sequence):
                delay, name, action = action_sequence[action_idx]
                if delay <= elapsed:
                    try:
                        client.send(action)
                        log_action(name, action)
                    except Exception as e:
                        print(f"[error] Failed to send {name}: {e}")
                    action_idx += 1
                else:
                    break

            # Print state summary every 2 seconds
            if last_state and elapsed - last_print >= 2.0:
                last_print = elapsed
                ps = last_state.player_state
                th = last_state.threat
                print(
                    f"[{elapsed:5.1f}s] "
                    f"HP:{ps.health:.0f}/{ps.max_health:.0f} "
                    f"Pos:({ps.x:.1f}, {ps.y:.1f}, {ps.z:.1f}) "
                    f"Yaw:{ps.yaw:.0f} "
                    f"Ground:{ps.on_ground} "
                    f"Hostiles:{th.hostile_count} "
                    f"States:{state_count}"
                )

            time.sleep(0.05)

    # Finalize log
    log["session"]["ended"] = datetime.now().isoformat()
    log["session"]["total_states"] = state_count
    log["session"]["total_actions"] = sum(1 for e in log["events"] if e["type"] == "action")

    log_path.write_text(json.dumps(log, indent=2))

    print()
    print(f"Session complete.")
    print(f"  Duration: {args.duration}s")
    print(f"  States received: {state_count}")
    print(f"  Actions sent: {log['session']['total_actions']}")
    print(f"  Log saved: {log_path}")


if __name__ == "__main__":
    main()
