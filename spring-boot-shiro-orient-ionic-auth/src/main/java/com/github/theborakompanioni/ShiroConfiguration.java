package com.github.theborakompanioni;

import com.github.theborakompanioni.repository.UserRepository;
import com.github.theborakompanioni.shiro.HazelcastSessionDao;
import com.github.theborakompanioni.shiro.OrientDbRealm;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * TODO add description
 */
@Configuration
public class ShiroConfiguration {

    @Bean
    public ShiroFilterFactoryBean shiroFilter(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager);
        return factoryBean;
    }

    @Bean(name = "securityManager")
    public DefaultWebSecurityManager securityManager(OrientDbRealm realm) {
        final DefaultWebSecurityManager securityManager
                = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        securityManager.setSessionManager(sessionManager());
        return securityManager;
    }

    @Bean
    public DefaultWebSessionManager sessionManager() {
        final DefaultWebSessionManager sessionManager
                = new DefaultWebSessionManager();
        sessionManager.setSessionDAO(sessionDao());
        sessionManager.setGlobalSessionTimeout(43200000); // 12 hours
        return sessionManager;
    }

    @Bean
    public SessionDAO sessionDao() {
        return new HazelcastSessionDao();
    }

    @Bean(name = "realm")
    @DependsOn("lifecycleBeanPostProcessor")
    public OrientDbRealm realm(UserRepository userRepository) {
        final OrientDbRealm realm = new OrientDbRealm(userRepository);
        realm.setCacheManager(new MemoryConstrainedCacheManager());
        realm.setCredentialsMatcher(credentialsMatcher());
        return realm;
    }

    @Bean(name = "credentialsMatcher")
    public PasswordMatcher credentialsMatcher() {
        final PasswordMatcher credentialsMatcher = new PasswordMatcher();
        credentialsMatcher.setPasswordService(passwordService());
        return credentialsMatcher;
    }

    @Bean(name = "passwordService")
    public DefaultPasswordService passwordService() {
        return new DefaultPasswordService();
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

}
