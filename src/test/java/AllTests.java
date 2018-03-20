
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ActionTest.class, DomainTest.class, LoginTest.class, LogoutTest.class, ModeTest.class,
        MonitoredWebPagesTest.class })
public class AllTests
{

}
