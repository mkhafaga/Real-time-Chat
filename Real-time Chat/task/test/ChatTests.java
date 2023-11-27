import com.microsoft.playwright.*;
import org.hyperskill.hstest.dynamic.DynamicTest;
import org.hyperskill.hstest.dynamic.input.DynamicTesting;
import org.hyperskill.hstest.stage.SpringTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.junit.After;
import org.junit.Before;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.hyperskill.hstest.testcase.CheckResult.correct;
import static org.hyperskill.hstest.testcase.CheckResult.wrong;

public class ChatTests extends SpringTest {
    final static String URL = "http://localhost:28852";
    final static String TITLE = "Chat";

    final static String INPUT_MSG_ID_SELECTOR = "#input-msg";
    final static String SEND_MSG_BTN_ID_SELECTOR = "#send-msg-btn";
    final static String MESSAGES_ID_SELECTOR = "#messages";
    final static String MESSAGE_CLASS_SELECTOR = ".message";
    final static String INCORRECT_OR_MISSING_TITLE_TAG_ERR = "tag \"title\" should have correct text";

    Playwright playwright;
    Browser browser;
    Page page;

    @Before
    public void initBrowser() {
        playwright = Playwright.create();

        browser = playwright.chromium().launch(
                new BrowserType
                        .LaunchOptions()
                        .setHeadless(false)
                        .setTimeout(1000 * 120)
                        .setSlowMo(15));
    }

    @After
    public void closeBrowser() {
        if (playwright != null) {
            playwright.close();
        }
    }

    // Tests

    @DynamicTest
    DynamicTesting[] dt = new DynamicTesting[]{
            () -> testInitAndOpenPage(URL),
            () -> testShouldContainProperTitle(page, TITLE),
            () -> testFillInputField(page, "Test message", INPUT_MSG_ID_SELECTOR),
            () -> testPressBtn(page, SEND_MSG_BTN_ID_SELECTOR),
            () -> testUserMessagesShouldHaveProperStructureAndShouldntBeEmpty(page, 5),
    };

    CheckResult testInitAndOpenPage(String url) {
        page = browser.newContext().newPage();
        page.navigate(url);
        page.setDefaultTimeout(1000 * 10);

        return correct();
    }

    CheckResult testShouldContainProperTitle(Page page, String title) {
        return title.equals(page.title()) ? correct() : wrong(INCORRECT_OR_MISSING_TITLE_TAG_ERR);
    }

    CheckResult testFillInputField(Page page, String msg, String inputFieldSelector) {
        try {
            page.fill(inputFieldSelector, msg);
            return correct();
        } catch (PlaywrightException e) {
            return wrong(e.getMessage());
        }
    }

    CheckResult testPressBtn(Page page, String btnSelector) {
        try {
            page.click(btnSelector);
            return correct();
        } catch (PlaywrightException e) {
            return wrong(e.getMessage());
        }
    }

    CheckResult testUserMessagesShouldHaveProperStructureAndShouldntBeEmpty(Page page, int numOfMessages) {
        Locator allMessagesLocator = page.locator(MESSAGES_ID_SELECTOR).locator(MESSAGE_CLASS_SELECTOR);

        try {
            assertThat(allMessagesLocator).hasCount(numOfMessages);

            for (int i = 0; i < numOfMessages; i++) {
                Locator messageLocator = allMessagesLocator.nth(i);

                assertThat(messageLocator).isVisible();
                assertThat(messageLocator).not().isEmpty();
            }

            return correct();
        } catch (AssertionError e) {
            return wrong(e.getMessage());
        }
    }
}
