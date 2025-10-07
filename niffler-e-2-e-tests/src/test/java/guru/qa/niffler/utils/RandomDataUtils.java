package guru.qa.niffler.utils;

import com.github.javafaker.Faker;

public class RandomDataUtils {

    private static final Faker faker = new Faker();

    public static String getRandomUserName() {
        return faker.name().username();
    }

    public static String getRandomPassword() {
        return faker.internet().password(3, 12, true, true, true);
    }
}
