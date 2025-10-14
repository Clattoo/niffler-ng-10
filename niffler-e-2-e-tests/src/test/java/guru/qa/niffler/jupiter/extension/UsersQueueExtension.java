package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class UsersQueueExtension implements
        BeforeTestExecutionCallback,
        AfterTestExecutionCallback,
        ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);

    public record StaticUser(String username, String password, String friend, String income, String outcome) {
    }

    private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_FRIEND_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_INCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_OUTCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();

    static {
        EMPTY_USERS.add(new StaticUser("Pedro", "test124", null, null, null));
        WITH_FRIEND_USERS.add(new StaticUser("Pablo", "test123", "Clatto", null, null));
        WITH_INCOME_REQUEST_USERS.add(new StaticUser("Mr.Black", "vipBlack", null, "Mr_White", null));
        WITH_OUTCOME_REQUEST_USERS.add(new StaticUser("Mr_White", "white!Purple", null, null, "Mr.Black"));
    }

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface UserType {
        Type value() default Type.EMPTY;

        enum Type {
            EMPTY, WITH_FRIEND, WITH_INCOME_REQUEST, WITH_OUTCOME_REQUEST
        }
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(p -> AnnotationSupport.isAnnotated(p, UserType.class))
                .forEach(p -> { // <-- теперь обрабатываем все параметры, а не только первый
                    UserType ut = p.getAnnotation(UserType.class);
                    Optional<StaticUser> user = Optional.empty();
                    StopWatch sw = StopWatch.createStarted();

                    while (user.isEmpty() && sw.getTime(TimeUnit.SECONDS) < 30) {
                        user = switch (ut.value()) {
                            case EMPTY -> Optional.ofNullable(EMPTY_USERS.poll());
                            case WITH_FRIEND -> Optional.ofNullable(WITH_FRIEND_USERS.poll());
                            case WITH_INCOME_REQUEST -> Optional.ofNullable(WITH_INCOME_REQUEST_USERS.poll());
                            case WITH_OUTCOME_REQUEST -> Optional.ofNullable(WITH_OUTCOME_REQUEST_USERS.poll());
                        };
                    }

                    Map<UserType.Type, StaticUser> usersForTest =
                            (Map<UserType.Type, StaticUser>) context.getStore(NAMESPACE)
                                    .getOrComputeIfAbsent(context.getUniqueId(), key -> new HashMap<>());

                    Allure.getLifecycle().updateTestCase(testCase ->
                            testCase.setStart(new Date().getTime())
                    );
                    user.ifPresentOrElse(
                            u ->
                                    usersForTest.put(
                                            ut.value(),
                                            u
                                    ),
                            () -> {
                                throw new IllegalStateException("Can`t obtain user after 30s.");
                            }
                    );
                });
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        Map<UserType.Type, StaticUser> users = context.getStore(NAMESPACE).get(
                context.getUniqueId(),
                Map.class
        );
        if (users != null) {
            for (Map.Entry<UserType.Type, StaticUser> userEntry : users.entrySet()) {
                UserType.Type type = userEntry.getKey();
                StaticUser user = userEntry.getValue();

                switch (type) {
                    case EMPTY -> EMPTY_USERS.add(user);
                    case WITH_FRIEND -> WITH_FRIEND_USERS.add(user);
                    case WITH_INCOME_REQUEST -> WITH_INCOME_REQUEST_USERS.add(user);
                    case WITH_OUTCOME_REQUEST -> WITH_OUTCOME_REQUEST_USERS.add(user);
                }
            }
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
    }

    @Override
    public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        UserType ut = parameterContext.findAnnotation(UserType.class)
                .orElseThrow();

        Map<UserType.Type, StaticUser> users = extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), Map.class);

        if (users == null) {
            throw new ParameterResolutionException("No users map found in store");
        }

        StaticUser user = users.get(ut.value());
        if (user == null) {
            throw new ParameterResolutionException("No user found for annotation: " + ut.value());
        }
        return user;
    }
}
