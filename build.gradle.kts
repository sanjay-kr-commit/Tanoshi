import org.jetbrains.compose.desktop.application.dsl.TargetFormat


plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "Tanoshi"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://jitpack.io")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(compose.materialIconsExtended)
                implementation("com.github.sanjay-kr-commit:tanoshi-source-api:0.1")
                implementation("com.squareup.okhttp:okhttp:2.7.5")
                implementation("org.jsoup:jsoup:1.15.3")
                runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:1.7.22")
                implementation("com.google.code.gson:gson:2.9.1")
                implementation("uk.co.caprica:vlcj:4.8.2")
                implementation(kotlin("reflect"))
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Tanoshi"
            packageVersion = "1.0.0"
            linux {
                iconFile.set(project.file("app_icon/linux.png"))
            }
        }
    }
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = "MainKt"
    }
}
