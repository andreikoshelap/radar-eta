package com.gatto.radar.eta;

import com.gatto.radar.zone.Zone;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BoltEtaScraperService {

    private static final Pattern MINUTES_PATTERN = Pattern.compile("(\\d+)\\s*мин");
    private static final String ETA_SELECTOR = "[data-testid='eta-badge']"; // подобрать через devtools/uiautomatorviewer под реальную верстку

    private final Playwright playwright;
    private final Browser browser;

    public BoltEtaScraperService() {
        this.playwright = Playwright.create();
        this.browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(true)
        );
    }

    public Optional<Duration> fetchEtaForZone(Zone zone) {
        try (BrowserContext context = browser.newContext(
                new Browser.NewContextOptions()
                        .setGeolocation(zone.getLatitude(), zone.getLongitude())
                        .setPermissions(java.util.List.of("geolocation"))
                        .setLocale("ru-RU")
                        .setUserAgent(randomUserAgent())
        )) {
            Page page = context.newPage();
            page.navigate("https://bolt.eu/en-ee/order-taxi/"); // уточнить реальный URL публичной страницы заказа

            page.waitForSelector(ETA_SELECTOR,
                    new Page.WaitForSelectorOptions()
                            .setState(WaitForSelectorState.VISIBLE)
                            .setTimeout(10_000));

            String etaText = page.textContent(ETA_SELECTOR);
            return parseMinutes(etaText).map(Duration::ofMinutes);

        } catch (TimeoutError e) {
            // селектор не появился — либо верстка изменилась, либо для этой зоны нет доступных водителей
            return Optional.empty();
        } catch (Exception e) {
            // логировать с уровня WARN, не ERROR — это ожидаемо нестабильный источник
            return Optional.empty();
        }
    }

    private Optional<Integer> parseMinutes(String text) {
        if (text == null) return Optional.empty();
        Matcher matcher = MINUTES_PATTERN.matcher(text);
        return matcher.find()
                ? Optional.of(Integer.parseInt(matcher.group(1)))
                : Optional.empty();
    }

    private String randomUserAgent() {
        // ротация из небольшого пула реалистичных UA — не выдумывать экзотику
        String[] pool = {
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0 Safari/537.36",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0 Safari/537.36"
        };
        return pool[new java.util.Random().nextInt(pool.length)];
    }

    @jakarta.annotation.PreDestroy
    void shutdown() {
        browser.close();
        playwright.close();
    }
}