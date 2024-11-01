plugins {
    id("java")
    kotlin("jvm") version "1.9.10"
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

application {
    mainClass.value("es.age.dgpe.placsp.risp.parser.BulkGeneratorKt")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs_estado", "include" to listOf("*.jar"))))

    implementation("org.apache.poi:poi:5.0.0")
    implementation("org.apache.poi:poi-ooxml:5.0.0")

    implementation("org.openjfx:javafx-controls:19")
    implementation("org.openjfx:javafx-fxml:19")

    implementation("com.squareup.okio:okio:3.9.1")
    implementation("javax.activation:activation:1.1")
    implementation("org.jvnet.jaxb2.maven2:maven-jaxb2-plugin:0.14.0")
    implementation("org.apache.logging.log4j:log4j-core:2.24.1")

    implementation("org.glassfish.jaxb:jaxb-runtime:2.3.0")
    implementation("com.sun.xml.bind:jaxb-core:3.0.2")
    implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation("com.sun.xml.bind:jaxb-impl:3.0.2")
    implementation("javax.activation:activation:1.1.1")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:3.0.1")
    implementation("org.jvnet.jaxb2.maven2:maven-jaxb2-plugin:0.15.3")


}
