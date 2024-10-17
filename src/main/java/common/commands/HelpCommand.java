package common.commands;

import common.iostream.Output;
import common.iostream.OutputHandler;
import common.models.Interaction;
import org.reflections.Reflections;

import java.util.Set;

public class HelpCommand implements BaseCommand {
    public Output output = new OutputHandler();

    // Получить короткое название команды
    public String getCommandName() {
        return "help";
    }

    // Получить описание команды
    public String getCommandDescription() {
        return "Справка по всем командам";
    }

    // Вызвать основной методы команды
    public void run(Interaction interaction) {
        try {

            // Получаем в Set все классы, которые имеют интерфейс BaseCommand и находятся в common.commands
            Reflections reflections = new Reflections("common.commands");
            Set<Class<? extends BaseCommand>> subclasses = reflections.getSubTypesOf(BaseCommand.class);

            output.output(interaction.setMessage("--------- HELP ---------").setInline(false));

            // Вывести короткое название и описание команды
            for (Class<? extends BaseCommand> subclass : subclasses) {
                BaseCommand command = subclass.getConstructor().newInstance();
                output.output(interaction.setMessage(command.getCommandName() + ":\n|---\t" + command.getCommandDescription()).setInline(false));
            }
            
            output.output(interaction.setMessage("--------- HELP ---------").setInline(false));
        } catch (Exception err) {
            System.out.println("[ERROR] Error: " + err);
        }
    }
}
