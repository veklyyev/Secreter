package main;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.List;
import java.util.function.Function;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.codeborne.selenide.Configuration;
import lombok.SneakyThrows;
import main.driver.CustomDriverFactory;
import main.model.Line;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byXpath;


public class Main {

    @Parameter(names = {"--url"}, required = true)
    private String secretUrl;

    @Parameter(names = {"--email"}, required = true)
    private String email;

    @Parameter(names = {"--password"}, required = true)
    private String password;

    @Parameter(names = {"--path"}, required = true)
    private String path;

    @Parameter(names = {"--environment"}, required = true)
    private String environment;

    private static final Function<String, Line> CONVERTER = line -> {
        line = line.replaceFirst("bamboo_secret_[a-zA-Z]*_", "");
        var key = line.split("=")[0];
        var value = line.split("=")[1];
        return new Line(key, value);
    };

    static {
        Configuration.browser = CustomDriverFactory.class.getName();
        Configuration.timeout = 60000;
        Configuration.holdBrowserOpen = true;
    }

    public static void main(String[] args) {
        Main main = new Main();
        JCommander.newBuilder()
                .addObject(main)
                .build()
                .parse(args);
        var textLines = main.getTextLines(main.path);
        var linesCollection = main.convertToLinesList(textLines);
        main.login(main.email, main.password);
        main.fillSecrets(main.secretUrl, main.environment, linesCollection);
    }

    public List<Line> convertToLinesList(Stream<String> lines) {
        return lines.map(CONVERTER).collect(Collectors.toCollection(LinkedList::new));
    }

    @SneakyThrows(IOException.class)
    public Stream<String> getTextLines(String relativePath) {
        String uri = new File(relativePath).getAbsolutePath();
        return Files.lines(Paths.get(uri));
    }

    public void login(String email, String password) {
        open("https://login.microsoftonline.com");
        $("input[type=email]").shouldBe(visible);
        $("input[type=email]").setValue(email);
        $("input[type=submit]").click();
        $("input[type=password]").shouldBe(visible);
        $("input[type=password]").setValue(password);
        $("input[type=submit]").click();
    }

    public void fillSecrets(String secretsUrl, String environment, List<Line> lines) {
        open(secretsUrl);
        $(byXpath("//tbody/tr/td[2]/*[text()='" + environment + "']/../../descendant::span[@class='MuiButton-label']")).shouldBe(visible);
        $(byXpath("//tbody/tr/td[2]/*[text()='" + environment + "']/../../descendant::span[@class='MuiButton-label']")).click();
        $(byXpath("//div[@role='presentation' and not(contains(@style,'visibility: hidden'))]//span[text()='Update Secret']")).shouldBe(visible);
        $(byXpath("//div[@role='presentation' and not(contains(@style,'visibility: hidden'))]//span[text()='Update Secret']")).click();

        $("input[type=checkbox]").shouldBe(exist);
        $("input[type=checkbox]").click();

        for (int i = 0; i < lines.size() - 1; i++) {
            $(byXpath("//span[contains(text(),'Add a key')]")).click();
        }

        var keys = $$("input#key-0");
        var values = $$("input#value-0");

        int i = 0;
        for (Line line : lines) {
            keys.get(i).setValue(line.getKey());
            values.get(i).setValue(line.getValue());
            i++;
        }
    }
}
