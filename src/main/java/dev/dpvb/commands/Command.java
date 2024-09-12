package dev.dpvb.commands;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public abstract class Command extends ListenerAdapter {

    private final String name;
    private final String description;

    public Command(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public SlashCommandData generateSlashCommand() {
        return Commands.slash(this.name, this.description);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}
