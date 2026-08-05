package starterpackage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan("starterpackage")
@ComponentScan("starterpackage.filtros")
public class SpringConfig {
    public static void main() {
        SpringApplication springApplication = new SpringApplication(SpringConfig.class);
        springApplication.setBannerMode(Banner.Mode.OFF);
        springApplication.setLogStartupInfo(false);
        springApplication.run();
    }
}
