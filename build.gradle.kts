plugins {
    id("java")
    kotlin("jvm") version "1.9.10"
    application
}

application {
    mainClass.value("es.age.dgpe.placsp.risp.parser.BulkGeneratorKt")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "lib", "include" to listOf("*.jar"))))

    implementation("org.jetbrains.exposed:exposed-core:0.44.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.44.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.44.0")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.44.0")
    implementation("mysql:mysql-connector-java:8.0.26")
    implementation("org.apache.poi:poi:5.0.0")
    implementation("org.apache.poi:poi-ooxml:5.0.0")
}