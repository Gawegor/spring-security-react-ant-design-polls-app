package com.example.polls.config;

import com.example.polls.security.CustomUserDetailsService;
import com.example.polls.security.JwtAuthenticationEntryPoint;
import com.example.polls.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * Created by rajeevkumarsingh on 01/08/17.
 */

@Configuration
@EnableWebSecurity
/* ***********************************************************************
 * 启动全局方法级安全，其中启动三种不同的注解来对方法授权
 *  - securedEnable，启用@Secured(String roleName)，只能标注角色名
 *  - jsr250Enabled，启动@RolesAllowed(String), 可以使用JSR标准
 *  - prePostEnabled，启动@PostAuthorize(String)，可以使用SpEL
 *
 * ***********************************************************************/
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /* 提供查找服务给Spring Security */
    @Autowired
    CustomUserDetailsService customUserDetailsService;

    /* 每次401异常都会执行这个对象里面方法*/
    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    /* JWT安全过滤器（接受、验证请求的JWT) */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    /*
     * AuthenticationManagerBuilder用于创建AuthenticationManager实例，
     * AuthenticationManager是Spring Security授权功能的主要对象
     * AuthenticationManagerBuilder可以配置内存、LDAP、JDBC或自定义的授权源、
     * 这个例子中我们通过给自定义的customUserDetailsService来给AuthenticationManagerBuilder提供源
     * 并且设置了编码方式（传入一个PasswordEncoder，我们使用了BCryptPasswordEncoder实现)
     */
    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    /*  这里将AuthenticationManager注入了Spring，并将ID设置为BeanIds.AUTHENTICATION_MANAGER */
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /* *********************************************************************************
     * HttpSecurity可以设置几类设置：
     * 1. 设置CORS（跨域资源共享）、关闭CSRF（关闭跨站请求伪造）；
     * 2. 设置异常处理器（授权失败处理）;
     * 3. 设置Session管理策略为STATELESS（无状态）;
     * 4. 设置公开静态资源和APIs;
     * 5. 最后设置其他全部APIs都需授权才能请求;
     * 6. 在最后添加JWT过滤器；
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                    .and()
                .csrf()
                    .disable()
                .exceptionHandling()
                    .authenticationEntryPoint(unauthorizedHandler)
                    .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .authorizeRequests()
                    .antMatchers("/",
                        "/favicon.ico",
                        "/**/*.png",
                        "/**/*.gif",
                        "/**/*.svg",
                        "/**/*.jpg",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js")
                        .permitAll()
                    .antMatchers("/api/auth/**")
                        .permitAll()
                    .antMatchers("/api/user/checkUsernameAvailability", "/api/user/checkEmailAvailability")
                        .permitAll()
                    .antMatchers(HttpMethod.GET, "/api/polls/**", "/api/users/**")
                        .permitAll()
                    .anyRequest()
                        .authenticated();

        // Add our custom JWT security filter
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    }
}