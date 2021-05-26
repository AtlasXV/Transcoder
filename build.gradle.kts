buildscript {

    extra["minSdkVersion"] = 21
    extra["compileSdkVersion"] = 30
    extra["targetSdkVersion"] = 30
    
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/AtlasXV/android-libs")
            credentials {
                username = System.getenv("GPR_USR") ?: project.findProperty("GPR_USR").toString()
                password = System.getenv("GPR_KEY") ?: project.findProperty("GPR_KEY").toString()
            }
        }
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.31")
        classpath("com.android.tools.build:gradle:4.1.3")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.4.30") // publisher uses 1.4.20 which goes OOM
        classpath("io.deepmedia.tools:publisher:0.5.0")
        classpath("com.atlasv.android.publishlib:plugin:1.2.25")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/AtlasXV/android-libs")
            credentials {
                username = System.getenv("GPR_USR") ?: project.findProperty("GPR_USR").toString()
                password = System.getenv("GPR_KEY") ?: project.findProperty("GPR_KEY").toString()
            }
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(buildDir)
}