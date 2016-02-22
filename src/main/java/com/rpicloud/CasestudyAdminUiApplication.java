package com.rpicloud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

@SpringBootApplication
@RestController
@EnableRedisHttpSession
public class CasestudyAdminUiApplication {

	@RequestMapping("/user")
	public Map<String, Object> user(Principal user) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("name", user.getName());
		map.put("roles", AuthorityUtils.authorityListToSet(((Authentication) user)
				.getAuthorities()));
		return map;
	}

	public static void main(String[] args) {
		SpringApplication.run(CasestudyAdminUiApplication.class, args);
	}

	@Configuration
	protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			// @formatter:off
			http
					.httpBasic()
					.and()
					.authorizeRequests()
					.antMatchers("/index.html", "/unauthenticated.html", "/").permitAll()
					.anyRequest().hasRole("ADMIN")
					.and()
					.csrf().disable();
			// @formatter:on
		}
	}

	@Autowired
	public void setEnvironment(Environment e){
		System.out.println(e.getProperty("configuration.projectName"));
	}

}

@RestController
@RefreshScope
class ProjectNameRestController {
	@Value("${configuration.projectName}")
	String projectName;

	@RequestMapping("/project-name")
	String projectName(){
		return this.projectName;
	}
}
