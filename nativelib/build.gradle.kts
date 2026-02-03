plugins {
}

    android {
    namespace = "com.example.nativelib"
    compileSdk = 35

    defaultConfig {
    minSdk = 26

      testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
      consumerProguardFiles("consumer-rules.pro")
      externalNativeBuild {
        cmake {
          cppFlags("")
        }
      }
    }

    buildTypes {
       release {
           isMinifyEnabled = false
           proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
       }
    }
    externalNativeBuild {
      cmake {
        path("src/main/cpp/CMakeLists.txt")
        version = "3.22.1"
      }
    }
    }

  dependencies {

  }