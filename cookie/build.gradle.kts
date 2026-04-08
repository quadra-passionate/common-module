plugins {
    id("com.quadra.spring-library-conventions")
}

dependencies {
    // for Spring MVC
    compileOnly("jakarta.servlet:jakarta.servlet-api")

    // for Spring WebFlux
    compileOnly("org.springframework:spring-web")

    // testing dependencies
    testCompileOnly("jakarta.servlet:jakarta.servlet-api")
    testImplementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-webflux")
}