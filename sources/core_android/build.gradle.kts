plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}
apply<ModuleConfigPlugin>()
android {
    namespace = "ru.mvrlrd.core_android"
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}