package com.mctoluene.productinformationmanagement.configuration.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;

@Component
public class SpringContext implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(SpringContext.class);

    private static ApplicationContext context;

    public static <T> T getBean(Class<T> beanClass) {
        ensureContextIsFullyBooted();

        return context.getBean(beanClass);
    }

    public static <T> T getBean(Class<T> beanClass, String qualifier) {
        ensureContextIsFullyBooted();

        return BeanFactoryAnnotationUtils.qualifiedBeanOfType(context.getAutowireCapableBeanFactory(), beanClass,
                qualifier);
    }

    private static void ensureContextIsFullyBooted() {
        if (context == null) {
            logger.error("Application context is not fully booted");
            throw new UnProcessableEntityException("Could not process request, service not fully booted!");
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
