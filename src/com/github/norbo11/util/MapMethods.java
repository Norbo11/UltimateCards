package com.github.norbo11.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.game.blackjack.BlackjackPlayer;
import com.github.norbo11.game.cards.Card;
import com.github.norbo11.game.poker.PokerPlayer;

@SuppressWarnings("deprecation")
public class MapMethods {
    private static class BlackjackRenderer extends MapRenderer {
        public BlackjackRenderer() {
            super(true);
        }

        @Override
        public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
            if (redrawsNeeded.get(player.getName())) {
                BlackjackPlayer blackjackPlayer = BlackjackPlayer.getBlackjackPlayer(player.getName());
                if (blackjackPlayer != null) {
                    // Draw dealer hand, or 2nd hand if exists
                    int x = 2;
                    int y = 8;
                    if (!blackjackPlayer.isSplit()) {
                        mapCanvas.drawImage(0, 0, blackjack_base);

                        ArrayList<Card> dealerCards = blackjackPlayer.getTable().getDealer().getHand().getCards();
                        for (int i = 0; i < 5; i++) {
                            if (i < dealerCards.size()) {
                                if (dealerCards.get(i) != blackjackPlayer.getTable().getDealer().getHoleCard()) {
                                    drawImageWithTransparency(mapCanvas, dealerCards.get(i).getImage(), x, y);
                                } else {
                                    drawImageWithTransparency(mapCanvas, card_facedown, x, y);
                                }
                            } else {
                                drawImageWithTransparency(mapCanvas, card_empty, x, y);
                            }
                            x += 12;
                        }
                    } else {
                        mapCanvas.drawImage(0, 0, blackjack_base_split);

                        ArrayList<Card> playerCards = blackjackPlayer.getHands().get(1).getHand().getCards();
                        for (int i = 0; i < 5; i++) {
                            if (i < playerCards.size()) {
                                drawImageWithTransparency(mapCanvas, playerCards.get(i).getImage(), x, y);
                            } else {
                                drawImageWithTransparency(mapCanvas, card_empty, x, y);
                            }
                            x += 12;
                        }
                    }

                    // Draw player hand
                    ArrayList<Card> playerCards = blackjackPlayer.getHands().get(0).getHand().getCards();
                    x = 2;
                    y = 52;
                    for (int i = 0; i < 5; i++) {
                        if (i < playerCards.size()) {
                            drawImageWithTransparency(mapCanvas, playerCards.get(i).getImage(), x, y);
                        } else {
                            drawImageWithTransparency(mapCanvas, card_empty, x, y);
                        }
                        x += 12;
                    }

                    // Draw player status
                    int i = 1;
                    x = 5;
                    y = 98;
                    for (BlackjackPlayer temp : blackjackPlayer.getTable().getBjPlayersThisHand()) {
                        if (i == 9) {
                            break;
                        }
                        if (i == 5) {
                            x = 5;
                            y = 111;
                        }
                        if (temp.isStayedOnAllHands()) {
                            mapCanvas.drawImage(x, y, status_folded);
                        } else if (temp.isAction()) {
                            mapCanvas.drawImage(x, y, status_action);
                        } else {
                            mapCanvas.drawImage(x, y, status_normal);
                        }

                        String playerName = temp.getPlayerName();
                        mapCanvas.drawText(x + 4, y + 3, MinecraftFont.Font, playerName.substring(0, 3).toUpperCase());
                        x += status_normal.getWidth() + 1;
                        i++;
                    }

                    // Draw dealer score
                    mapCanvas.drawText(80, 18, MinecraftFont.Font, "§32;" + blackjackPlayer.getTable().getDealer().getScore());

                    // Draw player score
                    mapCanvas.drawText(80, 47, MinecraftFont.Font, "§32;" + blackjackPlayer.scoreToString());

                    // Draw your bet
                    mapCanvas.drawText(80, 76, MinecraftFont.Font, "§32;" + Formatter.formatMoneyWithoutColor(blackjackPlayer.getTotalAmountBet()));

                    redrawsNeeded.put(player.getName(), false);
                    player.sendMap(mapView);
                }
            }
        }
    }

    private static class PokerRenderer extends MapRenderer {
        public PokerRenderer() {
            super(true);
        }

        @Override
        public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
            if (redrawsNeeded.get(player.getName())) {
                PokerPlayer pokerPlayer = PokerPlayer.getPokerPlayer(player.getName());
                if (pokerPlayer != null) {
                    // Draw base
                    mapCanvas.drawImage(0, 0, poker_base);

                    // Draw community cards
                    int x = 2;
                    int y = 8;
                    for (Card card : pokerPlayer.getPokerTable().getBoard().getCards()) {
                        drawImageWithTransparency(mapCanvas, card.getImage(), x, y);
                        x += card.getImage().getWidth() + 1;
                    }

                    // Draw player status
                    int i = 1;
                    x = 5;
                    y = 54;
                    for (PokerPlayer temp : pokerPlayer.getPokerTable().getPokerPlayersThisHand()) {
                        if (i == 9) {
                            break;
                        }
                        if (i == 5) {
                            x = 5;
                            y = 67;
                        }
                        if (temp.isFolded()) {
                            mapCanvas.drawImage(x, y, status_folded);
                        } else if (temp.isAction()) {
                            mapCanvas.drawImage(x, y, status_action);
                        } else {
                            mapCanvas.drawImage(x, y, status_normal);
                        }

                        // If the player is on the button, draw the little indicator
                        if (temp.isButton()) {
                            drawImageWithTransparency(mapCanvas, button, x, y);
                        }

                        String playerName = temp.getPlayerName();
                        mapCanvas.drawText(x + 4, y + 3, MinecraftFont.Font, playerName.substring(0, 3).toUpperCase());
                        x += status_normal.getWidth() + 1;
                        i++;
                    }

                    // Draw player cards
                    x = 2;
                    y = 91;
                    for (Card card : pokerPlayer.getHand().getCards()) {
                        drawImageWithTransparency(mapCanvas, card.getImage(), x, y);
                        x += card.getImage().getWidth() + 1;
                    }

                    // Draw current bet
                    mapCanvas.drawText(56, 94, MinecraftFont.Font, "§32;" + Formatter.formatMoneyWithoutColor(pokerPlayer.getPokerTable().getCurrentBet()));

                    // Draw total in pots
                    mapCanvas.drawText(56, 115, MinecraftFont.Font, "§32;" + Formatter.formatMoneyWithoutColor(pokerPlayer.getPokerTable().getHighestPot()));

                    redrawsNeeded.put(player.getName(), false);
                    player.sendMap(mapView);
                }
            }
        }
    }

    public static UltimateCards p;
    private static BufferedImage poker_base = null;
    private static BufferedImage blackjack_base = null;
    private static BufferedImage blackjack_base_split = null;
    private static BufferedImage status_normal = null;
    private static BufferedImage status_folded = null;
    private static BufferedImage status_action = null;
    private static BufferedImage card_facedown = null;
    private static BufferedImage card_empty = null;
    private static BufferedImage button = null;

    static {
        try {
            poker_base = ImageIO.read(ResourceManager.getResource("images/poker_base.png"));
            blackjack_base = ImageIO.read(ResourceManager.getResource("images/blackjack_base.png"));
            blackjack_base_split = ImageIO.read(ResourceManager.getResource("images/blackjack_base_split.png"));
            status_normal = ImageIO.read(ResourceManager.getResource("images/player_box_normal.png"));
            status_folded = ImageIO.read(ResourceManager.getResource("images/player_box_folded.png"));
            status_action = ImageIO.read(ResourceManager.getResource("images/player_box_action.png"));
            card_facedown = ImageIO.read(ResourceManager.getResource("images/card_facedown.png"));
            card_empty = ImageIO.read(ResourceManager.getResource("images/card_empty.png"));
            button = ImageIO.read(ResourceManager.getResource("images/button.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<Short> createdMaps = new ArrayList<Short>();

    private static final HashMap<String, Integer> redrawTasks = new HashMap<String, Integer>();
    private static final HashMap<String, Boolean> redrawsNeeded = new HashMap<String, Boolean>();
    private static HashMap<String, ItemStack> savedMaps = new HashMap<String, ItemStack>();

    private static final PokerRenderer pokerRenderer = new PokerRenderer();
    private static final BlackjackRenderer blackjackRenderer = new BlackjackRenderer();

    private static void clearRenderers(MapView map) {
        for (MapRenderer mr : map.getRenderers()) {
            map.removeRenderer(mr);
        }
    }

    public static void drawImageWithTransparency(MapCanvas canvas, BufferedImage img, int posX, int posY) {
        try {
            int height = img.getHeight();
            int width = img.getWidth();
            int i = 0;
            int[] pixels = new int[width * height];

            PixelGrabber pg = new PixelGrabber(img, 0, 0, width, height, pixels, 0, width);

            pg.grabPixels();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // If the pixel isn't a transparancy pixel
                    if (pixels[i] != 16777215) {
                        Color c = new Color(img.getRGB(x, y));

                        int red = c.getRed();
                        int green = c.getGreen();
                        int blue = c.getBlue();

                        canvas.setPixel(posX + x, posY + y, MapPalette.matchColor(red, green, blue));
                    }
                    i++;
                }
            }
        } catch (InterruptedException e) {
        }
    }

    public static ArrayList<Short> getCreatedMaps() {
        return createdMaps;
    }

    public static HashMap<String, ItemStack> getSavedMaps() {
        return savedMaps;
    }

    public static void giveMap(final Player player, String renderer) {
        // Create map
        MapView map = Bukkit.getServer().createMap(player.getWorld());
        clearRenderers(map);
        if (renderer.equalsIgnoreCase("poker")) {
            map.addRenderer(pokerRenderer);
        } else if (renderer.equalsIgnoreCase("blackjack")) {
            map.addRenderer(blackjackRenderer);
        }

        // Give map
        ItemStack mapItem = new ItemStack(Material.MAP, 1, map.getId());

        // Add to lists
        redrawsNeeded.put(player.getName(), true);
        savedMaps.put(player.getName(), mapItem);
        createdMaps.add(map.getId());

        // Schedule task
        redrawTasks.put(player.getName(), Bukkit.getScheduler().scheduleSyncRepeatingTask(p, new Runnable() {

            @Override
            public void run() {
                redrawsNeeded.put(player.getName(), true);
            }

        }, 0L, 20L));

        if (InventoryHelper.hasOpenSlotInInventory(player)) {
            ItemStack held = player.getInventory().getItem(0) == null ? new ItemStack(Material.AIR) : player.getInventory().getItem(0);
            player.getInventory().setItem(0, mapItem);
            player.getInventory().addItem(held);
            player.getInventory().setHeldItemSlot(0);
        } else {
            player.getWorld().dropItemNaturally(player.getLocation(), mapItem);
            Messages.sendMessage(player, "As you had no space in your inventory, your cards map interface was dropped in front of you. Please pick it up!");
        }

    }

    public static String mapExists(ItemStack itemStack) {
        for (Entry<String, ItemStack> entry : savedMaps.entrySet())
            if (entry == itemStack) return entry.getKey();
        return "";
    }

    public static void restoreAllMaps() {
        for (Entry<String, ItemStack> entry : savedMaps.entrySet()) {
            restoreMap(entry.getKey(), false);
        }
        savedMaps.clear();
    }

    public static void restoreMap(String playerName, boolean remove) {
        ItemStack mapItem = savedMaps.get(playerName);
        Player player = Bukkit.getPlayer(playerName);

        if (player != null) {
            player.getInventory().remove(mapItem);
        }

        createdMaps.remove(mapItem);
        if (remove) savedMaps.remove(playerName);
        Bukkit.getScheduler().cancelTask(redrawTasks.get(playerName));
        redrawTasks.remove(playerName);
    }
}
