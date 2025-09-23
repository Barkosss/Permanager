import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import common.commands.BaseCommand;
import common.models.Interaction;
import common.models.InteractionConsole;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LocalizationTest {
    private static final Map<String, BaseCommand> commands = new HashMap<>();

    @BeforeAll
    static void loadClasses() {
        try {
            Reflections reflections = new Reflections("common.commands");
            Set<Class<? extends BaseCommand>> subclasses = reflections.getSubTypesOf(BaseCommand.class);

            BaseCommand instanceClass;
            for (Class<? extends BaseCommand> subclass : subclasses) {
                if (Modifier.isAbstract(subclass.getModifiers()) || subclass.isInterface()) {
                    continue;
                }

                instanceClass = subclass.getConstructor().newInstance();
                commands.put(subclass.getSimpleName(), instanceClass);
            }
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }
    }

    @Test
    public void testLocalization() {
        boolean hasAllLocal = true;
        JavaParser javaParser = new JavaParser();

        Path commandsPath = Paths.get("src/main/java/common/commands");

        List<String> keys = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(commandsPath)) {
            walk.filter(p -> p.toString().endsWith(".java"))
                    .forEach(path -> {
                        try {
                            ParseResult<CompilationUnit> pcu = javaParser.parse(path);
                            Optional<CompilationUnit> ocu = pcu.getResult();
                            ocu.ifPresent(cu -> cu.findAll(MethodCallExpr.class).forEach(mce -> {
                                if (mce.getNameAsString().equals("getLanguageValue")) {
                                    mce.getArguments().forEach(arg -> {
                                        if (arg.isStringLiteralExpr()) {
                                            String key = arg.asStringLiteralExpr().getValue();

                                            if (key.startsWith(".")) {
                                                String className = mce.findAncestor(com.github.javaparser.ast.body.ClassOrInterfaceDeclaration.class)
                                                        .map(NodeWithSimpleName::getNameAsString)
                                                        .orElse(null);

                                                if (className != null && commands.containsKey(className)) {
                                                    try {
                                                        BaseCommand instance = commands.get(className);
                                                        String commandName = instance.getCommandName();
                                                        key = commandName + key;
                                                    } catch (Exception ex) {
                                                        System.out.println("Exception: " + ex.getMessage());
                                                    }
                                                }
                                            }
                                            keys.add(key);
                                        }
                                    });
                                }
                            }));

                        } catch (IOException ex) {
                            System.out.println("Exception: " + ex.getMessage());
                        }
                    });
        } catch (IOException ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        Interaction interaction = new InteractionConsole();
        for (String key : keys) {
            String local = interaction.getLanguageValue(key);
            if (local.equalsIgnoreCase(key)) {
                System.out.println("Found local key without text: " + key);
                hasAllLocal = false;
            }
        }

        System.out.println("All keys: " + keys.size());
        assertTrue(hasAllLocal);
    }
}
