import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.4.3"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	war
	kotlin("jvm") version "1.4.30"
	kotlin("plugin.spring") version "1.4.30"
}

group = "com.emr"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-neo4j")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.4.3")
	implementation("io.ktor:ktor-client-core:1.5.2")
	implementation("io.ktor:ktor-client-cio:1.5.2")
	implementation("com.google.code.gson:gson:2.8.6")
	implementation("com.rethinkdb:rethinkdb-driver:2.3.3")
	implementation("org.springframework.boot:spring-boot-starter-websocket")
	implementation("software.amazon.kinesis:amazon-kinesis-client:2.3.4")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
