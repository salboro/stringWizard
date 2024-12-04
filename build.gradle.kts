plugins {
	id("java")
	id("org.jetbrains.kotlin.jvm") version "1.9.21"
	id("org.jetbrains.intellij") version "1.17.4"
	id("com.gradleup.shadow") version "8.3.5"
}

group = "com.string.wizard"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
	version.set("2023.1.5")
	type.set("IC") // Target IDE Platform
	plugins.set(listOf("android"))
	updateSinceUntilBuild.set(false)
}

tasks {
	runIde {
		ideDir.set(file("/Applications/Android Studio.app/Contents"))
	}

	// Set the JVM compatibility versions
	withType<JavaCompile> {
		sourceCompatibility = "17"
		targetCompatibility = "17"
	}
	withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
		kotlinOptions.jvmTarget = "17"
	}

	patchPluginXml {
		sinceBuild.set("231")
	}

	signPlugin {
		certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
		privateKey.set(System.getenv("PRIVATE_KEY"))
		password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
	}

	publishPlugin {
		token.set(System.getenv("PUBLISH_TOKEN"))
	}
}

dependencies {
	implementation("org.apache.poi:poi:5.3.0")
	implementation("org.apache.poi:poi-ooxml:5.3.0")
	implementation("org.apache.poi:poi-ooxml-full:5.3.0")
	implementation("org.apache.xmlbeans:xmlbeans:5.2.2")
	implementation("org.apache.logging.log4j:log4j-api:2.24.2")
	implementation("org.apache.logging.log4j:log4j-core:2.24.2")
	implementation("org.apache.commons:commons-collections4:4.4")
}

tasks {
	shadowJar {
		archiveClassifier.set("")
		dependencies {
			include(dependency("org.apache.poi:poi:5.3.0"))
			include(dependency("org.apache.poi:poi-ooxml:5.3.0"))
			include(dependency("org.apache.poi:poi-ooxml-full:5.3.0"))
			include(dependency("org.apache.xmlbeans:xmlbeans:5.2.2"))
			include(dependency("org.apache.logging.log4j:log4j-api:2.24.2"))
			include(dependency("org.apache.logging.log4j:log4j-core:2.24.2"))
			include(dependency("org.apache.commons:commons-collections4:4.4"))
		}
	}
}