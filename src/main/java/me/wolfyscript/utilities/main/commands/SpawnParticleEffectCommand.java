package me.wolfyscript.utilities.main.commands;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.chat.ClickData;
import me.wolfyscript.utilities.api.utils.chat.ClickEvent;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffects;
import me.wolfyscript.utilities.main.WUPlugin;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import javax.annotation.Nonnull;
import java.util.*;

public class SpawnParticleEffectCommand implements CommandExecutor, TabCompleter {

    private final List<String> COMMANDS = Arrays.asList("spawn", "stop");

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("spawn")) {
                    if (WolfyUtilities.hasPermission(commandSender, "wolfyutilities.command.particle_effect.spawn")) {
                        if (args.length >= 2) {
                            String effectName = args[1];
                            NamespacedKey nameSpacedKey;
                            if (effectName.contains(":")) {
                                nameSpacedKey = new NamespacedKey(effectName.split(":")[0], effectName.split(":")[1]);
                            } else {
                                nameSpacedKey = new NamespacedKey("wolfyutilities", effectName);
                            }
                            if (args.length >= 5) {
                                try {
                                    double x = Double.parseDouble(args[2]);
                                    double y = Double.parseDouble(args[3]);
                                    double z = Double.parseDouble(args[4]);
                                    Location location = new Location(player.getWorld(), x, y, z);
                                    UUID uuid = ParticleEffects.spawnEffectOnLocation(nameSpacedKey, location);
                                    if (uuid == null) {
                                        WUPlugin.getWolfyUtilities().sendPlayerMessage(player, "&cFailed to spawn effect &4" + nameSpacedKey + "");
                                        return true;
                                    }
                                    WUPlugin.getWolfyUtilities().sendActionMessage(player, new ClickData("&eSpawened effect &6" + nameSpacedKey + "&e on &6" + x + " " + y + " " + z + " &ewith uuid ", null), new ClickData("&6" + uuid, null, new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, uuid.toString())));
                                } catch (NumberFormatException ex) {
                                    WUPlugin.getWolfyUtilities().sendPlayerMessage(player, "&cInvalid position! Please make sure you only use numbers for x/y/z!");
                                    return true;
                                }
                            } else {
                                Block block = player.getTargetBlockExact(10);
                                if (block != null) {
                                    UUID uuid = ParticleEffects.spawnEffectOnBlock(nameSpacedKey, block);
                                    if (uuid == null) {
                                        WUPlugin.getWolfyUtilities().sendPlayerMessage(player, "&cFailed to spawn effect &4" + nameSpacedKey + "");
                                        return true;
                                    }
                                    WUPlugin.getWolfyUtilities().sendActionMessage(player, new ClickData("&eSpawened effect &6" + nameSpacedKey + "&e on block &ewith uuid ", null), new ClickData("&6" + uuid, null, new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, uuid.toString())));
                                }
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("stop")) {
                    if (WolfyUtilities.hasPermission(commandSender, "wolfyutilities.command.particle_effect.spawn")) {
                        if (args.length >= 2) {
                            try {
                                UUID uuid = UUID.fromString(args[1]);
                                ParticleEffects.stopEffect(uuid);
                                WUPlugin.getWolfyUtilities().sendPlayerMessage(player, "&eStopped effect with uuid &6" + args[1] + " &eif it was active!");
                            } catch (IllegalArgumentException ex) {
                                WUPlugin.getWolfyUtilities().sendPlayerMessage(player, "&cInvalid UUID &4" + args[1]);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String s, @Nonnull String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length > 1) {
            if (args[0].equalsIgnoreCase("spawn")) {
                switch (args.length) {
                    case 2:
                        List<String> effects = new ArrayList<>();
                        for (NamespacedKey namespacedKey : ParticleEffects.getEffects().keySet()) {
                            effects.add(namespacedKey.toString());
                        }
                        StringUtil.copyPartialMatches(args[1], effects, results);
                        break;
                    case 3:
                        results.add("x");
                        break;
                    case 4:
                        results.add("y");
                        break;
                    case 5:
                        results.add("z");
                }
            } else if (args[0].equalsIgnoreCase("stop")) {
                results.add("<uuid>");
            }
        } else {
            StringUtil.copyPartialMatches(args[0], COMMANDS, results);
        }
        Collections.sort(results);
        return results;
    }
}
