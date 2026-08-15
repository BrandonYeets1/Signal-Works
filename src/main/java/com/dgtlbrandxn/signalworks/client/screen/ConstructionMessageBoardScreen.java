package com.dgtlbrandxn.signalworks.client.screen;

import com.dgtlbrandxn.signalworks.block.ConstructionMessageMode;
import com.dgtlbrandxn.signalworks.blockentity.ConstructionMessageBoardBlockEntity;
import com.dgtlbrandxn.signalworks.menu.ConstructionMessageBoardMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.Locale;

/** Field editor for portable amber LED message boards. */
public final class ConstructionMessageBoardScreen extends AbstractContainerScreen<ConstructionMessageBoardMenu> {
    private static final int AMBER = 0xFFFFB321;
    private static final int AMBER_DIM = 0xFF7A5416;
    private static final int GREEN = 0xFF6CFF8A;
    private static final int TEXT_DIM = 0xFF82918A;
    private static final int DARK = 0xFF070A08;

    private EditBox line1;
    private EditBox line2;
    private EditBox line3;
    private Button modeButton;
    private Button powerButton;
    private Button deployButton;
    private boolean initialTextLoaded;
    private boolean suppressResponder;
    private String status = "LOADING BOARD DATA...";

    public ConstructionMessageBoardScreen(
            ConstructionMessageBoardMenu menu,
            Inventory inventory,
            Component title
    ) {
        super(menu, inventory, title);
        imageWidth = 430;
        imageHeight = 248;
        inventoryLabelY = 10_000;
    }

    @Override
    protected void init() {
        imageWidth = 430;
        imageHeight = 248;
        super.init();

        line1 = addLine(18, 127, ConstructionMessageBoardBlockEntity.FIELD_LINE_1);
        line2 = addLine(18, 153, ConstructionMessageBoardBlockEntity.FIELD_LINE_2);
        line3 = addLine(18, 179, ConstructionMessageBoardBlockEntity.FIELD_LINE_3);

        addRenderableWidget(Button.builder(Component.literal("<"),
                        button -> send(ConstructionMessageBoardMenu.BUTTON_MODE_PREV))
                .bounds(leftPos + 252, topPos + 127, 20, 18).build());
        modeButton = addRenderableWidget(Button.builder(Component.literal("MODE"),
                        button -> send(ConstructionMessageBoardMenu.BUTTON_MODE_NEXT))
                .bounds(leftPos + 276, topPos + 127, 112, 18).build());
        addRenderableWidget(Button.builder(Component.literal(">"),
                        button -> send(ConstructionMessageBoardMenu.BUTTON_MODE_NEXT))
                .bounds(leftPos + 392, topPos + 127, 20, 18).build());

        powerButton = addRenderableWidget(Button.builder(Component.literal("POWER"),
                        button -> send(ConstructionMessageBoardMenu.BUTTON_POWER))
                .bounds(leftPos + 252, topPos + 157, 76, 18).build());
        deployButton = addRenderableWidget(Button.builder(Component.literal("DEPLOY"),
                        button -> send(ConstructionMessageBoardMenu.BUTTON_DEPLOY))
                .bounds(leftPos + 336, topPos + 157, 76, 18).build());

        addRenderableWidget(Button.builder(Component.literal("APPLY MESSAGE"), button -> saveText())
                .bounds(leftPos + 252, topPos + 187, 160, 18).build());

        setEditable(false);
        updateButtons();
        if (menu.textReady()) {
            loadInitialText();
        }
        setInitialFocus(line1);
    }

    private EditBox addLine(int x, int y, int field) {
        EditBox box = new EditBox(font, leftPos + x, topPos + y, 210, 18,
                Component.literal("message board line"));
        box.setMaxLength(ConstructionMessageBoardBlockEntity.MAX_TEXT_LENGTH);
        box.setTextColor(AMBER);
        box.setTextColorUneditable(TEXT_DIM);
        box.setResponder(value -> {
            if (!suppressResponder) {
                status = "UNSAVED MESSAGE // ENTER OR APPLY";
            }
        });
        return addRenderableWidget(box);
    }

    private void setEditable(boolean editable) {
        if (line1 != null) line1.setEditable(editable);
        if (line2 != null) line2.setEditable(editable);
        if (line3 != null) line3.setEditable(editable);
    }

    private void loadInitialText() {
        if (initialTextLoaded) {
            return;
        }
        suppressResponder = true;
        line1.setValue(menu.text(ConstructionMessageBoardBlockEntity.FIELD_LINE_1));
        line2.setValue(menu.text(ConstructionMessageBoardBlockEntity.FIELD_LINE_2));
        line3.setValue(menu.text(ConstructionMessageBoardBlockEntity.FIELD_LINE_3));
        suppressResponder = false;
        initialTextLoaded = true;
        setEditable(true);
        status = "READY // ENTER APPLIES // ESC CLOSES";
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        updateButtons();
        if (!initialTextLoaded && menu.textReady()) {
            loadInitialText();
        }
    }

    private void updateButtons() {
        if (modeButton != null) {
            modeButton.setMessage(Component.literal(menu.mode().displayName().toUpperCase(Locale.ROOT)));
        }
        if (powerButton != null) {
            powerButton.setMessage(Component.literal("POWER: " + (menu.lit() ? "ON" : "OFF")));
        }
        if (deployButton != null) {
            deployButton.setMessage(Component.literal(menu.deployed() ? "STOW" : "DEPLOY"));
        }
    }

    private void saveText() {
        if (!initialTextLoaded) {
            status = "WAITING FOR SERVER DATA";
            return;
        }
        saveField(ConstructionMessageBoardBlockEntity.FIELD_LINE_1, line1.getValue());
        saveField(ConstructionMessageBoardBlockEntity.FIELD_LINE_2, line2.getValue());
        saveField(ConstructionMessageBoardBlockEntity.FIELD_LINE_3, line3.getValue());
        status = "OK // MESSAGE APPLIED";
    }

    private void saveField(int field, String rawValue) {
        String value = sanitize(rawValue);
        send(ConstructionMessageBoardMenu.BUTTON_CLEAR_TEXT_BASE + field);
        for (int offset = 0; offset < value.length(); offset += 3) {
            int first = value.charAt(offset);
            int second = offset + 1 < value.length() ? value.charAt(offset + 1) : 0;
            int third = offset + 2 < value.length() ? value.charAt(offset + 2) : 0;
            send(ConstructionMessageBoardMenu.packTextChunk(field, offset / 3, first, second, third));
        }
        send(ConstructionMessageBoardMenu.BUTTON_COMMIT_TEXT_BASE + field);
    }

    private static String sanitize(String value) {
        StringBuilder clean = new StringBuilder();
        for (int index = 0; index < value.length()
                && clean.length() < ConstructionMessageBoardBlockEntity.MAX_TEXT_LENGTH; index++) {
            char character = value.charAt(index);
            if (character >= 32 && character <= 126) {
                clean.append(character);
            }
        }
        return clean.toString();
    }

    private void send(int buttonId) {
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, buttonId);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257 || keyCode == 335) {
            saveText();
            return true;
        }
        if (minecraft != null && (
                minecraft.options.keyInventory.matches(keyCode, scanCode)
                        || minecraft.options.keyChat.matches(keyCode, scanCode)
                        || minecraft.options.keyCommand.matches(keyCode, scanCode)
        )) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        saveText();
        super.onClose();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xF0060907);
        graphics.fill(leftPos + 3, topPos + 3, leftPos + imageWidth - 3, topPos + imageHeight - 3, 0xFF222A25);
        graphics.fill(leftPos + 7, topPos + 7, leftPos + imageWidth - 7, topPos + imageHeight - 7, DARK);
        panel(graphics, 12, 34, 406, 76);
        panel(graphics, 12, 116, 226, 104);
        panel(graphics, 244, 116, 174, 104);
        graphics.fill(leftPos + 12, topPos + 226, leftPos + imageWidth - 12, topPos + 239, 0xFF020403);
    }

    private void panel(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(leftPos + x, topPos + y, leftPos + x + width, topPos + y + height, 0xFF4B3814);
        graphics.fill(leftPos + x + 1, topPos + y + 1,
                leftPos + x + width - 1, topPos + y + height - 1, 0xFF0B0B08);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, Component.literal("SIGNAL WORKS CONSTRUCTION // PCMS"), 12, 12, AMBER, false);
        graphics.drawString(font, Component.literal("PORTABLE CHANGEABLE MESSAGE SIGN"), 247, 12, TEXT_DIM, false);
        graphics.drawString(font, Component.literal("LIVE LED PREVIEW"), 18, 38, TEXT_DIM, false);
        graphics.drawString(font, Component.literal("MESSAGE LINES"), 18, 119, AMBER, false);
        graphics.drawString(font, Component.literal("DISPLAY PROGRAM"), 252, 119, AMBER, false);
        graphics.drawString(font, Component.literal("> " + status), 16, 228,
                status.startsWith("OK") ? GREEN : AMBER, false);
        graphics.drawString(font, Component.literal("E T / SAFE"), 354, 228, TEXT_DIM, false);
        renderPreview(graphics);
    }

    private void renderPreview(GuiGraphics graphics) {
        int x0 = 43;
        int y0 = 49;
        int x1 = 387;
        int y1 = 101;
        graphics.fill(x0, y0, x1, y1, 0xFF2D2D29);
        graphics.fill(x0 + 4, y0 + 4, x1 - 4, y1 - 4, 0xFF020302);
        int color = menu.lit() ? AMBER : AMBER_DIM;
        ConstructionMessageMode mode = menu.mode();
        if (mode == ConstructionMessageMode.MESSAGE) {
            String[] lines = {
                    previewValue(line1, menu.text(0)),
                    previewValue(line2, menu.text(1)),
                    previewValue(line3, menu.text(2))
            };
            for (int row = 0; row < lines.length; row++) {
                String value = lines[row].toUpperCase(Locale.ROOT);
                int width = font.width(value);
                graphics.drawString(font, value, (x0 + x1 - width) / 2, y0 + 9 + row * 13, color, false);
            }
        } else {
            drawProgramPreview(graphics, mode, x0 + 18, y0 + 8, x1 - 18, y1 - 8, color);
        }
        if (!menu.deployed()) {
            graphics.fill(x0 + 4, y0 + 4, x1 - 4, y1 - 4, 0xAA000000);
            String stowed = "DISPLAY STOWED";
            graphics.drawString(font, stowed, (x0 + x1 - font.width(stowed)) / 2, y0 + 22, TEXT_DIM, false);
        }
    }

    private static String previewValue(EditBox box, String fallback) {
        return box == null ? fallback : box.getValue();
    }

    private static void drawProgramPreview(
            GuiGraphics graphics,
            ConstructionMessageMode mode,
            int x0,
            int y0,
            int x1,
            int y1,
            int color
    ) {
        int cy = (y0 + y1) / 2;
        boolean left = mode == ConstructionMessageMode.LEFT_ARROW || mode == ConstructionMessageMode.MERGE_LEFT;
        boolean right = mode == ConstructionMessageMode.RIGHT_ARROW || mode == ConstructionMessageMode.MERGE_RIGHT;
        if (mode == ConstructionMessageMode.CAUTION) {
            for (int i = 0; i < 3; i++) {
                int cx = x0 + 48 + i * 72;
                graphics.fill(cx - 3, y0 + 2, cx + 3, y1 - 2, color);
                graphics.fill(cx - 10, cy - 3, cx + 10, cy + 3, color);
            }
            return;
        }
        graphics.fill(x0 + 42, cy - 3, x1 - 42, cy + 3, color);
        if (left) {
            graphics.fill(x0 + 24, cy - 4, x0 + 48, cy + 4, color);
            for (int i = 0; i < 18; i += 3) {
                graphics.fill(x0 + 24 + i, cy - 18 + i, x0 + 30 + i, cy + 18 - i, color);
            }
        } else if (right) {
            graphics.fill(x1 - 48, cy - 4, x1 - 24, cy + 4, color);
            for (int i = 0; i < 18; i += 3) {
                graphics.fill(x1 - 30 - i, cy - 18 + i, x1 - 24 - i, cy + 18 - i, color);
            }
        }
        if (mode == ConstructionMessageMode.MERGE_LEFT || mode == ConstructionMessageMode.MERGE_RIGHT) {
            graphics.fill((x0 + x1) / 2 - 3, y0 + 4, (x0 + x1) / 2 + 3, cy + 2, color);
        }
    }
}
