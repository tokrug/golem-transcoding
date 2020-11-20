plugins {
    java
    id("org.springframework.boot")
    id("io.freefair.lombok")
}

group = "pl.krug.yagna.transcoding"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES))
    annotationProcessor(platform(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES))
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.zaxxer:nuprocess:2.0.0")
    implementation("org.dizitart:nitrite:3.4.2")
    implementation("org.mapstruct:mapstruct:1.4.1.Final")
    implementation("javax.inject:javax.inject:1")
    implementation("commons-io:commons-io:2.8.0")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

val test: Test by tasks
test.useJUnitPlatform()