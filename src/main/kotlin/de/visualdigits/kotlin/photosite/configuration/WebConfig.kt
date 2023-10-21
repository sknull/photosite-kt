package de.visualdigits.kotlin.photosite.configuration

import de.visualdigits.kotlin.photosite.model.siteconfig.SiteConfigHolder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Description
import org.springframework.web.servlet.ViewResolver
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.spring6.view.ThymeleafViewResolver
import org.thymeleaf.templateresolver.FileTemplateResolver
import org.thymeleaf.templateresolver.ITemplateResolver
import java.nio.file.Paths

@Configuration
open class WebConfig : WebMvcConfigurer {

    @Value("\${spring.resources.static-locations}")
    private lateinit var rootDirectory: String

    @Autowired
    private lateinit var siteConfigHolder: SiteConfigHolder

    //    @Bean
    //    @Description("Thymeleaf template resolver serving HTML 5")
    //    public ClassLoaderTemplateResolver templateResolver() {
    //        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
    //        templateResolver.setPrefix("templates/");
    //        templateResolver.setCacheable(false);
    //        templateResolver.setSuffix(".html");
    //        templateResolver.setTemplateMode("HTML");
    //        templateResolver.setCharacterEncoding("UTF-8");
    //        return templateResolver;
    //    }

    @Bean
    @Description("Thymeleaf file system template resolver serving HTML 5")
    open fun templateResolver(): ITemplateResolver {
        val templateResolver = FileTemplateResolver()
        templateResolver.prefix = Paths.get(
            rootDirectory.replace("file:", ""),
            "resources",
            "theme",
            siteConfigHolder.siteConfig?.site?.theme,
            "templates"
        ).toString().replace("\\", "/") + "/"
        templateResolver.isCacheable = false
        templateResolver.suffix = ".html"
        templateResolver.setTemplateMode("HTML")
        templateResolver.characterEncoding = "UTF-8"
        return templateResolver
    }

    @Bean
    @Description("Thymeleaf template engine with Spring integration")
    open fun templateEngine(): SpringTemplateEngine {
        val templateEngine = SpringTemplateEngine()
        templateEngine.setTemplateResolver(templateResolver())
        return templateEngine
    }

    @Bean
    @Description("Thymeleaf view resolver")
    open fun viewResolver(): ViewResolver {
        val viewResolver = ThymeleafViewResolver()
        viewResolver.templateEngine = templateEngine()
        viewResolver.characterEncoding = "UTF-8"
        return viewResolver
    }

    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addViewController("/").setViewName("index")
    }
}
