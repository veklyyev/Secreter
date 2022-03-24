package main.driver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.webdriver.ChromeDriverFactory;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

public class CustomDriverFactory extends ChromeDriverFactory {
    @Override
    @CheckReturnValue
    @Nonnull
    public MutableCapabilities createCapabilities(Config config, Browser browser,
                                                  @Nullable Proxy proxy, File browserDownloadsFolder) {
        var capabilities = createCommonCapabilities(config, browser, proxy);
        var options = new ChromeOptions();
        options.addArguments("--incognito");
        options.asMap().forEach(capabilities::setCapability);
        return new MutableCapabilities(capabilities);
    }
}
