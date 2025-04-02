plugins {
    id("buildsrc.convention.kotlin-jvm")

    application
}

dependencies {
    implementation(project(":shared"))
}

application {
    mainClass = "pointfeev.client.MainKt"
}