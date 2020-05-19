package cn.lanink.gunwar.command.base;

import cn.lanink.gunwar.GunWar;
import cn.lanink.gunwar.utils.Language;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author SmallasWater
 */
abstract public class BaseCommand extends Command {

    private final ArrayList<BaseSubCommand> subCommand = new ArrayList<>();
    private final ConcurrentHashMap<String, Integer> subCommands = new ConcurrentHashMap<>();
    protected GunWar gunWar = GunWar.getInstance();
    protected Language language = gunWar.getLanguage();

    public BaseCommand(String name, String description) {
        super(name,description);
    }

    /**
     * 判断权限
     * @param sender 玩家
     * @return 是否拥有权限
     */
    abstract public boolean hasPermission(CommandSender sender);

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if(hasPermission(sender)) {
            if (sender instanceof Player) {
                if(args.length > 0) {
                    String subCommand = args[0].toLowerCase();
                    if (subCommands.containsKey(subCommand)) {
                        BaseSubCommand command = this.subCommand.get(this.subCommands.get(subCommand));
                        return command.execute(sender, s, args);
                    } else {
                        sendHelp(sender);
                        return true;
                    }
                }else {
                    sendHelp(sender);
                    return true;
                }
            } else {
                sender.sendMessage(this.language.useCmdInCon);
                return true;
            }
        }
        sender.sendMessage(this.language.noPermission);
        return true;
    }

    /**
     * 发送帮助
     * @param sender 玩家
     * */
    abstract public void sendHelp(CommandSender sender);

    protected void addSubCommand(BaseSubCommand cmd) {
        this.subCommand.add(cmd);
        int commandId = (this.subCommand.size()) - 1;
        this.subCommands.put(cmd.getName().toLowerCase(), commandId);
        for (String alias : cmd.getAliases()) {
            this.subCommands.put(alias.toLowerCase(), commandId);
        }
    }

    protected void loadCommandBase(){
        this.commandParameters.clear();
        for(BaseSubCommand subCommand : this.subCommand){
            LinkedList<CommandParameter> parameters = new LinkedList<>();
            parameters.add(new CommandParameter(subCommand.getName(), new String[]{subCommand.getName()}));
            parameters.addAll(Arrays.asList(subCommand.getParameters()));
            this.commandParameters.put(subCommand.getName(),parameters.toArray(new CommandParameter[0]));
        }
    }

}