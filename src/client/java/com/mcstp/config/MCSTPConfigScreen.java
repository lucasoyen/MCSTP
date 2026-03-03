package com.mcstp.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.mcctp.api.MCCTPApi;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class MCSTPConfigScreen extends Screen {
    private final Screen parent;
    private MCSTPConfig config;
    private TextFieldWidget tickIntervalField;
    private TextFieldWidget radiusField;
    private TextFieldWidget maxEntitiesField;
    private final List<ButtonWidget> moduleButtons = new ArrayList<>();

    private static final Map<String, String> MODULE_DESCRIPTIONS = new LinkedHashMap<>();
    static {
        MODULE_DESCRIPTIONS.put("playerState",
                "Position, health, hunger, armor, velocity, movement flags.\nCore player vitals and physics state.");
        MODULE_DESCRIPTIONS.put("heldItem",
                "Main hand & offhand item name, category, durability.\nWhat the player is currently holding.");
        MODULE_DESCRIPTIONS.put("combatContext",
                "Crosshair target, attack cooldown, blocking, item use.\nCombat-relevant state for the current frame.");
        MODULE_DESCRIPTIONS.put("playerInput",
                "Movement keys, look deltas, jump/sprint/sneak.\nRaw input state from keybindings.");
        MODULE_DESCRIPTIONS.put("screenState",
                "Open screen type, cursor position, mouse buttons.\nGUI/inventory screen state.");
        MODULE_DESCRIPTIONS.put("statusEffects",
                "Active potion effects (speed, strength, poison, etc.).\n12 tracked effects + active count.");
        MODULE_DESCRIPTIONS.put("threat",
                "Nearest hostile distance/direction, hostile count.\nNearby threat assessment.");
        MODULE_DESCRIPTIONS.put("inventory",
                "All 41 inventory slots with item details.\nFull slot-by-slot inventory snapshot.");
        MODULE_DESCRIPTIONS.put("nearbyEntities",
                "Nearby living entities within scan radius.\nType, position, health, hostility.");
    }

    public MCSTPConfigScreen(Screen parent) {
        super(Text.literal("MCSTP Configuration"));
        this.parent = parent;
        this.config = MCSTPConfig.load();
    }

    @Override
    protected void init() {
        moduleButtons.clear();
        int centerX = this.width / 2;
        int y = 36;

        // Module toggle buttons with tooltips
        for (Map.Entry<String, String> desc : MODULE_DESCRIPTIONS.entrySet()) {
            String module = desc.getKey();
            String tooltip = desc.getValue();
            boolean enabled = config.isModuleEnabled(module);

            ButtonWidget btn = ButtonWidget.builder(
                    formatModuleLabel(module, enabled),
                    button -> {
                        boolean newState = !config.isModuleEnabled(module);
                        config.setModuleEnabled(module, newState);
                        button.setMessage(formatModuleLabel(module, newState));
                    })
                    .dimensions(centerX - 100, y, 200, 20)
                    .tooltip(Tooltip.of(Text.literal(tooltip)))
                    .build();
            addDrawableChild(btn);
            moduleButtons.add(btn);
            y += 22;
        }

        y += 6;

        // Tick interval
        tickIntervalField = new TextFieldWidget(this.textRenderer, centerX - 50, y, 150, 16, Text.literal("Tick Interval"));
        tickIntervalField.setText(String.valueOf(config.getTickInterval()));
        tickIntervalField.setTooltip(Tooltip.of(Text.literal("Ticks between state broadcasts (1 = every tick, 20 = once per second)")));
        addDrawableChild(tickIntervalField);
        y += 20;

        // Nearby entities radius
        radiusField = new TextFieldWidget(this.textRenderer, centerX - 50, y, 150, 16, Text.literal("Scan Radius"));
        radiusField.setText(String.valueOf(config.getNearbyEntitiesRadius()));
        radiusField.setTooltip(Tooltip.of(Text.literal("Block radius for nearby entity scanning (default: 16)")));
        addDrawableChild(radiusField);
        y += 20;

        // Max entities
        maxEntitiesField = new TextFieldWidget(this.textRenderer, centerX - 50, y, 150, 16, Text.literal("Max Entities"));
        maxEntitiesField.setText(String.valueOf(config.getNearbyEntitiesMax()));
        maxEntitiesField.setTooltip(Tooltip.of(Text.literal("Maximum entities to report, sorted by distance (default: 32)")));
        addDrawableChild(maxEntitiesField);
        y += 24;

        // Preset buttons row
        int presetWidth = 62;
        int presetGap = 4;
        int totalPresetWidth = presetWidth * 3 + presetGap * 2;
        int presetX = centerX - totalPresetWidth / 2;

        addDrawableChild(ButtonWidget.builder(Text.literal("None"), button -> {
            applyPreset("none");
        }).dimensions(presetX, y, presetWidth, 20)
                .tooltip(Tooltip.of(Text.literal("Disable all telemetry modules")))
                .build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Default"), button -> {
            applyPreset("default");
        }).dimensions(presetX + presetWidth + presetGap, y, presetWidth, 20)
                .tooltip(Tooltip.of(Text.literal("Reset to defaults: position, health, hunger, held item, combat")))
                .build());

        addDrawableChild(ButtonWidget.builder(Text.literal("All"), button -> {
            applyPreset("all");
        }).dimensions(presetX + (presetWidth + presetGap) * 2, y, presetWidth, 20)
                .tooltip(Tooltip.of(Text.literal("Enable all telemetry modules")))
                .build());

        y += 28;

        // Save button
        addDrawableChild(ButtonWidget.builder(Text.literal("Save & Close"), button -> {
            applyAndSave();
            close();
        }).dimensions(centerX - 100, y, 200, 20).build());
    }

    private void applyPreset(String preset) {
        Map<String, Boolean> defaults = Map.of(
                "playerState", true, "heldItem", true, "combatContext", true,
                "playerInput", false, "screenState", false, "statusEffects", false,
                "threat", false, "inventory", false, "nearbyEntities", false
        );

        int i = 0;
        for (String module : MODULE_DESCRIPTIONS.keySet()) {
            boolean enabled;
            switch (preset) {
                case "none" -> enabled = false;
                case "all" -> enabled = true;
                default -> enabled = defaults.getOrDefault(module, false);
            }
            config.setModuleEnabled(module, enabled);
            if (i < moduleButtons.size()) {
                moduleButtons.get(i).setMessage(formatModuleLabel(module, enabled));
            }
            i++;
        }
    }

    private Text formatModuleLabel(String module, boolean enabled) {
        return Text.literal(module + ": " + (enabled ? "\u00a7aON" : "\u00a7cOFF"));
    }

    private void applyAndSave() {
        try {
            config.setTickInterval(Integer.parseInt(tickIntervalField.getText()));
        } catch (NumberFormatException ignored) {}
        try {
            config.setNearbyEntitiesRadius(Integer.parseInt(radiusField.getText()));
        } catch (NumberFormatException ignored) {}
        try {
            config.setNearbyEntitiesMax(Integer.parseInt(maxEntitiesField.getText()));
        } catch (NumberFormatException ignored) {}

        config.save();
        MCCTPApi.setTickInterval(config.getTickInterval());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 12, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("\u00a77Telemetry Modules"),
                this.width / 2, 25, 0x999999);

        // Labels for text fields
        int centerX = this.width / 2;
        int labelX = centerX - 50 - 4;
        context.drawTextWithShadow(this.textRenderer, "Tick Interval:",
                labelX - this.textRenderer.getWidth("Tick Interval:"), tickIntervalField.getY() + 4, 0xAAAAAA);
        context.drawTextWithShadow(this.textRenderer, "Scan Radius:",
                labelX - this.textRenderer.getWidth("Scan Radius:"), radiusField.getY() + 4, 0xAAAAAA);
        context.drawTextWithShadow(this.textRenderer, "Max Entities:",
                labelX - this.textRenderer.getWidth("Max Entities:"), maxEntitiesField.getY() + 4, 0xAAAAAA);
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }
}
