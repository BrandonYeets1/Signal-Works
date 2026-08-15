package com.dgtlbrandxn.signalworks.client.screen;

import com.dgtlbrandxn.signalworks.block.SignalMovement;
import com.dgtlbrandxn.signalworks.blockentity.TrafficLightControllerBlockEntity;
import com.dgtlbrandxn.signalworks.menu.TrafficLightControllerMenu;
import com.dgtlbrandxn.signalworks.registry.ModItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;

/** Terminal-style controller with an integrated top-down intersection editor. */
public final class TrafficLightControllerScreen extends AbstractContainerScreen<TrafficLightControllerMenu> {
    private static final int PANEL_GREEN = 0xFF65FF86;
    private static final int PANEL_AMBER = 0xFFFFCE55;
    private static final int PANEL_RED = 0xFFFF6A6A;
    private static final int PANEL_BLUE = 0xFF75C7FF;
    private static final int PANEL_PURPLE = 0xFFC28CFF;
    private static final int TEXT_DIM = 0xFF7E9187;
    private static final int TEXT_NORMAL = 0xFFB8D5C0;
    private static final int GRID_DARK = 0xFF10231A;
    private static final int GRID_LINE = 0xFF244936;
    private static final int LOG_LINES = 10;

    private static final int MAP_LEFT = 18;
    private static final int MAP_TOP = 68;
    private static final int MAP_RIGHT = 315;
    private static final int MAP_BOTTOM = 222;
    private static final int INSPECTOR_X = 326;
    private static final int NODE_HIT_RADIUS = 9;
    private static final int CLUSTER_HIT_RADIUS = 12;
    private static final int OVERVIEW_CLUSTER_DISTANCE = 28;
    private static final int FOCUS_NODE_SPACING = 18;

    private enum ViewMode {
        HOME,
        TERMINAL,
        MAP
    }

    private final Deque<String> terminalLog = new ArrayDeque<>();
    private final List<Button> homeButtons = new ArrayList<>();
    private final List<Button> terminalButtons = new ArrayList<>();
    private final List<Button> mapButtons = new ArrayList<>();
    private final List<Integer> focusedLinks = new ArrayList<>();

    private EditBox commandBox;
    private Button commandTab;
    private Button mapTab;
    private Button homeTab;
    private Button overviewButton;
    private Button throughRoleButton;
    private Button leftRoleButton;
    private Button rightRoleButton;
    private Button throughLeftRoleButton;
    private Button pedestrianRoleButton;
    private Button typePrevButton;
    private Button typeNextButton;
    private Button northPriorityButton;
    private Button eastPriorityButton;
    private Button southPriorityButton;
    private Button westPriorityButton;

    private ViewMode viewMode = ViewMode.HOME;
    private int selectedLink = -1;

    public TrafficLightControllerScreen(TrafficLightControllerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 440;
        imageHeight = 274;
        inventoryLabelX = 0;
        inventoryLabelY = 0;
    }

    @Override
    protected void init() {
        super.init();
        homeButtons.clear();
        terminalButtons.clear();
        mapButtons.clear();

        commandTab = addRenderableWidget(Button.builder(
                        Component.literal("COMMAND TERMINAL"),
                        button -> switchView(ViewMode.TERMINAL)
                )
                .bounds(leftPos + 14, topPos + 36, 118, 18)
                .build());
        mapTab = addRenderableWidget(Button.builder(
                        Component.literal("INTERSECTION MAP"),
                        button -> switchView(ViewMode.MAP)
                )
                .bounds(leftPos + 136, topPos + 36, 118, 18)
                .build());

        homeTab = addRenderableWidget(Button.builder(
                        Component.literal("LAPTOP HOME"),
                        button -> switchView(ViewMode.HOME)
                )
                .bounds(leftPos + 258, topPos + 36, 82, 18)
                .build());

        overviewButton = addRenderableWidget(Button.builder(
                        Component.literal("OVERVIEW"),
                        button -> exitMapFocus()
                )
                .bounds(leftPos + 344, topPos + 36, 78, 18)
                .build());
        mapButtons.add(overviewButton);

        homeButtons.add(addRenderableWidget(Button.builder(
                        Component.literal("COMMAND TERMINAL"),
                        button -> switchView(ViewMode.TERMINAL)
                )
                .bounds(leftPos + 72, topPos + 108, 136, 42)
                .build()));
        homeButtons.add(addRenderableWidget(Button.builder(
                        Component.literal("INTERSECTION MAP"),
                        button -> switchView(ViewMode.MAP)
                )
                .bounds(leftPos + 232, topPos + 108, 136, 42)
                .build()));

        int settingsX = leftPos + 386;
        terminalButtons.add(addRenderableWidget(Button.builder(
                        Component.literal("[-]"),
                        button -> send(TrafficLightControllerMenu.BUTTON_TIMING_DOWN)
                )
                .bounds(settingsX, topPos + 79, 20, 18)
                .build()));
        terminalButtons.add(addRenderableWidget(Button.builder(
                        Component.literal("[+]"),
                        button -> send(TrafficLightControllerMenu.BUTTON_TIMING_UP)
                )
                .bounds(settingsX + 24, topPos + 79, 20, 18)
                .build()));
        terminalButtons.add(addRenderableWidget(Button.builder(
                        Component.literal("CYCLE"),
                        button -> send(TrafficLightControllerMenu.BUTTON_PRIORITY)
                )
                .bounds(settingsX, topPos + 110, 43, 18)
                .build()));
        terminalButtons.add(addRenderableWidget(Button.builder(
                        Component.literal("TOGGLE"),
                        button -> send(TrafficLightControllerMenu.BUTTON_NIGHT_FLASH)
                )
                .bounds(settingsX, topPos + 141, 43, 18)
                .build()));
        terminalButtons.add(addRenderableWidget(Button.builder(
                        Component.literal("CYCLE"),
                        button -> send(TrafficLightControllerMenu.BUTTON_SPEED)
                )
                .bounds(settingsX, topPos + 172, 43, 18)
                .build()));
        terminalButtons.add(addRenderableWidget(Button.builder(
                        Component.literal("TOGGLE"),
                        button -> send(TrafficLightControllerMenu.BUTTON_FAIL_SAFE)
                )
                .bounds(settingsX, topPos + 203, 43, 18)
                .build()));

        commandBox = new EditBox(
                font,
                leftPos + 18,
                topPos + 244,
                326,
                16,
                Component.literal("controller command")
        );
        commandBox.setMaxLength(80);
        commandBox.setBordered(false);
        commandBox.setTextColor(PANEL_GREEN);
        commandBox.setTextColorUneditable(TEXT_DIM);
        commandBox.setHint(Component.literal("type 'help' or 'status'"));
        addRenderableWidget(commandBox);

        int roleY = topPos + 238;
        throughRoleButton = addMapButton("THROUGH", leftPos + 18, roleY, 66, SignalMovement.THROUGH);
        leftRoleButton = addMapButton("LEFT", leftPos + 88, roleY, 58, SignalMovement.LEFT);
        rightRoleButton = addMapButton("RIGHT", leftPos + 150, roleY, 58, SignalMovement.RIGHT);
        throughLeftRoleButton = addMapButton("THRU + LEFT", leftPos + 212, roleY, 86, SignalMovement.THROUGH_LEFT);
        pedestrianRoleButton = addMapButton("PED", leftPos + 302, roleY, 52, SignalMovement.PEDESTRIAN);

        typePrevButton = addRenderableWidget(Button.builder(
                        Component.literal("< TYPE"),
                        ignored -> changeSelectedType(-1))
                .bounds(leftPos + 326, topPos + 151, 48, 18).build());
        typeNextButton = addRenderableWidget(Button.builder(
                        Component.literal("TYPE >"),
                        ignored -> changeSelectedType(1))
                .bounds(leftPos + 378, topPos + 151, 48, 18).build());
        mapButtons.add(typePrevButton);
        mapButtons.add(typeNextButton);

        northPriorityButton = addPriorityButton("N", leftPos + 326, topPos + 181, TrafficLightControllerMenu.BUTTON_PRIORITY_NORTH);
        eastPriorityButton = addPriorityButton("E", leftPos + 378, topPos + 181, TrafficLightControllerMenu.BUTTON_PRIORITY_EAST);
        southPriorityButton = addPriorityButton("S", leftPos + 326, topPos + 203, TrafficLightControllerMenu.BUTTON_PRIORITY_SOUTH);
        westPriorityButton = addPriorityButton("W", leftPos + 378, topPos + 203, TrafficLightControllerMenu.BUTTON_PRIORITY_WEST);

        terminalLog.clear();
        appendLog("SIGNAL WORKS FIELD OS 3.5.7");
        appendLog("controller node online");
        appendLog("intersection-map + detector modules loaded");
        appendLog("type 'help' for commands");
        switchView(viewMode);
    }

    private Button addMapButton(String label, int x, int y, int width, SignalMovement movement) {
        Button button = addRenderableWidget(Button.builder(
                        Component.literal(label),
                        ignored -> assignSelected(movement)
                )
                .bounds(x, y, width, 18)
                .build());
        mapButtons.add(button);
        return button;
    }

    private Button addPriorityButton(String cardinal, int x, int y, int buttonId) {
        Button button = addRenderableWidget(Button.builder(
                        Component.literal(cardinal), ignored -> send(buttonId))
                .bounds(x, y, 48, 18).build());
        mapButtons.add(button);
        return button;
    }

    private void changeSelectedType(int delta) {
        if (selectedLink < 0 || selectedLink >= menu.mapLinkCount()) return;
        send(TrafficLightControllerMenu.typeButtonId(selectedLink, delta));
    }

    private void switchView(ViewMode mode) {
        viewMode = mode;
        boolean home = mode == ViewMode.HOME;
        boolean terminal = mode == ViewMode.TERMINAL;
        boolean map = mode == ViewMode.MAP;

        for (Button button : homeButtons) {
            button.visible = home;
        }
        for (Button button : terminalButtons) {
            button.visible = terminal;
        }
        for (Button button : mapButtons) {
            button.visible = map;
        }
        if (overviewButton != null) {
            overviewButton.visible = map && isMapFocused();
            overviewButton.active = map && isMapFocused();
        }
        if (commandBox != null) {
            commandBox.visible = terminal;
            if (terminal) {
                setInitialFocus(commandBox);
            } else if (getFocused() == commandBox) {
                setFocused(null);
            }
        }
        if (commandTab != null) {
            commandTab.active = !terminal;
        }
        if (mapTab != null) {
            mapTab.active = !map;
        }
        if (homeTab != null) {
            homeTab.active = !home;
        }
        updateRoleButtons();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        int linkCount = menu.mapLinkCount();
        if (selectedLink >= linkCount) {
            selectedLink = linkCount == 0 ? -1 : linkCount - 1;
        }
        focusedLinks.removeIf(index -> index < 0 || index >= linkCount);
        if (overviewButton != null) {
            overviewButton.visible = viewMode == ViewMode.MAP && isMapFocused();
            overviewButton.active = isMapFocused();
        }
        updateRoleButtons();
    }

    private boolean isMapFocused() {
        return !focusedLinks.isEmpty();
    }

    private void enterMapFocus(List<Integer> links) {
        focusedLinks.clear();
        for (int link : links) {
            if (link >= 0 && link < menu.mapLinkCount() && !focusedLinks.contains(link)) {
                focusedLinks.add(link);
            }
        }
        selectedLink = -1;
        if (overviewButton != null) {
            overviewButton.visible = viewMode == ViewMode.MAP && isMapFocused();
            overviewButton.active = isMapFocused();
        }
        updateRoleButtons();
    }

    private void exitMapFocus() {
        focusedLinks.clear();
        selectedLink = -1;
        if (overviewButton != null) {
            overviewButton.visible = false;
            overviewButton.active = false;
        }
        updateRoleButtons();
    }

    private void updateRoleButtons() {
        if (throughRoleButton == null) {
            return;
        }
        boolean selected = selectedLink >= 0 && selectedLink < menu.mapLinkCount();
        int type = selected ? menu.linkType(selectedLink) : -1;

        boolean fixedSpecial = type == TrafficLightControllerBlockEntity.MAP_TYPE_STRAIGHT_ARROW
                || type == TrafficLightControllerBlockEntity.MAP_TYPE_U_TURN
                || type == TrafficLightControllerBlockEntity.MAP_TYPE_BUS;
        throughRoleButton.active = selected && !fixedSpecial
                && type != TrafficLightControllerBlockEntity.MAP_TYPE_PEDESTRIAN
                && type != TrafficLightControllerBlockEntity.MAP_TYPE_LEFT
                && type != TrafficLightControllerBlockEntity.MAP_TYPE_RIGHT;
        leftRoleButton.active = selected && !fixedSpecial
                && type != TrafficLightControllerBlockEntity.MAP_TYPE_PEDESTRIAN
                && type != TrafficLightControllerBlockEntity.MAP_TYPE_RAMP_METER
                && type != TrafficLightControllerBlockEntity.MAP_TYPE_RIGHT;
        rightRoleButton.active = selected && !fixedSpecial
                && type != TrafficLightControllerBlockEntity.MAP_TYPE_PEDESTRIAN
                && type != TrafficLightControllerBlockEntity.MAP_TYPE_RAMP_METER
                && type != TrafficLightControllerBlockEntity.MAP_TYPE_LEFT
                && type != TrafficLightControllerBlockEntity.MAP_TYPE_FOUR
                && type != TrafficLightControllerBlockEntity.MAP_TYPE_FIVE
                && type != TrafficLightControllerBlockEntity.MAP_TYPE_DOGHOUSE;
        throughLeftRoleButton.active = selected && (type == TrafficLightControllerBlockEntity.MAP_TYPE_FOUR
                || type == TrafficLightControllerBlockEntity.MAP_TYPE_FIVE
                || type == TrafficLightControllerBlockEntity.MAP_TYPE_DOGHOUSE);
        pedestrianRoleButton.active = selected && type == TrafficLightControllerBlockEntity.MAP_TYPE_PEDESTRIAN;
        if (typePrevButton != null) typePrevButton.active = selected;
        if (typeNextButton != null) typeNextButton.active = selected;
        updatePriorityButton(northPriorityButton, "N", menu.northPriority());
        updatePriorityButton(eastPriorityButton, "E", menu.eastPriority());
        updatePriorityButton(southPriorityButton, "S", menu.southPriority());
        updatePriorityButton(westPriorityButton, "W", menu.westPriority());
    }

    private void updatePriorityButton(Button button, String cardinal, int value) {
        if (button != null) {
            button.setMessage(Component.literal(cardinal + ":" + approachPriorityCode(value)));
        }
    }

    private void assignSelected(SignalMovement movement) {
        if (selectedLink < 0 || selectedLink >= menu.mapLinkCount()) {
            return;
        }
        send(TrafficLightControllerMenu.assignmentButtonId(selectedLink, movement));
    }

    private void send(int buttonId) {
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, buttonId);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (viewMode == ViewMode.TERMINAL
                && keyCode == GLFW.GLFW_KEY_ENTER
                && commandBox != null
                && commandBox.isFocused()) {
            runCommand(commandBox.getValue());
            commandBox.setValue("");
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_TAB) {
            switchView(switch (viewMode) {
                case HOME -> ViewMode.TERMINAL;
                case TERMINAL -> ViewMode.MAP;
                case MAP -> ViewMode.HOME;
            });
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Handle map nodes before AbstractContainerScreen consumes clicks inside the GUI.
        if (viewMode == ViewMode.MAP && button == 0 && handleMapClick(mouseX, mouseY)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean handleMapClick(double mouseX, double mouseY) {
        int localX = Mth.floor(mouseX) - leftPos;
        int localY = Mth.floor(mouseY) - topPos;
        if (localX < MAP_LEFT || localX > MAP_RIGHT || localY < MAP_TOP || localY > MAP_BOTTOM) {
            return false;
        }

        if (isMapFocused()) {
            for (MapNode node : focusedMapNodes()) {
                if (insideHitBox(localX, localY, node.x(), node.y(), NODE_HIT_RADIUS)) {
                    selectedLink = node.linkIndex();
                    updateRoleButtons();
                    return true;
                }
            }
        } else {
            for (MapCluster cluster : overviewClusters()) {
                if (!insideHitBox(localX, localY, cluster.x(), cluster.y(), CLUSTER_HIT_RADIUS)) {
                    continue;
                }

                if (cluster.links().size() > 1) {
                    enterMapFocus(cluster.links());
                } else {
                    selectedLink = cluster.links().getFirst();
                    updateRoleButtons();
                }
                return true;
            }
        }

        selectedLink = -1;
        updateRoleButtons();
        return true;
    }

    private static boolean insideHitBox(int mouseX, int mouseY, int centerX, int centerY, int radius) {
        return mouseX >= centerX - radius && mouseX <= centerX + radius
                && mouseY >= centerY - radius && mouseY <= centerY + radius;
    }

    private void runCommand(String rawCommand) {
        String command = rawCommand == null ? "" : rawCommand.trim().toLowerCase(Locale.ROOT);
        if (command.isEmpty()) {
            return;
        }

        appendLog("> " + command);
        String[] parts = command.split("\\s+");

        if (parts[0].equals("help")) {
            appendLog("set through <5-60>");
            appendLog("set priority <left|right|both>");
            appendLog("set night <on|off>");
            appendLog("set speed <slow|normal|fast>");
            appendLog("set failsafe <on|off>");
            appendLog("map / links / detectors / status");
            return;
        }

        if (parts[0].equals("map")) {
            switchView(ViewMode.MAP);
            return;
        }

        if (parts[0].equals("status")) {
            appendLog(String.format(Locale.ROOT, "through_green = %.1fs", menu.throughGreenTicks() / 20.0F));
            appendLog("turn_priority = " + priorityName(menu.priorityMode()));
            appendLog("night_flash = " + onOff(menu.nightFlashEnabled()));
            appendLog("phase_speed = " + speedName(menu.speedMode()));
            appendLog("fail_safe = " + onOff(menu.failSafeEnabled()));
            appendLog("linked_signals = " + menu.linkedSignalCount());
            appendLog("detectors = " + menu.detectorCount());
            appendLog("x_axis_call = " + demandName(menu.xDemandMask()));
            appendLog("z_axis_call = " + demandName(menu.zDemandMask()));
            return;
        }

        if (parts[0].equals("detectors")) {
            appendLog("detectors = " + menu.detectorCount());
            appendLog("x_axis_call = " + demandName(menu.xDemandMask()));
            appendLog("z_axis_call = " + demandName(menu.zDemandMask()));
            appendLog("mask: 1=through 2=turn 3=both");
            return;
        }

        if (parts[0].equals("links")) {
            appendLog("linked_signals = " + menu.linkedSignalCount());
            appendLog("open INTERSECTION MAP to assign roles");
            return;
        }

        if (parts.length < 3 || !parts[0].equals("set")) {
            appendLog("ERR unknown command; type 'help'");
            return;
        }

        switch (parts[1]) {
            case "through", "timing", "green" -> setThrough(parts[2]);
            case "priority", "turn" -> setPriority(parts[2]);
            case "night", "flash" -> setBooleanSetting(
                    parts[2],
                    menu.nightFlashEnabled(),
                    TrafficLightControllerMenu.BUTTON_NIGHT_FLASH,
                    "night_flash"
            );
            case "speed" -> setSpeed(parts[2]);
            case "failsafe", "fail-safe", "broken" -> setBooleanSetting(
                    parts[2],
                    menu.failSafeEnabled(),
                    TrafficLightControllerMenu.BUTTON_FAIL_SAFE,
                    "fail_safe"
            );
            default -> appendLog("ERR unknown setting '" + parts[1] + "'");
        }
    }

    private void setThrough(String value) {
        try {
            int requestedSeconds = Integer.parseInt(value);
            int targetSeconds = Mth.clamp(Math.round(requestedSeconds / 5.0F) * 5, 5, 60);
            int currentSeconds = menu.throughGreenTicks() / 20;
            int clicks = Math.abs(targetSeconds - currentSeconds) / 5;
            int button = targetSeconds > currentSeconds
                    ? TrafficLightControllerMenu.BUTTON_TIMING_UP
                    : TrafficLightControllerMenu.BUTTON_TIMING_DOWN;
            for (int i = 0; i < clicks; i++) {
                send(button);
            }
            appendLog("OK through_green -> " + targetSeconds + "s");
        } catch (NumberFormatException exception) {
            appendLog("ERR through time must be 5-60 seconds");
        }
    }

    private void setPriority(String requested) {
        int target = switch (requested) {
            case "left" -> TrafficLightControllerBlockEntity.PRIORITY_LEFT;
            case "right" -> TrafficLightControllerBlockEntity.PRIORITY_RIGHT;
            case "both" -> TrafficLightControllerBlockEntity.PRIORITY_BOTH;
            default -> -1;
        };
        if (target < 0) {
            appendLog("ERR priority must be left, right, or both");
            return;
        }
        int clicks = Math.floorMod(target - menu.priorityMode(), 3);
        for (int i = 0; i < clicks; i++) {
            send(TrafficLightControllerMenu.BUTTON_PRIORITY);
        }
        appendLog("OK turn_priority -> " + requested);
    }

    private void setSpeed(String requested) {
        int target = switch (requested) {
            case "slow" -> 0;
            case "normal" -> 1;
            case "fast" -> 2;
            default -> -1;
        };
        if (target < 0) {
            appendLog("ERR speed must be slow, normal, or fast");
            return;
        }
        int clicks = Math.floorMod(target - menu.speedMode(), 3);
        for (int i = 0; i < clicks; i++) {
            send(TrafficLightControllerMenu.BUTTON_SPEED);
        }
        appendLog("OK phase_speed -> " + requested);
    }

    private void setBooleanSetting(String value, boolean current, int button, String settingName) {
        Boolean requested = switch (value) {
            case "on", "true", "1", "enable", "enabled" -> Boolean.TRUE;
            case "off", "false", "0", "disable", "disabled" -> Boolean.FALSE;
            default -> null;
        };
        if (requested == null) {
            appendLog("ERR value must be on or off");
            return;
        }
        if (requested != current) {
            send(button);
        }
        appendLog("OK " + settingName + " -> " + onOff(requested));
    }

    private void appendLog(String line) {
        terminalLog.addLast(line);
        while (terminalLog.size() > LOG_LINES) {
            terminalLog.removeFirst();
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xF00A0E10);
        graphics.fill(leftPos + 2, topPos + 2, leftPos + imageWidth - 2, topPos + imageHeight - 2, 0xFF253039);
        graphics.fill(leftPos + 7, topPos + 7, leftPos + imageWidth - 7, topPos + imageHeight - 7, 0xFF07100C);
        graphics.fill(leftPos + 12, topPos + 31, leftPos + imageWidth - 12, topPos + 32, 0xFF315E42);
        graphics.fill(leftPos + 12, topPos + 58, leftPos + imageWidth - 12, topPos + 59, 0xFF315E42);

        if (viewMode == ViewMode.HOME) {
            // Procedural field-laptop shell and desktop; no texture asset needed.
            graphics.fill(leftPos + 38, topPos + 67, leftPos + imageWidth - 38, topPos + 205, 0xFF111820);
            graphics.fill(leftPos + 45, topPos + 74, leftPos + imageWidth - 45, topPos + 198, 0xFF071711);
            graphics.fill(leftPos + 28, topPos + 210, leftPos + imageWidth - 28, topPos + 244, 0xFF252C31);
            graphics.fill(leftPos + 92, topPos + 218, leftPos + imageWidth - 92, topPos + 237, 0xFF12171A);
            graphics.fill(leftPos + 178, topPos + 239, leftPos + imageWidth - 178, topPos + 245, 0xFF3B454B);
        } else if (viewMode == ViewMode.TERMINAL) {
            graphics.fill(leftPos + 269, topPos + 64, leftPos + 270, topPos + 228, 0xFF315E42);
            graphics.fill(leftPos + 12, topPos + 234, leftPos + imageWidth - 12, topPos + 235, 0xFF315E42);
            graphics.fill(leftPos + 12, topPos + 239, leftPos + imageWidth - 12, topPos + 264, 0xFF020806);
            graphics.fill(leftPos + 13, topPos + 240, leftPos + imageWidth - 13, topPos + 241, 0xFF173D28);
        } else {
            graphics.fill(leftPos + MAP_LEFT, topPos + MAP_TOP, leftPos + MAP_RIGHT, topPos + MAP_BOTTOM, GRID_DARK);
            graphics.fill(leftPos + 319, topPos + 64, leftPos + 320, topPos + 228, 0xFF315E42);
            graphics.fill(leftPos + 12, topPos + 230, leftPos + imageWidth - 12, topPos + 231, 0xFF315E42);
        }

        // Compact take-only field-kit drawer. Keeping the player inventory out of this app
        // prevents the screen from exceeding common 1080p GUI scales.
        graphics.fill(leftPos + 356, topPos + 224, leftPos + 428, topPos + 268, 0xFF111820);
        graphics.fill(leftPos + 357, topPos + 225, leftPos + 427, topPos + 226, 0xFF315E42);
        drawSlotFrame(graphics, 374, 243);
        drawSlotFrame(graphics, 396, 243);
    }

    private void drawSlotFrame(GuiGraphics graphics, int x, int y) {
        graphics.fill(leftPos + x - 1, topPos + y - 1, leftPos + x + 17, topPos + y + 17, 0xFF315E42);
        graphics.fill(leftPos + x, topPos + y, leftPos + x + 16, topPos + y + 16, 0xFF020806);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        String heading = switch (viewMode) {
            case HOME -> "SIGNAL WORKS FIELD LAPTOP";
            case TERMINAL -> "TRAFFIC SIGNAL COMMAND TERMINAL";
            case MAP -> "TRAFFIC SIGNAL INTERSECTION MAP";
        };
        graphics.drawString(font, Component.literal(heading), 13, 12, PANEL_GREEN, false);
        graphics.drawString(
                font,
                Component.literal("NODE: " + menu.containerId + "  //  ONLINE  //  LINKS: " + menu.linkedSignalCount() + "  //  DET: " + menu.detectorCount()),
                13,
                23,
                TEXT_DIM,
                false
        );
        graphics.drawString(font, Component.literal("FIELD KIT"), 366, 229, PANEL_AMBER, false);

        if (viewMode == ViewMode.HOME) {
            renderHomeLabels(graphics);
        } else if (viewMode == ViewMode.TERMINAL) {
            renderTerminalLabels(graphics);
        } else {
            renderMapLabels(graphics);
        }
    }

    private void renderHomeLabels(GuiGraphics graphics) {
        graphics.drawCenteredString(font, Component.literal("SIGNAL WORKS MUNICIPAL FIELD UNIT"), imageWidth / 2, 79, PANEL_GREEN);
        graphics.drawCenteredString(font, Component.literal("SELECT AN APPLICATION"), imageWidth / 2, 92, TEXT_DIM);
        graphics.drawCenteredString(font, Component.literal("timing, diagnostics, fail-safe"), 140, 155, TEXT_NORMAL);
        graphics.drawCenteredString(font, Component.literal("links, movements, detectors"), 300, 155, TEXT_NORMAL);
        graphics.drawCenteredString(font, Component.literal("CABINET ONLINE  //  TAB CYCLES APPS  //  ESC CLOSES LAPTOP"), imageWidth / 2, 190, PANEL_BLUE);
        graphics.drawString(
                font,
                Component.literal("LINKS " + menu.linkedSignalCount() + "   DETECTORS " + menu.detectorCount()),
                102,
                222,
                PANEL_AMBER,
                false
        );
    }

    private void renderTerminalLabels(GuiGraphics graphics) {
        graphics.drawString(font, Component.literal("SYSTEM LOG"), 13, 66, PANEL_AMBER, false);
        int logY = 79;
        for (String line : terminalLog) {
            int color = line.startsWith("ERR") ? PANEL_RED
                    : line.startsWith("OK") ? PANEL_GREEN
                    : line.startsWith(">") ? PANEL_BLUE
                    : TEXT_NORMAL;
            graphics.drawString(font, Component.literal(line), 13, logY, color, false);
            logY += 14;
        }

        graphics.drawString(font, Component.literal("INTERSECTION PROFILE"), 278, 66, PANEL_AMBER, false);
        drawSetting(graphics, "THROUGH GREEN", String.format(Locale.ROOT, "%.1fs", menu.throughGreenTicks() / 20.0F), 278, 80, PANEL_GREEN);
        drawSetting(graphics, "TURN PRIORITY", priorityName(menu.priorityMode()).toUpperCase(Locale.ROOT), 278, 111, PANEL_AMBER);
        drawSetting(graphics, "NIGHT FLASH", onOff(menu.nightFlashEnabled()), 278, 142, menu.nightFlashEnabled() ? PANEL_GREEN : TEXT_DIM);
        drawSetting(graphics, "PHASE SPEED", speedName(menu.speedMode()).toUpperCase(Locale.ROOT), 278, 173, PANEL_BLUE);
        drawSetting(graphics, "FAIL-SAFE", onOff(menu.failSafeEnabled()), 278, 204, menu.failSafeEnabled() ? PANEL_RED : PANEL_GREEN);
        graphics.drawString(font, Component.literal(">"), 14, 246, PANEL_GREEN, false);
    }

    private void renderMapLabels(GuiGraphics graphics) {
        MapTransform transform = isMapFocused() ? focusTransform() : overviewTransform();
        drawMapGrid(graphics, transform);

        if (isMapFocused()) {
            for (MapNode node : focusedMapNodes()) {
                drawSignalNode(graphics, node);
            }
        } else {
            for (MapCluster cluster : overviewClusters()) {
                drawOverviewCluster(graphics, cluster);
            }
        }

        graphics.drawString(font, Component.literal("N"), 164, 70, PANEL_GREEN, false);
        graphics.drawString(font, Component.literal("S"), 164, 210, PANEL_GREEN, false);
        graphics.drawString(font, Component.literal("W"), 22, 140, PANEL_GREEN, false);
        graphics.drawString(font, Component.literal("E"), 302, 140, PANEL_GREEN, false);

        graphics.drawString(font, Component.literal("NODE CAMERA // LIVE"), INSPECTOR_X, 68, PANEL_AMBER, false);
        if (selectedLink < 0 || selectedLink >= menu.mapLinkCount()) {
            graphics.fill(INSPECTOR_X, 82, 426, 148, 0xFF020806);
            graphics.drawCenteredString(font, Component.literal(isMapFocused() ? "SELECT SIGNAL" : "SELECT NODE"), 376, 109, TEXT_DIM);
        } else {
            int type = menu.linkType(selectedLink);
            graphics.fill(INSPECTOR_X, 82, 426, 148, 0xFF020806);
            graphics.fill(INSPECTOR_X + 2, 84, 424, 86, GRID_LINE);
            renderSignalCamera(graphics, type);
            graphics.drawString(font, Component.literal("L" + (selectedLink + 1) + " " + typeName(type)), INSPECTOR_X + 3, 137, TEXT_NORMAL, false);
        }

        graphics.drawString(font, Component.literal("APPROACH PRIORITY"), INSPECTOR_X, 171, PANEL_AMBER, false);

        if (menu.linkedSignalCount() > TrafficLightControllerBlockEntity.MAX_MAP_LINKS) {
            graphics.drawString(
                    font,
                    Component.literal("Showing first " + TrafficLightControllerBlockEntity.MAX_MAP_LINKS + " links"),
                    18,
                    224,
                    PANEL_AMBER,
                    false
            );
        }
    }

    private void renderSignalCamera(GuiGraphics graphics, int type) {
        ItemStack stack = signalItemForType(type);
        graphics.pose().pushPose();
        graphics.pose().translate(355.0F, 91.0F, 0.0F);
        graphics.pose().scale(2.2F, 2.2F, 1.0F);
        graphics.renderItem(stack, 0, 0);
        graphics.pose().popPose();
    }

    private static ItemStack signalItemForType(int type) {
        return switch (type) {
            case TrafficLightControllerBlockEntity.MAP_TYPE_FOUR -> ModItems.TRAFFIC_LIGHT_4.get().getDefaultInstance();
            case TrafficLightControllerBlockEntity.MAP_TYPE_FIVE -> ModItems.TRAFFIC_LIGHT_5.get().getDefaultInstance();
            case TrafficLightControllerBlockEntity.MAP_TYPE_FIVE_RIGHT -> ModItems.TRAFFIC_LIGHT_5_RIGHT.get().getDefaultInstance();
            case TrafficLightControllerBlockEntity.MAP_TYPE_DOGHOUSE -> ModItems.TRAFFIC_LIGHT_DOGHOUSE.get().getDefaultInstance();
            case TrafficLightControllerBlockEntity.MAP_TYPE_LEFT -> ModItems.TRAFFIC_LIGHT_TURN_LEFT.get().getDefaultInstance();
            case TrafficLightControllerBlockEntity.MAP_TYPE_RIGHT -> ModItems.TRAFFIC_LIGHT_TURN_RIGHT.get().getDefaultInstance();
            case TrafficLightControllerBlockEntity.MAP_TYPE_PEDESTRIAN -> ModItems.TRAFFIC_LIGHT_2.get().getDefaultInstance();
            case TrafficLightControllerBlockEntity.MAP_TYPE_RAMP_METER -> ModItems.RAMP_METER_SIGNAL.get().getDefaultInstance();
            case TrafficLightControllerBlockEntity.MAP_TYPE_STRAIGHT_ARROW -> ModItems.TRAFFIC_LIGHT_STRAIGHT_ARROW.get().getDefaultInstance();
            case TrafficLightControllerBlockEntity.MAP_TYPE_U_TURN -> ModItems.TRAFFIC_LIGHT_U_TURN.get().getDefaultInstance();
            case TrafficLightControllerBlockEntity.MAP_TYPE_BUS -> ModItems.TRAFFIC_LIGHT_BUS.get().getDefaultInstance();
            default -> ModItems.TRAFFIC_LIGHT.get().getDefaultInstance();
        };
    }

    private void drawSignalNode(GuiGraphics graphics, MapNode node) {
        int color = movementColor(menu.linkMovement(node.linkIndex()));
        graphics.fill(node.x() - 7, node.y() - 7, node.x() + 8, node.y() + 8, 0xFF020806);
        graphics.fill(node.x() - 5, node.y() - 5, node.x() + 6, node.y() + 6, color);
        if (selectedLink == node.linkIndex()) {
            drawSelectionFrame(graphics, node.x(), node.y(), 9);
        }
        graphics.drawCenteredString(
                font,
                Component.literal(typeCode(menu.linkType(node.linkIndex()))),
                node.x(),
                node.y() - 4,
                0xFF07100C
        );
    }

    private void drawOverviewCluster(GuiGraphics graphics, MapCluster cluster) {
        if (cluster.links().size() == 1) {
            drawSignalNode(graphics, new MapNode(cluster.links().getFirst(), cluster.x(), cluster.y()));
            return;
        }

        int color = clusterColor(cluster.links());
        graphics.fill(cluster.x() - 10, cluster.y() - 10, cluster.x() + 11, cluster.y() + 11, 0xFF020806);
        graphics.fill(cluster.x() - 8, cluster.y() - 8, cluster.x() + 9, cluster.y() + 9, color);
        drawSelectionFrame(graphics, cluster.x(), cluster.y(), 11);
        graphics.drawCenteredString(
                font,
                Component.literal(Integer.toString(cluster.links().size())),
                cluster.x(),
                cluster.y() - 4,
                0xFF07100C
        );
    }

    private void drawSelectionFrame(GuiGraphics graphics, int centerX, int centerY, int radius) {
        int left = centerX - radius;
        int top = centerY - radius;
        int right = centerX + radius + 1;
        int bottom = centerY + radius + 1;
        graphics.fill(left, top, right, top + 1, 0xFFFFFFFF);
        graphics.fill(left, bottom - 1, right, bottom, 0xFFFFFFFF);
        graphics.fill(left, top, left + 1, bottom, 0xFFFFFFFF);
        graphics.fill(right - 1, top, right, bottom, 0xFFFFFFFF);
    }

    private int clusterColor(List<Integer> links) {
        SignalMovement first = menu.linkMovement(links.getFirst());
        for (int link : links) {
            if (menu.linkMovement(link) != first) {
                return PANEL_GREEN;
            }
        }
        return movementColor(first);
    }

    private void drawMapGrid(GuiGraphics graphics, MapTransform transform) {
        int centerX = (MAP_LEFT + MAP_RIGHT) / 2;
        int centerY = (MAP_TOP + MAP_BOTTOM) / 2;
        for (int x = MAP_LEFT + 12; x < MAP_RIGHT; x += 16) {
            graphics.fill(x, MAP_TOP, x + 1, MAP_BOTTOM, GRID_LINE);
        }
        for (int y = MAP_TOP + 10; y < MAP_BOTTOM; y += 16) {
            graphics.fill(MAP_LEFT, y, MAP_RIGHT, y + 1, GRID_LINE);
        }

        graphics.fill(MAP_LEFT, centerY - 12, MAP_RIGHT, centerY + 13, 0xFF17251F);
        graphics.fill(centerX - 12, MAP_TOP, centerX + 13, MAP_BOTTOM, 0xFF17251F);
        graphics.fill(MAP_LEFT, centerY, MAP_RIGHT, centerY + 1, 0xFF506052);
        graphics.fill(centerX, MAP_TOP, centerX + 1, MAP_BOTTOM, 0xFF506052);

        int controllerX = transform.projectX(0);
        int controllerY = transform.projectY(0);
        if (controllerX >= MAP_LEFT + 5 && controllerX <= MAP_RIGHT - 5
                && controllerY >= MAP_TOP + 5 && controllerY <= MAP_BOTTOM - 5) {
            graphics.fill(controllerX - 4, controllerY - 4, controllerX + 5, controllerY + 5, PANEL_GREEN);
            graphics.drawCenteredString(font, Component.literal("C"), controllerX, controllerY - 4, 0xFF07100C);
        }

        String viewName = isMapFocused() ? "FOCUS" : "OVERVIEW";
        graphics.drawString(font, Component.literal("VIEW: " + viewName), MAP_LEFT + 4, MAP_TOP + 4, TEXT_DIM, false);
    }

    private List<MapCluster> overviewClusters() {
        List<Integer> allLinks = allMapLinks();
        if (allLinks.isEmpty()) {
            return List.of();
        }

        MapTransform transform = overviewTransform();
        List<MapNode> projected = projectLinks(allLinks, transform);
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (MapNode node : projected) {
            minX = Math.min(minX, node.x());
            maxX = Math.max(maxX, node.x());
            minY = Math.min(minY, node.y());
            maxY = Math.max(maxY, node.y());
        }

        // If the controller is far from a tightly packed intersection, show one group node.
        if (projected.size() > 1
                && transform.scale() <= 8.5D
                && maxX - minX <= 140
                && maxY - minY <= 110) {
            return List.of(clusterFromNodes(projected));
        }

        int clusterDistance = transform.scale() <= 9.0D ? OVERVIEW_CLUSTER_DISTANCE : 16;
        int clusterDistanceSquared = clusterDistance * clusterDistance;
        boolean[] visited = new boolean[projected.size()];
        List<MapCluster> clusters = new ArrayList<>();

        for (int start = 0; start < projected.size(); start++) {
            if (visited[start]) {
                continue;
            }

            List<MapNode> clusterNodes = new ArrayList<>();
            ArrayDeque<Integer> queue = new ArrayDeque<>();
            queue.add(start);
            visited[start] = true;

            while (!queue.isEmpty()) {
                int current = queue.removeFirst();
                MapNode currentNode = projected.get(current);
                clusterNodes.add(currentNode);

                for (int candidate = 0; candidate < projected.size(); candidate++) {
                    if (visited[candidate]) {
                        continue;
                    }
                    MapNode candidateNode = projected.get(candidate);
                    int dx = candidateNode.x() - currentNode.x();
                    int dy = candidateNode.y() - currentNode.y();
                    if (dx * dx + dy * dy <= clusterDistanceSquared) {
                        visited[candidate] = true;
                        queue.addLast(candidate);
                    }
                }
            }

            clusters.add(clusterFromNodes(clusterNodes));
        }

        return clusters;
    }

    private MapCluster clusterFromNodes(List<MapNode> nodes) {
        int x = 0;
        int y = 0;
        List<Integer> links = new ArrayList<>(nodes.size());
        for (MapNode node : nodes) {
            x += node.x();
            y += node.y();
            links.add(node.linkIndex());
        }
        x /= nodes.size();
        y /= nodes.size();
        x = Mth.clamp(x, MAP_LEFT + CLUSTER_HIT_RADIUS, MAP_RIGHT - CLUSTER_HIT_RADIUS);
        y = Mth.clamp(y, MAP_TOP + CLUSTER_HIT_RADIUS, MAP_BOTTOM - CLUSTER_HIT_RADIUS);
        return new MapCluster(List.copyOf(links), x, y);
    }

    private List<MapNode> focusedMapNodes() {
        if (!isMapFocused()) {
            return List.of();
        }
        return spreadNodes(projectLinks(focusedLinks, focusTransform()));
    }

    private List<MapNode> projectLinks(List<Integer> links, MapTransform transform) {
        List<MapNode> nodes = new ArrayList<>(links.size());
        for (int link : links) {
            if (link < 0 || link >= menu.mapLinkCount()) {
                continue;
            }
            nodes.add(new MapNode(
                    link,
                    transform.projectX(menu.linkDx(link)),
                    transform.projectY(menu.linkDz(link))
            ));
        }
        return nodes;
    }

    private List<MapNode> spreadNodes(List<MapNode> rawNodes) {
        int[][] offsets = {
                {0, 0},
                {FOCUS_NODE_SPACING, 0},
                {-FOCUS_NODE_SPACING, 0},
                {0, FOCUS_NODE_SPACING},
                {0, -FOCUS_NODE_SPACING},
                {FOCUS_NODE_SPACING, FOCUS_NODE_SPACING},
                {-FOCUS_NODE_SPACING, FOCUS_NODE_SPACING},
                {FOCUS_NODE_SPACING, -FOCUS_NODE_SPACING},
                {-FOCUS_NODE_SPACING, -FOCUS_NODE_SPACING},
                {FOCUS_NODE_SPACING * 2, 0},
                {-FOCUS_NODE_SPACING * 2, 0},
                {0, FOCUS_NODE_SPACING * 2},
                {0, -FOCUS_NODE_SPACING * 2},
                {FOCUS_NODE_SPACING * 2, FOCUS_NODE_SPACING},
                {-FOCUS_NODE_SPACING * 2, FOCUS_NODE_SPACING},
                {FOCUS_NODE_SPACING * 2, -FOCUS_NODE_SPACING},
                {-FOCUS_NODE_SPACING * 2, -FOCUS_NODE_SPACING},
                {FOCUS_NODE_SPACING, FOCUS_NODE_SPACING * 2},
                {-FOCUS_NODE_SPACING, FOCUS_NODE_SPACING * 2},
                {FOCUS_NODE_SPACING, -FOCUS_NODE_SPACING * 2},
                {-FOCUS_NODE_SPACING, -FOCUS_NODE_SPACING * 2}
        };

        List<MapNode> placed = new ArrayList<>(rawNodes.size());
        for (MapNode raw : rawNodes) {
            MapNode chosen = raw;
            for (int[] offset : offsets) {
                int x = Mth.clamp(raw.x() + offset[0], MAP_LEFT + NODE_HIT_RADIUS, MAP_RIGHT - NODE_HIT_RADIUS);
                int y = Mth.clamp(raw.y() + offset[1], MAP_TOP + NODE_HIT_RADIUS, MAP_BOTTOM - NODE_HIT_RADIUS);
                MapNode candidate = new MapNode(raw.linkIndex(), x, y);
                if (!overlapsPlacedNode(candidate, placed)) {
                    chosen = candidate;
                    break;
                }
            }
            placed.add(chosen);
        }
        return placed;
    }

    private boolean overlapsPlacedNode(MapNode candidate, List<MapNode> placed) {
        int minimumDistanceSquared = FOCUS_NODE_SPACING * FOCUS_NODE_SPACING;
        for (MapNode existing : placed) {
            int dx = candidate.x() - existing.x();
            int dy = candidate.y() - existing.y();
            if (dx * dx + dy * dy < minimumDistanceSquared) {
                return true;
            }
        }
        return false;
    }

    private List<Integer> allMapLinks() {
        List<Integer> links = new ArrayList<>(menu.mapLinkCount());
        for (int index = 0; index < menu.mapLinkCount(); index++) {
            links.add(index);
        }
        return links;
    }

    private MapTransform overviewTransform() {
        int maxRangeX = 4;
        int maxRangeZ = 4;
        for (int index = 0; index < menu.mapLinkCount(); index++) {
            maxRangeX = Math.max(maxRangeX, Math.abs(menu.linkDx(index)) + 1);
            maxRangeZ = Math.max(maxRangeZ, Math.abs(menu.linkDz(index)) + 1);
        }

        double mapWidth = MAP_RIGHT - MAP_LEFT - 28.0D;
        double mapHeight = MAP_BOTTOM - MAP_TOP - 28.0D;
        double scale = Math.max(
                3.0D,
                Math.min(14.0D, Math.min(mapWidth / (maxRangeX * 2.0D), mapHeight / (maxRangeZ * 2.0D)))
        );
        return new MapTransform(0.0D, 0.0D, scale);
    }

    private MapTransform focusTransform() {
        if (!isMapFocused()) {
            return overviewTransform();
        }

        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (int link : focusedLinks) {
            if (link < 0 || link >= menu.mapLinkCount()) {
                continue;
            }
            minX = Math.min(minX, menu.linkDx(link));
            maxX = Math.max(maxX, menu.linkDx(link));
            minZ = Math.min(minZ, menu.linkDz(link));
            maxZ = Math.max(maxZ, menu.linkDz(link));
        }

        if (minX == Integer.MAX_VALUE) {
            return overviewTransform();
        }

        double spanX = Math.max(1.0D, maxX - minX);
        double spanZ = Math.max(1.0D, maxZ - minZ);
        double mapWidth = MAP_RIGHT - MAP_LEFT - 36.0D;
        double mapHeight = MAP_BOTTOM - MAP_TOP - 36.0D;
        double scale = Math.max(
                7.0D,
                Math.min(24.0D, Math.min(mapWidth / (spanX + 3.0D), mapHeight / (spanZ + 3.0D)))
        );
        return new MapTransform((minX + maxX) / 2.0D, (minZ + maxZ) / 2.0D, scale);
    }

    private void drawLegend(GuiGraphics graphics, String label, int color, int x, int y) {
        graphics.fill(x, y + 1, x + 7, y + 8, color);
        graphics.drawString(font, Component.literal(label), x + 10, y, TEXT_NORMAL, false);
    }

    private void drawSetting(GuiGraphics graphics, String label, String value, int x, int y, int valueColor) {
        graphics.drawString(font, Component.literal(label), x, y, 0xFFC2D0C7, false);
        graphics.drawString(font, Component.literal(value), x, y + 11, valueColor, false);
    }

    private static int movementColor(SignalMovement movement) {
        return switch (movement) {
            case THROUGH -> PANEL_BLUE;
            case LEFT -> PANEL_GREEN;
            case RIGHT -> PANEL_AMBER;
            case THROUGH_LEFT -> PANEL_PURPLE;
            case THROUGH_RIGHT -> 0xFF53D8C9;
            case PEDESTRIAN -> 0xFFF0F0F0;
            case U_TURN -> 0xFFFF7A55;
            case BUS -> 0xFFFFFFFF;
        };
    }

    private static String movementName(SignalMovement movement) {
        return switch (movement) {
            case THROUGH -> "THROUGH";
            case LEFT -> "PROTECTED LEFT";
            case RIGHT -> "PROTECTED RIGHT";
            case THROUGH_LEFT -> "THROUGH + LEFT";
            case THROUGH_RIGHT -> "THROUGH + RIGHT";
            case PEDESTRIAN -> "PEDESTRIAN";
            case U_TURN -> "U-TURN";
            case BUS -> "BUS";
        };
    }

    private static String typeCode(int type) {
        return switch (type) {
            case TrafficLightControllerBlockEntity.MAP_TYPE_FOUR -> "4";
            case TrafficLightControllerBlockEntity.MAP_TYPE_FIVE -> "5L";
            case TrafficLightControllerBlockEntity.MAP_TYPE_FIVE_RIGHT -> "5R";
            case TrafficLightControllerBlockEntity.MAP_TYPE_DOGHOUSE -> "D";
            case TrafficLightControllerBlockEntity.MAP_TYPE_LEFT -> "L";
            case TrafficLightControllerBlockEntity.MAP_TYPE_RIGHT -> "R";
            case TrafficLightControllerBlockEntity.MAP_TYPE_PEDESTRIAN -> "P";
            case TrafficLightControllerBlockEntity.MAP_TYPE_RAMP_METER -> "RM";
            case TrafficLightControllerBlockEntity.MAP_TYPE_STRAIGHT_ARROW -> "SA";
            case TrafficLightControllerBlockEntity.MAP_TYPE_U_TURN -> "U";
            case TrafficLightControllerBlockEntity.MAP_TYPE_BUS -> "BUS";
            default -> "3";
        };
    }

    private static String typeName(int type) {
        return switch (type) {
            case TrafficLightControllerBlockEntity.MAP_TYPE_FOUR -> "4-SECTION";
            case TrafficLightControllerBlockEntity.MAP_TYPE_FIVE -> "5-SECTION LEFT";
            case TrafficLightControllerBlockEntity.MAP_TYPE_FIVE_RIGHT -> "5-SECTION RIGHT";
            case TrafficLightControllerBlockEntity.MAP_TYPE_DOGHOUSE -> "DOGHOUSE";
            case TrafficLightControllerBlockEntity.MAP_TYPE_LEFT -> "LEFT ARROW";
            case TrafficLightControllerBlockEntity.MAP_TYPE_RIGHT -> "RIGHT ARROW";
            case TrafficLightControllerBlockEntity.MAP_TYPE_PEDESTRIAN -> "PEDESTRIAN";
            case TrafficLightControllerBlockEntity.MAP_TYPE_RAMP_METER -> "RAMP METER";
            case TrafficLightControllerBlockEntity.MAP_TYPE_STRAIGHT_ARROW -> "STRAIGHT ARROW";
            case TrafficLightControllerBlockEntity.MAP_TYPE_U_TURN -> "U-TURN ARROW";
            case TrafficLightControllerBlockEntity.MAP_TYPE_BUS -> "BUS SIGNAL";
            default -> "3-SECTION";
        };
    }

    private static String formatOffset(int x, int y, int z) {
        return String.format(Locale.ROOT, "X%+d Y%+d Z%+d", x, y, z);
    }

    private static String approachPriorityCode(int value) {
        return switch (value) {
            case TrafficLightControllerBlockEntity.APPROACH_LESS -> "LESS";
            case TrafficLightControllerBlockEntity.APPROACH_PRIORITY -> "PRI";
            default -> "NORM";
        };
    }

    private static String priorityName(int priority) {
        return switch (priority) {
            case TrafficLightControllerBlockEntity.PRIORITY_RIGHT -> "right";
            case TrafficLightControllerBlockEntity.PRIORITY_BOTH -> "both";
            default -> "left";
        };
    }

    private static String speedName(int speed) {
        return switch (speed) {
            case 0 -> "slow";
            case 2 -> "fast";
            default -> "normal";
        };
    }

    private static String demandName(int mask) {
        return switch (mask & 7) {
            case 0 -> "none";
            case 1 -> "through";
            case 2 -> "turn";
            case 3 -> "through+turn";
            case 4 -> "bus";
            case 5 -> "through+bus";
            case 6 -> "turn+bus";
            default -> "through+turn+bus";
        };
    }

    private static String onOff(boolean enabled) {
        return enabled ? "ON" : "OFF";
    }

    private record MapNode(int linkIndex, int x, int y) {
    }

    private record MapCluster(List<Integer> links, int x, int y) {
    }

    private record MapTransform(double centerWorldX, double centerWorldZ, double scale) {
        private int projectX(double worldX) {
            int centerX = (MAP_LEFT + MAP_RIGHT) / 2;
            return centerX + Mth.floor((worldX - centerWorldX) * scale + 0.5D);
        }

        private int projectY(double worldZ) {
            int centerY = (MAP_TOP + MAP_BOTTOM) / 2;
            return centerY + Mth.floor((worldZ - centerWorldZ) * scale + 0.5D);
        }
    }
}
