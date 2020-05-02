package be.kdg.cluedobackend.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.stereotype.Component;

@Component
public class DefaultConversionServiceInitializer implements InitializingBean {
    @Override
    public void afterPropertiesSet() {
        DefaultConversionService conversionService = (DefaultConversionService) DefaultConversionService.getSharedInstance();
    }
}
