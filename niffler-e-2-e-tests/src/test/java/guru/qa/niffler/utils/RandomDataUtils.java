package guru.qa.niffler.utils;

import com.github.javafaker.Faker;

public class RandomDataUtils {

    private static final Faker faker = new Faker();

    public static Long randomInteger() {
        int digits = faker.number().randomDigitNotZero();
        return faker.number().randomNumber(digits, false);
    }

    public static String randomUsername() {
        return faker.name().username();
    }

    public static String randomName() {
        return faker.name().name();
    }

    public static String randomSurname() {
        return faker.name().lastName();
    }

    public static String randomCategoryName() {
        return faker.company().name();
    }

    public static String randomSentence(int wordsCount) {
        return faker.lorem().sentence(wordsCount);
    }

    public static String getRandomPassword() {
        return faker.internet().password(3, 12, true, true, true);
    }
}
