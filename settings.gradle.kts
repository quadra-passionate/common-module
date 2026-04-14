pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            url = uri("https://maven.pkg.github.com/quadra-passionate/convention-plugins")
            credentials {
                username = providers.gradleProperty("gpr.plugin.user").get()
                password = providers.gradleProperty("gpr.plugin.token").get()
            }
        }
    }
    plugins {
        id("com.quadra.spring-library-conventions") version "1.0.1"
        id("com.quadra.library-conventions") version "1.0.1"
    }
}

rootProject.name = "common-module"

include("core")
include("contract")
include("jwt")
include("cookie")