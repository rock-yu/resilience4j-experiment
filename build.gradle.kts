val resilience4jVersion = "2.2.0"

plugins {
    kotlin("jvm") version "1.9.21"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.resilience4j:resilience4j-kotlin:${resilience4jVersion}")
    // also have a dependency on the core module(s) needed - for example, retry:
    implementation("io.github.resilience4j:resilience4j-retry:${resilience4jVersion}")
    implementation("io.github.resilience4j:resilience4j-circuitbreaker:${resilience4jVersion}")
    implementation("io.github.resilience4j:resilience4j-ratelimiter:${resilience4jVersion}")
    implementation("io.github.resilience4j:resilience4j-timelimiter:${resilience4jVersion}")


    testImplementation("org.mockito:mockito-inline:4.8.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}