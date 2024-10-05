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
    implementation("org.reflections:reflections:0.10.2")
    implementation("net.dv8tion:JDA:5.1.0")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    implementation("org.slf4j:slf4j-nop:2.0.7")
}

tasks.test {
    useJUnitPlatform()
}