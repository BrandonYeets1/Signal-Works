package com.dgtlbrandxn.signalworks.client.screen;

import com.dgtlbrandxn.signalworks.block.MunicipalSignFont;
import com.dgtlbrandxn.signalworks.block.MunicipalSignShape;
import com.dgtlbrandxn.signalworks.block.MunicipalSignStyle;
import com.dgtlbrandxn.signalworks.block.MunicipalTextSize;
import com.dgtlbrandxn.signalworks.blockentity.MunicipalStreetSignBlockEntity;
import com.dgtlbrandxn.signalworks.menu.MunicipalStreetSignMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.Locale;

/** Compact visual municipal-sign editor with live previews and keyboard-safe text entry. */
public final class MunicipalStreetSignScreen extends AbstractContainerScreen<MunicipalStreetSignMenu> {
    private static final int PANEL_GREEN = 0xFF65FF86;
    private static final int PANEL_AMBER = 0xFFFFCE55;
    private static final int PANEL_BLUE = 0xFF75C7FF;
    private static final int TEXT_DIM = 0xFF7E9187;
    private static final int PANEL_EDGE = 0xFF315E42;
    private static final int PANEL_DARK = 0xFF07100C;

    private EditBox primaryBox;
    private EditBox crossBox;
    private EditBox districtBox;
    private EditBox blockBox;
    private Button fontButton;
    private Button sizeButton;
    private Button shapeButton;
    private Button uppercaseButton;
    private Button reflectiveButton;
    private Button doubleSidedButton;
    private Button backlitButton;

    private boolean initialTextLoaded;
    private boolean suppressTextResponder;
    private String status = "LOADING SIGN DATA...";

    public MunicipalStreetSignScreen(MunicipalStreetSignMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 460;
        imageHeight = 258;
        inventoryLabelY = 10_000;
    }

    @Override
    protected void init() {
        // Keep the designer inside a 1920x1080 window even at GUI scale 4.
        imageWidth = 460;
        imageHeight = 258;
        super.init();

        primaryBox = addTextBox(16, 114, 196, MunicipalStreetSignBlockEntity.FIELD_PRIMARY, 24);
        crossBox = addTextBox(16, 142, 196, MunicipalStreetSignBlockEntity.FIELD_CROSS, 24);
        districtBox = addTextBox(16, 170, 196, MunicipalStreetSignBlockEntity.FIELD_DISTRICT, 18);
        blockBox = addTextBox(16, 198, 86, MunicipalStreetSignBlockEntity.FIELD_BLOCK, 8);

        addRenderableWidget(Button.builder(Component.literal("<"),
                        button -> send(MunicipalStreetSignMenu.BUTTON_FONT_PREV))
                .bounds(leftPos + 228, topPos + 114, 18, 16).build());
        fontButton = addRenderableWidget(Button.builder(Component.literal("FONT"),
                        button -> send(MunicipalStreetSignMenu.BUTTON_FONT_NEXT))
                .bounds(leftPos + 249, topPos + 114, 78, 16).build());
        addRenderableWidget(Button.builder(Component.literal(">"),
                        button -> send(MunicipalStreetSignMenu.BUTTON_FONT_NEXT))
                .bounds(leftPos + 330, topPos + 114, 18, 16).build());

        addRenderableWidget(Button.builder(Component.literal("<"),
                        button -> send(MunicipalStreetSignMenu.BUTTON_TEXT_SIZE_PREV))
                .bounds(leftPos + 228, topPos + 150, 18, 16).build());
        sizeButton = addRenderableWidget(Button.builder(Component.literal("SIZE"),
                        button -> send(MunicipalStreetSignMenu.BUTTON_TEXT_SIZE_NEXT))
                .bounds(leftPos + 249, topPos + 150, 78, 16).build());
        addRenderableWidget(Button.builder(Component.literal(">"),
                        button -> send(MunicipalStreetSignMenu.BUTTON_TEXT_SIZE_NEXT))
                .bounds(leftPos + 330, topPos + 150, 18, 16).build());

        uppercaseButton = addRenderableWidget(Button.builder(Component.literal("UPPER"),
                        button -> send(MunicipalStreetSignMenu.BUTTON_UPPERCASE))
                .bounds(leftPos + 228, topPos + 178, 58, 16).build());
        reflectiveButton = addRenderableWidget(Button.builder(Component.literal("REFLECT"),
                        button -> send(MunicipalStreetSignMenu.BUTTON_REFLECTIVE))
                .bounds(leftPos + 290, topPos + 178, 58, 16).build());
        backlitButton = addRenderableWidget(Button.builder(Component.literal("BACKLIT"),
                        button -> send(MunicipalStreetSignMenu.BUTTON_BACKLIT))
                .bounds(leftPos + 228, topPos + 198, 58, 16).build());
        doubleSidedButton = addRenderableWidget(Button.builder(Component.literal("2-SIDED"),
                        button -> send(MunicipalStreetSignMenu.BUTTON_DOUBLE_SIDED))
                .bounds(leftPos + 290, topPos + 198, 58, 16).build());

        MunicipalSignStyle[] styles = MunicipalSignStyle.values();
        for (int index = 0; index < styles.length; index++) {
            int column = index & 1;
            int row = index >> 1;
            String label = switch (styles[index]) {
                case LA_BLUE -> "BLUE";
                case CLASSIC_GREEN -> "GRN";
                case HISTORIC_BROWN -> "BRN";
                case BLACK_WHITE -> "B/W";
            };
            int styleIndex = index;
            addRenderableWidget(Button.builder(Component.literal(label),
                            button -> send(MunicipalStreetSignMenu.BUTTON_STYLE_SET_BASE + styleIndex))
                    .bounds(leftPos + 362 + column * 43, topPos + 114 + row * 22, 39, 18)
                    .build());
        }

        addRenderableWidget(Button.builder(Component.literal("<"),
                        button -> send(MunicipalStreetSignMenu.BUTTON_SHAPE_PREV))
                .bounds(leftPos + 362, topPos + 172, 18, 18).build());
        shapeButton = addRenderableWidget(Button.builder(Component.literal("SHAPE"),
                        button -> send(MunicipalStreetSignMenu.BUTTON_SHAPE_NEXT))
                .bounds(leftPos + 383, topPos + 172, 44, 18).build());
        addRenderableWidget(Button.builder(Component.literal(">"),
                        button -> send(MunicipalStreetSignMenu.BUTTON_SHAPE_NEXT))
                .bounds(leftPos + 430, topPos + 172, 18, 18).build());

        addRenderableWidget(Button.builder(Component.literal("APPLY"), button -> saveText())
                .bounds(leftPos + 108, topPos + 198, 104, 16).build());

        setBoxesEditable(false);
        updateButtonLabels();
        if (menu.textReady()) {
            loadInitialText();
        }
        setInitialFocus(primaryBox);
    }

    private EditBox addTextBox(int x, int y, int width, int field, int maxLength) {
        EditBox box = new EditBox(
                font,
                leftPos + x,
                topPos + y,
                width,
                16,
                Component.literal("municipal sign text")
        );
        box.setMaxLength(maxLength);
        box.setTextColor(PANEL_GREEN);
        box.setTextColorUneditable(TEXT_DIM);
        box.setResponder(value -> {
            if (!suppressTextResponder) {
                status = "UNSAVED TEXT // ENTER OR APPLY";
            }
        });
        return addRenderableWidget(box);
    }

    private void setBoxesEditable(boolean editable) {
        if (primaryBox != null) primaryBox.setEditable(editable);
        if (crossBox != null) crossBox.setEditable(editable);
        if (districtBox != null) districtBox.setEditable(editable);
        if (blockBox != null) blockBox.setEditable(editable);
    }

    private void loadInitialText() {
        if (initialTextLoaded) {
            return;
        }
        suppressTextResponder = true;
        primaryBox.setValue(menu.text(MunicipalStreetSignBlockEntity.FIELD_PRIMARY));
        crossBox.setValue(menu.text(MunicipalStreetSignBlockEntity.FIELD_CROSS));
        districtBox.setValue(menu.text(MunicipalStreetSignBlockEntity.FIELD_DISTRICT));
        blockBox.setValue(menu.text(MunicipalStreetSignBlockEntity.FIELD_BLOCK));
        suppressTextResponder = false;
        initialTextLoaded = true;
        setBoxesEditable(true);
        status = "READY // ENTER APPLIES // ESC CLOSES";
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        updateButtonLabels();
        if (!initialTextLoaded && menu.textReady()) {
            loadInitialText();
        }
    }

    private void updateButtonLabels() {
        if (fontButton != null) {
            fontButton.setMessage(Component.literal(menu.fontProfile().displayName()));
        }
        if (sizeButton != null) {
            sizeButton.setMessage(Component.literal(menu.textSize().displayName()));
        }
        if (shapeButton != null) {
            shapeButton.setMessage(Component.literal(shortShape(menu.shape())));
        }
        if (uppercaseButton != null) {
            uppercaseButton.setMessage(Component.literal("UPPER: " + onOff(menu.uppercase())));
        }
        if (reflectiveButton != null) {
            reflectiveButton.setMessage(Component.literal("RFL: " + onOff(menu.reflective())));
        }
        if (backlitButton != null) {
            backlitButton.setMessage(Component.literal("LIT: " + onOff(menu.backlit())));
        }
        if (doubleSidedButton != null) {
            doubleSidedButton.setMessage(Component.literal("2S: " + onOff(menu.doubleSided())));
        }
    }

    private static String shortShape(MunicipalSignShape shape) {
        return switch (shape) {
            case RECTANGLE -> "RECT";
            case ROUNDED -> "ROUND";
            case CLIPPED -> "CLIP";
        };
    }

    private static String onOff(boolean enabled) {
        return enabled ? "ON" : "OFF";
    }

    private void saveText() {
        if (!initialTextLoaded) {
            status = "WAITING FOR SIGN DATA...";
            return;
        }
        sendText(MunicipalStreetSignBlockEntity.FIELD_PRIMARY, primaryBox.getValue());
        sendText(MunicipalStreetSignBlockEntity.FIELD_CROSS, crossBox.getValue());
        sendText(MunicipalStreetSignBlockEntity.FIELD_DISTRICT, districtBox.getValue());
        sendText(MunicipalStreetSignBlockEntity.FIELD_BLOCK, blockBox.getValue());
        status = "OK // SIGN TEXT APPLIED";
    }

    private void sendText(int field, String value) {
        String sanitized = sanitize(value);
        send(MunicipalStreetSignMenu.BUTTON_CLEAR_TEXT_BASE + field);
        for (int offset = 0, chunk = 0; offset < sanitized.length(); offset += 3, chunk++) {
            int first = sanitized.charAt(offset);
            int second = offset + 1 < sanitized.length() ? sanitized.charAt(offset + 1) : 0;
            int third = offset + 2 < sanitized.length() ? sanitized.charAt(offset + 2) : 0;
            send(MunicipalStreetSignMenu.packTextChunk(field, chunk, first, second, third));
        }
        send(MunicipalStreetSignMenu.BUTTON_COMMIT_TEXT_BASE + field);
    }

    private static String sanitize(String value) {
        StringBuilder clean = new StringBuilder();
        if (value == null) {
            return "";
        }
        for (int index = 0; index < value.length()
                && clean.length() < MunicipalStreetSignBlockEntity.MAX_TEXT_LENGTH; index++) {
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
            if (buttonId < MunicipalStreetSignMenu.BUTTON_CLEAR_TEXT_BASE) {
                status = "LIVE APPEARANCE UPDATED";
            }
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
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xF00A0E10);
        graphics.fill(leftPos + 2, topPos + 2, leftPos + imageWidth - 2, topPos + imageHeight - 2, 0xFF253039);
        graphics.fill(leftPos + 6, topPos + 6, leftPos + imageWidth - 6, topPos + imageHeight - 6, PANEL_DARK);
        graphics.fill(leftPos + 10, topPos + 30, leftPos + imageWidth - 10, topPos + 31, PANEL_EDGE);

        panel(graphics, 10, 36, 440, 58);
        panel(graphics, 10, 100, 208, 132);
        panel(graphics, 222, 100, 130, 132);
        panel(graphics, 356, 100, 94, 132);
        graphics.fill(leftPos + 10, topPos + 238, leftPos + imageWidth - 10, topPos + 250, 0xFF020806);
    }

    private void panel(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(leftPos + x, topPos + y, leftPos + x + width, topPos + y + height, 0xFF173D28);
        graphics.fill(leftPos + x + 1, topPos + y + 1,
                leftPos + x + width - 1, topPos + y + height - 1, 0xFF08130E);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, Component.literal("SIGNAL WORKS // MUNICIPAL SIGN DESIGNER"),
                11, 11, PANEL_GREEN, false);
        graphics.drawString(font, Component.literal("COMPACT 1080P LAYOUT"),
                337, 11, TEXT_DIM, false);
        graphics.drawString(font, Component.literal("LIVE PREVIEW"),
                12, 38, TEXT_DIM, false);

        graphics.drawString(font, Component.literal("PRIMARY STREET"), 16, 104, PANEL_AMBER, false);
        graphics.drawString(font, Component.literal("CROSS STREET"), 16, 132, TEXT_DIM, false);
        graphics.drawString(font, Component.literal("DISTRICT / CITY"), 16, 160, TEXT_DIM, false);
        graphics.drawString(font, Component.literal("BLOCK"), 16, 188, TEXT_DIM, false);

        graphics.drawString(font, Component.literal("FONT"), 228, 104, PANEL_AMBER, false);
        graphics.drawString(font, Component.literal("TEXT SIZE"), 228, 140, TEXT_DIM, false);

        graphics.drawString(font, Component.literal("PALETTE"), 362, 104, PANEL_AMBER, false);
        graphics.drawString(font, Component.literal("BLADE SHAPE"), 362, 162, TEXT_DIM, false);

        graphics.drawString(font, Component.literal("> " + status), 14, 240,
                status.startsWith("OK") ? PANEL_GREEN : PANEL_BLUE, false);
        graphics.drawString(font, Component.literal("E T / SAFE"), 386, 240, TEXT_DIM, false);

        renderPaletteMarkers(graphics);
        renderPreview(graphics);
    }

    private void renderPaletteMarkers(GuiGraphics graphics) {
        MunicipalSignStyle[] styles = MunicipalSignStyle.values();
        for (int index = 0; index < styles.length; index++) {
            int column = index & 1;
            int row = index >> 1;
            int x = 364 + column * 43;
            int y = 133 + row * 22;
            int color = 0xFF000000 | styles[index].backgroundColor();
            graphics.fill(x, y, x + 35, y + 2, color);
            if (menu.style() == styles[index]) {
                graphics.fill(x - 1, y - 1, x + 36, y, PANEL_GREEN);
                graphics.fill(x - 1, y + 2, x + 36, y + 3, PANEL_GREEN);
            }
        }
    }

    private void renderPreview(GuiGraphics graphics) {
        MunicipalSignStyle style = menu.style();
        MunicipalSignShape shape = menu.shape();
        int background = 0xFF000000 | style.backgroundColor();
        int textColor = 0xFF000000 | style.textColor();
        int border = 0xFF000000 | style.borderColor();

        int centerX = 230;
        drawBlade(graphics, shape, 42, 66, 418, 89, border, background);
        drawBlade(graphics, shape, 139, 47, 321, 68, border, background);

        String primary = previewText(primaryBox == null
                ? menu.text(MunicipalStreetSignBlockEntity.FIELD_PRIMARY) : primaryBox.getValue());
        String cross = previewText(crossBox == null
                ? menu.text(MunicipalStreetSignBlockEntity.FIELD_CROSS) : crossBox.getValue());
        String footer = previewText(districtBox == null
                ? menu.text(MunicipalStreetSignBlockEntity.FIELD_DISTRICT) : districtBox.getValue());
        String block = previewText(blockBox == null
                ? menu.text(MunicipalStreetSignBlockEntity.FIELD_BLOCK) : blockBox.getValue());
        if (!block.isBlank()) {
            footer = footer.isBlank() ? block : footer + "  " + block;
        }

        drawStyledCentered(graphics, primary, centerX, 70, 340, textColor, 0.84F);
        drawStyledCentered(graphics, cross, centerX, 51, 160, textColor, 0.74F);
        drawStyledCentered(graphics, footer, centerX, 81, 320, textColor, 0.53F);
    }

    private void drawBlade(
            GuiGraphics graphics,
            MunicipalSignShape shape,
            int x0,
            int y0,
            int x1,
            int y1,
            int border,
            int background
    ) {
        fillShape(graphics, shape, x0, y0, x1, y1, border);
        fillShape(graphics, shape, x0 + 2, y0 + 2, x1 - 2, y1 - 2, background);
    }

    private void fillShape(
            GuiGraphics graphics,
            MunicipalSignShape shape,
            int x0,
            int y0,
            int x1,
            int y1,
            int color
    ) {
        int height = y1 - y0;
        int cut = shape == MunicipalSignShape.CLIPPED ? 6 : 3;
        switch (shape) {
            case RECTANGLE -> graphics.fill(x0, y0, x1, y1, color);
            case ROUNDED -> {
                graphics.fill(x0 + cut, y0, x1 - cut, y1, color);
                graphics.fill(x0, y0 + 2, x1, y1 - 2, color);
            }
            case CLIPPED -> {
                int band = Math.max(2, height / 3);
                graphics.fill(x0 + cut, y0, x1 - cut, y0 + band, color);
                graphics.fill(x0, y0 + band, x1, y1 - band, color);
                graphics.fill(x0 + cut, y1 - band, x1 - cut, y1, color);
            }
        }
    }

    private String previewText(String text) {
        String value = text == null ? "" : text;
        return menu.uppercase() ? value.toUpperCase(Locale.ROOT) : value;
    }

    private void drawStyledCentered(
            GuiGraphics graphics,
            String text,
            int centerX,
            int y,
            int maxWidth,
            int color,
            float lineScale
    ) {
        if (text == null || text.isBlank()) {
            return;
        }

        MunicipalSignFont fontProfile = menu.fontProfile();
        MunicipalTextSize textSize = menu.textSize();
        float requestedScale = textSize.scale() * lineScale;
        float requestedWidth = Math.max(1.0F, font.width(text) * fontProfile.widthScale() * requestedScale);
        float fit = Math.min(1.0F, maxWidth / requestedWidth);
        float xScale = requestedScale * fit * fontProfile.widthScale();
        float yScale = requestedScale * fit;

        graphics.pose().pushPose();
        graphics.pose().translate(centerX, y, 0.0F);
        graphics.pose().scale(xScale, yScale, 1.0F);
        int x = -font.width(text) / 2;
        graphics.drawString(font, Component.literal(text), x, 0, color, false);
        if (fontProfile.bold()) {
            graphics.drawString(font, Component.literal(text), x + 1, 0, color, false);
        }
        graphics.pose().popPose();
    }
}
