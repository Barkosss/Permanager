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
    implementation("com.googlecode.json-simple:json-simple:1.1.1") // Работа с JSON файлами
    //implementation("org.slf4j:slf4j-log4j12:2.0.7") // Логирование (Зависимость Reflections)
    implementation("org.springframework.boot:spring-boot-starter-data-redis") // Кэширование
    implementation("org.springframework.boot:spring-boot-starter-data-cassandra") // База данных
}

tasks.test {
    useJUnitPlatform()
}