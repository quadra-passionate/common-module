plugins {
    id("com.quadra.spring-library-conventions")
}

dependencies {
    api(project(":core"))
    compileOnly("org.springframework.boot:spring-boot-starter-web")

    testImplementation(project(":core"))
    testCompileOnly("jakarta.validation:jakarta.validation-api")
    testImplementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("jakarta.servlet:jakarta.servlet-api")
}