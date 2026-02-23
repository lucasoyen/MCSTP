package com.mcstp.state;

import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;

public class ScreenStateInfo {
    public final boolean screenOpen;
    public final String screenType;
    public final double cursorX;
    public final double cursorY;
    public final boolean mouseLeft;
    public final boolean mouseRight;
    public final boolean shiftHeld;

    public ScreenStateInfo(MinecraftClient client) {
        Screen screen = client.currentScreen;
        this.screenOpen = screen != null;
        if (screen != null) {
            String title = screen.getTitle().getString();
            this.screenType = (title != null && !title.isEmpty()) ? title : screen.getClass().getSimpleName();
        } else {
            this.screenType = null;
        }

        long window = client.getWindow().getHandle();
        if (screen != null) {
            double[] mx = new double[1], my = new double[1];
            GLFW.glfwGetCursorPos(window, mx, my);
            double scale = client.getWindow().getScaleFactor();
            this.cursorX = mx[0] / scale / client.getWindow().getScaledWidth();
            this.cursorY = my[0] / scale / client.getWindow().getScaledHeight();
        } else {
            this.cursorX = 0;
            this.cursorY = 0;
        }

        this.mouseLeft = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        this.mouseRight = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        this.shiftHeld = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
                || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("screenOpen", screenOpen);
        if (screenType != null) {
            json.addProperty("screenType", screenType);
        }
        json.addProperty("cursorX", Math.round(cursorX * 1000.0) / 1000.0);
        json.addProperty("cursorY", Math.round(cursorY * 1000.0) / 1000.0);
        json.addProperty("mouseLeft", mouseLeft);
        json.addProperty("mouseRight", mouseRight);
        json.addProperty("shiftHeld", shiftHeld);
        return json;
    }
}
