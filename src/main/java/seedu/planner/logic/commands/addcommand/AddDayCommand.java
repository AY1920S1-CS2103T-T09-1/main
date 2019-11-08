package seedu.planner.logic.commands.addcommand;

import static java.util.Objects.requireNonNull;
import static seedu.planner.commons.util.CollectionUtil.requireAllNonNull;

import seedu.planner.commons.core.index.Index;
import seedu.planner.logic.CommandHistory;
import seedu.planner.logic.autocomplete.CommandInformation;
import seedu.planner.logic.commands.Command;
import seedu.planner.logic.commands.UndoableCommand;
import seedu.planner.logic.commands.exceptions.CommandException;
import seedu.planner.logic.commands.result.CommandResult;
import seedu.planner.logic.commands.result.UiFocus;
import seedu.planner.logic.commands.util.HelpExplanation;
import seedu.planner.logic.events.Event;
import seedu.planner.logic.events.EventFactory;
import seedu.planner.model.Model;
import seedu.planner.model.day.Day;

/**
 * Adds a number of days to the itinerary.
 */
public class AddDayCommand extends AddCommand {

    public static final String SECOND_COMMAND_WORD = "days";

    public static final HelpExplanation MESSAGE_USAGE = new HelpExplanation(
            COMMAND_WORD + " " + SECOND_COMMAND_WORD,
            "Adds the number of days specified to the itinerary.",
            COMMAND_WORD + " " + SECOND_COMMAND_WORD + " NUMBER OF DAYS",
            COMMAND_WORD + " " + SECOND_COMMAND_WORD + " 5"
    );

    public static final CommandInformation COMMAND_INFORMATION = new CommandInformation(
            COMMAND_WORD + " " + SECOND_COMMAND_WORD,
            "<NUM_DAYS>"
    );

    public static final String MESSAGE_SUCCESS = "%d day(s) added";
    public static final int MAX_NUMBER_OF_DAYS_ALLOWED_IN_PLANNER = 15;
    public static final String MESSAGE_NUMBER_OF_DAYS_LIMIT_EXCEEDED = "Total number of days exceeds the limit of "
            + MAX_NUMBER_OF_DAYS_ALLOWED_IN_PLANNER;

    private final int toAdd;
    private final Index index;
    private final Day dayToAdd;

    /**
     * Creates an AddDayCommand to add the specified {@code List} of {@code Day}s
     */
    public AddDayCommand(int numDays) {
        requireNonNull(numDays);
        toAdd = numDays;
        index = null;
        dayToAdd = null;
    }

    public AddDayCommand(Index index, Day dayToAdd) {
        requireAllNonNull(index, dayToAdd);
        this.index = index;
        this.dayToAdd = dayToAdd;
        toAdd = 1;
    }

    public int getToAdd() {
        return toAdd;
    }

    @Override
    public String getSecondCommandWord() {
        return SECOND_COMMAND_WORD;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        int currNumDays = model.getNumberOfDays();
        if ((currNumDays + toAdd) > MAX_NUMBER_OF_DAYS_ALLOWED_IN_PLANNER) {
            throw new CommandException(MESSAGE_NUMBER_OF_DAYS_LIMIT_EXCEEDED);
        }
        if (index == null && dayToAdd == null) {
            //Not due to undo method
            Event addDayEvent = EventFactory.parse(this, model);
            CommandHistory.addToUndoStack(addDayEvent);
            CommandHistory.clearRedoStack();
            model.addDays(toAdd);
        } else {
            //Due to undo method
            model.addDayAtIndex(index, dayToAdd);
        }
        return new CommandResult(
                String.format(MESSAGE_SUCCESS, toAdd),
                new UiFocus[]{UiFocus.AGENDA}
        );
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof AddContactCommand
                && toAdd == ((AddDayCommand) other).toAdd);
    }
}
