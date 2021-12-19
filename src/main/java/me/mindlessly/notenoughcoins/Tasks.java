package me.mindlessly.notenoughcoins;

import me.mindlessly.notenoughcoins.utils.ApiHandler;
import me.mindlessly.notenoughcoins.utils.Utils;

import java.io.IOException;

public class Tasks {
    public static Thread updateBalance = new Thread(() -> {
        while (true) {
            if (Config.enabled) {
                try {
                    ApiHandler.updatePurse();
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                } catch (Exception e) {
                    try {
                        Utils.sendMessageWithPrefix("&cFailed to update balance, please check if you set your API key correctly.");
                        Thread.sleep(60000); // Wait until the user sets the API key
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    e.printStackTrace();
                    break;
                }
            }
        }
    }, "Not Enough Coins Balance Updating Task");
    public static Thread updateAverageItem = new Thread(() -> {
        while (true) {
            if (Config.enabled || Config.bestSellingMethod) {
                try {
                    ApiHandler.updateAverage();
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Utils.blacklistMessage();
                    try {
                        Thread.sleep(60000); // sleep 60s if the API is down or got blacklisted
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace(); // This shouldn't happen though
                }
            }
        }
    }, "Not Enough Coins Average Item Updating Task");
    public static Thread updateLBINItem = new Thread(() -> {
        while (true) {
            if (Config.enabled || Config.bestSellingMethod) {
                try {
                    ApiHandler.updateLBIN();
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Utils.blacklistMessage();
                    try {
                        Thread.sleep(60000); // sleep 60s if the API is down or got blacklisted
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace(); // This shouldn't happen though
                }
            }
        }
    }, "Not Enough Coins Lowest Buy-It-Now Updating Task");
    public static Thread updateBazaarItem = new Thread(() -> {
        while (true) {
            if (Config.enabled || Config.bestSellingMethod) {
                try {
                    ApiHandler.updateBazaar();
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Utils.blacklistMessage();
                    try {
                        Thread.sleep(60000); // sleep 60s if the API is down or got blacklisted
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace(); // This shouldn't happen though
                }
            }
        }
    }, "Not Enough Coins Bazaar Updating Task");
}
