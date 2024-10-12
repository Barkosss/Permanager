plugins {
    id("java")
    id("org.springframework.boot") version "3.2.1"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "Permanager"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://plugins.gradle.org/m2/")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0")) // Тесты
    testImplementation("org.junit.jupiter:junit-jupiter") // Тесты
    implementation("org.reflections:reflections:0.10.2") // Reflections (Для получения классов с пакетов)
    implementation("net.dv8tion:JDA:5.1.0") // Библиотека для работы с Discord
    implementation("org.slf4j:slf4j-nop:2.0.7") // Логирование (Зависимость Reflection)
    //implementation("org.springframework.boot:spring-boot-starter-data-redis") // Кэширование
    //implementation("org.springframework.boot:spring-boot-starter-data-cassandra") // База данных
    implementation("org.telegram:telegrambots-longpolling:7.10.0") // Интеграция с Telegram
    implementation("com.googlecode.json-simple:json-simple:1.1.1") // Взаимодействие с JSON файлами
}

tasks.test {
    useJUnitPlatform()
}