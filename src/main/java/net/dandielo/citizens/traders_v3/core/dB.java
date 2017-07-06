package net.dandielo.citizens.traders_v3.core;

import net.dandielo.citizens.traders_v3.bukkit.DtlTraders;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

public class dB {
    private static final String DEBUG;
    private static dB.DebugLevel debugLevel;
    private static ConsoleCommandSender sender;

    public dB() {
    }

    public static boolean levelEnabled(dB.DebugLevel level) {
        return debugLevel.levelEnabled(level);
    }

    public static void critical(Object... args) {
        if(levelEnabled(dB.DebugLevel.CRITICAL)) {
            sendMessage(ChatColor.RED, "CRITICAL", new Object[]{ChatColor.GOLD, args});
        }
    }

    public static void high(Object... args) {
        if(levelEnabled(dB.DebugLevel.HIGH)) {
            sendMessage(ChatColor.GOLD, "SEVERE", new Object[]{ChatColor.BOLD, args});
        }
    }

    public static void normal(Object... args) {
        if(levelEnabled(dB.DebugLevel.NORMAL)) {
            sendMessage(ChatColor.YELLOW, "NORMAL", args);
        }
    }

    public static void low(Object... args) {
        if(levelEnabled(dB.DebugLevel.LOW)) {
            sendMessage(ChatColor.AQUA, "LOW", args);
        }
    }

    public static void info(Object... args) {
        if(levelEnabled(dB.DebugLevel.INFO)) {
            sendMessage(ChatColor.GREEN, "INFO", args);
        }
    }

    public static void spec(dB.DebugLevel lvl, Object... args) {
        if(levelEnabled(lvl)) {
            sendMessage(ChatColor.GREEN, lvl.name().toUpperCase(), args);
        }
    }

    private static String mergeArgs(Object... args) {
        StringBuilder builder = new StringBuilder();
        Object[] var2 = args;
        int var3 = args.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Object arg = var2[var4];
            if(arg != null && arg.getClass().isArray()) {
                Object[] var6 = (Object[])((Object[])arg);
                int var7 = var6.length;

                for(int var8 = 0; var8 < var7; ++var8) {
                    Object o = var6[var8];
                    if(o != null && o.getClass().isArray()) {
                        Object[] var10 = (Object[])((Object[])o);
                        int var11 = var10.length;

                        for(int var12 = 0; var12 < var11; ++var12) {
                            Object co = var10[var12];
                            builder.append(co);
                        }
                    } else {
                        builder.append(o);
                    }
                }
            } else {
                builder.append(arg);
            }
        }

        return builder.toString();
    }

    private static void sendMessage(ChatColor color, String prefix, Object... args) {
        if(sender == null) {
            sender = Bukkit.getServer().getConsoleSender();
        }

        if(sender == null) {
            Bukkit.getLogger().info("NULL console sender...");
        } else {
            sender.sendMessage(mergeArgs(new Object[]{DtlTraders.PREFIX, DEBUG, color, "[" + prefix + "] ", ChatColor.RESET, args}));
        }
    }

    static {
        DEBUG = ChatColor.DARK_PURPLE + "[DEBUG]" + ChatColor.RESET;
        debugLevel = dB.DebugLevel.valueOf(PluginSettings.debugLevel());
        sender = Bukkit.getServer().getConsoleSender();
    }

    public static enum DebugLevel {
        NONE,
        FALSE,
        DISABLE,
        CRITICAL,
        HIGH,
        NORMAL,
        LOW,
        INFO,
        S1_ADONDRIEL,
        S2_MAGIC_POWA,
        S3_ATTRIB,
        CURRENCY;

        private DebugLevel() {
        }

        boolean showCritical() {
            return this.showHigh() || this.equals(CRITICAL);
        }

        boolean showHigh() {
            return this.showNormal() || this.equals(HIGH);
        }

        boolean showNormal() {
            return this.showLow() || this.equals(NORMAL);
        }

        boolean showLow() {
            return this.showInfo() || this.equals(LOW);
        }

        boolean showInfo() {
            return this.equals(INFO);
        }

        boolean showSpecific(DebugLevel level) {
            switch(level) {
                case S1_ADONDRIEL:
                    return this.equals(S1_ADONDRIEL);
                case S2_MAGIC_POWA:
                    return this.equals(S2_MAGIC_POWA);
                case S3_ATTRIB:
                    return this.equals(S3_ATTRIB);
                case CURRENCY:
                    return this.equals(CURRENCY);
                default:
                    return false;
            }
        }

        boolean levelEnabled(dB.DebugLevel level) {
            switch(level) {
                case FALSE:
                    return false;
                case DISABLE:
                    return false;
                case NONE:
                    return false;
                case CRITICAL:
                    return this.showCritical();
                case HIGH:
                    return this.showHigh();
                case NORMAL:
                    return this.showNormal();
                case LOW:
                    return this.showLow();
                case INFO:
                    return this.showInfo();
                default:
                    return this.showSpecific(level);
            }
        }
    }
}
