///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS info.picocli:picocli:4.7.6

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

@Command(name = "create-gitconfig", mixinStandardHelpOptions = true, version = "1.0",
        description = "Creates a minimal .gitconfig file in the local directory.")
public class CreateGitConfig implements Callable<Integer> {

    @Option(names = {"-n", "--name"}, description = "Git user name", required = false)
    private String name = "Werner Fouché";

    @Option(names = {"-e", "--email"}, description = "Git user email", required = false)
    private String email = "werner.fouche@gmail.com";

    public static void main(String[] args) {
        int exitCode = new CommandLine(new CreateGitConfig()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        Path targetPath = Paths.get(".gitconfig");
        
        // The structured content for a standard git config file
        String configContent = String.format(
                "[user]\n" +
                "\tname = %s\n" +
                "\temail = %s\n", 
                name, email
        );

        try {
            System.out.println("Writing configuration to " + targetPath.toAbsolutePath());
            Files.writeString(targetPath, configContent);
            System.out.println("✓ .gitconfig created successfully!");
            return 0;
        } catch (IOException e) {
            System.err.println("❌ Error writing .gitconfig file: " + e.getMessage());
            return 1;
        }
    }
}