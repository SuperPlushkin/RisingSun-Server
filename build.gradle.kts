
plugins {
    id("java")

    id("org.springframework.boot") version("3.4.0")
    id("io.spring.dependency-management") version("1.1.7")

    id("application")
    id("org.openjfx.javafxplugin") version "0.0.13"
}

application {
    mainClass.set("com.RisingSun.Main")
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}


group = "com.RisingSun"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    implementation("org.postgresql:postgresql")

    // JavaFX
    implementation("org.openjfx:javafx-controls:17.0.2")
    implementation("org.openjfx:javafx-fxml:17.0.2")
    implementation("org.openjfx:javafx-base:17.0.2")
    implementation("org.openjfx:javafx-graphics:17.0.2")
    implementation("org.openjfx:javafx-web:17.0.2")

    // HTTP Client для общения с сервером
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("org.slf4j:slf4j-simple:2.0.7")

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
}

tasks.test {
    useJUnitPlatform()
}

// Отключение ошибок
tasks.withType<JavaExec>().configureEach {
    jvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
}

javafx {
    version = "17"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.web")
}

tasks.withType<JavaExec>().configureEach {
    jvmArgs = listOf(
        "--module-path=classpath",
        "--add-modules=javafx.controls,javafx.fxml,javafx.base,javafx.graphics,javafx.web",
        "--enable-native-access=ALL-UNNAMED"
    )
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.RisingSun.messenger.MessengerApp"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}