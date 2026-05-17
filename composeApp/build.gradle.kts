import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    // --- TUS NUEVOS PLUGINS ---
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)

            // --- DEPENDENCIAS ESPECÍFICAS DE ANDROID ---
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.koin.android)
            implementation(libs.ktor.client.android) // El motor de red para Android
            implementation(libs.sqldelight.android.driver) // El motor de base de datos local para Android
        }

        iosMain.dependencies {
            // --- DEPENDENCIAS ESPECÍFICAS DE IOS ---
            implementation(libs.ktor.client.darwin) // El motor de red nativo de Apple
            implementation(libs.sqldelight.native.driver) // El motor de base de datos local para iOS
        }

        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(compose.materialIconsExtended)
            implementation("cafe.adriel.voyager:voyager-screenmodel:1.0.0")
            implementation("cafe.adriel.voyager:voyager-tab-navigator:1.0.0")

            // --- TUS ARMAS NIVEL SENIOR (COMPARTIDAS PARA AMBAS PLATAFORMAS) ---

            // 1. Corrutinas y Tiempo
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)

            // 2. Koin (Inyección de Dependencias)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)

            // 3. Ktor (Red)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            // 4. SQLDelight (Bóveda Offline)
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)

            // 5. Voyager (Navegación de pantallas)
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.transitions)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.jastin.boveda"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.jastin.boveda"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}

// --- CONFIGURACIÓN DE LA BASE DE DATOS LOCAL ---
sqldelight {
    databases {
        create("BovedaDatabase") {
            packageName.set("com.jastin.boveda.data.local")
        }
    }
}