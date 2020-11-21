plugins {
    java
    id("org.springframework.boot")
    id("io.freefair.lombok")
    id("com.github.node-gradle.node")
}

group = "pl.krug.yagna.transcoding"
version = "1.0.0"

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

val jsProject: String = "${project.projectDir}/src/main/js"

val yarn: Task by tasks

val buildProd = tasks.register<com.moowork.gradle.node.yarn.YarnTask>("buildProd") {
    dependsOn(yarn)
    setWorkingDir(file(jsProject))
    args = listOf("build:prod")
}

val bootJar: Task by tasks
bootJar.dependsOn(buildProd)

node {
    version = "10.19.0"
    yarnVersion = "0.17.8"
    download = true
    // Set the work directory where node_modules should be located
    nodeModulesDir = file(jsProject)
}