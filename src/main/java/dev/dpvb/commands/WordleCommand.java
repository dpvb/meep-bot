package dev.dpvb.commands;

import dev.dpvb.mongo.MongoManager;
import dev.dpvb.mongo.services.WordleEntryService;

public abstract class WordleCommand extends Command {

    protected final WordleEntryService wes;

    public WordleCommand(String name, String description) {
        super(name, description);

        this.wes = MongoManager.getInstance().getWordleEntryService();
    }

}
