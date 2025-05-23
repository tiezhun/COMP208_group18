package com.iurac.recruit.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.iurac.recruit.security.CustomerRealm;
import com.iurac.recruit.security.RedisCacheManager;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.context.annotation.Bean;

@Configuration
public class ShiroConfig {


    @Bean
    public ShiroDialect shiroDialect(){
        return new ShiroDialect();
    }

    @Bean
    public SessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setGlobalSessionTimeout(1800000); // 30分钟
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setSessionIdCookieEnabled(true);  // ✅ 允许 Cookie 传递
        sessionManager.setSessionIdUrlRewritingEnabled(false);  // ✅ 避免 URL 携带 JSESSIONID
        return sessionManager;
    }

    @Bean
    public DefaultWebSecurityManager securityManager(CustomerRealm customerRealm, RedisCacheManager redisCacheManager, SessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        // 设置密码匹配器
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        hashedCredentialsMatcher.setHashIterations(1024);
        customerRealm.setCredentialsMatcher(hashedCredentialsMatcher);

        // 配置缓存
        customerRealm.setCachingEnabled(true);
        customerRealm.setAuthorizationCachingEnabled(true);
        customerRealm.setAuthenticationCachingEnabled(true);
        customerRealm.setAuthorizationCacheName("authorizationCacheName");
        customerRealm.setAuthenticationCacheName("authenticationCacheName");
        customerRealm.setCacheManager(redisCacheManager);

        securityManager.setRealm(customerRealm);
        securityManager.setSessionManager(sessionManager); // ✅ 确保 Shiro 处理 Session
//        securityManager.setSessionMode(SecurityManager.SESSION_MODE_HTTP);

        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);//设置安全管理器

        Map<String, String> urlMap = new HashMap<>();
        urlMap.put("/index","anon");
        urlMap.put("/", "anon");
        urlMap.put("/register", "anon");
        urlMap.put("/login", "anon");
        urlMap.put("/logout", "anon");
        urlMap.put("/kaptcha/**", "anon");
        urlMap.put("/layer/**", "anon");
        urlMap.put("/layui/**", "anon");
        urlMap.put("/**/*.js", "anon");
        urlMap.put("/**/*.png", "anon");
        urlMap.put("/druid/**", "anon");


        urlMap.put("/**", "authc");


        shiroFilterFactoryBean.setFilterChainDefinitionMap(urlMap);
        shiroFilterFactoryBean.setLoginUrl("/login");

        return shiroFilterFactoryBean;
    }
}
