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

public class MapMethods
{
    private class BlackjackRenderer extends MapRenderer
    {
        public BlackjackRenderer()
        {
            super(true);
        }

        @Override
        public void render(MapView mapView, MapCanvas mapCanvas, Player player)
        {
            if (redrawsNeeded.get(player.getName()))
            {
                BlackjackPlayer blackjackPlayer = BlackjackPlayer.getBlackjackPlayer(player.getName());
                if (blackjackPlayer != null)
                {
                    // Draw dealer hand, or 2nd hand if exists
                    int x = 2;
                    if (!blackjackPlayer.isSplit())
                    {
                        mapCanvas.drawImage(0, 0, blackjack_base);

                        ArrayList<Card> dealerCards = blackjackPlayer.getBlackjackTable().getDealer().getHand().getCards();
                        for (int i = 0; i < 5; i++)
                        {
                            if (i < dealerCards.size())
                            {
                                if (dealerCards.get(i) != blackjackPlayer.getBlackjackTable().getDealer().getHoleCard())
                                {
                                    drawImageWithTransparency(mapCanvas, dealerCards.get(i).getImage(), x, 15);
                                } else
                                {
                                    drawImageWithTransparency(mapCanvas, card_facedown, x, 15);
                                }
                            } else
                            {
                                drawImageWithTransparency(mapCanvas, card_empty, x, 15);
                            }
                            x += 12;
                        }
                    } else
                    {
                        mapCanvas.drawImage(0, 0, blackjack_base_split);

                        ArrayList<Card> playerCards = blackjackPlayer.getHands().get(1).getHand().getCards();
                        for (int i = 0; i < 5; i++)
                        {
                            if (i < playerCards.size())
                            {
                                drawImageWithTransparency(mapCanvas, playerCards.get(i).getImage(), x, 15);
                            } else
                            {
                                drawImageWithTransparency(mapCanvas, card_empty, x, 15);
                            }
                            x += 12;
                        }
                    }

                    // Draw player hand
                    ArrayList<Card> playerCards = blackjackPlayer.getHands().get(0).getHand().getCards();
                    x = 2;
                    for (int i = 0; i < 5; i++)
                    {
                        if (i < playerCards.size())
                        {
                            drawImageWithTransparency(mapCanvas, playerCards.get(i).getImage(), x, 55);
                        } else
                        {
                            drawImageWithTransparency(mapCanvas, card_empty, x, 55);
                        }
                        x += 12;
                    }

                    // Draw player status
                    int i = 1;
                    x = 5;
                    int y = 98;
                    for (BlackjackPlayer temp : blackjackPlayer.getBlackjackTable().getPlayersThisHand())
                    {
                        if (i == 9)
                        {
                            break;
                        }
                        if (i == 5)
                        {
                            x = 5;
                            y = 111;
                        }
                        if (temp.isStayedOnAllHands())
                        {
                            mapCanvas.drawImage(x, y, status_folded);
                        } else if (temp.isAction())
                        {
                            mapCanvas.drawImage(x, y, status_action);
                        } else
                        {
                            mapCanvas.drawImage(x, y, status_normal);
                        }

                        String playerName = temp.getPlayerName();
                        mapCanvas.drawText(x + 4, y + 3, MinecraftFont.Font, playerName.substring(0, 3).toUpperCase());
                        x += status_normal.getWidth() + 1;
                        i++;
                    }

                    // Draw dealer score
                    mapCanvas.drawText(80, 31, MinecraftFont.Font, "§16;" + blackjackPlayer.getBlackjackTable().getDealer().getScore());

                    // Draw player score
                    mapCanvas.drawText(80, 51, MinecraftFont.Font, "§16;" + blackjackPlayer.scoreToString());

                    // Draw your bet
                    mapCanvas.drawText(80, 71, MinecraftFont.Font, "§16;" + Formatter.formatMoneyWithoutCurrency(blackjackPlayer.getTotalAmountBet()));

                    redrawsNeeded.put(player.getName(), false);
                    player.sendMap(mapView);
                }
            }
        }
    }

    private class PokerRenderer extends MapRenderer
    {
        public PokerRenderer()
        {
            super(true);
        }

        @Override
        public void render(MapView mapView, MapCanvas mapCanvas, Player player)
        {
            if (redrawsNeeded.get(player.getName()))
            {
                PokerPlayer pokerPlayer = PokerPlayer.getPokerPlayer(player.getName());
                if (pokerPlayer != null)
                {
                    // Draw base
                    mapCanvas.drawImage(0, 0, poker_base);

                    // Draw community cards
                    int x = 2;
                    for (Card card : pokerPlayer.getPokerTable().getBoard())
                    {
                        drawImageWithTransparency(mapCanvas, card.getImage(), x, 15);
                        x += card.getImage().getWidth() + 1;
                    }

                    // Draw player status
                    int i = 1;
                    x = 5;
                    int y = 58;
                    for (PokerPlayer temp : pokerPlayer.getPokerTable().getPokerPlayers())
                    {
                        if (i == 9)
                        {
                            break;
                        }
                        if (i == 5)
                        {
                            x = 5;
                            y = 71;
                        }
                        if (temp.isFolded())
                        {
                            mapCanvas.drawImage(x, y, status_folded);
                        } else if (temp.isAction())
                        {
                            mapCanvas.drawImage(x, y, status_action);
                        } else
                        {
                            mapCanvas.drawImage(x, y, status_normal);
                        }

                        String playerName = temp.getPlayerName();
                        mapCanvas.drawText(x + 4, y + 3, MinecraftFont.Font, playerName.substring(0, 3).toUpperCase());
                        x += status_normal.getWidth() + 1;
                        i++;
                    }

                    // Draw player cards
                    x = 2;
                    for (Card card : pokerPlayer.getHand().getCards())
                    {
                        drawImageWithTransparency(mapCanvas, card.getImage(), x, 92);
                        x += card.getImage().getWidth() + 1;
                    }

                    // Draw current bet
                    mapCanvas.drawText(55, 95, MinecraftFont.Font, "§16;" + Formatter.formatMoneyWithoutCurrency(pokerPlayer.getPokerTable().getCurrentBet()));

                    // Draw total in pots
                    mapCanvas.drawText(55, 115, MinecraftFont.Font, "§16;" + Formatter.formatMoneyWithoutCurrency(pokerPlayer.getPokerTable().countPotAmounts()));

                    redrawsNeeded.put(player.getName(), false);
                    player.sendMap(mapView);
                }
            }
        }
    }

    UltimateCards p = null;
    private static BufferedImage poker_base = null;
    private static BufferedImage blackjack_base = null;
    private static BufferedImage blackjack_base_split = null;
    private static BufferedImage status_normal = null;
    private static BufferedImage status_folded = null;
    private static BufferedImage status_action = null;
    private static BufferedImage card_facedown = null;
    private static BufferedImage card_empty = null;
    static
    {
        try
        {
            poker_base = ImageIO.read(UltimateCards.getResourceManager().getResource("images/poker_base.png"));
            blackjack_base = ImageIO.read(UltimateCards.getResourceManager().getResource("images/blackjack_base.png"));
            blackjack_base_split = ImageIO.read(UltimateCards.getResourceManager().getResource("images/blackjack_base_split.png"));
            status_normal = ImageIO.read(UltimateCards.getResourceManager().getResource("images/player_box_normal.png"));
            status_folded = ImageIO.read(UltimateCards.getResourceManager().getResource("images/player_box_folded.png"));
            status_action = ImageIO.read(UltimateCards.getResourceManager().getResource("images/player_box_action.png"));
            card_facedown = ImageIO.read(UltimateCards.getResourceManager().getResource("images/card_facedown.png"));
            card_empty = ImageIO.read(UltimateCards.getResourceManager().getResource("images/card_empty.png"));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static ArrayList<Short> createdMaps = new ArrayList<Short>();

    public static ArrayList<Short> getCreatedMaps()
    {
        return createdMaps;
    }

    private final HashMap<String, Integer> redrawTasks = new HashMap<String, Integer>();
    private final HashMap<String, Boolean> redrawsNeeded = new HashMap<String, Boolean>();
    private static HashMap<String, Short> savedMaps = new HashMap<String, Short>();

    public static HashMap<String, Short> getSavedMaps()
    {
        return savedMaps;
    }

    public static String mapExists(ItemStack itemStack)
    {
        for (Entry<String, Short> entry : savedMaps.entrySet())
            if (entry.getValue() == itemStack.getDurability()) return entry.getKey();
        return "";
    }

    private final PokerRenderer pokerRenderer = new PokerRenderer();

    private final BlackjackRenderer blackjackRenderer = new BlackjackRenderer();

    public MapMethods(UltimateCards p)
    {
        this.p = p;
    }

    private void clearRenderers(MapView map)
    {
        for (MapRenderer mr : map.getRenderers())
        {
            map.removeRenderer(mr);
        }
    }

    public void drawImageWithTransparency(MapCanvas canvas, BufferedImage img, int posX, int posY)
    {
        try
        {
            int height = img.getHeight();
            int width = img.getWidth();
            int i = 0;
            int[] pixels = new int[width * height];

            PixelGrabber pg = new PixelGrabber(img, 0, 0, width, height, pixels, 0, width);

            pg.grabPixels();
            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    if (pixels[i] != 16777215)
                    {
                        Color c = new Color(img.getRGB(x, y));

                        int red = c.getRed();
                        int green = c.getGreen();
                        int blue = c.getBlue();

                        canvas.setPixel(posX + x, posY + y, MapPalette.matchColor(red, green, blue));
                    }
                    i++;
                }
            }
        } catch (InterruptedException e)
        {
        }
    }

    public void giveMap(final Player player, String renderer)
    {
        //Schedule task
        redrawTasks.put(player.getName(), Bukkit.getScheduler().scheduleAsyncRepeatingTask(p, new Runnable()
        {

            @Override
            public void run()
            {
                redrawsNeeded.put(player.getName(), true);
            }

        }, 0L, 20L));

        //Create map
        final MapView map = Bukkit.getServer().createMap(player.getWorld());
        clearRenderers(map);
        if (renderer.equalsIgnoreCase("poker"))
        {
            map.addRenderer(pokerRenderer);
        } else if (renderer.equalsIgnoreCase("blackjack"))
        {
            map.addRenderer(blackjackRenderer);
        }

        //Add to lists
        redrawsNeeded.put(player.getName(), true);
        savedMaps.put(player.getName(), map.getId());
        createdMaps.add(map.getId());

        //Give map
        ItemStack mapItem = new ItemStack(Material.MAP, 1, map.getId());

        if (HelperMethods.hasOpenSlotInInventory(player))
        {
            player.getInventory().addItem(mapItem);
        } else
        {
            player.getWorld().dropItemNaturally(player.getLocation(), mapItem);
        }
    }

    public void restoreAllMaps()
    {
        for (Entry<String, Short> entry : savedMaps.entrySet())
        {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null)
            {
                ItemStack mapItem = new ItemStack(Material.MAP, 1, savedMaps.get(player.getName()));
                if (player.getInventory().contains(mapItem))
                {
                    player.getInventory().remove(mapItem);
                    createdMaps.remove(new Short(mapItem.getDurability()));
                }
                savedMaps.remove(player.getName());
                redrawTasks.remove(player.getName());
                Bukkit.getScheduler().cancelTask(redrawTasks.get(player.getName()));
            }
        }
    }

    public void restoreMap(Player player)
    {
        ItemStack mapItem = new ItemStack(Material.MAP, 1, savedMaps.get(player.getName()));
        if (player.getInventory().contains(mapItem))
        {
            player.getInventory().remove(mapItem);
            createdMaps.remove(new Short(mapItem.getDurability()));
        }
        savedMaps.remove(player.getName());
        redrawTasks.remove(player.getName());
        Bukkit.getScheduler().cancelTask(redrawTasks.get(player.getName()));
    }
}
