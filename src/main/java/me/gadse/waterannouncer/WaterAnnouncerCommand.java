package me.gadse.waterannouncer;

import com.google.common.base.Stopwatch;
import me.gadse.waterannouncer.messages.Messages;
import me.gadse.waterannouncer.messages.PlaceholderType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WaterAnnouncerCommand extends Command implements TabExecutor {

    private final WaterAnnouncer plugin;

    public WaterAnnouncerCommand(WaterAnnouncer plugin) {
        super("waterannouncer", "waterannouncer.admin", "wa", "announcer");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String subCommand = args.length == 0 ? "help" : args[0].toLowerCase();

        switch (subCommand) {
            case "reload": {
                Stopwatch stopwatch = Stopwatch.createStarted();
                plugin.reload();
                stopwatch.stop();
                Messages.RELOAD.send(sender,
                        PlaceholderType.TIME.with("" + stopwatch.elapsed(TimeUnit.MILLISECONDS)));
                break;
            }
            case "start": {
                if (args.length < 2) {
                    showHelp(sender);
                    break;
                }

                Announcement announcement = plugin.getAnnouncements().get(args[1]);
                if (announcement == null)
                    break;
                announcement.stop();
                announcement.start();
                Messages.ANNOUNCEMENT_START.send(sender,
                        PlaceholderType.ANNOUNCEMENT_NAME.with(announcement.getId()),
                        PlaceholderType.ANNOUNCEMENT_INTERVAL.with(announcement.getInterval()),
                        PlaceholderType.ANNOUNCEMENT_SERVER.with(announcement.getServerName()));
                break;
            }
            case "stop": {
                if (args.length < 2) {
                    showHelp(sender);
                    break;
                }

                Announcement announcement = plugin.getAnnouncements().get(args[1]);
                if (announcement == null)
                    break;
                announcement.stop();
                Messages.ANNOUNCEMENT_STOP.send(sender,
                        PlaceholderType.ANNOUNCEMENT_NAME.with(announcement.getId()),
                        PlaceholderType.ANNOUNCEMENT_INTERVAL.with(announcement.getInterval()),
                        PlaceholderType.ANNOUNCEMENT_SERVER.with(announcement.getServerName()));
                break;
            }
            case "list": {
                plugin.getAnnouncements().forEach((name, announcement) ->
                    Messages.ANNOUNCEMENT_INFO.send(sender,
                            PlaceholderType.ANNOUNCEMENT_NAME.with(announcement.getId()),
                            PlaceholderType.ANNOUNCEMENT_INTERVAL.with(announcement.getInterval()),
                            PlaceholderType.ANNOUNCEMENT_SERVER.with(announcement.getServerName()))
                );
                break;
            }
            default: {
                showHelp(sender);
                break;
            }
        }
    }

    private void showHelp(CommandSender sender) {
        Messages.HELP.send(sender,
                PlaceholderType.COMMAND.with("/wa reload"),
                PlaceholderType.DESCRIPTION.with("Reloads the configuration."));
        Messages.HELP.send(sender,
                PlaceholderType.COMMAND.with("/wa start <announcement>"),
                PlaceholderType.DESCRIPTION.with("Starts an announcement."));
        Messages.HELP.send(sender,
                PlaceholderType.COMMAND.with("/wa stop <announcement>"),
                PlaceholderType.DESCRIPTION.with("Stops an announcement."));
        Messages.HELP.send(sender,
                PlaceholderType.COMMAND.with("/wa list"),
                PlaceholderType.DESCRIPTION.with("Lists all announcements."));
    }

    private final List<String> args_zero = Arrays.asList("help", "reload","start","stop","list");

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>(args_zero);
            completions.removeIf(arg -> !arg.startsWith(args[0]));
            return completions;
        }
        if (args.length == 2) {
            if (!args[0].equalsIgnoreCase("start") && !args[0].equalsIgnoreCase("stop"))
                return new ArrayList<>();

            List<String> completions = new ArrayList<>(plugin.getAnnouncements().keySet());
            completions.removeIf(arg -> !arg.startsWith(args[1]));
            return completions;
        }
        return new ArrayList<>();
    }
}
