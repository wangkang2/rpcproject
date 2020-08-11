package com.rpc.test.beandefinition;

import com.rpc.annotation.RpcInterface;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.stereotype.Component;

import java.util.Map;

//@Component
//public class MyBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
//    //可用来注册更多的bean到spring容器中
//    @Override
//    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
//        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(TestServiceImpl.class);
//        beanDefinitionRegistry.registerBeanDefinition("testService",rootBeanDefinition);
//    }
//
//    /**
//     *
//     * @param configurableListableBeanFactory
//     * @throws BeansException
//     */
//    //主要用来对bean的定义做一些改变
//    @Override
//    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
//        int size = configurableListableBeanFactory.getBeanDefinitionCount();
//        System.out.println(size);
//        Map<String, Object> bean = configurableListableBeanFactory.getBeansWithAnnotation(RpcInterface.class);
//        System.out.println(bean);
//    }
//}
