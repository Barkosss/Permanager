plugins {
    id("java")
}

group = "Permanager"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0")) // Тесты
    testImplementation("org.junit.jupiter:junit-jupiter") // Тесты
    implementation("org.reflections:reflections:0.10.2") // Reflections (Для получения классов с пакетов)
    implementation("net.dv8tion:JDA:5.1.0") // Библиотека для работы с Discord
    implementation("org.slf4j:slf4j-nop:2.0.7") // Логирование (Зависимость Reflections)
    implementation("org.slf4j:slf4j-api:1.7.30") // Логирование
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.17.1") // Логирование
}

tasks.test {
    useJUnitPlatform()
}