package com.dgtlbrandxn.signalworks.client.screen;

import com.dgtlbrandxn.signalworks.catalog.RoadSignCatalog;
import com.dgtlbrandxn.signalworks.catalog.RoadSignEntry;
import com.dgtlbrandxn.signalworks.client.render.RoadSignTextureManager;
import com.dgtlbrandxn.signalworks.menu.RoadSignMenu;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Searchable 1024-ready sign library with category and custom-pack support. */
public final class RoadSignCatalogScreen extends AbstractContainerScreen<RoadSignMenu> {
    private static final int COLS = 5;
    private static final int ROWS = 3;
    private static final int PAGE_SIZE = COLS * ROWS;
    private static final int CELL = 48;
    private static final int GRID_X = 14;
    private static final int GRID_Y = 66;
    private static final int PANEL_DARK = 0xFF08110D;
    private static final int PANEL = 0xFF1A2921;
    private static final int EDGE = 0xFF3A6F4F;
    private static final int GREEN = 0xFF74FF98;
    private static final int DIM = 0xFF92A69A;
    private static final int SELECT = 0xFFFFD768;

    private EditBox searchBox;
    private Button categoryButton;
    private Button pageButton;
    private final List<Integer> filtered = new ArrayList<>();
    private int categoryIndex = -1;
    private int page;
    private int hoveredCatalogIndex = -1;
    private String status = "1024 MASTER QUALITY // SELECT A SIGN";

    public RoadSignCatalogScreen(RoadSignMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 460;
        imageHeight = 258;
        inventoryLabelY = 10_000;
    }

    @Override
    protected void init() {
        imageWidth = 460;
        imageHeight = 258;
        super.init();

        searchBox = new EditBox(font, leftPos + 14, topPos + 36, 210, 18,
                Component.literal("Search road signs"));
        searchBox.setMaxLength(48);
        searchBox.setHint(Component.literal("Search sign name..."));
        searchBox.setTextColor(GREEN);
        searchBox.setResponder(value -> {
            page = 0;
            rebuildFilter();
        });
        addRenderableWidget(searchBox);

        addRenderableWidget(Button.builder(Component.literal("<"), button -> cycleCategory(-1))
                .bounds(leftPos + 234, topPos + 36, 18, 18).build());
        categoryButton = addRenderableWidget(Button.builder(Component.literal("ALL CATEGORIES"),
                        button -> cycleCategory(1))
                .bounds(leftPos + 255, topPos + 36, 114, 18).build());
        addRenderableWidget(Button.builder(Component.literal(">"), button -> cycleCategory(1))
                .bounds(leftPos + 372, topPos + 36, 18, 18).build());

        addRenderableWidget(Button.builder(Component.literal("FOLDER"), button ->
                        Util.getPlatform().openPath(RoadSignCatalog.customRoot()))
                .bounds(leftPos + 394, topPos + 36, 52, 18).build());

        addRenderableWidget(Button.builder(Component.literal("< PREV"), button -> changePage(-1))
                .bounds(leftPos + 14, topPos + 220, 54, 18).build());
        pageButton = addRenderableWidget(Button.builder(Component.literal("PAGE"), button -> changePage(1))
                .bounds(leftPos + 72, topPos + 220, 74, 18).build());
        addRenderableWidget(Button.builder(Component.literal("NEXT >"), button -> changePage(1))
                .bounds(leftPos + 150, topPos + 220, 54, 18).build());
        addRenderableWidget(Button.builder(Component.literal("RELOAD CUSTOM"), button -> reloadCatalog())
                .bounds(leftPos + 210, topPos + 220, 96, 18).build());
        addRenderableWidget(Button.builder(Component.literal("DONE"), button -> onClose())
                .bounds(leftPos + 366, topPos + 220, 80, 18).build());

        rebuildFilter();
        setInitialFocus(searchBox);
    }

    private void cycleCategory(int direction) {
        int count = RoadSignCatalog.categories().size();
        int slots = count + 1;
        int encoded = categoryIndex + 1;
        encoded = Math.floorMod(encoded + direction, slots);
        categoryIndex = encoded - 1;
        page = 0;
        rebuildFilter();
    }

    private void changePage(int direction) {
        int pages = pageCount();
        page = Math.floorMod(page + direction, pages);
        updateLabels();
    }

    private void reloadCatalog() {
        RoadSignTextureManager.clearCustomCache();
        RoadSignCatalog.reload();
        send(RoadSignMenu.BUTTON_RELOAD);
        categoryIndex = -1;
        page = 0;
        status = "CATALOG RELOADED // " + RoadSignCatalog.entries().size() + " SIGNS";
        rebuildFilter();
    }

    private void rebuildFilter() {
        filtered.clear();
        String query = searchBox == null ? "" : searchBox.getValue().trim().toLowerCase(Locale.ROOT);
        String category = categoryIndex >= 0 && categoryIndex < RoadSignCatalog.categories().size()
                ? RoadSignCatalog.categories().get(categoryIndex) : null;
        List<RoadSignEntry> entries = RoadSignCatalog.entries();
        for (int index = 0; index < entries.size(); index++) {
            RoadSignEntry entry = entries.get(index);
            if (category != null && !entry.category().equals(category)) {
                continue;
            }
            String haystack = (entry.name() + " " + entry.category() + " " + entry.packName())
                    .toLowerCase(Locale.ROOT);
            if (!query.isBlank() && !haystack.contains(query)) {
                continue;
            }
            filtered.add(index);
        }
        page = Math.min(page, pageCount() - 1);
        updateLabels();
    }

    private int pageCount() {
        return Math.max(1, (filtered.size() + PAGE_SIZE - 1) / PAGE_SIZE);
    }

    private void updateLabels() {
        if (categoryButton != null) {
            String label = categoryIndex < 0 ? "ALL CATEGORIES"
                    : RoadSignCatalog.categories().get(categoryIndex).toUpperCase(Locale.ROOT);
            categoryButton.setMessage(Component.literal(trim(label, 18)));
        }
        if (pageButton != null) {
            pageButton.setMessage(Component.literal((page + 1) + " / " + pageCount()));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int localX = (int) mouseX - leftPos;
        int localY = (int) mouseY - topPos;
        if (button == 0 && localX >= GRID_X && localX < GRID_X + COLS * CELL
                && localY >= GRID_Y && localY < GRID_Y + ROWS * CELL) {
            int column = (localX - GRID_X) / CELL;
            int row = (localY - GRID_Y) / CELL;
            int visibleIndex = page * PAGE_SIZE + row * COLS + column;
            if (visibleIndex >= 0 && visibleIndex < filtered.size()) {
                int catalogIndex = filtered.get(visibleIndex);
                send(RoadSignMenu.BUTTON_SELECT_BASE + catalogIndex);
                RoadSignEntry entry = RoadSignCatalog.entry(catalogIndex);
                status = "APPLIED // " + entry.name().toUpperCase(Locale.ROOT);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void send(int buttonId) {
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, buttonId);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
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
        if (hoveredCatalogIndex >= 0) {
            RoadSignEntry hovered = RoadSignCatalog.entry(hoveredCatalogIndex);
            String tip = hovered.tooltip().isBlank() ? hovered.name()
                    : hovered.name() + " // " + hovered.tooltip();
            graphics.renderTooltip(font, Component.literal(tip), mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xF0080B0A);
        graphics.fill(leftPos + 2, topPos + 2, leftPos + imageWidth - 2, topPos + imageHeight - 2, PANEL);
        graphics.fill(leftPos + 6, topPos + 6, leftPos + imageWidth - 6, topPos + imageHeight - 6, PANEL_DARK);
        graphics.fill(leftPos + 10, topPos + 29, leftPos + imageWidth - 10, topPos + 30, EDGE);

        graphics.drawString(font, "SIGNAL WORKS // USDOT SIGN CATALOG",
                leftPos + 14, topPos + 13, GREEN, false);
        graphics.drawString(font, filtered.size() + " MATCHES",
                leftPos + 326, topPos + 13, DIM, false);

        hoveredCatalogIndex = -1;
        int start = page * PAGE_SIZE;
        for (int slot = 0; slot < PAGE_SIZE; slot++) {
            int x = leftPos + GRID_X + (slot % COLS) * CELL;
            int y = topPos + GRID_Y + (slot / COLS) * CELL;
            int filteredIndex = start + slot;
            graphics.fill(x, y, x + 44, y + 44, 0xFF111A15);
            graphics.fill(x + 1, y + 1, x + 43, y + 43, 0xFF26352D);
            if (filteredIndex >= filtered.size()) {
                continue;
            }
            int catalogIndex = filtered.get(filteredIndex);
            RoadSignEntry entry = RoadSignCatalog.entry(catalogIndex);
            int edgeColor = catalogIndex == menu.selectedIndex() ? SELECT : EDGE;
            graphics.fill(x, y, x + 44, y + 2, edgeColor);
            graphics.fill(x, y + 42, x + 44, y + 44, edgeColor);
            graphics.fill(x, y, x + 2, y + 44, edgeColor);
            graphics.fill(x + 42, y, x + 44, y + 44, edgeColor);
            graphics.blit(RoadSignTextureManager.front(entry), x + 6, y + 4,
                    0.0F, 0.0F, 32, 32, 32, 32);
            graphics.drawCenteredString(font, trim(entry.name(), 8), x + 22, y + 35,
                    catalogIndex == menu.selectedIndex() ? SELECT : 0xFFE4ECE6);
            if (mouseX >= x && mouseX < x + 44 && mouseY >= y && mouseY < y + 44) {
                hoveredCatalogIndex = catalogIndex;
            }
        }

        int previewX = leftPos + 270;
        int previewY = topPos + 66;
        int previewW = 176;
        int previewH = 144;
        graphics.fill(previewX, previewY, previewX + previewW, previewY + previewH, 0xFF111A15);
        graphics.fill(previewX + 2, previewY + 2, previewX + previewW - 2, previewY + previewH - 2, 0xFF203027);
        RoadSignEntry selected = RoadSignCatalog.entry(menu.selectedIndex());
        graphics.blit(RoadSignTextureManager.front(selected), previewX + 34, previewY + 10,
                0.0F, 0.0F, 108, 108, 108, 108);
        graphics.drawCenteredString(font, trim(selected.name(), 25), previewX + previewW / 2,
                previewY + 121, SELECT);
        graphics.drawCenteredString(font,
                trim(selected.category() + " // " + selected.packName(), 28),
                previewX + previewW / 2, previewY + 133, DIM);

        graphics.drawString(font, trim(status, 54), leftPos + 14, topPos + 244, DIM, false);
    }

    private static String trim(String value, int length) {
        if (value == null) {
            return "";
        }
        return value.length() <= length ? value : value.substring(0, Math.max(1, length - 1)) + "…";
    }
}
