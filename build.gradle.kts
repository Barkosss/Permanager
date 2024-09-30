plugins {
    id("java")
}

group = "Permanager"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("net.dv8tion:JDA:5.1.0")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
}

tasks.test {
    useJUnitPlatform()
}