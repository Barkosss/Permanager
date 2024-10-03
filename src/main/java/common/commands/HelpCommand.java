package common.commands;

import common.iostream.Output;
import common.iostream.OutputTerminal;

import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class HelpCommand implements BaseCommand {
    public Output output = new OutputTerminal();

    public void main(String[] args) {
        System.out.println(args);
        JSONParser parser = new JSONParser();
        try {
            JSONObject root = (JSONObject) parser.parse(new FileReader("./src/main/java/common/commands/commandsInfo.json"));
            JSONObject commandInfo = (JSONObject)root.get("commandInfo");

            output.output("--------- HELP ---------", false);

            for(Object commandName : commandInfo.keySet()) {
                JSONObject commandObject = (JSONObject)commandInfo.get(commandName);
                output.output(commandObject.get("name") + ":\n|---\t" + commandObject.get("description"), false);
            }

            output.output("--------- HELP ---------", false);
        } catch (Exception err) {
            System.out.println("[ERROR] Error: " + err);
        }
    }
}
